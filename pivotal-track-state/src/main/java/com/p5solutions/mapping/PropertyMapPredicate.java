package com.p5solutions.mapping;

import java.lang.reflect.Method;

/**
 * Interface for a predicate class that defined the logic when an Entity property can be map or not.
 * 
 * @author smin
 * 
 */
public interface PropertyMapPredicate {

  /**
   * Should we ignore this method
   * 
   * @param method
   *          the getMethod to be execute to retrieve the property from the destination object
   * @param dst
   *          Destination object that we to set the new value
   * @param methodValue
   *          the value to set in the destination object
   * @return
   */
  Boolean ignoreProperty(Method method, Object methodValue, Object dst);

  /**
   * 
   * @param method
   * @param value
   * @param dst
   * @return
   */
  Boolean executeMap(Method method, Method setter, Object value, Object dst);

}
