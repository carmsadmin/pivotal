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

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;

/**
 * The Interface EntityParser.
 */
public interface EntityParser {

  /**
   * Find single result by query.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param query
   *          the query
   * @return the t
   */
  <T> T findSingleResultByQuery(Class<T> entityClass, Query query);

  /**
   * Find single result by query.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param query
   *          the query
   * @param joinFilter
   *          the join filter
   * @return the t
   */
  <T> T findSingleResultByQuery(Class<T> entityClass, Query query, DependencyJoinFilter joinFilter);

  /**
   * Find raw result by query.
   * 
   * @param query
   *          the query
   * @return the object
   */
  Object findRawResultByQuery(Query query);

  /**
   * Find raw results by query.
   * 
   * @param query
   *          the query
   * @return the list
   */
  List<?> findRawResultsByQuery(Query query);

  /**
   * Find a single entity using a single primary key column. Entity class type
   * must only have a single {@link Id}.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class type of type <T>
   * @param id
   *          the id used to match against the primary key column of the entity
   * @return the <T> instance of the entity, otherwise <code>null</code> if does
   *         not exist
   */
  <T> T find(Class<T> entityClass, Object id);

  /**
   * Find a single entity using an instance of that entity, however the primary
   * key be present within the instance, otherwise a invalid exception will
   * occur.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  <T> T find(T entity);

  /**
   * Find a single entity using a single primary key column. Entity class type
   * must only have a single {@link Id}. But instead use a
   * {@link DependencyJoinFilter} to prevent recursion loops, this will make
   * sure that entities that are already found via {@link JoinColumn}
   * annotations are not reprocessed when a inverse-join is found.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param id
   *          the id
   * @param joinFilter
   *          the join filter
   * @return the t
   */
  <T> T find(Class<T> entityClass, Object id, DependencyJoinFilter joinFilter);

  /**
   * Find all instances of the entity using no restrictions.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class type of type <T>
   * @return the <T> instance of the entity, otherwise <code>null</code> if does
   *         not exist
   */
  <T> List<T> findAll(Class<T> entityClass);

  /**
   * Find results by query.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param query
   *          the query
   * @return the list
   */
  <T> List<T> findResultsByQuery(Class<T> entityClass, Query query);

  /**
   * Find results by query. Filter out recursion loops using a
   * {@link DependencyJoinFilter}. This will make sure that entities that are
   * already found via {@link JoinColumn} annotations are not reprocessed when a
   * inverse-join is found.
   * 
   * @param <T>
   *          the generic type
   * @param entityClass
   *          the entity class
   * @param query
   *          the query
   * @param joinFilter
   *          the join filter
   * @return the list
   */
  <T> List<T> findResultsByQuery(Class<T> entityClass, Query query, DependencyJoinFilter joinFilter);

  /**
   * Gets the jdbc template.
   * 
   * @return the jdbc template
   */
  NamedParameterJdbcTemplate getJdbcTemplate();

  /**
   * Sets the jdbc template.
   * 
   * @param jdbcTemplate
   *          the new jdbc template
   */
  void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate);

  /**
   * Gets the data source.
   * 
   * @return the data source
   */
  DataSource getDataSource();

  /**
   * Sets the data source.
   * 
   * @param dataSource
   *          the new data source
   */
  void setDataSource(DataSource dataSource);

  /**
   * Sets the entity utility.
   * 
   * @param entityUtility
   *          the new entity utility
   */
  void setEntityUtility(EntityUtility entityUtility);

  /**
   * Gets the entity utility.
   * 
   * @return the entity utility
   */
  EntityUtility getEntityUtility();

  /**
   * Sets the conversion utility.
   * 
   * @param conversionUtility
   *          the new conversion utility
   */
  void setConversionUtility(ConversionUtility conversionUtility);

  /**
   * Gets the conversion utility.
   * 
   * @return the conversion utility
   */
  ConversionUtility getConversionUtility();

  /**
   * Sets the map utility.
   * 
   * @param mapUtility
   *          the new map utility
   */
  void setMapUtility(MapUtility mapUtility);

  /**
   * Gets the map utility.
   * 
   * @return the map utility
   */
  MapUtility getMapUtility();

  /**
   * Sets the transaction template.
   * 
   * @param transactionTemplate
   *          the new transaction template
   */
  void setTransactionTemplate(TransactionTemplate transactionTemplate);

  /**
   * Gets the transaction template.
   * 
   * @return the transaction template
   */
  TransactionTemplate getTransactionTemplate();

}