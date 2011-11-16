/* Pivotal 5 Solutions Inc. - Object Change Tracking and Mapping Utilities - Java Library.
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
package com.p5solutions.trackstate.aop;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.p5solutions.core.jpa.orm.entity.aop.Targetable;

/**
 * TrackStateProxy: Interface defined as part of the enhanced proxy class. All
 * instances of {@link TrackStateProxyFactoryImpl} will implement this
 * interface, which can be type casted such that state of the proxy can be
 * monitored or changed.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-10
 * @see Track annotation for method level tracking
 * @see TrackStateProxyAspect for an automatic proxying of value objects
 *      returned via a service method call, e.g: an implementation of
 *      {@link ActionService}.
 * @see TrackState annotation for details on how to wrap an object for tracking
 * @see TrackStateProxyMethodHandler interface for implementation definition.
 * @see TrackStateProxyMethodHandlerImpl for actual implementation details.
 */
public interface TrackStateProxy extends Targetable {

	public Object writeReplace();

	/**
	 * Checks if is initialized.
	 * 
	 * @return true, if is initialized
	 */
	boolean isInitialized();

	/**
	 * Sets the initialized.
	 * 
	 * @param initialized
	 *          the new initialized
	 */
	void setInitialized(boolean initialized);

	/**
	 * Set the laundry flags. <note>Restrict from calling this method directly, or
	 * look at {@link TrackStateProxyWrapper}</note>
	 * 
	 * @param trackStateLaundryMap
	 */
	void setTrackStateLaundryMap(
	    Map<String, TrackStateLaundry> trackStateLaundryMap);

	/**
	 * Reset the laundry list.
	 */
	void reset();

	/**
	 * Gets the track state laundry map defined for this proxy instance.
	 * 
	 * @return the laundry list
	 */
	Map<String, TrackStateLaundry> getTrackStateLaundryMap();

	/**
	 * Gets the track state laundry list defined for this proxy instance.
	 * 
	 * @see TrackStateProxy#getTrackStateLaundryMap()
	 * @return the track state laundry list
	 */
	Collection<TrackStateLaundry> getTrackStateLaundryList();

	/**
	 * Gets the affected classes.
	 * 
	 * @return the affected classes
	 */
	List<Class<?>> getAffectedClasses();

	/**
	 * Gets the track state laundry list searched for a specific class type.
	 * 
	 * @param forClazz
	 *          the for clazz
	 * @return the track state laundry list
	 */
	List<TrackStateLaundry> getTrackStateLaundryListForClass(Class<?> forClazz);
}