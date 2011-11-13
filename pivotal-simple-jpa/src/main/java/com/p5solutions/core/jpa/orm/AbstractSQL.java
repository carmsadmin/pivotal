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

import java.util.Hashtable;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.p5solutions.core.jpa.orm.exceptions.TypeConversionException;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

public abstract class AbstractSQL {

  /** The entity class. */
  private Class<?> entityClass;

  /** The sql. */
  private String sql;

  /** The criteria. */
  private Hashtable<String, SQLParameterCriteria> criteria;

  /** The sql identifier. */
  private String sqlIdentifier;

  /**
   * Instantiates a new query.
   * 
   * @param entityClass
   *          the entity class
   */
  public AbstractSQL(Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Checks for value.
   * 
   * @param bindingPath
   *          the binding name
   * @return true, if successful
   */
  public boolean hasValue(String bindingPath) {
    if (getCriteria() != null && getCriteria().containsKey(bindingPath)) {
      return true;
    }
    return false;
  }

  /**
   * Gets the value.
   * 
   * @param bindingPath
   *          the binding name
   * @return the value
   */
  public SQLParameterCriteria getValue(String bindingPath) {
    if (getCriteria() != null) {
      return getCriteria().get(bindingPath);
    }
    return null;
  }

  /**
   * Adds the query criteria.
   * 
   * @param bindingIndex
   *          the binding index
   * @param value
   *          the value
   * @return the query parameter criteria
   */
  public SQLParameterCriteria addQueryCriteria(Integer bindingIndex, Object value) {
    throw new NotImplementedException("Bind by index not yet implemented, since we only use the NamedParameterJdbcTemplate");
  }

  /**
   * Adds the query criteria.
   * 
   * @param bindingName
   *          the binding name
   * @param value
   *          the value
   * @return the query parameter criteria
   */
  public SQLParameterCriteria addQueryCriteria(String bindingPath, Object value) {
    return addQueryCriteria(bindingPath, value, null);
  }

  /**
   * Adds the query criteria.
   * 
   * @param bindingName
   *          the binding name
   * @param value
   *          the value
   * @param sqlType
   *          the sql type
   * @return the query parameter criteria
   */
  public SQLParameterCriteria addQueryCriteria(String bindingPath, Object value, Class<?> sqlType) {
    return addQueryCriteria(bindingPath, value, sqlType, null);
  }

  /**
   * Adds the query criteria.
   * 
   * @param bindingName
   *          the binding name
   * @param value
   *          the value
   * @param sqlType
   *          the sql type
   * @param bindingType
   *          the binding type
   * @return the query parameter criteria
   */
  public SQLParameterCriteria addQueryCriteria(String bindingPath, Object value, Class<?> sqlType, Class<?> bindingType) {

    if (criteria == null) {
      criteria = new Hashtable<String, SQLParameterCriteria>();
    }

    if (criteria.containsKey(bindingPath)) {
      // TODO log warning
      criteria.remove(bindingPath);
    }

    // convert to sql friendly binding path
    bindingPath = ParameterBinder.getBindingPathSQL(bindingPath);

    SQLParameterCriteria qc = new SQLParameterCriteria();
    qc.setValue(value);
    qc.setBindingPath(bindingPath);
    // qc.setBindingName(bindingName);

    // TODO ?? probably should check for null ? eq is null??
    if (bindingType == null && value != null) {
      bindingType = value.getClass();
    }

    qc.setBindingType(bindingType);

    addQueryCriteria(qc);

    return qc;
  }

  /**
   * Adds the query criteria.
   * 
   * @param qc
   *          the qc
   * @return the query parameter criteria
   */
  public SQLParameterCriteria addQueryCriteria(SQLParameterCriteria qc) {
    if (criteria == null) {
      criteria = new Hashtable<String, SQLParameterCriteria>();
    }
    criteria.put(qc.getBindingPath(), qc);
    // criteria.put(qc.getBindingName(), qc);
    return qc;
  }

  /**
   * Gets the query identifier. Generates a key value pair separated by a
   * semi-colon
   * 
   * @return the query identifier
   */
  protected String getSQLIdentifier() {
    if (!Comparison.isEmpty(this.sqlIdentifier)) {
      return this.sqlIdentifier;
    }

    String identifier = entityClass.getName();
    if (criteria != null) {
      for (String key : this.criteria.keySet()) {
        if (identifier.length() > 0) {
          identifier += ";";
        }
        SQLParameterCriteria qpc = this.criteria.get(key);
        identifier += key + "=";

        Object value = qpc.getValue();
        if (value == null) {
          identifier += "DBNULL";
        } else {
          identifier += qpc.getValue().toString();
        }
      }
    }

    this.sqlIdentifier = identifier;
    return this.sqlIdentifier;
  }

  /**
   * New sql parameter source.
   * 
   * @param query
   *          the query
   * @param conversionUtility
   *          the conversion utility
   * @return the sql parameter source
   */
  public static SqlParameterSource newSQLParameterSource(final AbstractSQL query, final ConversionUtility conversionUtility) {

    SqlParameterSource ps = new SqlParameterSource() {

      protected final void throwQueryNullException() {
        if (query == null) {
          throw new NullPointerException("Query cannot be null when generating new SQL Parameter Source" + this);
        }
        if (conversionUtility == null) {
          throw new NullPointerException("Conversion Utility cannot be null when generating new SQL Parameter Source" + this);
        }
      }

      @Override
      public boolean hasValue(String paramName) {
        throwQueryNullException();
        return query.hasValue(paramName);
      }

      @Override
      public Object getValue(String paramName) throws IllegalArgumentException {
        throwQueryNullException();
        SQLParameterCriteria qc = query.getValue(paramName);
        Object value = qc.getValue();

        // this will handle deep entity objects as well.
        try {
          value = conversionUtility.convert(qc.getParameterBinder(), value, paramName, qc.getBindingType());
        } catch (TypeConversionException e) {
          throw new RuntimeException("Unable convert value " + value + " to target type " + qc.getBindingType() + ", please check target class "
              + (query.getEntityClass() != null ? query.getEntityClass() : "<NULL>") + " for the problem when binding path " + paramName, e);
        }
        return value;
      }

      @Override
      public String getTypeName(String paramName) {
        SQLParameterCriteria qc = query.getValue(paramName);
        if (qc != null && qc.getValue() != null) {
          Object value = qc.getValue();
          return value.getClass().getName();
        }

        return null;
      }

      @Override
      public int getSqlType(String paramName) {
        // TODO use some sort of utility to determine the sql type,
        // since blob and other types may not work correctly.
        return TYPE_UNKNOWN;
      }
    };

    return ps;
  }

  /**
   * Gets the entity class.
   * 
   * @return the entity class
   */
  public Class<?> getEntityClass() {
    return entityClass;
  }

  /**
   * Sets the entity class.
   * 
   * @param entityClass
   *          the new entity class
   */
  public void setEntityClass(Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Gets the criteria.
   * 
   * @return the criteria
   */
  public Hashtable<String, SQLParameterCriteria> getCriteria() {
    return criteria;
  }

  /**
   * Clear criteria.
   */
  public void clearCriteria() {
    criteria = null;
  }

  /**
   * Gets the sQL.
   * 
   * @return the sQL
   */
  protected final String getSQL() {
    return sql;
  }

  /**
   * Sets the sQL.
   * 
   * @param query
   *          the new sQL
   */
  protected final void setSQL(String query) {
    this.sql = query;
  }

}
