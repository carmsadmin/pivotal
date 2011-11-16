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
package com.p5solutions.trackstate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;
import com.p5solutions.trackstate.annotation.MapExpand;

/**
 * MapClassTracker: Class which defines a state for the current recursive call
 * to {@link EntityMapper#attemptComplexMap(MapClassTracker, Method, Object)}
 * and {@link EntityMapper#map(Object, Object...)}.
 * 
 * The values stored within an instance of this class are usually derived from
 * the {@link MapClass} and {@link MapClasses} annotation defined as part of the
 * source object being mapped.
 * 
 * However, in some cases such an instance of this class is reused as when
 * method
 * {@link EntityMapper#attemptComplexMap(MapClassTracker, Method, Object)}
 * detects a method with the {@link MapExpand} annotation.
 * 
 * @author Kasra Rasaee
 * @since 2009-05-08
 */
public final class MapClassTracker {

  /** The original source clazz. */
  Class<?> originalSourceClazz;

  /** The real source clazz. */
  Class<?> realSourceClazz;

  /** The instances. */
  Map<Class<?>, Object> instances;

  /** Filter out these classes. */
  List<Class<?>> filters;

  /** Reverse the use of the filter. */
  boolean reverseFilter = false;

  /**
   * Reverse filter usage.
   * 
   * @param reverse
   *          the reverse
   */
  void reverseFilter(boolean reverse) {
    this.reverseFilter = reverse;
  }

  /**
   * New filters.
   * 
   * @return the list< class<?>>
   */
  private List<Class<?>> newFilters() {
    if (filters == null) {
      filters = new ArrayList<Class<?>>();
    }
    return filters;
  }

  /**
   * Adds the filter.
   * 
   * @param clazz
   *          the clazz
   */
  void addFilter(Class<?> clazz) {
    newFilters().add(clazz);
  }

  /**
   * Adds the filters.
   * 
   * @param clazzes
   *          the clazzes
   */
  void addFilters(Class<?>[] clazzes) {
    for (Class<?> clazz : clazzes) {
      addFilter(clazz);
    }
  }

  /**
   * Removes the filter.
   * 
   * @param clazz
   *          the clazz
   */
  void removeFilter(Class<?> clazz) {
    newFilters().remove(clazz);
  }

  /**
   * Clear filters.
   */
  void clearFilters() {
    if (filters == null) {
      return;
    }
    filters.clear();
    filters = null;
  }

  /**
   * Checks for filter.
   * 
   * @param clazz
   *          the clazz
   * 
   * @return true, if successful
   */
  boolean hasFilter(Class<?> clazz) {
    return !reverseFilter & (filters == null ? false : filters.contains(clazz));
  }

  /**
   * Instantiates a new map class tracker.
   * 
   * @param originalClazz
   *          the clazz type even if its a proxy
   * @param realClazz
   *          the clazz type of the original from the target within the proxy
   *          (could be the same as the originalClazz)
   * @param instances
   *          the instances
   */
  MapClassTracker(Class<?> originalClazz, Class<?> realClazz) {
    this.realSourceClazz = realClazz;
    this.originalSourceClazz = originalClazz;
  }

  MapClassTracker(List<Class<?>> clazzes) {
    this.instances = new HashMap<Class<?>, Object>();

    for (Class<?> clazz : clazzes) {
      newInstance(clazz);
    }
  }

  public class PlaceHolder {
    MapClassTracker tracker;
    Class<?> clazz;
  }

  public PlaceHolder newPlaceHolder(Class<?> clazz) {
    PlaceHolder holder = new PlaceHolder();
    holder.tracker = this;
    holder.clazz = clazz;
    return holder;

  }

  /**
   * Initialize the {@link MapClassTracker}.
   */
  public void init() {
    init((Object[]) null);
  }

  /**
   * Initialize the {@link MapClassTracker}.
   * 
   * @param instances
   *          the instances
   */
  public void init(Object... instances) {
    this.instances = new HashMap<Class<?>, Object>();

    if (!pushExistingInstances(instances)) {
      MapClass[] classes = EntityMapper.classes(this.realSourceClazz);
      for (MapClass map : classes) {
        newInstance(map);
      }
    }
  }

  /**
   * Push existing instances.
   * 
   * @param instances
   *          the instances
   * 
   * @return true, if successful
   */
  private boolean pushExistingInstances(Object... instances) {
    boolean flag = false;
    boolean allnull = true;
    if (instances != null) {
      for (Object instance : instances) {
        if (instances == null) {
          continue;
        }
        allnull = false;
        if (instance instanceof Collection<?>) {
          for (Object i : ((Collection<?>) instance)) {
            pushInstance(i);
            flag = true;
          }
        } else {
          pushInstance(instance);
          flag = true;
        }
      }

      if (allnull) {
        return false;
      }
    }
    return flag;
  }

  /**
   * Push instance.
   * 
   * @param o
   *          the o
   */
  private void pushInstance(Object o) {
    Class<?> clazz = o.getClass();
    instances.put(clazz, o);
  }

  /**
   * New instance by clazz type.
   * 
   * @param clazz
   *          the clazz type to instantiate
   */
  private void newInstance(Class<?> clazz) {
    Object o = ReflectionUtility.newInstance(clazz);
    pushInstance(o);
  }

  /**
   * New instance.
   * 
   * @param map
   *          the map
   */
  private void newInstance(MapClass map) {
    Class<?> clazz = map.to();
    newInstance(clazz);
  }

  /**
   * Value.
   * 
   * @return the object
   */
  Object value() {
    List<Object> list = values();
    if (list.size() == 1) {
      return list.get(0);
    }
    return list;
  }

  /**
   * Values.
   * 
   * @return the list< object>
   */
  List<Object> values() {
    if (instances != null && instances.size() > 0) {
      List<Object> list = new ArrayList<Object>();
      for (Object o : instances.values()) {
        list.add(o);
      }
      return list;
    }
    return null;
  }

  /**
   * Gets the.
   * 
   * @param clazz
   *          the clazz
   * 
   * @return the object
   */
  Object get(Class<?> clazz) {
    return instances.get(clazz);
  }

  /**
   * Gets the.
   * 
   * @param clazz
   *          the clazz
   * @param field
   *          the field
   * 
   * @return the object
   */
  Object get(Class<?> clazz, String field) {
    Object object = instances.get(clazz);
    return ReflectionUtility.getValue(field, object);
  }

  /**
   * Map.
   * 
   * @param field
   *          the field
   * @param entity
   *          the entity
   * @param value
   *          the value
   */
  void map(String field, Object entity, Object value) {
    ReflectionUtility.setValue(field, entity, value);
  }

  /**
   * Map.
   * 
   * @param field
   *          the field
   * @param value
   *          the value
   * @param targetClazz
   *          the target clazz
   */
  void map(String field, Object value, Class<?> targetClazz) {
    Object entity = instances.get(targetClazz);
    map(field, entity, value);
  }

  /**
   * Map.
   * 
   * @param field
   *          the field
   * @param value
   *          the value
   */
  void map(String field, Object value) {
    // for (Object entity : instances.values()) {
    for (Class<?> clazz : instances.keySet()) {
      if (!hasFilter(clazz)) {
        Object entity = instances.get(clazz);
        map(field, entity, value);
      }
    }
  }

  /**
   * Gets the original source clazz.
   * 
   * @return the original source clazz
   */
  public Class<?> getOriginalSourceClazz() {
    return originalSourceClazz;
  }

  /**
   * Sets the original source clazz.
   * 
   * @param originalSourceClazz
   *          the new original source clazz
   */
  public void setOriginalSourceClazz(Class<?> originalSourceClazz) {
    this.originalSourceClazz = originalSourceClazz;
  }

  /**
   * Gets the real source clazz.
   * 
   * @return the real source clazz
   */
  public Class<?> getRealSourceClazz() {
    return realSourceClazz;
  }

  /**
   * Sets the real source clazz.
   * 
   * @param realSourceClazz
   *          the new real source clazz
   */
  public void setRealSourceClazz(Class<?> realSourceClazz) {
    this.realSourceClazz = realSourceClazz;
  }
}