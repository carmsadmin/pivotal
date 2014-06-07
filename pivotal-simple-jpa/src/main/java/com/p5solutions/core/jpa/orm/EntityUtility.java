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
package com.p5solutions.core.jpa.orm;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.p5solutions.core.jpa.orm.DMLOperation.OperationType;
import com.p5solutions.core.jpa.orm.annotations.EntityAnnotationScanner;
import com.p5solutions.core.jpa.orm.entity.aop.EntityProxy;
import com.p5solutions.core.jpa.orm.exceptions.AnnotationNotDefinedException;
import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class EntityUtility: This utility will evaluate a list of entities,
 * including table-entities, and generate a list of {@link ParameterBinder}'s
 * which will help in addressing the basic operations of persistence and
 * selection of data from and to the database.
 * 
 * @author Kasra Rasaee
 * @since 2010-10-29
 * 
 * @see EntityParser
 * @see EntityPersister
 * @see EntityPersistUtility
 * 
 * @see TransactionTemplate
 * 
 * @see EntityDetail for details on a specific entity type, based on class type
 */
public class EntityUtility {

  /** The logger. */
  private static Log logger = LogFactory.getLog(EntityUtility.class);

  /** The cache entity details. */
  private Hashtable<Class<?>, EntityDetail<?>> cacheEntityDetails;

  /** The cached global named native queries. */
  private Map<String, NamedNativeQuery> cachedGlobalNamedNativeQuery = new HashMap<String, NamedNativeQuery>();

  /** The entity persist utility. */
  private EntityPersistUtility entityPersistUtility;

  /** List of entity packages to scan for entities. */
  private List<String> entityPackages;

  /**
   * DataSource to retrieve meta data for given table entities, for example
   * column data type, size, ?null, so forth
   **/
  private DataSource dataSource;

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getTargetEntityClass(T entity) {
    Class<T> entityClass = (Class<T>) entity.getClass();
    if (entity instanceof EntityProxy) {
      entityClass = (Class<T>) ((EntityProxy) entity).getTarget().getClass();
    }
    return entityClass;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getTargetEntity(T entity) {
    if (entity instanceof EntityProxy) {
      EntityProxy entityProxy = (EntityProxy) entity;
      entity = (T) entityProxy.getTarget();
    }
    return entity;
  }

  /**
   * Initialize. Initialize all entities using Entity class scanner.
   */
  public void initialize() {
    if (entityPackages != null) {
      EntityAnnotationScanner scanner = new EntityAnnotationScanner();
      for (String entityPackage : entityPackages) {
        String typeName = null;
        try {

          for (Class<? extends AbstractEntity> type : scanner.getComponentClasses(entityPackage)) {
            typeName = type.getSimpleName();
            EntityDetail<?> detail = getEntityDetail(type);

            // only generate dml operations for table annotations.
            Table table = ReflectionUtility.findAnnotation(type, Table.class);
            if (table != null) {
              // build the basic dml operations for persistence.
              getEntityPersistUtility().build(type);
            }
          }

          // build the meta data for all the entity tables.
          buildColumnMetaDataAll();

        } catch (ClassNotFoundException e) {
          String msg = "Cannot initialize entity binders for given class name " + typeName + " because it does not exist within the provided classpath. "
              + "Please make sure the class exists, and there are no errors in the path and name.";
          logger.fatal(msg);
          throw new RuntimeException(msg);
        } catch (Exception e) {
          logger.fatal(e);
          throw new RuntimeException(e);
        }
      }
    }
  }

  /**
   * Set the datasource for this entity utility
   * 
   * @param dataSource
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Get the datasource for this entity utility.
   * 
   * @return
   */
  protected DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * Throw entity class null.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   */
  protected <T> void throwEntityClassNull(Class<T> entityClass) {
    if (entityClass == null) {
      throw new NullPointerException("Entity class cannot be null");
    }
  }

  /**
   * Throw recursion filter list null.
   * 
   * @param recursionFilterList
   *          the recursion filter list
   */
  protected void throwRecursionFilterListNull(List<Class<?>> recursionFilterList) {
    // / cannot have a null recursion filter list, this can cause a
    // recursive loop, which will blow the stack!
    if (recursionFilterList == null) {
      throw new NullPointerException("Recursion Filter List cannot be null, it is needed to "
          + "prevent recursive loops when building the object graph - e.g. via @" + JoinColumn.class + " annotations defined via an inverse annotation!");
    }
  }

  /**
   * Gets the binding character.
   * 
   * @return the binding character
   */
  protected String getBindingCharacter() {
    // TODO should be unique per database source?? .NET is unique per database
    // type
    return ":";
  }

  /**
   * Gets the sQL parameter separater character.
   * 
   * @return the sQL parameter separater character
   */
  protected String getSQLParameterSeparaterCharacter() {
    return ",";
  }

  /**
   * Format binding name.
   * 
   * @param name
   *          the name
   * @return the string
   */
  protected String formatBindingName(String name) {
    return name;
    // return getBindingCharacter().concat(name);
  }

  /**
   * Returns a table name for a specified entity class (if found). If not found
   * it will return null.
   * 
   * @param entityClass
   * @return
   */
  public String getTableName(Class<? extends AbstractEntity> entityClass) {
    EntityDetail<?> entityDetail = cacheEntityDetails.get(entityClass);
    return entityDetail == null ? null : entityDetail.getTableName();
  }

  /**
   * Gets the entity detail.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @return the entity detail
   */
  public <T> EntityDetail<T> getEntityDetail(Class<T> entityClass) {
    List<Class<?>> recursionFilterList = new ArrayList<Class<?>>();
    return getEntityDetail(entityClass, recursionFilterList);
  }

  /**
   * Gets the entity detail.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param recursionFilterList
   *          the recursion filter list
   * @return the entity detail
   */
  public <T> EntityDetail<T> getEntityDetail(Class<T> entityClass, List<Class<?>> recursionFilterList) {
    if (EntityProxy.class.isAssignableFrom(entityClass)) {
      logger.debug("retreiving entity details for class " + entityClass);
    }

    if (cacheEntityDetails == null) {
      cacheEntityDetails = new Hashtable<Class<?>, EntityDetail<?>>();
    }

    EntityDetail<T> entityDetail = null;
    if (!cacheEntityDetails.containsKey(entityClass)) {
      entityDetail = buildEntityDetail(entityClass, recursionFilterList);

      // this could return null for several reasons
      // one, it was unable to parse the entity details for any number of
      // exceptions, probably thrown and not hit this
      // or the filter list, filtered it out, since there was some sort of join
      // which inevitable returned to the
      // same entity type, for example either an inverse join, or a join which
      // joined against the same entity,
      // such as a recursive entity.
      if (entityDetail != null) {
        cacheEntityDetails.put(entityClass, entityDetail);
      }
    } else {
      // suppressed warning, since this will return
      // null, unless the entity class is of type T
      entityDetail = (EntityDetail<T>) cacheEntityDetails.get(entityClass);
      if (logger.isDebugEnabled()) {
        logger.debug("** Found join-columns which inevitably resulted in a recursion. Returning entity class " + entityClass
            + " which is already being built as part of the dependency graph. This is just " + "informational, and NOT an error!");
      }
    }

    return entityDetail;
  }

  /**
   * Builds an entity detail object which defines an entities parameters, and
   * related structure.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param recursionFilterList
   *          the recursion filter list
   * @return the entity detail
   */
  protected <T> EntityDetail<T> buildEntityDetail(Class<T> entityClass, List<Class<?>> recursionFilterList) {
    // check for nulls
    throwEntityClassNull(entityClass);
    throwRecursionFilterListNull(recursionFilterList);

    // return null if this is part of the recursion list, meaning already
    // hit this
    // entity before.
    
    // if the join is not null, and the class of the parameter matches the class that we are trying to build the details for, then
    // we have a recursion entity which references itself, this means, that we cannot simply ignore the join-column, but rather
    // need to embrace it and build its dependency join column.

    if (recursionFilterList.contains(entityClass)) {
      return null;
    }
  
    // add this entity to the recursion filter list, so we can prevent
    // recursive loops!
    // *** NOTE
    // This will probably happen in a (entity x) OneToOne <> (entity y)
    // OneToOne relationship??
    // ***
    // This will not happen on a ManyToOne <> OneToMany relationship
    // since Collections are not iterated (because its type is not known)
    // as such, the inverse join column is used to determine the dependency
    // of the entities (aka. tables)
    recursionFilterList.add(entityClass);
  
    EntityDetail<T> entityDetail = new EntityDetail<T>(entityClass);
    List<ParameterBinder> pbs = new ArrayList<ParameterBinder>();

    // build a list of all parameters, including joins, embedded, columns,
    // ids, transients, etc.
    // this method will build the parameters from the root, and not as part
    // of another parent class
    build(entityClass, pbs, recursionFilterList);

    // setup attribute overrides, if any on the entity class
    setupAttributeOverrides(entityClass, pbs);

    // set all the parameters
    entityDetail.setParameters(pbs);

    // get all named native queries and append them to the global named native query list.
    buildGlobalNamedNativeQueries(entityDetail);

    // return the entity detail
    return entityDetail;
  }

  /**
   * Builds the global named native queries cache, in sequential order of
   * scanned entity.
   * 
   * If duplicate keys are found, the last entities query name will take
   * precedence.
   * 
   * @param <T>
   *          the generic type
   * @param entityDetail
   *          the entity detail
   */
  protected <T> void buildGlobalNamedNativeQueries(EntityDetail<T> entityDetail) {
    Map<String, NamedNativeQuery> nativeQueries = entityDetail.getNamedNativeQueries();
    if (nativeQueries != null) {
      logger.info("Populating named native queries in global cache for " + entityDetail.getEntityClass());
      for (String key : nativeQueries.keySet()) {
        NamedNativeQuery newNativeQuery = nativeQueries.get(key);
        logger.debug(" -- query name: " + key);
        logger.debug(" --> query sql:  [" + newNativeQuery + "]");

        if (this.cachedGlobalNamedNativeQuery.containsKey(key)) {
          String warning = " -->> Warning, [" + key + "] already exists in global cache, check entity " + entityDetail.getEntityClass();
          logger.warn(warning);
        }

        this.cachedGlobalNamedNativeQuery.put(key, newNativeQuery);
      }
    }
  }

  /**
   * Find named native query in the global cache, and not specific to a given
   * entity.
   * 
   * @param name
   *          the name
   * @return the named native query
   */
  public NamedNativeQuery findGlobalNamedNativeQuery(String name) {
    if (this.cachedGlobalNamedNativeQuery == null) {
      return null;
    }

    return this.cachedGlobalNamedNativeQuery.get(name);
  }

  /**
   * Find any attribute override or overrides, and return a single list of all
   * of them.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @return the list
   */
  protected <T> List<AttributeOverride> findAttributeOverrides(Class<T> entityClass) {
    List<AttributeOverride> overrides = new ArrayList<AttributeOverride>();

    // try to add a single attribute override
    AttributeOverride ao = ReflectionUtility.findAnnotation(entityClass, AttributeOverride.class);
    if (ao != null) {
      overrides.add(ao);
    }

    // try to add all the attribute overrides
    AttributeOverrides aos = ReflectionUtility.findAnnotation(entityClass, AttributeOverrides.class);
    // if both attribute overrride and overrides is defined, throw exception
    if (ao != null && aos != null) {
      String error = "Cannot define entity with BOTH " + AttributeOverride.class + " & @" + AttributeOverrides.class;
      logger.error(error);
      throw new RuntimeException(error);
    }

    // if overrides is not empty or null, then add them all in
    if (aos != null) {
      for (AttributeOverride ao2 : aos.value()) {
        overrides.add(ao2);
      }
    }

    return Comparison.isEmptyOrNull(overrides) ? null : overrides;
  }

  /**
   * Setup attribute overrides.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param pbs
   *          the pbs
   */
  protected <T> void setupAttributeOverrides(Class<T> entityClass, List<ParameterBinder> pbs) {
    List<AttributeOverride> overrides = findAttributeOverrides(entityClass);

    // not the most efficient way of doing this, but its not overwhelming,
    // and its only done once!
    if (!Comparison.isEmptyOrNull(overrides)) {
      for (AttributeOverride override : overrides) {
        boolean found = false;
        for (ParameterBinder pb : pbs) {
          if (override.name().equals(pb.getFieldName())) {
            found = true;
            pb.setOverrideColumn(override.column());
          }
        }

        if (!found) {
          String error = "No override method found when using attribute-override in entity class " + entityClass
              + " when an attempt was made to match against field name " + override.name();
          logger.error(error);
          throw new RuntimeException(new NoSuchFieldException(error));
        }
      }
    }
  }

  /**
   * Builds the Parameter Binder list starting from a class (ideally the root
   * class).
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param pbs
   *          the pbs
   * @param recursionFilterList
   *          the recursion filter list
   */
  public <T> void build(Class<T> clazz, List<ParameterBinder> pbs, List<Class<?>> recursionFilterList) {

    // build root node
    build(clazz, "", null, null, null, pbs, recursionFilterList);
  }

  /**
   * Build the database-meta-data for all table entities.
   */
  protected void buildColumnMetaDataAll() {
    Connection connection = null;

    try {
      connection = DataSourceUtils.getConnection(dataSource);
      for (EntityDetail<?> detail : this.cacheEntityDetails.values()) {
        Table table = detail.getTableAnnotation();
        if (table != null) {
          buildColumnMetaData(table, detail, connection);
        }
      }
    } catch (Exception e) {
      logger.error(e.toString());
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          ;
        }
        connection = null;
      }
    }
  }

  /**
   * Build the database-meta-dta for a given table entity, using an existing
   * connection.
   * 
   * @param table
   *          annotation
   * @param detail
   *          {@link EntityDetail} probably provided by the
   *          {@link #cacheEntityDetails}
   * @param connection
   *          an existing mock or real, database connection.
   */
  protected void buildColumnMetaData(Table table, EntityDetail<?> detail, Connection connection) {

    Statement stmt = null;
    ResultSet rs = null;

    try {
      String sql = "SELECT * FROM " + table.name() + " WHERE 1=0";

      stmt = connection.createStatement();

      // set the maximum result set to zero, just in-case!?
      stmt.setMaxRows(0);

      rs = stmt.executeQuery(sql);
      ResultSetMetaData rsMeta = rs.getMetaData();

      logger.info("** Building Database MetaData for Table " + table.name());

      for (int ic = 1; ic <= rsMeta.getColumnCount(); ic++) {
        String columnName = rsMeta.getColumnName(ic);
        ParameterBinder binder = detail.getParameterBinderByAny(columnName);
        if (binder == null) {
          if (logger.isErrorEnabled()) {
            String error = " -- Column " + columnName + " as defined by the table meta-data, cannot be found within the scope of " + detail.getEntityClass();
            logger.error(error);
          }

          // TODO ?? throw new RuntimeException(new
          // NoColumnDefinedException(error));
        } else {
          ParameterBinderColumnMetaData columnMetaData = new ParameterBinderColumnMetaData();
          // columnMetaData.setColumnIndex(ic); // USELESS, EVERY UNIQUE QUERY
          // STRING WOULD RESULT IN A DIFFERENT INDEX. EASIER TO CACHE IT BASED
          // ON UNIQUE QUERY STRINGS.
          // columnMetaData.setColumnLabel(rsMeta.getColumnLabel(ic));
          columnMetaData.setColumnName(columnName);
          columnMetaData.setLength(rsMeta.getColumnDisplaySize(ic));
          columnMetaData.setPrecision(rsMeta.getPrecision(ic));
          columnMetaData.setScale(rsMeta.getScale(ic));
          columnMetaData.setColumnType(rsMeta.getColumnType(ic));
          columnMetaData.setColumnTypeName(rsMeta.getColumnTypeName(ic));
          binder.setColumnMetaData(columnMetaData);

          if (logger.isDebugEnabled()) {
            logger.debug(" -- [" + columnMetaData.toString() + "]");
          }
        }
      }

    } catch (SQLException e) {
      logger.error(">> *UNABLE* to retrieve meta data for table " + table.name() + ", doesn't exist?");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          ;
        }
        rs = null;
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          ;
        }
        stmt = null;
      }

    }
  }

  /**
   * Builds the the Parameter Binder list starting from a class, not necessarily
   * the root class.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the clazz
   * @param bindingPath
   *          the binding path
   * @param parentClass
   *          the parent class, <code>null</code> if root class
   * @param parentGetterMethod
   *          the parent getter method, <code>null</code> if root class
   * @param parentSetterMethod
   *          the parent setter method, <code>null</code> if root class
   * @param pbs
   *          the pbs
   * @param recursionFilterList
   *          the recursion filter list
   */
  public <T> void build(Class<T> entityClass, String bindingPath, Class<?> parentClass, Method parentGetterMethod, Method parentSetterMethod,
      List<ParameterBinder> pbs, List<Class<?>> recursionFilterList) {

    List<Method> methods = ReflectionUtility.findGetMethodsWithNoParams(entityClass);

    // if the methods are blank or null then throw an exception, cannot be
    // an empty entity type
    if (Comparison.isEmptyOrNull(methods)) {
      String error = "No getters found for entity/embedded class type " + entityClass + " which represent a column property; make sure "
          + "the getters do no have any accepting parameters; such as getSomeField(...) { }";
      logger.error(error);
      throw new NullPointerException(error);
    }

    int index = 1; // the parameter binding index in a Sql Parameter

    // //
    for (Method getterMethod : methods) {
      // @Transient methods are not to be used for binding
      if (ReflectionUtility.isTransient(getterMethod)) {
        continue;
      }
      // look up the setter method (must exist for non-transient getters)
      Method setterMethod = ReflectionUtility.findSetterMethod(entityClass, getterMethod);
      if (setterMethod == null) {
        String error = "No related 'set' method found for 'get' method " + getterMethod.getName() + " when processing entity type " + entityClass;
        logger.fatal(error);
        throw new RuntimeException(new NoSuchMethodException(error));
      }

      // attempt to build the embeddable, if any, if not returns
      // false, and continues with default parameter builder
      if (!buildEmbeddable(entityClass, bindingPath, getterMethod, setterMethod, pbs, recursionFilterList)) {

        // default non embedded params
        ParameterBinder pb = buildParameterBinder(entityClass, bindingPath, getterMethod, setterMethod, parentGetterMethod, parentSetterMethod, index++,
            recursionFilterList);

        // MOVED TO buildParamaterBinder
        // // set the binding path if any.
        // String fieldName = ReflectionUtility.buildFieldName(getterMethod);
        // if (parentGetterMethod != null) {
        // pb.setBindingPath(bindingPath + "." + fieldName);
        // } else {
        // pb.setBindingPath(fieldName);
        // }

        // this may not be null, such as embedded objects, the parent
        // methods should be available.
        pb.setEntityClass(entityClass); // probably redundant, since its done
        // in buildParamterBinder(...)
        pb.setParentClass(parentClass);
        pb.setParentGetterMethod(parentGetterMethod);
        pb.setParentSetterMethod(parentSetterMethod);

        // add the parameter to the list of all parameters
        pbs.add(pb);
      }
    }
  }

  /**
   * Builds the embeddable parameter list.
   * 
   * @param <T>
   *          the generic type
   * @param parentClazz
   *          the parent clazz
   * @param bindingPath
   *          the binding path
   * @param getterMethod
   *          the getter method
   * @param setterMethod
   *          the setter method
   * @param pbs
   *          the pbs
   * @param recursionFilterList
   *          the recursion filter list
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  public <T> boolean buildEmbeddable(Class<T> parentClazz, String bindingPath, Method getterMethod, Method setterMethod, List<ParameterBinder> pbs,
      List<Class<?>> recursionFilterList) {

    Embedded embedded = ReflectionUtility.findAnnotation(getterMethod, Embedded.class);

    // TODO Should we process the class if the getter is not marked with
    // @Embedded? but the class itself is marked with @Embeddedable??
    // Hibernate does it, but hibernate tries to be too COOL!
    if (embedded == null) {

      // check for embeddedable on the actual class type!!
      Class<?> returnType = getterMethod.getReturnType();
      if (ReflectionUtility.hasAnyAnnotation(returnType, Embeddable.class)) {
        String error = "For consistency sake, you must define the " + Embedded.class + " annotation on the getter method " + getterMethod.getName()
            + " within entity type " + getterMethod.getDeclaringClass();
        throw new AnnotationNotDefinedException(error);
      }

    } else {
      Class<?> embeddableClazz = getterMethod.getReturnType();

      // [Java GENERICS] - Parameterized annotation class type "..." is
      // unchecked, probably should create Annotation[] { } ?
      if (!ReflectionUtility.hasAnyAnnotation(embeddableClazz, Embeddable.class)) {
        String error = "Class type " + embeddableClazz + " not marked with @" + Embeddable.class + " but used as an embedded parameter within " + parentClazz;
        logger.error(error);
        throw new RuntimeException(error);
      }

      // append the field name
      if (bindingPath.length() > 0) {
        bindingPath += ".";
      }

      String fieldName = ReflectionUtility.buildFieldName(getterMethod);
      bindingPath += fieldName;

      // recursively build the embedded objects as parameters
      build(embeddableClazz, bindingPath, parentClazz, getterMethod, setterMethod, pbs, recursionFilterList);

      return true;
    }
    return false;
  }

  /**
   * Throw basic class type exception on join column. If the JoinColumn data
   * type is set to a basic type, then we cannot use the {@link JoinColumn} or
   * {@link JoinColumns} annotation. This method checks for basic class types on
   * a {@link ParameterBinder}
   * 
   * @param pb
   *          the parameter binder to check against
   */
  protected void throwBasicClassTypeExceptionOnJoinColumn(ParameterBinder pb) {
    // if its a basic type, then throw an exception, shouldn't be joining
    // non
    // entity types, it doesn't make sense to do it.
    if (ReflectionUtility.isBasicClass(pb.getTargetValueType())) {
      String error = "Cannot use a basic class such as Short, Integer, " + "String, Date, etc.. types for a join column pairing. "
          + "Please check the entity class " + pb.getEntityClass() + " and its associated method " + pb.getGetterMethod().getName();

      logger.error(error);

      throw new RuntimeException(error);
    }
  }

  /**
   * Create a {@link DependencyJoin} and search the join entity for the primary
   * key column(s). Example, if we have a single {@link OneToOne} relationship,
   * or for most of the time, even a {@link ManyToOne} relationship with a
   * {@link JoinColumn} that uses the primary key.
   * 
   * @param pb
   *          the {@link ParameterBinder}
   * @param joinEntityDetail
   *          the join entity detail, this is the entity class / details of the
   *          join, for example the Parent Entity of a {@link ManyToOne}
   *          relationship
   */
  protected void doDependencyByPrimaryKey(ParameterBinder pb, EntityDetail<?> joinEntityDetail) {

    String columnName = pb.getJoinColumnName();
    Class<?> entityClass = pb.getEntityClass();
    Class<?> joinTargetClass = pb.getTargetValueType();
    // Then we want to bind by the primary key.

    // set it up to the other join
    DependencyJoin dj = new DependencyJoin();
    dj.setDependencyClass(joinTargetClass);

    // the dependency parameter should be the primary key at this point
    List<ParameterBinder> pbPrimaryKeys = joinEntityDetail.getPrimaryKeyParameterBinders();
    // TODO should support multiple primary keys, however the JoinColumns
    // should be used??
    // REMOVE THIS when support for composite-keys is available
    if (Comparison.isEmptyOrNull(pbPrimaryKeys)) {
      String error = "No primary keys found for entity type " + joinTargetClass;
      logger.error(error);
      throw new NullPointerException(error);
    } else if (pbPrimaryKeys.size() != 1) {
      String error = "Implementation does not currently support composite key join columns, please check entity type " + entityClass + " on column "
          + columnName + " and its associated inverse join.";
      logger.error(error);
      throw new RuntimeException(error);
    }

    ParameterBinder pbpk = pbPrimaryKeys.get(0);

    dj.setDependencyParameterBinder(pbpk);
    dj.setInverseJoin(false);

    // the dependency goes against the current param binder
    pb.setDependencyJoin(dj);
  }

  protected void doOneToManyDependencyJoinColumn(ParameterBinder pb, ParameterBinder pbJoinColumn) {
    Class<?> entityClass = pb.getEntityClass();
    Class<?> joinTargetClass = pb.getTargetValueType();
    
    // for example.
    // ParentEntity->List<Child> (joinColumn="PARENT_ID")
    // ChildEntity->Parent (joinColumn="PARENT_ID")
    // the query for ParentEntity->List<Child> would be
    // - select * from child where parent_id=2
    // while all instances of ChildEntity.Parent = same instance of Parent

    // setup a dependency for the current entity to the join
    // column entity - the inverse class with the join column
    DependencyJoin dj = new DependencyJoin();
    dj.setDependencyClass(joinTargetClass);
    dj.setDependencyParameterBinder(pbJoinColumn);
    dj.setInverseJoin(true);

    // the dependency goes against the current param binder
    pb.setDependencyJoin(dj);

    // setup the inverse join.. TODO is this logic correct? is this really
    // the inverse join or is the above the inverse join??
    DependencyJoin djInverse = new DependencyJoin();
    djInverse.setDependencyClass(entityClass);
    djInverse.setDependencyParameterBinder(pb);
    djInverse.setInverseJoin(true);
    pbJoinColumn.setDependencyJoin(djInverse);

  }

  protected void doSelfDependencyJoinByJoinColumn(ParameterBinder pbJoinColumn) {
    return;
    /*
    Class<?> entityClass = pbJoinColumn.getEntityClass();
    
    if (Comparison.isNotEmpty(pbJoinColumn.getJoinColumn().referencedColumnName())) {
      throw new IllegalArgumentException("Cannot have an entity with a column that has a dependency on the same entity (itself) and use the referenced-column-name attribute. " +
      		"technically you can, if the parameter is referenced column is unique, however, this jpa implementation only supports joining against the primary key. - for now. " +
      		"I wouldn't know why the heck you would want to do that anyway!");
      
      // TODO investigate into building a proper graph, this is a half ass job at building the dependency graph..
    }
    
    EntityDetail<?> details = new EntityDetail<>(entityClass);
  
    // the dependency parameter should be the primary key at this point
    List<ParameterBinder> pbPrimaryKeys = details.getPrimaryKeyParameterBinders();
    
    if (Comparison.isEmptyOrNull(pbPrimaryKeys)) {
      
      // this should probably never happen..
      String error = "No primary keys found for entity type " + entityClass;
      logger.error(error);
      throw new NullPointerException(error);
      
    } else if (pbPrimaryKeys.size() != 1) {
      String error = "Implementation does not currently support composite key join columns, please check entity type " + entityClass + " on column " + pbJoinColumn.getColumnNameAnyJoinOrColumn() + " and its associated inverse join.";
      logger.error(error);
      
      throw new RuntimeException(error);
    }
    
    ParameterBinder pbpk = pbPrimaryKeys.get(0);

    DependencyJoin dj = new DependencyJoin();
    dj.setDependencyClass(entityClass);
    dj.setDependencyParameterBinder(pbpk);
    dj.setInverseJoin(false);

    // the dependency goes against the current param binder
    pbJoinColumn.setDependencyJoin(dj);*/
  }
  
  protected void doDepedencyByJoinColumn(ParameterBinder pb, ParameterBinder pbJoinColumn) {
    // Then we want to bind by the join column name on the inverse entity.
    if (pbJoinColumn.isOneToMany()) {
      doOneToManyDependencyJoinColumn(pb, pbJoinColumn);
    } else if (pbJoinColumn.isOneToOne()) {
      // TODO is this even legal? probably..
    } else if (pbJoinColumn.isManyToOne()) {
      // TODO this is illegal??
    } else if (pbJoinColumn.isManyToMany()) {
      // TODO need an interm-table??
    } else {
      // TODO throw exception, no join found type found
    }
  }

  protected void doManyToOne(ParameterBinder pb, List<Class<?>> recursionFilterList) {

    Class<?> joinTargetClass = pb.getTargetValueType();

    // Get the column name of the join column
    String columnName = pb.getJoinColumnName();
  
    // Get the entity details for the given join column class type, for
    // example Parent.class
    EntityDetail<?> joinEntityDetail = null;
    
    // if the entity class is the same as the join target class, meaning its a recursive join to itself, probably by a parent id column,
    // then we need to get the entities values, otherwise the recursion, NOTE, this may cause an infinit loop, we should probably create
    // a graph of the entities, and check the loaded entities such that it does not continue to load data, incase the relationships are
    // setup such as e.g: a->b->c->b->c and so forth.
    
    // TODO check this logic very carefully. perhaps use a combination of target class and parameter binder as the join filter identifier, rather than just entity class type.
     joinEntityDetail = getEntityDetail(joinTargetClass, recursionFilterList);
    

    
    // If this returns, then we are good to process the join column,
    // otherwise
    // simply ignore it (probably filtered by recursionFilterList)
    if (joinEntityDetail != null) {
      // find the inverse join on the inverse entity
      ParameterBinder pbJoinColumn = joinEntityDetail.getParameterBinderByJoinColumn(columnName);

      boolean isDebug = logger.isDebugEnabled();

      // if no join column was found then we probably want to join against the
      // primary key
      // @ManyToOne makes sense in this case.
      if (pbJoinColumn == null) {
        doDependencyByPrimaryKey(pb, joinEntityDetail);
        if (isDebug) {
          logger.debug("Entity " + pb.getEntityClass() + " on parameter " + pb.getBindingPath() + " is joining against table "
              + joinEntityDetail.getEntityClass() + " using its primary key");
        }
      } else {
        doDepedencyByJoinColumn(pb, pbJoinColumn);
        if (isDebug) {
          logger.debug("Entity " + pb.getEntityClass() + " on parameter " + pb.getBindingPath() + " is joining against table "
              + joinEntityDetail.getTableName() + " on parameter " + pbJoinColumn.getBindingPath());
        }
      }
    } else if (joinTargetClass.equals(pb.getEntityClass())) {
      // continue to build the dependency graph since the target class is actually the same as the entity class,
      // basically what this means is that, the entity refers to itself, and since we cannot continue to build
      // the entity details over-and-over again, we need to refer the parameter to itself.
      doSelfDependencyJoinByJoinColumn(pb);
    }

  }

  //
  // ManyToOne error??
  // } else if (oneToMany != null) {
  // throw new NoJoinColumnMethodsDefinedException("No @"
  // + JoinColumn.class
  // + " defined on inverse entity of type "
  // + joinTargetClass
  // + " when searching for column name "
  // + columnName
  // + " please note it is NOT case sensitive!");
  // } else {
  // }

  /**
   * Check for the {@link OneToMany} annotation, if it exists, then process the
   * ParameterBinder. This method may inevitably call a supporting method, which
   * in turn may call other methods that may inevitably call this method again.
   * 
   * @param pb
   *          the {@link ParameterBinder} in question.
   * @param recursionFilterList
   *          the recursion filter list, a restriction filter, such that
   *          dependency graphs don't go into a recursive loop.
   */
  protected void doOneToMany(ParameterBinder pb, List<Class<?>> recursionFilterList) {

    if (ReflectionUtility.isCollectionClass(pb.getTargetValueType())) {
      Class<?> entityClass = pb.getEntityClass();

      // if its a collection, we cannot determine its type, so we need to
      // search
      // for the inverse join column within another entity, usually done
      // when
      // building / iterating each entity!

      // TODO check for oneToMany??

      // if the collection is marked with many to one,
      // this is incorrect!! TODO double check this behavour!
      if (pb.isManyToOne() || pb.isOneToOne()) {
        String error = "Trying to use a collection of entities bounded against " + pb.getColumnNameAnyJoinOrColumn() + " within entity " + entityClass
            + " with @" + JoinColumn.class + " and when the inversed join needs to be 1-n";
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
  }

  /**
   * Do column. checks whether the parameter has a {@link Column} annotation.
   * 
   * @param pb
   *          the {@link ParameterBinder} in question
   * @param recursionFilterList
   *          the recursion filter list
   * @return true, if successful
   */
  protected boolean doColumn(ParameterBinder pb, List<Class<?>> recursionFilterList) {
    if (pb.isColumn()) {
      return true;
    }
    return false;
  }

  /**
   * Do join column. checks whether the parameter has a {@link JoinColumn}
   * annotation. If so then processes the Dependency Graph for it. For example
   * if its a {@link OneToMany} relationship, it usually skips it and waits for
   * the inverse join {@link ManyToOne}. Note: this method will inevitably be
   * called recursively via supporting methods.
   * 
   * @param pb
   *          the {@link ParameterBinder} of the entity class in question.
   * @param recursionFilterList
   *          the recursion filter list
   * @return true, if successful
   */
  protected boolean doJoinColumn(ParameterBinder pb, List<Class<?>> recursionFilterList) {

    // if the binder is not a regular column, but a join column
    if (pb.isJoinColumn()) {

      throwBasicClassTypeExceptionOnJoinColumn(pb);

      if (pb.isManyToOne()) {
        doManyToOne(pb, recursionFilterList);
      } else if (pb.isOneToMany()) {
        doOneToMany(pb, recursionFilterList);
      } else if (pb.isOneToOne()) {

      } else if (pb.isManyToMany()) {

      }

      return true;
    }

    return false;
  }

  /**
   * Builds a single {@link ParameterBinder} for a given entity class method.
   * For example get methods that have been annotated with {@link Column} or
   * {@link JoinColumn} or {@link JoinColumns}
   * 
   * @param <T>
   *          the generic type of the entity class in question.
   * @param entityClass
   *          the entity class as specified
   * @param getterMethod
   *          the getter method of the parameter
   * @param setterMethod
   *          the supporting setter method of the getter method of the parameter
   * @param index
   *          the index of the parameter being processed
   * @param recursionFilterList
   *          the recursion filter list, a restriction filter, such that
   *          dependency graphs don't go into a recursive loop.
   * @return the {@link ParameterBinder} that was built for the given
   *         getter/setter and entity.
   */
  public <T> ParameterBinder buildParameterBinder(Class<T> entityClass, String bindingPath, Method getterMethod, Method setterMethod,
      Method parentGetterMethod, Method parentSetterMethod, int index, List<Class<?>> recursionFilterList) {

    ParameterBinder binder = new ParameterBinder();

    // set the binding path if any. note this might be part of a recursive call
    // usually a join-column or embedded object
    String fieldName = ReflectionUtility.buildFieldName(getterMethod);
    if (parentGetterMethod != null) {
      binder.setBindingPath(bindingPath + "." + fieldName);
    } else {
      binder.setBindingPath(fieldName);
    }

    // build the binding name
    String bindingName = formatBindingName(ReflectionUtility.buildFieldName(getterMethod));
    binder.setBindingIndex(index);
    binder.setBindingName(bindingName);
    binder.setGetterMethod(getterMethod);
    binder.setSetterMethod(setterMethod);
    binder.setEntityClass(entityClass);

    if (doColumn(binder, recursionFilterList)) {
      // TODO ??
    } else if (doJoinColumn(binder, recursionFilterList)) {
      // TODO ??
      
      //if (entityClass.equals(binder.getEntityClass())) {
        
      //}
    }

    /*
     * for (int j = 0; j < annotations.length; j++) { Annotation annotation =
     * annotations[j]; if (annotation instanceof Column) { Column column =
     * (Column) annotation; } // TODO check for max, min lengths!! else if
     * (annotation instanceof Id) { // Id id = (Id) annotation;
     * binder.setPrimaryKey(true); // } else if (annotation instanceof
     * SequenceGenerator) { // SequenceGenerator sequenceGenerator = //
     * (SequenceGenerator)annotation; } else if (annotation instanceof
     * Transient) { binder.setTransient(true); } }
     */

    // check for setter method if not transient get method
    if (!binder.isTransient()) {
      if (binder.getSetterMethod() == null) {
        String error = "No set method for corresponding get method " + getterMethod.getName() + " found in entity class type " + entityClass;
        logger.error(error);
        throw new RuntimeException(new NoSuchMethodException(error));
      }
    }

    return binder;
  }

  /**
   * Gets the DML operation for a given table entity class defined by the
   * {@link Table} and {@link Entity} annotations.
   * 
   * @param <T>
   *          the generic type of the table-entity
   * @param tableClass
   *          the table-entity class
   * @param operationType
   *          the operation type, such as insert, update, delete, merge, etc.
   * @return the DML operation used at the persistent layer.
   */
  public <T> DMLOperation getDMLOperation(Class<T> tableClass, OperationType operationType) {
    if (getEntityPersistUtility() == null) {
      String error = "The data munipalation persist utility is not defined by any context. " + "Please make sure that when defining a " + EntityUtility.class
          + " you also define an implementation or sub-type of " + EntityPersistUtility.class;
      logger.error(error);
      throw new NullPointerException();
    }

    EntityPersistUtility epu = getEntityPersistUtility();
    return epu.getDMLOperation(tableClass, operationType);
  }

  /**
   * Sets the entity persist utility.
   * 
   * @param entityPersistUtility
   *          the new entity persist utility
   */
  public void setEntityPersistUtility(EntityPersistUtility entityPersistUtility) {
    this.entityPersistUtility = entityPersistUtility;
    if (this.entityPersistUtility != null) {
      this.entityPersistUtility.setEntityUtility(this);
    }
  }

  /**
   * Gets the entity persist utility.
   * 
   * @return the entity persist utility
   */
  public EntityPersistUtility getEntityPersistUtility() {
    return entityPersistUtility;
  }

  /**
   * @return
   */
  public List<String> getEntityPackages() {
    return entityPackages;
  }

  /**
   * @param entityPackages
   */
  public void setEntityPackages(List<String> entityPackages) {
    this.entityPackages = entityPackages;
  }

}