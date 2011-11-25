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
package com.p5solutions.core.jpa.orm.rowbinder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import com.p5solutions.core.jpa.orm.DependencyJoin;
import com.p5solutions.core.jpa.orm.DependencyJoinFilter;
import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.EntityParser;
import com.p5solutions.core.jpa.orm.EntityParserImpl;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.jpa.orm.MapUtility;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Criteria;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Criterion;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Restrictions;
import com.p5solutions.core.jpa.orm.exceptions.NoColumnFoundInResultSetException;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class EntityRowBinder.
 * 
 * @param <T>
 *          the generic type
 */
public class EntityRowBinder<T> implements RowMapper<T> {

  /** The entity class. */
  private Class<T> entityClass;

  /** The entity utility. */
  private EntityUtility entityUtility;

  /** The entity detail. */
  private EntityDetail<T> entityDetail;

  /** The entity parser. */
  private EntityParser entityParser;

  /** The joinfilter. */
  private DependencyJoinFilter joinFilter;

  /** The map utility. */
  private MapUtility mapUtility;

  /** The logger. */
  private static Log logger = LogFactory.getLog(EntityRowBinder.class);

  /**
   * The current query identifier used when issuing the query when issued by the
   * {@link EntityParser}.
   */
  private String currentQueryIdentifier;

  /**
   * Instantiates a new entity row binder.
   * 
   * @param entityClass
   *          the entity class
   * @param conversionUtility
   *          the conversion utility
   * @param entityUtility
   *          the entity utility
   * @param currentQueryIdentifier
   *          the current query identifier
   * @param joinFilter
   *          the join filter
   * @param entityParser
   *          the entity parser
   */
  public EntityRowBinder(Class<T> entityClass, EntityUtility entityUtility, MapUtility mapUtility, String currentQueryIdentifier,
      DependencyJoinFilter joinFilter, EntityParser entityParser) {

    //
    this.entityClass = entityClass;
    this.mapUtility = mapUtility;
    this.entityUtility = entityUtility;
    this.entityParser = entityParser;
    this.joinFilter = joinFilter;
    this.currentQueryIdentifier = currentQueryIdentifier;

    // there should be a valid detail
    this.entityDetail = entityUtility.getEntityDetail(entityClass);

    if (this.entityDetail == null) {
      String error = EntityUtility.class + " returned a null " + EntityDetail.class + ", please make sure the entity is valid, or check the "
          + EntityUtility.class + " cache.";
      logger.error(error);
      throw new NullPointerException(error);
    }
  }

  /**
   * Gets the parameter binder by the result data meta data column name.
   * 
   * Throws RuntimeException if parameter binder was not found
   * 
   * @param columnName
   *          the column name
   * @return the parameter binder
   */
  @Deprecated // use entityDetail.getParameterBinder(columnName) directly?
  protected ParameterBinder getParameterBinder_deprecated(String columnName) {
    // get the parameter binder by the column name
    ParameterBinder pb = entityDetail.getParameterBinderByColumn(columnName);

    // if no parameter binder was found, then the entity does not
    // have a column mapping for the selected column
    if (pb == null) {
      String error = "No column mapping found on entity type " + entityClass + " for column name " + columnName;
      logger.error(error);
      throw new RuntimeException(error);
    }

    return pb;
  }

  protected int findColumnIndex(ResultSetMetaData metaData, String columnName) throws SQLException {
    // TODO fix this or cache it somehow, so it doesn't iterate each query
    for (int i = 1; i <= metaData.getColumnCount(); i++) {
      String name = metaData.getColumnName(i);

      if (name.toUpperCase().equals(columnName)) {
        return i;
      }
    }
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
   * int)
   */
  @Override
  public T mapRow(ResultSet rs, int rowNum) throws SQLException {
    EntityDetail<T> entityDetail = getEntityUtility().getEntityDetail(entityClass);
    T entity = entityDetail.newInstance();
    ResultSetMetaData metaData = rs.getMetaData();

    // add the entity to the join filter list.
    getJoinFilter().add(this.currentQueryIdentifier, entity);

    boolean isDebug = logger.isDebugEnabled();

    if (isDebug) {
      // attempt to parse the resultset into the entity.********
      logger.debug("Mapping row for entity type: " + entityClass);
    }

    for (ParameterBinder pb : entityDetail.getParameterBinders()) {

      // skip over transient methods
      if (pb.isTransient()) {
        continue;
      }

      String columnName = pb.getColumnNameAnyJoinOrColumn();

      if (columnName == null) {
        columnName = pb.getBindingNameUpper();
      }

      // find the column index based on column name, case insensitive
      // TODO deprecated as per change to adding meta-data code generation to EntityUtility# 
      int columnIndex = findColumnIndex(metaData, columnName);
       
      //int columnIndex = pb.getColumnMetaData().getColumnIndex();
      
      if (columnIndex == -1) {
        NoColumnFoundInResultSetException e = new NoColumnFoundInResultSetException(entityDetail.getEntityClass(), columnName);
        logger.debug(e.toString());
        throw e;
      }

      // get the value
      Object resultValue = rs.getObject(columnIndex);

      if (doColumn(pb, entity, resultValue)) {

        if (isDebug) {
          logger.debug(" -> Column [" + pb.getColumnMetaData().getColumnName() + "] with value [" + (resultValue == null ? " NULL " : resultValue)
              + "] to path " + pb.getBindingPath());
        }
      } else if (doJoinColumn(pb, entity, resultValue)) {
        // / TODO ??
      }
    }

    return entity;
  }

  /**
   * Process a basic column parameter
   * 
   * @param pb
   *          the pb
   * @param entity
   *          the entity
   * @param resultValue
   *          the result value
   * @return true, if successful
   */
  protected boolean doColumn(ParameterBinder pb, Object entity, Object resultValue) {
    // If the parameter binder suggests a basic column, then map the column
    // directly by invoking the setter method of the parameter bind;
    // However,
    // if the parameter binder is not a Column 'e.g. JoinColumn instead' but
    // the target type is still a basic class type, then map the value.
    if (pb.isColumn()) {
      // return value will be 'entity'
      Object ret = mapUtility.map(pb, entity, resultValue, pb.getBindingPath());
      return true;
    }
    return false;
  }

  /**
   * Process a Join Column parameter.
   * 
   * @param pb
   *          the pb
   * @param entity
   *          the entity
   * @param resultValue
   *          the result value
   * @return true, if successful
   */
  protected boolean doJoinColumn(ParameterBinder pb, Object entity, Object resultValue) {
    if (pb.isJoinColumn()) {
      if (pb.isManyToOne()) {
        doManyToOne(pb, entity, resultValue);
      } else if (pb.isOneToMany()) {
        doOneToMany(pb, entity, resultValue);
      } else if (pb.isOneToOne()) {

      } else if (pb.isManyToMany()) {

      }
      return true;
    }
    return false;
  }

  /**
   * Process a many-to-one join-column value into the entity using the parameter
   * binder.
   * 
   * @param pb
   *          the pb
   * @param entity
   *          the entity
   * @param resultValue
   *          the result value
   */
  protected void doManyToOne(ParameterBinder pb, Object entity, Object resultValue) {

    // get the joining parameter
    DependencyJoin dj = pb.getDependencyJoin();
    Class<?> joinTargetClass = pb.getTargetValueType();

    if (ReflectionUtility.isBasicClass(joinTargetClass)) {

    } else {
      // TODO probably best for the end, that way we can do multiple join
      // column bindings
      // TODO check for ManyToOne or OneToMany or OneToOne or ManyToMany
      // bindings
      // do a recursive lookup
      if (resultValue != null) {
        ParameterBinder pbpk = dj.getDependencyParameterBinder();

        // TODO should probably get the list based on above parameter
        Object joinValue = entityParser.find(joinTargetClass, resultValue, this.getJoinFilter());

        // TODO check for casting issues !!!

        mapUtility.map(pb, entity, joinValue, pb.getBindingPath());
        // ReflectionUtility.setValue(joinValue, target, joinValue);
      }
    }
  }

  protected void doOneToMany(ParameterBinder pb, Object entity, Object resultValue) {

    // ALL THIS NEEDS TO GO INTO A TEMPORARY STAGING AREA, SUCH THAT
    // COMPOSITE
    // JOIN COLUMNS CAN BE ACCESSED THE BELOW CODE SHOULD BE DONE AFTER ALL
    // COLUMNS HAVE BEEN PROCESSED OR NOT?? CAN WE GET THE RESULT OF ANOTHER
    // COLUMN
    // WHILE PROCESSING THE CURRENT ONE? I DO NOT SEE WHY NOT? ITS JUST
    // RESULTSET[xyz]??

    DependencyJoin dj = pb.getDependencyJoin();
    JoinColumn joinColumn = pb.getJoinColumn();
    Class<?> joinTargetClass = pb.getTargetValueType();

    // if the list is a collection class, then its probably
    // an inverse join, meaning, the foreign key value is
    // not within this table, but rather the children table
    if (ReflectionUtility.isCollectionClass(joinTargetClass)) {

      // Criteria<?> query = new Criteria(targetType, entityUtility);
      EntityDetail<?> djEntityDetail = getEntityUtility().getEntityDetail(dj.getDependencyClass());
      Criteria<?> criteria = new Criteria(dj.getDependencyClass());

      Class<?> djClass = dj.getDependencyClass();
      ParameterBinder djPB = dj.getDependencyParameterBinder();
      Criterion restriction = Restrictions.eq(djClass, djPB.getBindingName(), 1L);
      criteria.add(restriction);
      Query query = criteria.getQuery(getEntityUtility());

      List<?> results = getEntityParser().findResultsByQuery(dj.getDependencyClass(), query, this.getJoinFilter());

      // map the result to the binding path of the entity
      mapUtility.map(pb, entity, results, pb.getBindingPath());

      // FIGURE IT OUT !!! GRRR lol

      // / TODO fix problem with recursion, all instances of
      // dependencyClass should be the same instance of entityClass

      // this.entityParser.findResultsByQuery(dj.getDependencyClass(),
      // query);

      // / TODO probably needs to find the column value to select against
      // based on the joinColumn(name="XYZ")
      // for example, if we are looking for a collection of children, we
      // need to select * from children where
      // parent = `parent_id`.. but since this parameter binder is a
      // collection and the value

      // System.out.println(entityDetail);
    }
  }

  /**
   * Gets the basic result object.
   * 
   * @param rs
   *          the rs
   * @return the basic result object
   * @throws SQLException
   *           the sQL exception
   */
  private Object getBasicResultObject(ResultSet rs) throws SQLException {
    final boolean isPlainClass = Object.class.equals(entityClass);
    final boolean isBasicClass = ReflectionUtility.isBasicClass(entityClass);

    ResultSetMetaData metaData = rs.getMetaData();

    // if the entity is a of sub-type object.class or is some sort of
    // primitive class such as BigDecimal, Integer, Double, Short, etc.
    if (isPlainClass || isBasicClass) {

      // / obviously if we have more than one column, we cannot
      // possibly map it
      // / to a plain old java object of type Object.class, since
      // there are no
      // / members to map the columns to!
      if (metaData.getColumnCount() > 1) {
        String error = "Cannot return multi-column resultset into " + "a plain object of type Object.class. If you need to map a multi-column "
            + "resultset, please use an object marked with @" + Entity.class + " annotation.";
        logger.error(error);

        throw new RuntimeException(error);
      }

      // // THIS SHOULD NEVER HAPPEN, QUERY EXCEPTION SHOULD
      // // BE THROWN IF THERE IS A SYNTAX ERROR IN THE QUERY.
      // if (metaData.getColumnCount() == 0) { }

      // Otherwise if there is only 1 column, and its within the scope
      // of plain object.class
      return (T) rs.getObject(1);
    }

    return null;
  }

  /**
   * Gets the entity parser.
   * 
   * @return the entity parser
   */
  public EntityParser getEntityParser() {
    return entityParser;
  }

  /**
   * Sets the entity parser.
   * 
   * @param entityParser
   *          the new entity parser
   */
  public void setEntityParser(EntityParser entityParser) {
    this.entityParser = entityParser;
  }

  /**
   * Sets the join filter.
   * 
   * @param joinfilter
   *          the new join filter
   */
  public void setJoinFilter(DependencyJoinFilter joinfilter) {
    this.joinFilter = joinfilter;
  }

  /**
   * Gets the join filter.
   * 
   * @return the join filter
   */
  public DependencyJoinFilter getJoinFilter() {
    return joinFilter;
  }

  /**
   * Sets the entity utility.
   * 
   * @param entityUtility
   *          the new entity utility
   */
  public void setEntityUtility(EntityUtility entityUtility) {
    this.entityUtility = entityUtility;
  }

  /**
   * Gets the entity utility.
   * 
   * @return the entity utility
   */
  public EntityUtility getEntityUtility() {
    return entityUtility;
  }
}