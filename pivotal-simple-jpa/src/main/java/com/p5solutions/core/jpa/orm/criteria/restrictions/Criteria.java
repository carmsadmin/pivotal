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
package com.p5solutions.core.jpa.orm.criteria.restrictions;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.jpa.orm.Order;
import com.p5solutions.core.jpa.orm.ParameterBinder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.exceptions.EntityNotDefinedWithTableAnnotationException;
import com.p5solutions.core.jpa.orm.exceptions.TooManyIDColumnException;
import com.p5solutions.core.utils.Comparison;

/**
 * The Class Criteria: Allows for generation of queries for a given entity
 * class. As well as restricting and ordering the data set
 * 
 * @author Kasra Rasaee
 * @since 2010-11-22
 * 
 * @param <T>
 *          the generic type
 * 
 * @see Query
 * @see Criterion for restricting the data set
 * @see Order class for ordering the data set
 */
public class Criteria<T> {

	/** The entity class. */
	protected Class<T> entityClass;

	// protected EntityUtility entityUtility;
	/** The restrictions. */
	protected List<Criterion> restrictions;

	/** The orders. */
	protected List<Order> orders;

	/**
	 * Instantiates a new criteria.
	 */
	public Criteria() {
		super();
	}

	/**
	 * Throw no table annotation definition.
	 * 
	 * @param entityClass
	 *          the entity class
	 * @param table
	 *          the table
	 */
	protected void throwNoTableAnnotationDefinition(Class<T> entityClass, Table table) {
		if (table == null) {
			throw new EntityNotDefinedWithTableAnnotationException("No " + Table.class + " was defined on entity type "
					+ entityClass + ", cannot call single find method without table definition");
		}
	}

	/**
	 * Throw too many primary key definition.
	 * 
	 * @param entityClass
	 *          the entity class
	 * @param entityDetail
	 *          the entity detail
	 * @param pbsPK
	 *          the pbs pk
	 */
	protected void throwTooManyPrimaryKeyDefinition(Class<T> entityClass, EntityDetail<T> entityDetail,
			List<ParameterBinder> pbsPK) {
		if (pbsPK.size() > 1) {
			throw new TooManyIDColumnException("Cannot match result of a single primary " + "key value to table entity type "
					+ entityClass + " having a composite primary key definition. " + entityDetail.toStringParamterBinders(pbsPK));
		}
	}

	/**
	 * Throw null primary key column definition.
	 * 
	 * @param entityClass
	 *          the entity class
	 * @param pbsPK
	 *          the pbs pk
	 */
	protected void throwNullPrimaryKeyColumnDefinition(Class<T> entityClass, List<ParameterBinder> pbsPK) {
		if (Comparison.isEmptyOrNull(pbsPK)) {
			throw new NullPointerException("No primary key parameters found for table entity type " + entityClass);
		}
	}

	/**
	 * Adds the.
	 * 
	 * @param restriction
	 *          the restriction
	 */
	public void add(Criterion restriction) {
		if (Comparison.isEmptyOrNull(restrictions)) {
			restrictions = new ArrayList<Criterion>();
		}

		restrictions.add(restriction);
	}

	/**
	 * Adds the order.
	 * 
	 * @param order
	 *          the order
	 */
	public void addOrder(Order order) {
		if (this.orders == null) {
			this.orders = new ArrayList<Order>();
		}
		orders.add(order);
	}

	/**
	 * Gets the query.
	 * 
	 * @param entityUtility
	 *          the entity utility
	 * @return the query
	 */
	public Query getQuery(EntityUtility entityUtility) {

		Query query = new Query(entityClass);
		StringBuilder sb = new StringBuilder();

		// build the basic select x, y, z from tab clause.
		buildStandardSelectStatement(entityUtility, sb);

		// build restrictions into query string
		buildRestrictions(query, entityUtility, sb);

		// build the order by list
		buildOrderBy(query, entityUtility, sb);

		query.setQuery(sb.toString());

		return query;
	}

	/**
	 * Builds the restrictions.
	 * 
	 * @param query
	 *          the query
	 * @param entityUtility
	 *          the entity utility
	 * @param sb
	 *          the sb
	 */
	protected void buildRestrictions(Query query, EntityUtility entityUtility, StringBuilder sb) {

		// skip restrictions if list is empty
		if (Comparison.isEmptyOrNull(restrictions)) {
			return;
		}

		// find the entity details
		EntityDetail<T> entityDetail = entityUtility.getEntityDetail(entityClass);
		sb.append(" WHERE ");

		StringBuilder sbWhere = new StringBuilder();
		for (Criterion restriction : restrictions) {
			if (sbWhere.length() > 0) {
				// TODO add support for JUNCTIONs such as OR AND OR of PAIRED VALUES,
				// ETC.
				// PROBABLY NEEDS TO BE PART OF THE CRITERION ?

				sbWhere.append(" AND ");
			}

			String condition = restriction.toSql(entityDetail);
			sbWhere.append(condition);

			// / add the actual parameter value binding
			restriction.addQueryCriteriaToQuery(entityDetail, query);

			// query.addQueryCriteria(restriction, value)
			// TODO needs to support OR AND LIKE, CONJUNCTIONS, COMPOSITES, etc. etc.,
			// etc..
		}
		sb.append(sbWhere);
	}

	/**
	 * Builds the order by clause for this criteria.
	 * 
	 * @param query
	 *          the query
	 * @param entityUtility
	 *          the entity utility
	 * @param sb
	 *          the sb
	 */
	protected void buildOrderBy(Query query, EntityUtility entityUtility, StringBuilder sb) {
		// skip orders list if empty
		if (Comparison.isEmptyOrNull(orders)) {
			return;
		}

		// find the entity details
		EntityDetail<T> entityDetail = entityUtility.getEntityDetail(entityClass);
		sb.append(" ORDER BY ");

		StringBuilder sbOrder = new StringBuilder();
		for (Order order : orders) {
			if (sbOrder.length() > 0) {
				// TODO support for composite orders??
				sbOrder.append(", ");
			}

			String ordering = order.toSql(entityDetail);
			sbOrder.append(ordering);
		}
		sb.append(sbOrder);
	}

	/**
	 * Gets the primary key parameter binders.
	 * 
	 * @param entityUtility
	 *          the entity utility
	 * @return the primary key parameter binders
	 */
	protected List<ParameterBinder> getPrimaryKeyParameterBinders(EntityUtility entityUtility) {

		// find the entity details
		EntityDetail<T> entityDetail = entityUtility.getEntityDetail(entityClass);

		// find all the primary key parameter binders
		List<ParameterBinder> pbsPK = entityDetail.getPrimaryKeyParameterBinders();

		// check to make sure primary keys exist and within the single primary key
		// value
		throwNullPrimaryKeyColumnDefinition(entityClass, pbsPK);
		throwTooManyPrimaryKeyDefinition(entityClass, entityDetail, pbsPK);

		// get the one and only primary key column
		// ParameterBinderExtended pkpb = pbsPK.get(0);

		return pbsPK;
	}

	/**
	 * Builds the standard select statement.
	 * 
	 * @param entityUtility
	 *          the entity utility
	 * @param sb
	 *          the sb
	 * @return the string builder
	 */
	protected StringBuilder buildStandardSelectStatement(EntityUtility entityUtility, StringBuilder sb) {
		if (sb == null) {
			sb = new StringBuilder();
		}

		// find the entity details
		EntityDetail<T> entityDetail = entityUtility.getEntityDetail(entityClass);

		// get the table annotation for the table entity class, if any
		Table table = entityDetail.getTableAnnotation();

		// check to make sure table annotation is present
		throwNoTableAnnotationDefinition(entityClass, table);

		String tableName = table.name();
		String catalogName = table.catalog();
		String schemaName = table.schema();

		List<ParameterBinder> pbs = entityDetail.getParameterBinders();
		for (ParameterBinder pb : pbs) {

			// TODO refactor - SIMPLIFY

			if (pb.isColumn()) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(pb.getColumnNameUpper());
			} else if (pb.isJoinColumn()) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(pb.getJoinColumnNameUpper());
			}
		}

		sb.insert(0, "SELECT ");
		sb.append(" FROM ");

		// TODO if MSSQL??? only??
		if (Comparison.isNotEmpty(catalogName)) {
			// TODO design for MSSQL / MYSQL, etc.
			// TODO .. if MSSQL ,, usually requires something like
			// [catalog].[schema].[table]
			sb.append(catalogName);
			sb.append('.');
		}

		if (Comparison.isNotEmpty(schemaName)) {
			// TODO .. if MSSQL ,, usually requires something like
			// [catalog].[schema].[table]
			sb.append(schemaName);
			sb.append('.');
		}

		sb.append(table.name());

		return sb;
	}

	/**
	 * Instantiates a new criteria.
	 * 
	 * @param entityClass
	 *          the entity class
	 */
	public Criteria(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Gets the entity class.
	 * 
	 * @return the entity class
	 */
	public Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * Sets the entity class.
	 * 
	 * @param entityClass
	 *          the new entity class
	 */
	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

}
