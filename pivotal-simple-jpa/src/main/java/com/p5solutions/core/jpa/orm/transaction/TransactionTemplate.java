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

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.p5solutions.core.jpa.orm.InterceptorUtility;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.criteria.restrictions.Criteria;

// TODO: Auto-generated Javadoc
/**
 * The Interface TransactionTemplate.
 */
public interface TransactionTemplate {
  // TODO fix throws exception, to something more specific??

  // Raw DML
  /**
   * Execute raw DML statements.
   * 
   * @param sql
   *          statement ready to be prepared
   * @param map
   *          of parameters
   * @return number of affected raws
   */
  Long executeDML(String sql, Map<String, Object> params);

  // PERSIST METHODS
  /**
   * Save an entity that is not currently in the database, this issues an INSERT
   * statement.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   * @throws Exception
   *           the exception
   */
  <T> T save(T entity) throws Exception;

  /**
   * Update an entity that is already within the database context, this issues
   * an UPDATE statement.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   * @throws Exception
   *           the exception
   */
  <T> T update(T entity) throws Exception;

  /**
   * Merge an existing or new entity to the database context, this issues a
   * MERGE statement.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   * @throws Exception
   *           the exception
   */
  <T> T merge(T entity) throws Exception;

  /**
   * Save or update an entity based on whether the primary key is set to null or
   * not, if the primary key is not set, then it will issue
   * {@link #save(Object)}, otherwise an {@link #update(Object)}. Considering
   * using {@link #merge(Object)}
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   * @throws Exception
   *           the exception
   */
  <T> T saveOrUpdate(T entity) throws Exception;

  /**
   * Delete an existing entity by using an instance of the actual entity.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the int
   * @throws Exception
   *           the exception
   */
  <T> int delete(T entity) throws Exception;

  /**
   * Delete an existing entity by a single primary key column.
   * 
   * @param <T>
   *          the generic type
   * @param tableClass
   *          the table class
   * @param id
   *          the id
   * @return the int
   * @throws Exception
   *           the exception
   */
  <T> int delete(Class<T> tableClass, Object id) throws Exception;

  // FIND METHODS

  /**
   * Find an instance of an entity by a single primary key column.
   * 
   * @param <T>
   *          the generic type
   * @param tableClass
   *          the table class
   * @param id
   *          the id
   * @return the t
   */
  <T> T find(Class<T> tableClass, Object id);

  /**
   * Find all instances of a given table-entity type.
   * 
   * @param <T>
   *          the generic type
   * @param tableClass
   *          the table class
   * @return the list
   */
  <T> List<T> findAll(Class<T> tableClass);

  /**
   * Find single result by query, with no argument filtering.
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param query
   *          the query
   * @return the t
   */
  <T> T findSingleResultByQuery(Class<T> clazz, String query);

  /**
   * Find single result by query, with a list of key value pairings.
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
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param query
   *          the query
   * @param parameters
   *          the parameters
   * @return the t
   */
  <T> T findSingleResultByQuery(Class<T> clazz, String query, Object[] parameters);

  /**
   * Find single result by query.
   * 
   * @param <T>
   *          the generic type
   * @param query
   *          the query
   * @return the t
   */
  <T> T findSingleResultByQuery(Query query);

  /**
   * Find raw results by query.
   * 
   * @param query
   *          the query
   * @return the list
   */
  List<?> findRawResultsByQuery(String query);

  /**
   * Find raw results by query.
   * 
   * @param query
   *          the query
   * @param parameters
   *          the parameters
   * @return the list
   */
  List<?> findRawResultsByQuery(String query, Object[] parameters);

  /**
   * Find raw results by query.
   * 
   * @param query
   *          the query
   * @param keyValue
   *          the key value
   * @return the list
   */
  List<?> findRawResultsByQuery(String query, Map<String, Object> keyValue);

  /**
   * List of results from a named query. Result entity class extracted from the
   * query name. Requires the name to be prefixed with a Class name followed by
   * a dot. Example: "Document.medproDocs"
   * 
   * @param queryName
   * @param keyValue
   * @return
   */
  List<?> findResultsByNamedNativeQuery(String queryName, Map<String, Object> keyValue);

  /**
   * List of results from a named query. Result entity class specified as a
   * parameter.
   * 
   * @param <T>
   * @param clazz
   * @param queryName
   * @param keyValue
   * @return
   */
  <T> List<T> findResultsByNamedNativeQuery(Class<T> clazz, String queryName, Map<String, Object> keyValue);

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
  <T> List<T> findResultsByGlobalNamedNativeQuery(Class<T> mapClazz, String queryName, Map<String, Object> keyValue);

  /**
   * Find results by criteria.
   * 
   * @param <T>
   *          the generic type
   * @param criteria
   *          the criteria
   * @return the list
   */
  <T> List<T> findResultsByCriteria(Criteria<T> criteria);

  /**
   * Find results by query.
   * 
   * @param <T>
   *          the generic type
   * @param query
   *          the query
   * @return the list
   */
  <T> List<T> findResultsByQuery(Query query);

  /**
   * Query given SQL to create a prepared statement from SQL and a list of arguments to bind to the query, expecting a result list.
   * The results will be mapped to a List (one entry for each row) of Maps (one entry for each column, using the column name as the key).
   * Relies on the {@link NamedParameterJdbcTemplate #queryForList}.
   *
   * @param query
   * @param keyValue
   * @return list of map represented rows
   */
  List<Map<String, Object>> findResultsAsListByQuery(String query, Map<String, ?> keyValue);

  /**
   * Similar to {@link #findResultsAsListByQuery} it will return results as a list of mapped rows. But<br/>
   * first it will resolve the query from the global cache.
   * 
   * @param query
   * @param keyValue
   * @return
   */
  List<Map<String, Object>> findResultsAsListByNamedNativeQuery(String queryName, Map<String, ?> keyValue);

  // MISC METHODS
  /**
   * Gets the sequence value.
   * 
   * @param sequenceName
   *          the sequence name
   * @return the sequence value
   */
  Object getSequenceValue(String sequenceName);

  /**
   * Sets the interceptor utility.
   * 
   * @param interceptorUtility
   *          the new interceptor utility
   */
  void setInterceptorUtility(InterceptorUtility interceptorUtility);

  /**
   * Gets the interceptor utility.
   * 
   * @return the interceptor utility
   */
  InterceptorUtility getInterceptorUtility();
}