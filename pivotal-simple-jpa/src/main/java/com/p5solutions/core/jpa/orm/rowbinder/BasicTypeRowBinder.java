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
package com.p5solutions.core.jpa.orm.rowbinder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.persistence.Entity;

import org.springframework.jdbc.core.RowMapper;

/**
 * The Class BasicTypeRowBinder.
 * 
 * @param <T>
 *            the generic type
 */
public class BasicTypeRowBinder<T> implements RowMapper<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();

		// / obviously if we have more than one column, we cannot possibly
		// map it
		// / to a plain old java object of type Object.class, since there
		// are no
		// / members to map the columns to!
		if (metaData.getColumnCount() > 1) {
			throw new RuntimeException(
					"Cannot return multi-column resultset into "
							+ "a plain object of type Object.class. If you need to map a multi-column "
							+ "resultset, please use an object marked with @"
							+ Entity.class + " annotation.");
		}

		// // THIS SHOULD NEVER HAPPEN, QUERY EXCEPTION SHOULD
		// // BE THROWN IF THERE IS A SYNTAX ERROR IN THE QUERY.
		// if (metaData.getColumnCount() == 0) { }

		// Otherwise if there is only 1 column, and its within the scope of
		// plain object.class
		// returnResults.add((T)rs.getObject(1));
		return (T) rs.getObject(1);
	}
}