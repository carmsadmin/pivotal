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
package com.p5solutions.core.jpa.orm.exceptions;

import javax.persistence.Transient;

/**
 * NoColumnDefinedException: Thrown when a column cannot be mapped, either when generating the metadata from the resultset, or 
 * when there is no column definition on a property that requires it, e.g. a column that hasn't been defined with {@link Transient}.
 * 
 * @author Kasra Rasaee
 * @since 2011
 *
 */
public class NoColumnDefinedException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor : Empty
	 */
	public NoColumnDefinedException() {
		super();
	}
	
	/**
	 * Constructor : Message
	 * @param message
	 */
	public NoColumnDefinedException(String message) {
		super(message);
	}
	
	/**
	 * Constructor : for column name and entity class
	 *  
	 * @param columnName
	 * @param entityClazz
	 */
	public NoColumnDefinedException(String columnName, Class<?> entityClazz) {
		this("No column by the name " + columnName + " defined within " + entityClazz);
	}
}
