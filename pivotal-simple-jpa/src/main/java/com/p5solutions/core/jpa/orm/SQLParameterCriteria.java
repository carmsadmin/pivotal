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

import javax.persistence.Temporal;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLParameterCriteria.
 */
public class SQLParameterCriteria {

	/** The value. */
	private Object value;
	// private String bindingName;
	/** The binding path. */
	private String bindingPath;

	/** The binding index. */
	private Integer bindingIndex;

	/** The binding type. */
	private Class<?> bindingType;

	/** The parameter binder. */
	private ParameterBinder parameterBinder;

	// public QueryParameterCriteria(Class<?> bindingType) {
	// this.setBindingType(bindingType);
	// }

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *          the new value
	 */
	public void setValue(Object value) {
		this.value = value;
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
	 * Sets the binding path.
	 * 
	 * @param bindingPath
	 *          the new binding path
	 */
	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	// public String getBindingName() {
	// return bindingName;
	// }
	//
	// public void setBindingName(String bindingName) {
	// this.bindingName = bindingName;
	// }

	/**
	 * Gets the binding index.
	 * 
	 * @return the binding index
	 */
	public Integer getBindingIndex() {
		if (bindingIndex == null) {
			throw new NullPointerException("Binding index is not defined, please use binding by name, instead");
		}
		return bindingIndex;
	}

	/**
	 * Sets the binding index.
	 * 
	 * @param bindingIndex
	 *          the new binding index
	 */
	public void setBindingIndex(Integer bindingIndex) {
		this.bindingIndex = bindingIndex;
	}

	/**
	 * Gets the value type.
	 * 
	 * @return the value type
	 */
	public Class<?> getValueType() {
		if (value != null) {
			return value.getClass();
		}
		return null;
	}

	/**
	 * Sets the binding type.
	 * 
	 * @param bindingType
	 *          the new binding type
	 */
	public void setBindingType(Class<?> bindingType) {
		this.bindingType = bindingType;
	}

	/**
	 * Gets the binding type.
	 * 
	 * @return the binding type
	 */
	public Class<?> getBindingType() {
		return bindingType;
	}

	/**
	 * Gets the parameter binder.
	 * 
	 * @return the parameter binder
	 */
	public ParameterBinder getParameterBinder() {
		return parameterBinder;
	}

	/**
	 * Sets the parameter binder. (optional) This is usually used in the
	 * {@link MapUtility} or {@link ConversionUtility} to determine the binding
	 * specifics; for example {@link Temporal}
	 * 
	 * @param parameterBinder
	 *          the new parameter binder
	 */
	public void setParameterBinder(ParameterBinder parameterBinder) {
		this.parameterBinder = parameterBinder;
	}
}