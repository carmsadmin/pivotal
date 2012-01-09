/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  KASRA RASAEE
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.p5solutions.core.jpa.orm.oracle;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import oracle.sql.BLOB;
import oracle.sql.TIMESTAMP;

import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;
import com.p5solutions.core.jpa.orm.lob.BlobStream;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class ConversionUtilityImpl for Oracle.
 * 
 * @author Kasra Rasaee
 * @since 2011-11-22
 */
public class ConversionUtilityImpl extends com.p5solutions.core.jpa.orm.ConversionUtilityImpl implements ConversionUtility {

  /** The data source. */
  private DataSource dataSource;

  // TODO should be configurable?? handles reading of strings, blobs and clobs.
  /** The charset. */
  private Charset charset = Charset.forName("UTF-8");

  /**
   * Checks if is oracle time stamp.
   * 
   * @param type
   *          the type
   * @return true, if is oracle time stamp
   */
  protected boolean isOracleTimeStamp(Class<?> type) {
    if (oracle.sql.TIMESTAMP.class.isAssignableFrom(type)) {
      return true;
    }
    return false;
  }

  /**
   * If oracle blob.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @return the object
   */
  protected Object ifOracleBLOB(ParameterBinder pb, Object value) {

    // if for some reason the object is already an Oracle oracle.sql.BLOB
    if (value instanceof BLOB) {
      BLOB blob = (BLOB) value;

      // simple, if the target and source are the same
      if (Types.BLOB == getSqlType(pb)) {
        return blob;
      }

      // if the destination is an array? not sure why this would happen
      // unless the data model changed during runtime?? return byte array.
      if (Types.ARRAY == getSqlType(pb)) {
        try {
          return blob.getBytes();
        } catch (Exception e) {
          logger.error("Unable to get bytes for when working with " + blob);
        }
      }

      // if the object is of type BLOB but the destination is something else,
      // such as a TIMESTAMP, then its incompatible.
      logger.warn("*** Incompatible value passed of type " + BLOB.class + ", when the target is expecting [sql-type: " + getSqlType(pb)
          + "]. Please check " + pb.getEntityClass() + " on paramater " + pb.getBindingPath() + " column name " + pb.getColumnNameUpper());
    }

    return null;
  }

  /**
   * If blob.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @return the object
   */
  protected Object toSqlBlob(ParameterBinder pb, Object value) {
    if (value == null) {
      return null;
    }

    int type = getSqlType(pb);

    // if the sql target type is blob
    if (Types.BLOB == type) {

      Class<?> clazz = value.getClass();
      boolean isByteArray = ReflectionUtility.isByteArray(clazz);
      boolean isBlob = isByteArray ? false : ReflectionUtility.isBlob(clazz);
      boolean isString = isByteArray | isBlob ? false : ReflectionUtility.isBlob(clazz);
      boolean isInputStream = isByteArray | isBlob | isString ? false : ReflectionUtility.isStringClass(clazz);

      // if the datasource is not set, then throw an error
      if (dataSource == null) {
        logger.error("Required datasource has not been set for " //
            + getClass() + ", when dealing with Lob values, datasource " //
            + "is required for creation of lob space in DB.");

        return null;
      }

      // scope variables
      BLOB blob = null;
      OutputStream os = null;

      // get a database connection
      Connection conn = DataSourceUtils.getConnection(dataSource);

      try {
        // activate the connection and create an empty blob pointer
        blob = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
        blob.open(BLOB.MODE_READWRITE);
        os = blob.setBinaryStream(0);
      } catch (Exception e) {
        logger.error("Unable to create temporary blob when accessing entity " + pb.getEntityClass() + " on paramater "
            + pb.getBindingPath() + " and column " + pb.getColumnName());
        blob = null;
        os = null;
        return null;
      }

      InputStream is = null;

      // if the source is of type byte[]
      if (isByteArray) {
        blob.setBytes((byte[]) value);
      } else if (isBlob) {
        Blob sourceBlob = (Blob) value;
        try {
          is = sourceBlob.getBinaryStream();
        } catch (Exception e) {
          logger.error("Unable to copy input stream to output when accessing entity " + pb.getEntityClass() + " on paramater "
              + pb.getBindingPath() + " and column " + pb.getColumnName());

          is = null;
        }
      } else if (isString) {
        String v = (String) value;
        blob.setBytes(v.getBytes());
      } else if (isInputStream) {
        is = (InputStream) value;
      }

      // if the input stream is set
      if (is != null) {
        try {
          IOUtils.copy(is, os);
        } catch (Exception e) {
          logger.error("Unable to copy input stream to output when accessing entity " + pb.getEntityClass() + " on paramater "
              + pb.getBindingPath() + " and column " + pb.getColumnName());
        }
      }

      if (os != null) {
        try {
          os.close();
          blob.close();
        } catch (Exception e) {
          logger.error("Unable to close stream properly when accessing entity " + pb.getEntityClass() + " on paramater "
              + pb.getBindingPath() + " and column " + pb.getColumnName());
        }
      }

      return blob;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.ConversionUtilityImpl#convertToSqlType(com
   * .p5solutions.core.jpa.orm.ParameterBinder, java.lang.Object)
   */
  @Override
  public Object convertToSqlType(ParameterBinder pb, Object value) {

    Object ret = ifOracleBLOB(pb, value);

    if (ret != null) {
      return ret;
    }

    // if blob destination, do appropriate conversion and return value
    ret = toSqlBlob(pb, value);
    if (ret != null) {
      return ret;
    }

    // TODO should handle blob or any other conversion here.

    return super.convertToSqlType(pb, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.ConversionUtilityImpl#convertSimpleValue(com
   * .p5solutions.core.jpa.orm.ParameterBinder, java.lang.Object,
   * java.lang.String, java.lang.Class, java.lang.Class)
   */
  @Override
  protected Object convertSimpleValue(ParameterBinder pb, Object value, String bindingPath, Class<?> sourceType, Class<?> targetType)
      throws TypeConversionException {

    // TODO need to convert blob to byte[] or outputstream?

    Object ret = toJavaBlob(pb, value, targetType);
    if (ret != null) {
      return ret;
    }

    if (isOracleTimeStamp(sourceType)) {

      // TODO needs adjustment refactoring?
      TIMESTAMP tz = (TIMESTAMP) value;
      try {
        return tz.timestampValue();
      } catch (SQLException e) {
        throw new RuntimeException("Unable to convert oracle timestamp on parameter binder " + pb.getBindingPath() + " for entity type "
            + pb.getEntityClass());
      }
    }

    // otherwise
    return super.convertSimpleValue(pb, value, bindingPath, sourceType, targetType);
  }

  /**
   * To blob.
   * 
   * @param pb
   *          the pb
   * @param value
   *          the value
   * @param targetType
   *          the target type
   * @return the object
   */
  private Object toJavaBlob(ParameterBinder pb, Object value, Class<?> targetType) {

    if (value instanceof Blob) {
      boolean isBlob = ReflectionUtility.isBlob(targetType);
      boolean isInputStream = ReflectionUtility.isInputStream(targetType);
      boolean isByteArray = ReflectionUtility.isByteArray(targetType);
      boolean isString = ReflectionUtility.isStringClass(targetType);

      Blob blob = (Blob) value;

      // if the target entity->property is of blob, return quickly.
      if (isBlob) {
        return blob;
      }

      try {
        // if the target is a string, then convert the buffer
        if (isString) {
          // if the target entity->property is a string
          int length = (int) blob.length();
          byte[] ba = blob.getBytes(0L, length);
          return new String(ba, charset);
        } else if (isByteArray) {
          // if the target entity->property is a byte array
          int length = (int) blob.length();
          byte[] ba = blob.getBytes(0L, length);
          return ba;
        } else if (isInputStream) {
          return blob.getBinaryStream();
        } else if (BlobStream.class.isAssignableFrom(targetType)) {
          BlobStream bs = new BlobStream(blob.getBinaryStream());
          return bs;
        }

      } catch (Exception e) {
        logger.error("Unable to copy data from blob stream into target byte array on entity " + pb.getEntityClass() + " paramater "
            + pb.getBindingPath() + " and column " + pb.getColumnName());
      }
    }

    return null;
  }

  /**
   * Sets the data source.
   * 
   * @param dataSource
   *          the new data source
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
