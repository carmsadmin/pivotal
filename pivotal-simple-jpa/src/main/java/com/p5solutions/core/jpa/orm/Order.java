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

import java.io.Serializable;

import com.p5solutions.core.jpa.orm.exceptions.ParameterBinderNotFoundException;

public class Order implements Serializable {

	private boolean asc;
	private boolean ignoreCase;
	private String bindingPath;
	
	public String toString() {
		return bindingPath + ' ' + (asc ? "ASC" : "DESC");
	}
	
	public Order ignoreCase() {
		ignoreCase = true;
		return this;
	}

	protected Order(String bindingPath, boolean asc) {
		this.bindingPath = bindingPath;
		this.asc = asc;
	}

	/**
	 * Render the SQL fragment
	 *
	 */
	public <T> String toSql(EntityDetail<T> entityDetail) {
		ParameterBinder pb = entityDetail.getParameterBinderByBindingPath(bindingPath);
		if (pb == null) {
			throw new ParameterBinderNotFoundException(entityDetail.getEntityClass(), bindingPath);
		}
		
		return pb.getColumnNameAnyJoinOrColumn() + ' ' + (asc ? "ASC" : "DESC");
		
		/*
		String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, bindingPath);
		Type type = criteriaQuery.getTypeUsingProjection(criteria, bindingPath);
		StringBuffer fragment = new StringBuffer();
		for ( int i=0; i<columns.length; i++ ) {
			SessionFactoryImplementor factory = criteriaQuery.getFactory();
			boolean lower = ignoreCase && type.sqlTypes( factory )[i]==Types.VARCHAR;
			if (lower) {
				fragment.append( factory.getDialect().getLowercaseFunction() )
					.append('(');
			}
			fragment.append( columns[i] );
			if (lower) fragment.append(')');
			fragment.append( ascending ? " asc" : " desc" );
			if ( i<columns.length-1 ) fragment.append(", ");
		}
		return fragment.toString();*/
	}

	/**
	 * Ascending order
	 *
	 * @param bindingPath
	 * @return Order
	 */
	public static Order asc(String bindingPath) {
		return new Order(bindingPath, true);
	}

	/**
	 * Descending order
	 *
	 * @param bindPath
	 * @return Order
	 */
	public static Order desc(String bindPath) {
		return new Order(bindPath, false);
	}

}