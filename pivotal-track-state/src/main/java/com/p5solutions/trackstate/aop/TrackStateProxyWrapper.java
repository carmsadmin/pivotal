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

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * TrackStateProxyWrapper: Track State Proxy wrapper class, which allows for
 * byte-code enhanced (aka Proxied objects via enhancers such as cglib) to be
 * properly deserialized, this is particularly useful in RMI (remote invocation
 * calls), or clustering of tomcat (application servers), especially of your
 * object is part of a session scope.
 * 
 * @author Kasra Rasaee
 * @since 2009-03-24
 * @see TrackStateProxyMethodHandlerImpl#writeReplace() for implementation
 * @see TrackStateProxy#writeReplace() definition such that
 *      {@link ObjectOutputStream} can pickup the method defined within the
 *      proxied objects callback class.
 */
public class TrackStateProxyWrapper implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant factory. */
  private static final TrackStateProxyFactoryImpl factory = new TrackStateProxyFactoryImpl();

  /** The initialized. */
  private boolean initialized = false;

  /** The target. */
  private Object target = null;

  /** The dirties. */
  private Map<String, TrackStateLaundry> trackStateLaundryMap = null;

  /**
   * Read resolve.
   * 
   * @return the object
   * 
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public Object readResolve() throws ClassNotFoundException {

    if (target != null) {
      Class<?> targetClazz = target.getClass();
      TrackStateProxy proxy = (TrackStateProxy) factory.createProxy(
          targetClazz, target);
      proxy.setInitialized(this.initialized);
      proxy.setTarget(target);
      proxy.setTrackStateLaundryMap(trackStateLaundryMap);
      return proxy;
    }

    return new NullPointerException("No target object was defined");
  }

  /**
   * Checks if is initialized.
   * 
   * @return true, if is initialized
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Sets the initialized.
   * 
   * @param initialized
   *          the new initialized
   */
  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  /**
   * Gets the target.
   * 
   * @return the target
   */
  public Object getTarget() {
    return target;
  }

  /**
   * Sets the target.
   * 
   * @param target
   *          the new target
   */
  public void setTarget(Object target) {
    this.target = target;
  }

  /**
   * Gets the track state laundry map.
   * 
   * @return the track state laundry map
   */
  public Map<String, TrackStateLaundry> getTrackStateLaundryMap() {
    return trackStateLaundryMap;
  }

  /**
   * Sets the track state laundry map.
   * 
   * @param trackStateLaundryMap
   *          the track state laundry map
   */
  public void setTrackStateLaundryMap(
      Map<String, TrackStateLaundry> trackStateLaundryMap) {
    this.trackStateLaundryMap = trackStateLaundryMap;
  }
}