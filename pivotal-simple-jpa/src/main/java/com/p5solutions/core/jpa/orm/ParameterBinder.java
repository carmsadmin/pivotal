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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * ParameterBinderExtended: Holds information about a single entity property.
 * This class will return various information about how the property should
 * behave.
 * 
 * As an example, a parameter may be an Id parameter, which may also contain an
 * {@link Column} annotation, or it may be a {@link ManyToOne} relationship with
 * a {@link JoinColumn} or {@link JoinColumns} annotation, or it may very well
 * be a {@link Transient} property.
 * 
 * @author Kasra Rasaee
 * @since 2010-10-30
 * 
 * @see EntityUtility for details on how to process and generate a
 *      {@link ParameterBinder}
 * @see EntityDetail for details on where a single instance of the
 *      {@link ParameterBinder} is stored.
 * @see EntityParser for details on evaluating queries and how a
 *      {@link ParameterBinder} is used.
 * @see EntityPersister for details on evaluating dml operations such as insert,
 *      delete, update, merge, etc.
 * @see TransactionTemplate the template which inevitably calls the persister or
 *      parser utilities.
 */
public class ParameterBinder extends AbstractParameterBinder {

  /** The override column. */
  private Column overrideColumn;

  /** The getter method. */
  private Method getterMethod;

  /** The setter method. */
  private Method setterMethod;

  /** The entity class. */
  private Class<?> entityClass;

  /** The parent class. */
  private Class<?> parentClass;

  /** The parent getter method. */
  private Method parentGetterMethod;

  /** The parent setter method. */
  private Method parentSetterMethod;

  /** The binding path. */
  private String bindingPath;

  /** The sql column meta-data for this column, if any **/
  private ParameterBinderColumnMetaData columnMetaData;
  
  /** The dependency join. */
  /*
   * TODO probably needs to be some sort of list, or perhaps within the
   * DependencyJoin class define a list such that JoinColumns (plural) will work
   */
  private DependencyJoin dependencyJoin;

  /**
   * Gets the generated value.
   * 
   * @return the generated value
   */
  public GeneratedValue getGeneratedValue() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), GeneratedValue.class);
    }
    return null;
  }

  /**
   * Gets the sequence generator.
   * 
   * @return the sequence generator
   */
  public SequenceGenerator getSequenceGenerator() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), SequenceGenerator.class);
    }
    return null;
  }

  /**
   * Gets the embedded.
   * 
   * @return the embedded
   */
  public Embedded getEmbedded() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), Embedded.class);
    }
    return null;
  }

  /**
   * Gets the column.
   * 
   * @return the column
   */
  public Column getColumn() {
    if (getOverrideColumn() != null) {
      return this.overrideColumn;
    }

    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), Column.class);
    }

    return null;
  }

  /**
   * Gets the join column class.
   * 
   * @return the join column class
   */
  public Class<?> getJoinColumnClass() {
    if (isJoinColumn()) {
      return getGetterMethod().getReturnType();
    }
    return null;
  }

  /**
   * Gets the join column.
   * 
   * @return the join column
   */
  public JoinColumn getJoinColumn() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), JoinColumn.class);
    }
    return null;
  }

  /**
   * Gets the sequence name.
   * 
   * @return the sequence name
   */
  public String getSequenceName() {
    SequenceGenerator sg = getSequenceGenerator();
    if (sg != null) {
      return sg.sequenceName();
    }
    return null;
  }

  /**
   * Gets the not null.
   * 
   * @return the not null
   */
  public NotNull getNotNull() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), NotNull.class);
    }
    return null;
  }

  /**
   * Gets the one to one.
   * 
   * @return the one to one
   */
  public OneToOne getOneToOne() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), OneToOne.class);
    }
    return null;
  }

  /**
   * Gets the one to many.
   * 
   * @return the one to many
   */
  public OneToMany getOneToMany() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), OneToMany.class);
    }
    return null;
  }

  /**
   * Gets the many to many.
   * 
   * @return the many to many
   */
  public ManyToMany getManyToMany() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), ManyToMany.class);
    }
    return null;
  }

  /**
   * Gets the many to one.
   * 
   * @return the many to one
   */
  public ManyToOne getManyToOne() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), ManyToOne.class);
    }
    return null;
  }

  /**
   * Gets the max.
   * 
   * @return the max
   */
  public Max getMax() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), Max.class);
    }
    return null;
  }

  /**
   * Gets the min.
   * 
   * @return the min
   */
  public Min getMin() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), Min.class);
    }
    return null;
  }

  /**
   * Gets the temporal.
   * 
   * @return the temporal
   */
  public Temporal getTemporal() {
    if (getGetterMethod() != null) {
      return ReflectionUtility.findAnnotation(getGetterMethod(), Temporal.class);
    }
    return null;
  }

  /**
   * Checks if is column.
   * 
   * @param columnName
   *          the column name
   * @return true, if is column
   */
  public boolean isColumn(String columnName) {
    if (isColumn() && getColumnNameUpper().equals(columnName)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if is join column.
   * 
   * @param columnName
   *          the column name
   * @return true, if is join column
   */
  public boolean isJoinColumn(String columnName) {
    if (isJoinColumn() && getJoinColumnNameUpper().equals(columnName)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if is embedded.
   * 
   * @return true, if is embedded
   */
  public boolean isEmbedded() {
    return getEmbedded() != null;
  }

  /**
   * Checks if is sequence generator.
   * 
   * @return true, if is sequence generator
   */
  public boolean isSequenceGenerator() {
    return getSequenceGenerator() != null;
  }

  /**
   * Checks if is many to one.
   * 
   * @return true, if is many to one
   */
  public boolean isManyToOne() {
    return getManyToOne() != null;
  }

  /**
   * Checks if is many to many.
   * 
   * @return true, if is many to many
   */
  public boolean isManyToMany() {
    return getManyToMany() != null;
  }

  /**
   * Checks if is one to many.
   * 
   * @return true, if is one to many
   */
  public boolean isOneToMany() {
    return getOneToMany() != null;
  }

  /**
   * Checks if is one to one.
   * 
   * @return true, if is one to one
   */
  public boolean isOneToOne() {
    return getOneToOne() != null;
  }

  /**
   * Gets the target value type.
   * 
   * @return the target value type
   */
  public Class<?> getTargetValueType() {
    if (getGetterMethod() != null) {
      return getGetterMethod().getReturnType();
    }
    return null;
  }

  /**
   * Gets the field name.
   * 
   * @return the field name
   */
  public String getFieldName() {
    Method method = getGetterMethod();
    if (method != null) {
      return ReflectionUtility.buildFieldName(method);
    }
    return null;
  }

  /**
   * Checks for column.
   * 
   * @return true, if successful
   */
  public boolean isColumn() {
    return getColumn() != null;
  }

  /**
   * Checks if is join column.
   * 
   * @return true, if is join column
   */
  public boolean isJoinColumn() {
    return getJoinColumn() != null;
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
   * Gets the parent class.
   * 
   * @return the parent class
   */
  public Class<?> getParentClass() {
    return parentClass;
  }

  /**
   * Sets the parent class.
   * 
   * @param parentClass
   *          the new parent class
   */
  public void setParentClass(Class<?> parentClass) {
    this.parentClass = parentClass;
  }

  /**
   * Gets the getter method.
   * 
   * @return the getter method
   */
  public Method getGetterMethod() {
    return getterMethod;
  }

  /**
   * Sets the getter method.
   * 
   * @param method
   *          the new getter method
   */
  public void setGetterMethod(Method method) {
    this.getterMethod = method;
  }

  /**
   * Gets the setter method.
   * 
   * @return the setter method
   */
  public Method getSetterMethod() {
    return setterMethod;
  }

  /**
   * Sets the setter method.
   * 
   * @param setterMethod
   *          the new setter method
   */
  public void setSetterMethod(Method setterMethod) {
    this.setterMethod = setterMethod;
  }

  /**
   * Gets the parent getter method.
   * 
   * @return the parent getter method
   */
  public Method getParentGetterMethod() {
    return parentGetterMethod;
  }

  /**
   * Sets the parent getter method.
   * 
   * @param parentGetterMethod
   *          the new parent getter method
   */
  public void setParentGetterMethod(Method parentGetterMethod) {
    this.parentGetterMethod = parentGetterMethod;
  }

  /**
   * Gets the parent setter method.
   * 
   * @return the parent setter method
   */
  public Method getParentSetterMethod() {
    return parentSetterMethod;
  }

  /**
   * Sets the parent setter method.
   * 
   * @param parentSetterMethod
   *          the new parent setter method
   */
  public void setParentSetterMethod(Method parentSetterMethod) {
    this.parentSetterMethod = parentSetterMethod;
  }

  /**
   * Gets the binding path.
   * 
   * @return the binding path
   */
  public String getBindingPath() {
    return bindingPath;
  }

  /**
   * Gets the binding path for sql parameterization.
   * 
   * @return the binding path sql
   */
  public String getBindingPathSQL() {
    return ParameterBinder.getBindingPathSQL(getBindingPath());
  }

  /**
   * Gets the binding path for sql parameterization.
   * 
   * @return the binding path sql
   */
  public static String getBindingPathSQL(String bindingPath) {
    if (bindingPath != null) {
      return bindingPath.replace('.', '_');
    }
    return bindingPath;
  }

  /**
   * Sets the binding path.
   * 
   * @param bindingPath
   *          the new binding path
   */
  public void setBindingPath(String bindingPath) {
    this.bindingPath = bindingPath;
  }

  /**
   * Gets the column name any join or column attributes.
   * 
   * @return the column name any join or column
   */
  public String getColumnNameAnyJoinOrColumn() {
    if (isColumn()) {
      return getColumnNameUpper();
    }

    if (isJoinColumn()) {
      return getJoinColumnNameUpper();
    }

    return null;
  }

  /**
   * Gets the column name.
   * 
   * @return the column name
   */
  public String getColumnName() {
    Column column = getColumn();
    if (column == null) {
      // TODO logger
      System.out.println("Column definition is null for binding path " + getBindingPath() + " on entity class of type " + entityClass);
    }
    return column.name();
  }

  /**
   * Gets the join column name.
   * 
   * @return the join column name
   */
  public String getJoinColumnName() {
    return getJoinColumn().name();
  }

  /**
   * Gets the column name upper.
   * 
   * @return the column name upper
   */
  public String getColumnNameUpper() {
    return getColumnName().toUpperCase();
  }

  /**
   * Gets the join column name upper.
   * 
   * @return the join column name upper
   */
  public String getJoinColumnNameUpper() {
    return getJoinColumnName().toUpperCase();
  }

  /**
   * Checks if is primary key.
   * 
   * @return true, if is primary key
   */
  @SuppressWarnings("unchecked")
  public boolean isPrimaryKey() {
    // TODO add in IdClass or EmbeddedId
    return ReflectionUtility.hasAnyAnnotation(getGetterMethod(), Id.class);
  }

  /**
   * Checks if is nullable.
   * 
   * @return true, if is nullable
   */
  public boolean isNullable() {
    NotNull notNull = ReflectionUtility.findAnnotation(getGetterMethod(), NotNull.class);
    if (notNull != null) {
      return false;
    }

    Column column = getColumn();
    if (column != null) {
      return column.nullable();
    }

    JoinColumn joinColumn = getJoinColumn();
    if (joinColumn != null) {
      joinColumn.nullable();
    }

    return true;
  }

  /**
   * Checks if is insertable.
   * 
   * @return true, if is insertable
   */
  public boolean isInsertable() {
    Column column = getColumn();
    if (column != null) {
      return column.insertable();
    }

    JoinColumn joinColumn = getJoinColumn();
    if (joinColumn != null) {
      return joinColumn.insertable();
    }

    return true;
  }

  /**
   * Checks if is updatable.
   * 
   * @return true, if is updatable
   */
  public boolean isUpdatable() {
    Column column = getColumn();
    if (column != null) {
      return column.updatable();
    }

    JoinColumn joinColumn = getJoinColumn();
    if (joinColumn != null) {
      return joinColumn.updatable();
    }

    return true;
  }

  /**
   * Checks if is transient.
   * 
   * @return true, if is transient
   */
  @SuppressWarnings("unchecked")
  public boolean isTransient() {
    // TODO check getter method for transient flag.
    return ReflectionUtility.hasAnyAnnotation(getterMethod, Transient.class);
  }

  /**
   * Checks whether the column is targetting a lob type column
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean isLob() {
	 return ReflectionUtility.hasAnyAnnotation(getterMethod, Lob.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String columnName = getColumnNameAnyJoinOrColumn();
    String output = "";
    if (columnName != null) {
      output += columnName;
    }

    String bindingPath = getBindingPath();

    if (bindingPath != null) {
      if (output.length() > 0) {
        output += ":";
      }

      output += bindingPath;
    }

    return output;
  }

  /**
   * Sets the override column.
   * 
   * @param overrideColumn
   *          the new override column
   */
  public void setOverrideColumn(Column overrideColumn) {
    this.overrideColumn = overrideColumn;
  }

  /**
   * Gets the override column.
   * 
   * @return the override column
   */
  public Column getOverrideColumn() {
    return overrideColumn;
  }

  /**
   * Gets the database table's column meta data, comprised of sql type and other set of information.
   * @return
   */
  public ParameterBinderColumnMetaData getColumnMetaData() {
	return columnMetaData;
  }
  
  /**
   * Sets the database table's column meta data information.
   * 
   * @param columnMetaData
   */
  public void setColumnMetaData(ParameterBinderColumnMetaData columnMetaData) {
	this.columnMetaData = columnMetaData;
  }
  
  /**
   * Sets the dependency join.
   * 
   * @param dependencyJoin
   *          the new dependency join
   */
  public void setDependencyJoin(DependencyJoin dependencyJoin) {
    this.dependencyJoin = dependencyJoin;
  }

  /**
   * Gets the dependency join.
   * 
   * @return the dependency join
   */
  public DependencyJoin getDependencyJoin() {
    return dependencyJoin;
  }
}