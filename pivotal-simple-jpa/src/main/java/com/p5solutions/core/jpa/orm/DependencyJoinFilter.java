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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.JoinColumn;

import com.p5solutions.core.jpa.orm.rowbinder.EntityRowBinder;

/**
 * The Class DependencyJoinFilter. A class that holds information about a series
 * of entities being processed as part of an {@link EntityRowBinder}. Prevents
 * recursive loops, by returning, instead of processing a {@link JoinColumn}
 * entity or its inverse-join counterpart.
 * 
 * @author Kasra Rasaee
 * @since 2010-11-08
 * 
 * @see EntityParser and all related find.. methods
 * @see EntityRowBinder#mapRow(java.sql.ResultSet, int)
 */
public class DependencyJoinFilter {

	/**
	 * The Class JoinFilterItem.
	 */
	public class JoinFilterItem {

		/** The id. */
		public Object id;

		/** The instance. */
		public Object instance;

		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		public Object getId() {
			return id;
		}

		/**
		 * Sets the id.
		 * 
		 * @param id
		 *            the new id
		 */
		public void setId(Object id) {
			this.id = id;
		}

		/**
		 * Gets the single instance of JoinFilterItem.
		 * 
		 * @return single instance of JoinFilterItem
		 */
		public Object getInstance() {
			return instance;
		}

		/**
		 * Sets the instance.
		 * 
		 * @param instance
		 *            the new instance
		 */
		public void setInstance(Object instance) {
			this.instance = instance;
		}
	}

	/** The join filter items. */
	private Map<Object, JoinFilterItem> joinFilterItems;

	/**
	 * Gets the join filter items.
	 * 
	 * @return the join filter items
	 */
	public Map<Object, JoinFilterItem> getJoinFilterItems() {
		return joinFilterItems;
	}

	/**
	 * Sets the join filter items.
	 * 
	 * @param joinFilterItems
	 *            the join filter items
	 */
	public void setJoinFilterItems(Map<Object, JoinFilterItem> joinFilterItems) {
		this.joinFilterItems = joinFilterItems;
	}

	/**
	 * Checks for a given identifier within the join filter items list.
	 * 
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	public boolean hasId(Object id) {
		if (this.joinFilterItems == null) {
			return false;
		}

		return this.joinFilterItems.containsKey(id);
	}

	/**
	 * Gets the join filter item by the identifier.
	 * 
	 * @param id
	 *            the id
	 * @return the join filter item
	 */
	public JoinFilterItem get(Object id) {
		if (this.joinFilterItems != null) {
			return this.joinFilterItems.get(id);
		}
		return null;
	}

	/**
	 * Adds the.
	 * 
	 * @param id
	 *            the id
	 * @param instance
	 *            the instance
	 * @return the join filter item
	 */
	public JoinFilterItem add(Object id, Object instance) {
		if (this.joinFilterItems == null) {
			this.joinFilterItems = new HashMap<Object, DependencyJoinFilter.JoinFilterItem>();
		}
		JoinFilterItem item = new JoinFilterItem();
		item.setId(id);
		item.setInstance(instance);
		this.joinFilterItems.put(id, item);
		return item;
	}
}