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
package com.p5solutions.core.jpa.orm.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.lang.NotImplementedException;

import com.p5solutions.core.aop.ProxyFactory;
import com.p5solutions.core.jpa.orm.EntityParser;
import com.p5solutions.core.jpa.orm.EntityPersister;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.jpa.orm.InterceptorUtility;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Criteria;
import com.p5solutions.core.jpa.orm.entity.aop.EntityProxy;
import com.p5solutions.core.jpa.orm.entity.aop.EntityProxyFactoryImpl;
import com.p5solutions.core.jpa.orm.exceptions.TooManyResultsException;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionTemplateImpl.
 */
public class TransactionTemplateImpl implements TransactionTemplate {

  /** The entity utility. */
  private EntityUtility entityUtility;

  /** The persister. */
  private EntityPersister persister;

  /** The parser. */
  private EntityParser parser;

  /** The data source. */
  private DataSource dataSource;

  /** The entity proxy factory. */
  private ProxyFactory entityProxyFactory;

  /** The interceptor utility. */
  private InterceptorUtility interceptorUtility;

  /**
   * Initialization.
   */
  public void initialize() {
    getPersister().setInterceptorUtility(getInterceptorUtility());
  }

  /**
   * Proxy an entity.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  @SuppressWarnings("unchecked")
  protected <T> T proxy(T entity) {
    if (entity instanceof EntityProxy) {
      return entity;
    }

    if (entity == null) {
      return null;
    }

    Class<T> entityClass = (Class<T>) entity.getClass();
    return proxy(entityClass, entity);
  }

  /**
   * Proxy an entity with a given class type, generic type T bound.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param entity
   *          the entity
   * @return the t
   */
  protected <T> T proxy(Class<T> entityClass, T entity) {
    if (null != ReflectionUtility.findAnnotation(entityClass, Table.class)) {
      if (entity == null) {
        return null;
      }

      return getEntityProxyFactory().createProxy(entityClass, entity);
    }
    return entity;
  }

  /**
   * Proxy a list of entities of type T.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param entities
   *          the entities
   * @return the list
   */
  protected <T> List<T> proxyList(Class<T> entityClass, List<T> entities) {
    if (entities == null) {
      throw new NullPointerException("Cannot proxy a null list of entities type");
    }

    if (null != ReflectionUtility.findAnnotation(entityClass, Table.class)) {
      List<T> returnList = new ArrayList<T>(entities.size());
      for (T entity : entities) {
        returnList.add(proxy(entityClass, entity));
      }
      return returnList;
    }
    return entities;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#executeDML
   * (java.lang. String, java.util.Map)
   */
  @Override
  public Long executeDML(String sql, Map<String, Object> params) {
    return persister.executeDML(sql, params);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#save(java.
   * lang.Object)
   */
  @Override
  public <T> T save(T entity) throws Exception {
    entity = proxy(persister.save(entity));
    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#update(java
   * .lang.Object )
   */
  @Override
  public <T> T update(T entity) throws Exception {
    entity = proxy(persister.update(entity));
    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#saveOrUpdate
   * (java.lang .Object)
   */
  @Override
  public <T> T saveOrUpdate(T entity) throws Exception {
    return proxy(persister.saveOrUpdate(entity));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#merge(java
   * .lang.Object)
   */
  @Override
  public <T> T merge(T entity) throws Exception {
    // TODO not implemented properly.
    // not even sure if this is possible with merge... well maybe..
    throw new NotImplementedException(
        "Currently not implemented, use of MERGE INTO table A USING (...) ON WHEN MATCHED THEN... WHEN NOT MATCHED THEN...");
    // return persister.merge(entity);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#delete(java
   * .lang.Object )
   */
  @Override
  public <T> int delete(T entity) throws Exception {
    int ret = persister.delete(entity);
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#delete(java
   * .lang.Class, java.lang.Object)
   */
  @Override
  public <T> int delete(Class<T> tableClass, Object id) throws Exception {
    return persister.delete(tableClass, id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#find(java.
   * lang.Class, java.lang.Object)
   */
  @Override
  public <T> T find(Class<T> clazz, Object id) {
    return proxy(clazz, parser.find(clazz, id));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findAll(java
   * .lang.Class )
   */
  @Override
  public <T> List<T> findAll(Class<T> tableClass) {
    List<T> list = parser.findAll(tableClass);
    return proxyList(tableClass, list);
  }

  /**
   * e com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#
   * findSingleResultByQuery (java.lang.Class, java.lang.String)
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @param query the query
   * @return the t
   */
  @Override
  public <T> T findSingleResultByQuery(Class<T> clazz, String query) {
    Query q = new Query(clazz);
    q.setQuery(query);
    return proxy(clazz, parser.findSingleResultByQuery(clazz, q));
  }

  /**
   * Find single result by query.
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @param query the query
   * @param parameters the parameters
   * @return the t
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findSingleResultByQuery
   * (java.lang.Class, java.lang.String, java.lang.Object[])
   */
  @Override
  public <T> T findSingleResultByQuery(Class<T> clazz, String query, Object[] parameters) {
    Query q = new Query(clazz);
    q.setQuery(query);
    buildQueryParameters(parameters, q);
    return parser.findSingleResultByQuery(clazz, q);
  }

  /**
   * Find single result by query.
   *
   * @param <T> the generic type
   * @param query the query
   * @return the t
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findSingleResultByQuery(com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  public <T> T findSingleResultByQuery(Query query) {
    List<T> list = findResultsByQuery(query);
    if (list.size() > 1) {
      throw new TooManyResultsException(list.size(), query.getEntityClass(), query.getQuery());
    }

    if (!Comparison.isEmptyOrNull(list)) {
      return list.get(0);
    }

    return null;
  }

  /**
   * Builds the query parameters. Takes in a list of Object[] split by key then
   * value. Each key should be the binding path of the field,
   * 
   * <pre>
   * 	Example:	 
   * 
   * 	parameters[0] = "address.city"
   *  parameters[1] = "Ottawa"
   *  parameters[2] = "address.postalCode"
   *  parameters[3] = "K2K5K5"
   * </pre>
   * 
   * @param parameters
   *          the parameters
   * @param query
   *          the query
   */
  protected void buildQueryParameters(Object[] parameters, Query query) {
    if (!Comparison.isEmptyOrNull(parameters)) {
      int length = parameters.length;

      double mod = length % 2;
      if (mod == 0) {
        int div = length / 2;
        for (int i = 0; i < div; i++) {
          String bindingPath = (String) parameters[i * 2];
          Object value = parameters[i * 2 + 1];
          bindingPath = ParameterBinder.getBindingPathSQL(bindingPath);
          query.addQueryCriteria(bindingPath, value);
        }
      }
    }
  }

  /**
   * Builds the query parameters. Takes in a Map<String, Object>.
   * 
   * @param keyValue
   *          the key value
   * @param query
   *          the query
   */
  protected void buildQueryParameters(Map<String, Object> keyValue, Query query) {
    if (keyValue != null && keyValue.size() > 0) {
      for (String keyBindingPath : keyValue.keySet()) {
        Object value = keyValue.get(keyBindingPath);
        String bindingPath = ParameterBinder.getBindingPathSQL(keyBindingPath);
        query.addQueryCriteria(bindingPath, value);
      }
    }
  }

  /**
   * Find raw results by query.
   *
   * @param query the query
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findRawResultsByQuery(java.lang.String)
   */
  @Override
  public List<?> findRawResultsByQuery(String query) {
    Query q = new Query(null);
    q.setQuery(query);
    return parser.findRawResultsByQuery(q);
  }

  /**
   * Find raw results by query.
   *
   * @param query the query
   * @param parameters the parameters
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findRawResultsByQuery(java.lang.String,
   * java.lang.Object[])
   */
  @Override
  public List<?> findRawResultsByQuery(String query, Object[] parameters) {
    Query q = new Query(null);
    q.setQuery(query);
    buildQueryParameters(parameters, q);
    return parser.findRawResultsByQuery(q);
  }

  /**
   * Find raw results by query.
   *
   * @param query the query
   * @param keyValue the key value
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findRawResultsByQuery
   * (java.lang.String, java.util.Map)
   */
  @Override
  public List<?> findRawResultsByQuery(String query, Map<String, Object> keyValue) {
    Query q = new Query(null);
    q.setQuery(query);
    buildQueryParameters(keyValue, q);
    return parser.findRawResultsByQuery(q);
  }

  /**
   * Find results by named native query.
   *
   * @param queryName the query name
   * @param keyValue the key value
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findResultsByNamedNativeQuery(java.lang.String,
   * java.util.Map)
   */
  @Override
  public List<?> findResultsByNamedNativeQuery(String queryName, Map<String, Object> keyValue) {
    String className = queryName.substring(0, queryName.lastIndexOf('.'));
    int i = 0;
    try {
      return findResultsByNamedNativeQuery(Class.forName(className), queryName, keyValue);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Named query result class not found.", e);
    }
  }

  /**
   * Find results by named native query.
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @param queryName the query name
   * @param keyValue the key value
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findResultsByNamedNativeQuery(java.lang.Class,
   * java.lang.String, java.util.Map)
   */
  @Override
  public <T> List<T> findResultsByNamedNativeQuery(Class<T> clazz, String queryName, Map<String, Object> keyValue) {
    Query query = new Query(clazz);
    String sql = extractSqlFromNamedQuery(clazz, queryName);

    // if the named query was not found within the 
    if (Comparison.isEmpty(sql)) {
      return findResultsByGlobalNamedNativeQuery(clazz, queryName, keyValue);
    }
    
    query.setQuery(sql);
    buildQueryParameters(keyValue, query);
    return parser.findResultsByQuery(clazz, query);
  }

  /**
   * Find results by named native query from the global named query cache.
   * 
   * Note that queries will be overwritten in sequential order of the entity
   * scanner, a warning message will be logged for duplicate named queries.
   * 
   * If you need to access a specific query within an entity, use the
   * {@link #findResultsByNamedNativeQuery(Class, String, Map)}
   * 
   * @param <T>
   *          the generic type
   * @param mapClazz
   *          the map clazz
   * @param queryName
   *          the query name
   * @param keyValue
   *          the key value
   * @return the list
   */
  public <T> List<T> findResultsByGlobalNamedNativeQuery(Class<T> mapClazz, String queryName, Map<String, Object> keyValue) {
    NamedNativeQuery nnq = entityUtility.findGlobalNamedNativeQuery(queryName);
    if (nnq == null) {
      throw new RuntimeException("No named native query found using " + queryName + " name in global native named query cache.");
    }

    Query query = new Query(mapClazz);
    query.setQuery(nnq.query());
    buildQueryParameters(keyValue, query);
    return parser.findResultsByQuery(mapClazz, query);
  }

  /**
   * Extracts the named query from a specified class. Throws runtime exception
   * if it doesn't find the query.
   *
   * @param clazz the clazz
   * @param queryName the query name
   * @return the string
   */
  private String extractSqlFromNamedQuery(Class<?> clazz, String queryName) {
    // test for a single named query annotated
    NamedNativeQuery query = ReflectionUtility.findAnnotation(clazz, NamedNativeQuery.class);
    if (query != null && queryName.equals(query.name())) {
      return query.query();
    }
    // otherwise test for a list of named queries
    NamedNativeQueries queries = ReflectionUtility.findAnnotation(clazz, NamedNativeQueries.class);
    if (queries == null) {
      return null;
    }
    
    for (NamedNativeQuery q : queries.value()) {
      if (queryName.equals(q.name())) {
        return q.query();
      }
    }
    
    return null;
  }

  /**
   * Find results by criteria.
   *
   * @param <T> the generic type
   * @param criteria the criteria
   * @return the list
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findResultsByCriteria
   * (com.p5solutions.core.jpa.orm.criteria.restrictions.Criteria)
   */
  @Override
  public <T> List<T> findResultsByCriteria(Criteria<T> criteria) {
    if (criteria == null) {
      throw new NullPointerException("Criteria cannot be null when issueing a select by " + Criteria.class);
    }

    Query query = criteria.getQuery(parser.getEntityUtility());
    List<T> list = parser.findResultsByQuery(criteria.getEntityClass(), query);
    Class<T> tableClass = criteria.getEntityClass();
    return proxyList(tableClass, list);
  }

  /* (non-Javadoc)
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#findResultsByQuery(com.p5solutions.core.jpa.orm.Query)
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> findResultsByQuery(Query query) {
    if (query == null) {
      throw new NullPointerException("Query cannot be null when requesting data by " + Query.class);
    }

    // possible casting issue
    Class<T> clazz = (Class<T>) query.getEntityClass();
    List<T> list = parser.findResultsByQuery(clazz, query);
    return list;
  }

  /**
   * Gets the sequence value.
   *
   * @param sequenceName the sequence name
   * @return the sequence value
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#getSequenceValue(java
   * .lang.String)
   */
  @Override
  public Object getSequenceValue(String sequenceName) {
    Query query = new Query(BigDecimal.class);
    query.setQuery("SELECT " + sequenceName + ".nextval FROM DUAL");
    return parser.findRawResultByQuery(query);
  }

  /**
   * Gets the entity utility.
   * 
   * @return the entity utility
   */
  public EntityUtility getEntityUtility() {
    return entityUtility;
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
   * Gets the persister.
   * 
   * @return the persister
   */
  public EntityPersister getPersister() {
    return persister;
  }

  /**
   * Sets the persister.
   * 
   * @param persister
   *          the new persister
   */
  public void setPersister(EntityPersister persister) {
    this.persister = persister;
    if (persister != null) {
      persister.setTransactionTemplate(this);
    }
  }

  /**
   * Gets the parser.
   * 
   * @return the parser
   */
  public EntityParser getParser() {
    return parser;
  }

  /**
   * Sets the parser.
   * 
   * @param parser
   *          the new parser
   */
  public void setParser(EntityParser parser) {
    this.parser = parser;
    if (parser != null) {
      parser.setTransactionTemplate(this);
    }
  }

  /**
   * Gets the data source.
   * 
   * @return the data source
   */
  public DataSource getDataSource() {
    return dataSource;
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

  /**
   * Sets the entity proxy factory.
   * 
   * @param entityProxyFactory
   *          the new entity proxy factory
   */
  public void setEntityProxyFactory(ProxyFactory entityProxyFactory) {
    this.entityProxyFactory = entityProxyFactory;
  }

  /**
   * Gets the entity proxy factory.
   * 
   * @return the entity proxy factory
   */
  public ProxyFactory getEntityProxyFactory() {
    if (entityProxyFactory == null) {
      this.entityProxyFactory = new EntityProxyFactoryImpl();
    }
    return entityProxyFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#
   * setInterceptorUtility (com.p5solutions.core.jpa.orm.InterceptorUtility)
   */
  @Override
  public void setInterceptorUtility(InterceptorUtility interceptorUtility) {
    this.interceptorUtility = interceptorUtility;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.core.jpa.orm.transaction.TransactionTemplate#
   * getInterceptorUtility()
   */
  @Override
  public InterceptorUtility getInterceptorUtility() {
    return interceptorUtility;
  }
}
