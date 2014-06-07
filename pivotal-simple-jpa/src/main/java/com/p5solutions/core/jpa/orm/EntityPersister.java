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

import java.util.Map;

import javax.persistence.Id;
import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;

/**
 * EntityPersister. Defines the available functionality of a given entity
 * persistence processor. Different implementations may be required to support a
 * range of relational databases.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-10
 * 
 * @see EntityPersisterImpl
 */
public interface EntityPersister {

  /**
   * Checks if is oracle data source.
   * 
   * @return true, if is oracle data source
   */
  boolean isOracleDataSource();

  /**
   * Checks if is named parameter jdbc template.
   * 
   * @return true, if is named parameter jdbc template
   */
  boolean isNamedParameterJdbcTemplate();

  /**
   * Execute raw DML statements.
   * 
   * @param sql
   * @param params
   * @return
   */
  Long executeDML(String sql, Map<String, Object> params);

  /**
   * Process.
   * 
   * @param entityClass
   *          the entity class
   * @param entity
   *          the entity
   * @return the map sql parameter source
   */
  MapSqlParameterSource process(Class<?> entityClass, Object entity);

  /**
   * Save or update.
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
   * Merge an entity to the database, this uses the MERGE statement, not a
   * 'fake' select-update/save.
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
   * Persist an entity to the database.
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
   * Update an previously persisted entity to the database.
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
   * Delete a previously persisted entity from the database.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return true, if successful
   * @throws Exception
   *           the exception
   */
  <T> int delete(T entity) throws Exception;

  /**
   * Delete a previously persisted entity form the database using a single
   * primary key value; this will try to match against the {@link Id}
   * parameters.
   * 
   * @param <T>
   *          the generic type
   * @param tableClass
   *          the table class
   * @param id
   *          the id
   * @return true, if successful
   * @throws Exception
   *           the exception
   */
  <T> int delete(Class<T> tableClass, Object id) throws Exception;

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
   * Gets the entity parser.
   * 
   * @return the entity parser
   */
  EntityParser getEntityParser();

  /**
   * Sets the entity parser.
   * 
   * @param entityParser
   *          the new entity parser
   */
  void setEntityParser(EntityParser entityParser);

  /**
   * Gets the map utility.
   * 
   * @return the map utility
   */
  MapUtility getMapUtility();

  /**
   * Sets the map utility.
   * 
   * @param mapUtility
   *          the new map utility
   */
  void setMapUtility(MapUtility mapUtility);

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
