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
package com.p5solutions.trackstate.utils;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.trackstate.aop.Track;
import com.p5solutions.trackstate.aop.TrackState;
import com.p5solutions.trackstate.aop.TrackStateIgnoreOnIsNull;
import com.p5solutions.trackstate.aop.TrackStateLaundry;
import com.p5solutions.trackstate.aop.TrackStateProxy;
import com.p5solutions.trackstate.aop.TrackStateProxyAspect;
import com.p5solutions.trackstate.aop.TrackStateProxyFactoryImpl;
import com.p5solutions.trackstate.aop.WrapTrackStateProxy;

/**
 * TrackStateUtility: Track state utility, allowing easy access to a track-able
 * classes.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-18
 * @see TrackStateProxy for type cast conversion of proxy
 * @see TrackStateProxyFactoryImpl for proxy factory implementation
 * @see TrackStateLaundry for tracking of changes
 * @see TrackState annotation for objects that are allowed to be enhanced.
 * @see Track for annotation on methods(and their fields) to track
 * @see TrackStateProxyAspect which works which allows aop handled objects via
 *      spring to be automatically proxied via the {@link WrapTrackStateProxy}
 *      annotation on any method.
 */
public class TrackStateUtility {

  /**
   * Checks for a specific clazz type presence on a {@link Track#clazz()} list.
   * 
   * @param clazz
   *          The {@link Class} type to search for
   * @param track
   *          the track
   * 
   * @return <code>true</code> if found, otherwise <code>false</code>
   */
  public static boolean hasClazz(Track track, Class<?> clazz) {
    Class<?>[] clazzes = track.clazz();
    for (Class<?> trackClazz : clazzes) {
      if (Comparison.isEqual(trackClazz, clazz)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Are values null.
   * 
   * @param object
   *          the object
   * 
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  public static boolean areValuesNull(Object object) {
    if (object == null) {
      throw new NullPointerException("Object cannot be null when invoking " + TrackStateUtility.class.getName() + ".areVAluesNull(...)");
    }

    Class<?> clazz = object.getClass();
    for (Method method : ReflectionUtility.findGetMethodsWithNoParams(clazz)) {
      if (!ReflectionUtility.isValueNull(object, method)) {
        boolean v = ReflectionUtility.hasAnyAnnotation(method, TrackStateIgnoreOnIsNull.class);

        // if the value is true then skip this
        if (v) {
          continue;
        }

        // otherwise return false, the values are not null
        return false;
      }
    }
    return true;
  }

  /**
   * Pulls all methods with annotated {@link Track} for the specific class type,
   * and checks each value for a not null. If any of the values defined for the
   * specific class type "forClazz" is not null, then <code>true</code> is
   * returned.
   * 
   * @param object
   *          The object instance in question.
   * @param forClazz
   *          The {@link Class} type to filter the methods down to. For example
   *          if ?.class is defined only methods with {@link Track#clazz()}
   *          defined with ?.class are checked for null.
   * 
   * @return <code>true</code>, if all values are null, otherwise
   *         <code>false</code>
   */
  public static boolean areValuesNull(Object object, Class<?> forClazz) {
    Class<?> clazz = object.getClass();
    boolean isNull = true;
    for (Method method : ReflectionUtility.getMethodsByAnnotation(clazz, Track.class)) {

      // does the method have any arguments? if so skip this method
      if (ReflectionUtility.doesMethodHaveParams(method)) {
        continue;
      }

      // get the track annotation
      Track track = ReflectionUtility.findAnnotation(method, Track.class);

      // if the track list of clasess has the class we are searching
      if (hasClazz(track, forClazz)) {
        if (!ReflectionUtility.isValueNull(object, method)) {
          isNull = false;
          break;
        }
      }
    }

    return isNull;
  }

  /**
   * Checks for changes made on an instance of {@link TrackStateProxy} enhanced
   * via the {@link WrapTrackStateProxy} annotation and the proxy factory
   * {@link TrackStateProxyFactoryImpl}.
   * 
   * @param object
   *          The object instance in question.
   * @param forClazz
   *          The {@link Class} type to check for changes on.
   * 
   * @return <code>true</code>, if any value for the specific ?.class were
   *         changed, otherwise <code>false</code>
   */
  public static Boolean hasChanges(Object object, Class<?> forClazz) {

    // check object existence.
    if (object == null) {
      // throw exception if the object is null
      throw new NullPointerException("Object instance cannot be null");
    } else if (object instanceof TrackStateProxy) {
      // cast the proxy to a TrackStateProxy
      TrackStateProxy proxy = (TrackStateProxy) object;

      // get a track state laundry list for a given TrackState(clazz=?) type
      List<TrackStateLaundry> laundryList = proxy.getTrackStateLaundryListForClass(forClazz);

      // if the laundry list came back as not null then changes
      // were made to the proxy for a given class tracked class type.
      if (laundryList != null) {
        return true;
      }
      // if the object is instance of track state proxy then do something.
    } else {
      // since there is no proxy, return null
      return null;
    }

    // if all fails then no changes were made.
    return false;
  }

  /**
   * Similar to {@link #hasChanges(Object, Class)} the method will check if a
   * specified property had been changed.
   * 
   * @param object
   *          The object instance in question.
   * @param property
   *          The object's property in question.
   * @param object
   *          The object instance in question.
   * @param forClazz
   *          The {@link Class} type to check for changes on.
   * 
   * @return <code>true</code>, if any value for the specific ?.class were
   *         changed, otherwise <code>false</code>
   */
  public static Boolean hasChanges(Object object, String property, Class<?> forClazz) {

    // check object existence.
    if (object == null || StringUtils.isBlank(property)) {
      // throw exception if the object is null
      throw new NullPointerException("Object and property cannot be null/empty.");

    } else if (object instanceof TrackStateProxy) {
      // cast the proxy to a TrackStateProxy
      TrackStateProxy proxy = (TrackStateProxy) object;

      // get a track state laundry list for a given TrackState(clazz=?) type
      List<TrackStateLaundry> laundryList = proxy.getTrackStateLaundryListForClass(forClazz);
      // if the laundry list came back as not null then changes
      // were made to the proxy for a given class tracked class type.
      if (laundryList != null) {
        String propertyGetter = "get" + StringUtils.capitalize(property);
        for (TrackStateLaundry tsl : laundryList) {
          if (tsl.getMethod().equals(propertyGetter)) {
            return tsl.isDirty();
          }
        }
      }
      // didn't find the property
      return false;
    }
    // since there is no proxy, return null
    else {
      return null;
    }
  }

  /**
   * Checks for any changes made on a specific {@link TrackState} proxy. If the
   * object has not been proxied, the values are checked for <code>null</code>,
   * if all values for the specific searching class type are null, then
   * <code>false</code> is returned, otherwise <code>true</code>.
   * 
   * @param object
   *          The object in question.
   * @param forClazz
   *          The {@link Class} type to filter down the search to.
   * 
   * @return <code>true</code>, if the object should be persisted, otherwise
   *         <code>false</code>.
   */
  public static boolean hasChangesOrIsNew(Object object, Class<?> forClazz) {
    Boolean hasChanged = hasChanges(object, forClazz);
    // if there are no changes, then its a new object
    if (hasChanged == null) {
      // if the values are all null then return false
      return !areValuesNull(object, forClazz);
    }
    return hasChanged.booleanValue();
  }

  /**
   * Checks for track state proxy on a given target object.
   * 
   * @param target
   *          the target, must either be an instance of {@link TrackStateProxy}
   * 
   * @return true, if instance is of type {@link TrackStateProxy}
   */
  public static boolean hasTrackStateProxy(Object target) {
    if (target instanceof TrackStateProxy) {
      return true;
    }

    return false;
  }

  /**
   * Expose real method.
   * 
   * @param method
   *          the method
   * @param target
   *          the target
   * @return the method
   */
  public static Method exposeRealMethod(Method method, Object target) {
    if (method == null) {
      return null;
    }

    String name = method.getName();

    if (hasTrackStateProxy(target)) {
      TrackStateProxy proxy = (TrackStateProxy) target;
      Class<?> clazz = proxy.getTarget().getClass();

      return ReflectionUtility.findMethod(clazz, name);
    }

    return method;
  }
}
