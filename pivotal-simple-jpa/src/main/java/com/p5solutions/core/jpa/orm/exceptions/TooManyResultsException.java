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

/**
 * The Class TooManyResultsException: This exception is usually thrown in
 * situations where the result is not the expected value of either
 * <code>null</code> or a single return row result.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-24
 * 
 */
public class TooManyResultsException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new too many results exception.
	 * 
	 * @param message
	 *            the message
	 */
	public TooManyResultsException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new too many results exception.
	 * 
	 * @param resultCount
	 *            the result count
	 * @param entityClass
	 *            the entity class
	 * @param query
	 *            the query
	 */
	public TooManyResultsException(int resultCount, Class<?> entityClass,
			String query) {

		super("Expecting a single result but got " + resultCount
				+ " when processing entity type " + entityClass
				+ ", issueing query [" + query + "]");
	}
}