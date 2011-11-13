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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import com.p5solutions.core.jpa.orm.transaction.EntityKey;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityDetail.
 * 
 * @param <T>
 *          the generic type
 */
public class EntityDetail<T> {

  /** The entity class. */
  private Class<T> entityClass;

  /** The parameters. */
  private List<ParameterBinder> parameters;

  /** The cache column name to parameter index. */
  private Map<String, Integer> columnNameToIndex;

  /** The cache parameter id. */
  private List<ParameterBinder> cacheParameterID;

  /**
   * Instantiates a new entity detail.
   * 
   * @param entityClass
   *          the entity class
   */
  public EntityDetail(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * New instance of the entity defined as part of this.entityClass of type T.
   * 
   * @return the t
   */
  public T newInstance() {
    return ReflectionUtility.newInstance(entityClass);
  }

  /**
   * Gets the table name.
   * 
   * @return the table name
   */
  public String getTableName() {
    Table table = getTableAnnotation();
    return table != null ? table.name() : null;
  }

  /**
   * Gets the table annotation.
   * 
   * @return the table annotation
   */
  public Table getTableAnnotation() {
    return ReflectionUtility.findAnnotation(getEntityClass(), Table.class);
  }

  /**
   * Gets the entity annotation.
   * 
   * @return the entity annotation
   */
  public Entity getEntityAnnotation() {
    return ReflectionUtility.findAnnotation(getEntityClass(), Entity.class);
  }

  /**
   * Gets the mapped superclass annotation.
   * 
   * @return the mapped superclass annotation
   */
  public MappedSuperclass getMappedSuperclassAnnotation() {
    return ReflectionUtility.findAnnotation(getEntityClass(), MappedSuperclass.class);
  }

  /**
   * Gets the primary key parameter binders.
   * 
   * @return the primary key parameter binders
   */
  public List<ParameterBinder> getPrimaryKeyParameterBinders() {
    if (cacheParameterID == null) {
      cacheParameterID = new ArrayList<ParameterBinder>();
      for (ParameterBinder pb : parameters) {
        if (pb.isPrimaryKey()) {
          cacheParameterID.add(pb);
        }
      }
    }

    return cacheParameterID;
  }

  public String getEntityKey(T entity) {

    // TODO part of build entity key
    // EntityKey entityKey = new EntityKey<T>();

    List<ParameterBinder> pks = getPrimaryKeyParameterBinders();

    if (Comparison.isEmptyOrNull(pks)) {
      throw new RuntimeException("No primary key methods found for entity of type " + getEntityClass());
    }

    // placeholder for all the id key/values.
    // String[] bindingPaths = new String[pks.size()];
    // Object[] bindingValues = new Object[pks.size()];
    // entityKey.setBindingPaths(bindingPaths);
    // entityKey.setBindingValues(bindingValues);

    StringBuilder key = new StringBuilder();
    int i = 0;
    key.append(getEntityClass());
    key.append('>');
    for (ParameterBinder binder : pks) {
      if (key.length() > 0) {
        key.append(':');
      }
      key.append(binder.getBindingPath());
      key.append('=');
      key.append(getValue(entity, binder));
    }

    return key.toString();
    // return entityKey;
  }

  /**
   * To string paramter binders.
   * 
   * @param pbs
   *          the pbs
   * @return the string
   */
  public String toStringParamterBinders(List<ParameterBinder> pbs) {
    StringBuilder output = new StringBuilder();

    if (!Comparison.isEmptyOrNull(pbs)) {
      for (ParameterBinder pb : pbs) {
        output.append(pb.toString());
      }
    }
    return "!empty parameter binder list!";
  }

  /**
   * Gets the column name to index.
   * 
   * @return the column name to index
   */
  protected Map<String, Integer> getColumnNameToIndex() {
    if (columnNameToIndex != null) {
      return columnNameToIndex;
    }

    // build a new map
    columnNameToIndex = new Hashtable<String, Integer>();

    // some counters and index found pointers
    int index = 0;

    // build column to index map
    for (ParameterBinder p : this.parameters) {
      String keyName = "";
      String keyNameBinding = "";
      String keyPathBinding = "";
      if (p.isColumn()) {
        keyName = "C_" + p.getColumnNameUpper();
      } else if (p.isJoinColumn()) {
        keyName = "J_" + p.getJoinColumnNameUpper();
      }
      keyNameBinding = "BN_" + p.getBindingName().toUpperCase();
      keyPathBinding = "BP_" + p.getBindingPath();

      // add the column name as a key, and the binding name as a key
      columnNameToIndex.put(keyName, index);
      columnNameToIndex.put(keyNameBinding, index);
      columnNameToIndex.put(keyPathBinding, index++);
    }

    return columnNameToIndex;
  }

  /**
   * Checks if is primary key of a given entity instance is null.
   * 
   * Note, if premitives are used instead, such as int, instead of Integer, then
   * this could give negative results. Highly recommend using Integer instead of
   * int, or Boolean instead of boolean; despite it being boxed.
   * 
   * @param entity
   *          the entity
   * @return true, if is primary key null
   */
  public boolean isPrimaryKeyNull(T entity) {
    List<ParameterBinder> pks = getPrimaryKeyParameterBinders();

    if (pks == null) {
      throw new NullPointerException("No primary key paramaters found for entity " + entity);
      // TODO dump entire object graph??
    }

    boolean isNull = true;
    for (ParameterBinder pb : pks) {
      Object value = ReflectionUtility.getValue(pb.getGetterMethod(), entity);
      if (value != null) {
        isNull = false;
        break;
      }
    }

    return isNull;
  }

  /**
   * Gets the primary key value for a given index, usually zero if a single key.
   * 
   * @param entity
   *          the entity
   * @param index
   *          the index
   * @return the primary key value
   */
  public Object getPrimaryKeyValue(T entity, int index) {
    List<ParameterBinder> pks = getPrimaryKeyParameterBinders();

    if (pks == null) {
      throw new NullPointerException("No primary key paramaters found for entity " + entity);
      // TODO dump entire object graph??
    }

    ParameterBinder pkPb = pks.get(index);
    return ReflectionUtility.getValue(pkPb.getGetterMethod(), entity);
  }

  /**
   * Gets the value on a given parameter binder.
   * 
   * @param entity
   *          the entity
   * @param binder
   *          the binder
   * @return the value
   */
  public Object getValue(T entity, ParameterBinder binder) {
    if (binder == null) {
      throw new NullPointerException("Cannot get value on a null parameter binder for entity class type " + getEntityClass());
    }

    return ReflectionUtility.getValue(binder.getGetterMethod(), entity);
  }

  /**
   * Gets the parameter binder by the type (Join(J) or Column(C)) filtering out
   * by columnName
   * 
   * @param type
   *          the type
   * @param name
   *          the column name
   * @return the parameter binder
   */
  protected ParameterBinder getParameterBinder(String type, String name) {
    return getParameterBinder(type, name, true);
  }

  /**
   * Gets the parameter binder by the type (Join(J) or Column(C)) filtering out
   * by columnName
   * 
   * @param type
   *          the type
   * @param name
   *          the column name
   * @return the parameter binder
   */
  protected ParameterBinder getParameterBinder(String type, String name, boolean ignoreCase) {
    if (Comparison.isEmpty(name)) {
      throw new NullPointerException("Cannot find null or empty column name in Parameter Binder Cache!");
    }

    Map<String, Integer> cache = getColumnNameToIndex();

    // upper case
    if (ignoreCase) {
      name = name.toUpperCase();
    }

    String keyName = type + name;
    Integer index = cache.get(keyName);

    if (index != null) {
      return parameters.get(index.intValue());
    }

    return null;
  }

  /**
   * Gets the parameter binder for a given {@link Column}.
   * 
   * @param columnName
   *          the column name
   * @return the parameter binder
   */
  public ParameterBinder getParameterBinder(String columnName) {
    return getParameterBinder("C_", columnName);
  }

  /**
   * Gets the join column parameter binder for a given {@link JoinColumn}.
   * 
   * @param columnName
   *          the column name
   * @return the join column parameter binder
   */
  public ParameterBinder getJoinColumnParameterBinder(String columnName) {
    return getParameterBinder("J_", columnName);
  }

  /**
   * Gets the parameter binder by binding name.
   * 
   * @param bindingName
   *          the binding name
   * @return the parameter binder by binding name
   */
  public ParameterBinder getParameterBinderByBindingName(String bindingName) {
    return getParameterBinder("BN_", bindingName);
  }

  /**
   * Gets the parameter binder by binding path.
   * 
   * @param bindingPath
   *          the binding path
   * @return the parameter binder by binding path
   */
  public ParameterBinder getParameterBinderByBindingPath(String bindingPath) {
    return getParameterBinder("BP_", bindingPath, false);
  }

  /**
   * Adds the parameter binder.
   * 
   * @param pb
   *          the pb
   */
  public void addParameterBinder(ParameterBinder pb) {
    if (parameters == null) {
      parameters = new ArrayList<ParameterBinder>();
    }

    parameters.add(pb);
  }

  /**
   * Gets the parameter binder.
   * 
   * @param index
   *          the index
   * @return the parameter binder
   */
  public ParameterBinder getParameterBinder(int index) {
    if (!Comparison.isEmptyOrNull(parameters) && parameters.size() < index) {
      return parameters.get(index);
    }

    return null;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  public void setParameters(List<ParameterBinder> parameters) {
    this.parameters = parameters;
  }

  /**
   * Gets the parameter binders.
   * 
   * @return the parameter binders
   */
  public List<ParameterBinder> getParameterBinders() {
    return parameters;
  }

  /**
   * Gets the entity class.
   * 
   * @return the entity class
   */
  public Class<T> getEntityClass() {
    return entityClass;
  }
}