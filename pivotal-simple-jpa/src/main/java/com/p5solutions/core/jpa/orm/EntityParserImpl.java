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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.p5solutions.core.jpa.orm.DependencyJoinFilter.JoinFilterItem;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Criteria;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Restrictions;
import com.p5solutions.core.jpa.orm.rowbinder.BasicTypeRowBinder;
import com.p5solutions.core.jpa.orm.rowbinder.EntityRowBinder;
import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * EntityParserImpl: Implementation of the {@link EntityParser} interface,
 * handles JPA based annotations. This implementation allows retrieval of data
 * to entities "POJO's" defined by the {@link Entity} and or {@link Table}
 * annotations.
 * 
 * TODO: * DOES NOT SUPPORT ALL JPA features; such as multi-join-columns,
 * many-to-many, one-to-one.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-01
 * 
 * @see EntityParser
 * @see EntityUtility
 * @see ConversionUtility
 * @see NamedParameterJdbcTemplate
 * @see MapUtility
 */
public class EntityParserImpl implements EntityParser {

  private static Log logger = LogFactory.getLog(EntityParserImpl.class);

  /** The conversion utility. */
  private ConversionUtility conversionUtility;

  /** The map utility. */
  private MapUtility mapUtility;

  /** The entity utility. */
  private EntityUtility entityUtility;

  /** The data source. */
  private DataSource dataSource;

  /** The jdbc template. */
  private NamedParameterJdbcTemplate jdbcTemplate;

  /**
   * The transaction template. a hook back into the transaction template;
   * possible use may be to check consistency
   */
  private TransactionTemplate transactionTemplate;

  /**
   * Instantiates a new entity parser impl.
   */
  public EntityParserImpl() {
    super();
  }

  /**
   * Throw query null exception.
   * 
   * @param clazz
   *          the clazz
   * @param query
   *          the query
   */
  protected void throwQueryNullException(Class<?> clazz, Query query) {
    if (query == null || Comparison.isEmpty(query.getQuery())) {
      String error = "Query or query statement within cannot be null when issued for clazz type " + clazz;
      logger.error(error);
      throw new NullPointerException(error);
    }
  }

  /**
   * Gets the appropriate row mapper for the building of the result return type.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @return the row mapper
   */
  protected <T> RowMapper<T> getRowMapper(Class<T> entityClass, String currentQueryIdentifier, DependencyJoinFilter joinFilter) {

    boolean isPlainClass = Object.class.equals(entityClass);
    boolean isBasicClass = ReflectionUtility.isBasicClass(entityClass);

    RowMapper<T> rowMapper = null;

    // if the entity is a of sub-type object.class or is some sort of
    // primitive class such as BigDecimal, Integer, Double, Short, etc.
    if (isPlainClass || isBasicClass || entityClass == null) {
      rowMapper = new BasicTypeRowBinder<T>();
    } else {
      rowMapper = new EntityRowBinder<T>(entityClass, getEntityUtility(), getMapUtility(), currentQueryIdentifier, joinFilter, this);
    }
    return rowMapper;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findSingleResultByQuery(java.lang.Class,
   *      com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  public <T> T findSingleResultByQuery(Class<T> entityClass, Query query) {
    return findSingleResultByQuery(entityClass, query, new DependencyJoinFilter());
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findSingleResultByQuery(java.lang.Class,
   *      com.p5solutions.core.jpa.orm.Query,
   *      com.p5solutions.core.jpa.orm.DependencyJoinFilter)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T findSingleResultByQuery(Class<T> entityClass, Query query, DependencyJoinFilter joinFilter) {
    throwQueryNullException(entityClass, query);

    String identifier = query.getQueryIdentifier();

    if (joinFilter.hasId(identifier)) {
      JoinFilterItem jf = joinFilter.get(identifier);
      Object instance = jf.getInstance();
      // TODO check for type casting issue to type T

      return (T) instance;
    } else {
      RowMapper<T> rowMapper = getRowMapper(entityClass, identifier, joinFilter);
      SqlParameterSource paramSource = Query.newSQLParameterSource(query, getConversionUtility());
      try {
        T instance = getJdbcTemplate().queryForObject(query.getQuery(), paramSource, rowMapper);
        // not sure why the jdbcTemplate decided to do a
        // DataAccessUtils.requiredSingleResult(results)
        // as such we must capture the exception, and return null if no record
        // was found.
        return instance;
      } catch (EmptyResultDataAccessException e) {
        return null;
      }
    }
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findRawResultByQuery(com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  public Object findRawResultByQuery(Query query) {
    RowMapper<?> rowMapper = getRowMapper(query.getEntityClass(), null, null);
    SqlParameterSource paramSource = Query.newSQLParameterSource(query, getConversionUtility());
    Object instance = getJdbcTemplate().queryForObject(query.getQuery(), paramSource, rowMapper);
    return instance;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findRawResultsByQuery(com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  public List<?> findRawResultsByQuery(Query query) {
    RowMapper<?> rowMapper = getRowMapper(query.getEntityClass(), null, null);
    SqlParameterSource paramSource = Query.newSQLParameterSource(query, getConversionUtility());
    List<?> instances = getJdbcTemplate().query(query.getQuery(), paramSource, rowMapper);
    return instances;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#find(java.lang.Class,
   *      java.lang.Object)
   */
  @Override
  public <T> T find(Class<T> entityClass, Object id) {
    return find(entityClass, id, new DependencyJoinFilter());
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#find(java.lang.Class,
   *      java.lang.Object, com.p5solutions.core.jpa.orm.DependencyJoinFilter)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T find(Class<T> entityClass, Object id, DependencyJoinFilter joinFilter) {
    String identifier = entityClass.getName() + ";key=" + id;

    if (joinFilter.hasId(identifier)) {
      return (T) joinFilter.get(identifier).getInstance();
    } else {
      Criteria<T> criteria = new Criteria<T>(entityClass);
      criteria.add(Restrictions.idEq(entityClass, id));
      Query q = criteria.getQuery(getEntityUtility());
      T instance = findSingleResultByQuery(entityClass, q);
      return instance;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T find(T entity) {
    if (entity == null) {
      // TODO better logging
      throw new NullPointerException("Entity instance cannot be null");
    }

    Class<T> entityClass = (Class<T>) entity.getClass();
    EntityDetail<T> entityDetail = getEntityUtility().getEntityDetail(entityClass);

    Criteria<T> criteria = new Criteria<T>(entityClass);
    List<ParameterBinder> pkBinders = entityDetail.getPrimaryKeyParameterBinders();

    for (ParameterBinder binder : pkBinders) {
      Object value = entityDetail.getValue(entity, binder);
      criteria.add(Restrictions.eq(entityClass, binder.getBindingPath(), value));
    }

    Query query = criteria.getQuery(getEntityUtility());
    return findSingleResultByQuery(entityClass, query);
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findAll(java.lang.Class)
   */
  @Override
  public <T> List<T> findAll(Class<T> entityClass) {
    Criteria<T> criteria = new Criteria<T>(entityClass);
    Query q = criteria.getQuery(getEntityUtility());
    List<T> instances = findResultsByQuery(entityClass, q);
    return instances;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findResultsByQuery(java.lang.Class,
   *      com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  public <T> List<T> findResultsByQuery(Class<T> entityClass, Query query) {
    DependencyJoinFilter joinFilter = new DependencyJoinFilter();
    return findResultsByQuery(entityClass, query, joinFilter);
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#findResultsByQuery(java.lang.Class,
   *      com.p5solutions.core.jpa.orm.Query,
   *      com.p5solutions.core.jpa.orm.DependencyJoinFilter)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> findResultsByQuery(Class<T> entityClass, Query query, DependencyJoinFilter joinFilter) {
    throwQueryNullException(entityClass, query);

    String identifier = query.getQueryIdentifier();

    if (joinFilter.hasId(identifier)) {
      JoinFilterItem jf = joinFilter.get(identifier);
      Object instance = jf.getInstance();
      if (instance instanceof List<?>) {
        return (List<T>) instance;
      }

      String error = "Cannot cast object type " + instance + " to a List<" + entityClass + ">";
      logger.error(error);
      throw new ClassCastException(error);
    } else {
      String sql = query.getQuery();
      RowMapper<T> rowMapper = getRowMapper(entityClass, identifier, joinFilter);
      SqlParameterSource paramSource = Query.newSQLParameterSource(query, getConversionUtility());
      List<T> instances = getJdbcTemplate().query(sql, paramSource, rowMapper);
      return instances;
    }
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getJdbcTemplate()
   */
  @Override
  public NamedParameterJdbcTemplate getJdbcTemplate() {
    if (jdbcTemplate == null) {
      jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    }
    return jdbcTemplate;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setJdbcTemplate(org.springframework.jdbc
   *      .core.namedparam.NamedParameterJdbcTemplate)
   */
  @Override
  public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getDataSource()
   */
  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setDataSource(javax.sql.DataSource)
   */
  @Override
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setEntityUtility(com.p5solutions.core.jpa.orm.EntityUtility)
   */
  @Override
  public void setEntityUtility(EntityUtility entityUtility) {
    this.entityUtility = entityUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getEntityUtility()
   */
  @Override
  public EntityUtility getEntityUtility() {
    return entityUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setConversionUtility(com.p5solutions.core.jpa.orm.
   *      ConversionUtility)
   */
  @Override
  public void setConversionUtility(ConversionUtility conversionUtility) {
    this.conversionUtility = conversionUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getConversionUtility()
   */
  @Override
  public ConversionUtility getConversionUtility() {
    return conversionUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setMapUtility(com.p5solutions.core.jpa.orm.MapUtility)
   */
  @Override
  public void setMapUtility(MapUtility mapUtility) {
    this.mapUtility = mapUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getMapUtility()
   */
  @Override
  public MapUtility getMapUtility() {
    return mapUtility;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#setTransactionTemplate(com.p5solutions.core.jpa.orm
   *      .transaction.TransactionTemplate)
   */
  @Override
  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  /**
   * @see com.p5solutions.core.jpa.orm.EntityParser#getTransactionTemplate()
   */
  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }
}
