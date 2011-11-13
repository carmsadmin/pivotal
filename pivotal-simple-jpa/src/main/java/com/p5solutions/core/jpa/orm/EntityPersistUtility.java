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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.p5solutions.core.jpa.orm.DMLOperation.OperationType;
import com.p5solutions.core.jpa.orm.entity.aop.EntityProxy;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class EntityPersistUtility: This utility will help generate the basic DML
 * operations for a any given Table-Entity. The class passed in, must be
 * annotated with the {@link Table} and {@link Entity} annotations.
 * 
 * <pre>
 * Example:
 * 	OperationKey = User.class, INSERT
 *  Operation = INSERT INTO USER_TBL (USR_ID, FRST_NM, LST_NM) VALUES (:userId, :firstName, :lastName);
 *  
 *  The parameter binding names are generated based on the get methods for 
 *  any given column field, including {@link ManyToOne}, {@link OneToOne} 
 *  join-columns
 * </pre>
 * 
 * @author Kasra Rasaee
 * @since 2010-11-10
 * 
 * @see EntityUtility
 * @see EntityDetail
 * @see DMLOperation
 * @see DMLOperationKey
 */
public class EntityPersistUtility {

	/** Is the JdbcTemplate a Name based Parameter Binder? for now always TRUE */
	private boolean namedParameters = true;

	/** The parent entity utility used for generating parameter binders. */
	private EntityUtility entityUtility;

	/**
	 * The cache used for storing all basic dml operations for a given
	 * table-entity.
	 */
	private Hashtable<DMLOperationKey, DMLOperation> cache = new Hashtable<DMLOperationKey, DMLOperation>();

	/**
	 * Throw table-entity is null exception.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param table
	 *          the table
	 * @param entity
	 *          the entity
	 */
	protected <T> void throwTableEntityNullException(Class<T> clazz, Table table, Entity entity) {
		// if the class is not annotated with table and entity
		// annotations, then throw an exception, we cannot process
		// none table entities and generate basic operations for them.
		if (entity == null || table == null) {
			throw new NullPointerException("Class type " + clazz + " must be annotated with @" + Entity.class + " and @"
					+ Table.class + " when building basic SELECT / DML operations.");
		}
	}

	/**
	 * Process whether the primary key fields annotated with {@link Id} should be
	 * part of the dml operation.
	 * 
	 * @param binder
	 *          the binder
	 * @return true, if successful
	 */
	protected boolean doId(ParameterBinder binder) {
		if (binder.isPrimaryKey()) {
			return true;
		}
		return false;
	}

	/**
	 * Process whether the column field annotated with {@link Column} should be
	 * part of the dml operation.
	 * 
	 * @param binder
	 *          the binder
	 * @param forInsert
	 *          the for insert
	 * @param forUpdate
	 *          the for update
	 * @return true, if successful
	 */
	protected boolean doColumn(ParameterBinder binder, boolean forInsert, boolean forUpdate) {
		if (binder.isColumn()) {
			if (forInsert && binder.isInsertable()) {
				return true;
			}

			if (forUpdate && binder.isUpdatable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process whether the join-column field annotated with {@link JoinColumn}
	 * should be part of the dml operation.
	 * 
	 * @param binder
	 *          the binder
	 * @param forInsert
	 *          the for insert
	 * @param forUpdate
	 *          the for update
	 * @return true, if successful
	 */
	protected boolean doJoinColumn(ParameterBinder binder, boolean forInsert, boolean forUpdate) {

		boolean go = false;
		if (forInsert && binder.isInsertable()) {
			go = true;
		}

		if (forUpdate && binder.isUpdatable()) {
			go = true;
		}

		if (binder.isManyToOne()) {
			return go;
		} else if (binder.isOneToMany()) {
			// TODO ignore? since this is not really a column
			return false;
		} else if (binder.isManyToMany()) {
			// TODO ignore? since this is not really a column
			// probably should expand this method to support the @TableJoin ???
			return false;
		} else if (binder.isOneToOne()) {
			return go;
		}

		return false;
	}

	/**
	 * Attempt to add the database column-name to the insert statements column
	 * name list.
	 * 
	 * Example values = "USR_ID, FRST_NM, LST_NM, etc....."
	 * 
	 * @param pb
	 *          the pb
	 * @param values
	 *          the values
	 * @return the string builder
	 */
	protected StringBuilder appendFieldForInsert(ParameterBinder binder, StringBuilder columns, String tableAlias) {

		String v = binder.getColumnNameAnyJoinOrColumn();

		if (Comparison.isNotEmpty(tableAlias)) {
			v = tableAlias + "." + v;
		}

		if (doId(binder)) {
			// TODO
		} else if (doColumn(binder, true, false)) {
			// TODO
		} else if (doJoinColumn(binder, true, false)) {
			// TODO
		} else {
			v = null; // ignore
		}

		if (v != null) {
			if (columns.length() > 0) {
				columns.append(getEntityUtility().getSQLParameterSeparaterCharacter());
			}
			columns.append(v);
		}
		return columns;
	}

	/**
	 * Attempt to add the field binding name to the insert statements value list.
	 * 
	 * Example values = ":userId, :firstName, :lastName, etc......"
	 * 
	 * @param pb
	 *          the pb
	 * @param values
	 *          the values
	 * @return the string builder
	 */
	protected StringBuilder appendValueForInsert(ParameterBinder binder, StringBuilder values) {

		String v = getEntityUtility().getBindingCharacter() + binder.getBindingPathSQL();

		if (doId(binder)) {
			// TODO
		} else if (doColumn(binder, true, false)) {
			// TODO
		} else if (doJoinColumn(binder, true, false)) {
			// TODO
		} else {
			v = null; // ignore
		}

		if (v != null) {
			if (values.length() > 0) {
				values.append(getEntityUtility().getSQLParameterSeparaterCharacter());
			}

			// Use param name since we are using the
			// NamedParameterJdbcTemplate, template!
			if (isNamedParameters()) {
				values.append(v);
			} else {
				// TODO this doesn't work... since right now we are using the
				// NamedParameter->JdbcTemplate
				// use index instead. ?? only if we use JdbcTemplate!
				values.append("?");
			}
		}

		return values;
	}

	/**
	 * Append the key value pairing for a given {@link ParameterBinder}.
	 * 
	 * <pre>
	 * For example if the statement is 
	 * 	UPDATE USR_TBL SET ... 
	 * 
	 * 	- The fieldValues could potentially be generated as such.
	 * 
	 * 	USR_ID=:userId, FRST_NM=:firstName, LST_NM=:lastName, etc..
	 * </pre>
	 * 
	 * @param pb
	 *          the pb
	 * @param fieldsValues
	 *          the fields values
	 * @return the string builder
	 */
	protected StringBuilder appendFieldValueUpdate(ParameterBinder pb, StringBuilder fieldsValues, String tableAlias) {
		String columnName = pb.getColumnNameAnyJoinOrColumn();
		String bindingName = getEntityUtility().getBindingCharacter() + pb.getBindingPathSQL();

		if (doId(pb)) {
			// TODO
		} else if (doColumn(pb, false, true)) {
			// TODO
		} else if (doJoinColumn(pb, false, true)) {
			// TODO
		} else {
			columnName = null; // ignore
			bindingName = null; // ignore
		}

		if (columnName != null) {
			if (fieldsValues.length() > 0) {
				fieldsValues.append(", ");
			}

			// append a table alias to the column if one exists, perhaps in a MERGE statement, for example UPDATE TABLE A SET A.COL = :BB
			if (Comparison.isNotEmpty(tableAlias)) {
				fieldsValues.append(tableAlias);
				fieldsValues.append('.');
			}
			
			fieldsValues.append(columnName);
			fieldsValues.append("=");
			fieldsValues.append(bindingName);
		}

		return fieldsValues;
	}

	/**
	 * Builds the basic DML operations, such as INSERT, DELETE, UPDATE, MERGE for
	 * a given table-entity type.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityClass
	 *          the entity class
	 * @throws Exception
	 *           the exception
	 */
	public <T> void build(Class<T> entityClass) throws Exception {
		buildInsertDML(entityClass);
		buildUpdateDML(entityClass);
		buildDeleteDML(entityClass);
		buildMergeDML(entityClass);
	}

	/**
	 * Builds the delete dml operation statement for a given table-entity type.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityClass
	 *          the entity class
	 * @throws Exception
	 *           the exception
	 */
	protected <T> void buildDeleteDML(Class<T> entityClass) throws Exception {
		Table ta = ReflectionUtility.findAnnotation(entityClass, Table.class);
		Entity ea = ReflectionUtility.findAnnotation(entityClass, Entity.class);

		// check and throw if null
		throwTableEntityNullException(entityClass, ta, ea);

		// get the entity detail for a given table-entity class
		EntityDetail<T> entityDetail = getEntityUtility().getEntityDetail(entityClass);

		// generate the delete dml operation
		String deleteDML = "DELETE FROM " + ta.name() + " WHERE ";

		// generate and append the where clause based on a list of primary key
		// parameters
		deleteDML += buildPrimaryKeyWhereClause(entityDetail);

		// store the delete dml operation
		addDMLOperation(entityClass, deleteDML, OperationType.DELETE);
	}

	/**
	 * Builds the update statements set key=value pairing.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityDetail
	 *          the entity detail
	 * @return the string
	 */
	protected <T> String buildUpdateSET(EntityDetail<T> entityDetail, String tableAlias) {
		StringBuilder fieldsValues = new StringBuilder();
		for (ParameterBinder pb : entityDetail.getParameterBinders()) {
			// TODO probably should ignore the primary key columns

			appendFieldValueUpdate(pb, fieldsValues, tableAlias);
		}
		return fieldsValues.toString();
	}

	/**
	 * Builds the update dml operation statement for a given table-entity type.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityClass
	 *          the entity class
	 * @throws Exception
	 *           the exception
	 */
	protected <T> void buildUpdateDML(Class<T> entityClass) throws Exception {
		Table ta = ReflectionUtility.findAnnotation(entityClass, Table.class);
		Entity ea = ReflectionUtility.findAnnotation(entityClass, Entity.class);

		// check and throw if null
		throwTableEntityNullException(entityClass, ta, ea);

		String updateDML = "UPDATE " + ta.name() + " SET ";
		EntityDetail<T> entityDetail = getEntityUtility().getEntityDetail(entityClass);

		// build the update set key value pair for the update dml statement
		String fieldsValues = buildUpdateSET(entityDetail, null);

		// append all the key=value pairs in need of updating to the update dml.
		updateDML += fieldsValues;

		// build the primary key where clause.
		String where = buildPrimaryKeyWhereClause(entityDetail);

		// append it to the update dml operation
		updateDML += " WHERE " + where;

		// store the update dml operation
		addDMLOperation(entityClass, updateDML, OperationType.UPDATE);
	}

	/**
	 * Builds the primary key where clause as a string for where WHERE PK_ID =
	 * :pkId.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityDetail
	 *          the entity detail
	 * @return the string
	 */
	protected <T> String buildPrimaryKeyWhereClause(EntityDetail<T> entityDetail) {
		// generate where statement
		String where = "";
		for (ParameterBinder pb : entityDetail.getPrimaryKeyParameterBinders()) {
			if (where.length() > 0) {
				where += " AND ";
			}
			String bindName = getEntityUtility().getBindingCharacter() + pb.getBindingPathSQL();
			where += pb.getColumnNameAnyJoinOrColumn() + " = " + bindName;
		}
		return where;
	}

	/**
	 * Builds the insert dml operation for a given table-entity class.
	 * 
	 * @param entityClass
	 *          the clazz
	 * @throws Exception
	 *           the exception
	 */
	protected <T> void buildInsertDML(Class<T> entityClass) throws Exception {
		Table ta = ReflectionUtility.findAnnotation(entityClass, Table.class);
		Entity ea = ReflectionUtility.findAnnotation(entityClass, Entity.class);

		// check and throw if null
		throwTableEntityNullException(entityClass, ta, ea);

		// build the insert statement, but not for a merge, rather insert statement.
		String insertDML = buildInsertDML(entityClass, ta.name(), false, null);

		// TODO ??? -NICE-TO-HAVE- oracle supports the RETURNING CLAUSE, this
		// "could" potentially be used in
		// conjunction with the jdbcTemplate.execute and the CALLBACK statement
		// a.k.a PreparedStatementCallback<T>,
		// such that when the dml happens, the value(s) are stored back into the
		// target entities via the parameter binder
		// that was used to generate the RETURNING clause; rather than having to
		// process a separate query for the sequence
		// generator.

		// **// append the returning clause to the insert DML
		// **// if (Comparison.isNotEmpty(returningColumns)) {
		// **// insertDML += " RETURNING " + returningColumns + " INTO " +
		// **// returningBinders;
		// **// }

		// / Add to list of operation keys.
		addDMLOperation(entityClass, insertDML, OperationType.INSERT);
	}

	/**
	 * Builds the dml for an insert statement. If is for merging, then tableName
	 * can be <code>null</code>
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityClass
	 *          the entity class
	 * @param tableName
	 *          the table name, <code>null</code> if isMerge equals to
	 *          <code>true</true>
	 * @param isMerge
	 *          the is merge
	 * @return the string
	 */
	protected <T> String buildInsertDML(Class<T> entityClass, String tableName, boolean isMerge, String tableAlias) {
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();

		String insertDML = null;

		if (isMerge) {
			insertDML = "INSERT (";
		} else {
			insertDML = "INSERT INTO " + tableName + " (";
		}

		EntityDetail<?> entityDetail = getEntityUtility().getEntityDetail(entityClass);

		for (ParameterBinder pb : entityDetail.getParameterBinders()) {
			appendFieldForInsert(pb, fields, tableAlias);
			appendValueForInsert(pb, values);
		}

		insertDML += fields;
		insertDML += ") VALUES (";
		insertDML += values;
		insertDML += ")";
		return insertDML;
	}

	protected <T> void buildMergeDML(Class<T> entityClass) throws Exception {
		Table ta = ReflectionUtility.findAnnotation(entityClass, Table.class);
		Entity ea = ReflectionUtility.findAnnotation(entityClass, Entity.class);

		// check and throw if null
		throwTableEntityNullException(entityClass, ta, ea);

		// get the entity detail for the given table-entity class type
		EntityDetail<T> entityDetail = getEntityUtility().getEntityDetail(entityClass);

		// StringBuilder fields = new StringBuilder();
		// StringBuilder values = new StringBuilder();

		// String returningColumns = "";
		// String returningBinders = "";
		String mergeDML = "MERGE INTO " + ta.name() + " b USING (";

		// get a list of id fields, if none where found, then throw an exception!!
		List<ParameterBinder> pkPBs = entityDetail.getPrimaryKeyParameterBinders();
		if (Comparison.isEmptyOrNull(pkPBs)) {
			throw new NullPointerException("No id fields found for given table-entity type " + entityClass);
		}

		// build select statement for MERGE ON
		String mergeJoinOn = "";
		String selectQuery = "SELECT ";
		String selectWhere = "";
		for (ParameterBinder pb : pkPBs) {
			if (selectWhere.length() > 0) {
				selectWhere += " AND ";
				selectQuery += ",";
				mergeJoinOn += " AND ";
			}
			selectQuery += pb.getColumnNameAnyJoinOrColumn();

			// build the where clause
			selectWhere += pb.getColumnNameAnyJoinOrColumn();
			selectWhere += "=" + getEntityUtility().getBindingCharacter() + pb.getBindingPathSQL();

			// MergeJoin ON
			mergeJoinOn += "b." + pb.getColumnNameAnyJoinOrColumn();
			mergeJoinOn += "=";
			mergeJoinOn += "e." + pb.getColumnNameAnyJoinOrColumn();
		}
		selectQuery += " FROM " + ta.name();
		selectQuery += " WHERE " + selectWhere;

		// append select statement to merge dml operation statement
		mergeDML += selectQuery + ") e";
		mergeDML += " ON (" + mergeJoinOn + ")";

		// when matched (UPDATE)
		// build the update set key value pair for the update statement
		mergeDML += " WHEN MATCHED THEN ";
		mergeDML += "UPDATE SET " + buildUpdateSET(entityDetail, "b"); // b = table alias of merge statement

		// when not matched (INSERT)
		mergeDML += " WHEN NOT MATCHED THEN ";
		mergeDML += buildInsertDML(entityClass, null, true, "b"); // b = table alias of merge statement
		
		// add the merge dml statement to the dml cache
		addDMLOperation(entityClass, mergeDML, OperationType.MERGE);
	}

	/**
	 * Adds the dml operation to the DML cache list.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param entityClass
	 *          the entity class
	 * @param dml
	 *          the sql defining the DML
	 * @param operationType
	 *          the operation type, INSERT, UPDATE, DELETE, MERGE, or as listed in
	 *          {@link OperationType}
	 * @return a new instance of the {@link DMLOperation}
	 */
	protected <T> DMLOperation addDMLOperation(Class<T> entityClass, String dml, OperationType operationType) {
		// / Add to list of operation keys.
		DMLOperationKey operationKey = new DMLOperationKey(entityClass, operationType);
		DMLOperation operation = new DMLOperation();
		operation.setStatement(dml);
		cacheDMLOperation(operationKey, operation);
		return operation;
	}

	/**
	 * Gets a DML operation based on class type and type of operation - INSERT,
	 * UPDATE, DELETE, MERGE.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param operationType
	 *          the operation type
	 * @return the dML operation
	 */
	protected DMLOperation getDMLOperation(Class<?> clazz, OperationType operationType) {

		if (EntityProxy.class.isAssignableFrom(clazz)) {
			clazz = clazz.getSuperclass();
		}
		
		DMLOperationKey key = new DMLOperationKey(clazz, operationType);

		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		return null;
	}

	/**
	 * Cache the DML operation.
	 * 
	 * @param key
	 *          the key
	 * @param operation
	 *          the operation
	 */
	protected void cacheDMLOperation(DMLOperationKey key, DMLOperation operation) {
		if (cache != null) {
			cache.put(key, operation);
		}
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

	/**
	 * Sets the named parameters.
	 * 
	 * @param namedParameters
	 *          the new named parameters
	 */
	protected void setNamedParameters(boolean namedParameters) {
		this.namedParameters = namedParameters;
	}

	/**
	 * Checks if is named parameters.
	 * 
	 * @return true, if is named parameters
	 */
	protected boolean isNamedParameters() {
		return namedParameters;
	}

}
