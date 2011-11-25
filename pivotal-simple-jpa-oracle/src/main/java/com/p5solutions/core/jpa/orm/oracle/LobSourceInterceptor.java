package com.p5solutions.core.jpa.orm.oracle;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import oracle.sql.BLOB;

import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.p5solutions.core.jpa.orm.Interceptor;
import com.p5solutions.core.jpa.orm.LobSource;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * Looks for the {@link @LobSource} annotation on {@link @Lob} annotated properties and takes care of moving the data in
 * to the blob. It's done on before-save and before-update.
 * 
 * @author zguzijan
 */
@Deprecated // should be handled at the ConversionUtilityImpl
public class LobSourceInterceptor implements Interceptor {

	/** Data source used for temp blob creation. */
	private DataSource dataSource;

	/** Blob list used to later free the temporary space occupied by temp blobs. */
	private static ThreadLocal<List<Blob>> tempBlobsHolder = new ThreadLocal<List<Blob>>();

	/**
	 * Before the entity is saved writing lob to the database is taken care of.
	 * 
	 * @param <T>
	 * @param entity
	 * @return entity
	 */
	@Override
	public <T> T beforeSave(T entity) {
		prepareLobsFromLobSources(entity);
		return entity;
	}

	/**
	 * After the entity is saved there is some cleanup activity taking place.
	 * 
	 * @param <T>
	 * @param entity
	 * @return entity
	 */
	@Override
	public <T> T afterSave(T entity) {
		cleanupLobs();
		return entity;
	}

	/**
	 * Before the entity is updated writing lob to the database is taken care of.
	 * 
	 * @param <T>
	 * @param entity
	 * @return entity
	 */
	@Override
	public <T> T beforeUpdate(T entity) {
		prepareLobsFromLobSources(entity);
		return entity;
	}

	/**
	 * After the entity is updated there is some cleanup activity taking place.
	 * 
	 * @param <T>
	 * @param entity
	 * @return entity
	 */
	@Override
	public <T> T afterUpdate(T entity) {
		cleanupLobs();
		return entity;
	}

	/**
	 * Parse the entity for all {@link @LobSource} annotations and provide the data to the lob from those lob sources.
	 * 
	 * @param entity
	 */
	private void prepareLobsFromLobSources(Object entity) {
		try {
			tempBlobsHolder.set(new ArrayList<Blob>());
			List<Method> lobGetters = ReflectionUtility.findMethodsWithAnnotation(entity.getClass(), LobSource.class);
			for (Method lobGetter : lobGetters) {
				lobGetter.getAnnotations();
				if (!Blob.class.isAssignableFrom(lobGetter.getReturnType())) {
					throw new RuntimeException("LobSource annotation must be found only on java.sql.Blob properties.");
				}
				String lobSetterName = lobGetter.getName().replaceAll("^get", "set");
				Method lobSetter = ReflectionUtility.findSetterMethod(entity.getClass(), lobGetter);
				if (lobSetter == null) {
					throw new RuntimeException("Lob property must have a setter method defined as " + lobSetterName);
				}
				LobSource lobSource = lobGetter.getAnnotation(LobSource.class);
				// loop through the listed source properties and consider the first
				// not-null property
				String[] sourceProperties = lobSource.sourceProperties();
				for (String sourceProperty : sourceProperties) {
					Method sourcePropertyGetter = ReflectionUtility.findGetterMethod(entity.getClass(), sourceProperty);
					if (sourcePropertyGetter == null) {
						throw new RuntimeException("Lob Source property getter " + sourcePropertyGetter + " doesn't exist.");
					}
					// skip null sources
					Object sourcePropertyValue = sourcePropertyGetter.invoke(entity, new Object[0]);
					if (sourcePropertyValue == null) {
						continue;
					}
					Blob blob = prepareLobFromLobSource(entity, sourcePropertyValue);
					tempBlobsHolder.get().add(blob);
					lobSetter.invoke(entity, blob);
					// exist after the first not-null source had been found and used
					break;
				}
				// check if blob has been set in the previous attempt
				if (tempBlobsHolder.get().isEmpty() && lobSource.required()) {
					throw new RuntimeException("No valid (not null) sources for the lob have been found.");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Preparing entity's lobs from lob sources failed.", e);
		}
	}

	/**
	 * Process an lob source field.
	 * 
	 * @param entity
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private Blob prepareLobFromLobSource(Object entity, Object source) throws Exception {
		// check supported sources
		if (source instanceof Blob) {
			return (Blob) source;
		} else if (source instanceof InputStream) {
			return prepareLob((InputStream) source);
		} else if (source instanceof byte[]) {
			return prepareLob((byte[]) source);
		} else {
			throw new RuntimeException("LobSource type " + source.getClass().getName() + " is not supported.");
		}
	}

	/**
	 * Create a (temporary) blob and write to it from the provided input stream.
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private Blob prepareLob(InputStream is) throws Exception {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		BLOB blob = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
		blob.open(BLOB.MODE_READWRITE);
		OutputStream os = blob.setBinaryStream(0);
		IOUtils.copy(is, os);
		os.close();
		blob.close();
		return blob;
	}

	/**
	 * Create a (temporary) blob and write to it from the provided byte array.
	 * 
	 * @param ba
	 * @return
	 * @throws Exception
	 */
	private Blob prepareLob(byte[] ba) throws Exception {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		BLOB blob = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
		blob.open(BLOB.MODE_READWRITE);
		blob.setBytes(1, ba);
		blob.close();
		return blob;
	}

	/**
	 * Cleanup temporary space taken by created temporary blobs.
	 */
	private void cleanupLobs() {
		try {
			for (Blob blob : tempBlobsHolder.get()) {
				BLOB.freeTemporary((BLOB) blob);
			}
		} catch (Exception e) {
			// close your eyes?
		}
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public <T> void beforeDelete(T entity) {
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public <T> void afterDelete(T entity) {
	}

	/**
	 * Injected data source.
	 * 
	 * @return data source
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Injected data source.
	 * 
	 * @param dataSource
	 *          data source
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
