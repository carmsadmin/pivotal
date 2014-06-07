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

import java.io.Serializable;
import java.util.List;

import com.p5solutions.trackstate.utils.TrackStateUtility;

/**
 * TrackStateLaundry: POJO defining the method and classes that were affected
 * for a given class annotated by the {@link TrackState} annotation for any
 * given methods which annotated by {@link Track}
 * 
 * @author Kasra Rasaee
 * @since 2009-02-11
 * 
 * @see Track for method tracking
 * @see TrackState for pojo tracking
 * @see TrackStateProxy for castable instances derived from the
 *      {@link TrackStateProxyFactoryImpl}
 * @see TrackStateProxyAspect for automatic proxying of methods return values
 *      via the {@link WrapTrackStateProxy} annotation
 * @see TrackStateProxyFactoryImpl factory which builds the track state proxy
 * @see TrackStateProxyMethodHandlerImpl callback method handler handles the
 *      invocation of methods, castable to a {@link TrackStateProxy}.
 * @see WrapTrackStateProxy annotation defined on methods which need auto
 *      track-state proxying of return values.
 * @see TrackStateUtility for utilities on how to capture data from a
 *      {@link TrackStateProxy} instance.
 */
public class TrackStateLaundry implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

	public String method;

  /** The dirty. */
  public boolean dirty;

  /** The original. */
  public Object original;

  /** The affected. */
  public List<Class<?>> affected;

  /**
   * Gets the method.
   * 
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * Sets the method.
   * 
   * @param method
   *          the new method
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * Checks if is dirty.
   * 
   * @return true, if is dirty
   */
  public boolean isDirty() {
    return dirty;
  }

  /**
   * Sets the dirty.
   * 
   * @param dirty
   *          the new dirty
   */
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * Gets the original.
   * 
   * @return the original
   */
  public Object getOriginal() {
    return original;
  }

  /**
   * Sets the original.
   * 
   * @param original
   *          the new original
   */
  public void setOriginal(Object original) {
    this.original = original;
  }

  /**
   * Gets the affected.
   * 
   * @return the affected
   */
  public List<Class<?>> getAffected() {
    return affected;
  }

  /**
   * Sets the affected.
   * 
   * @param affected
   *          the new affected
   */
  public void setAffected(List<Class<?>> affected) {
    this.affected = affected;
  }
}