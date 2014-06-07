package com.p5solutions.mapping;

import java.lang.reflect.Method;

import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.trackstate.annotation.MapTransient;

/**
 * 
 * @author smin
 * 
 */
public class PropertyMapPredicateImpl implements PropertyMapPredicate {

  /**
   * Ignore the field if MapTransient is found and its "ignored" property is false.<br/>
   * Otherwise (annotation not found or "ignored" is true) it will return true.
   * 
   * @param method
   *          the method
   * 
   * @return true, if successful
   */
  @Override
  public Boolean ignoreProperty(Method method, Object methodValue, Object dst) {
    MapTransient mapTransient = ReflectionUtility.findAnnotation(method, MapTransient.class);
    return mapTransient != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.mapping.PropertyMapPredicate#executeMap(java.lang.reflect.Method, java.lang.Object,
   * java.lang.Object, boolean)
   * 
   * This method will only executed the setter if the destination is different then the value
   */
  @Override
  public Boolean executeMap(Method getter, Method setter, Object propertyValue, Object dst) {

    Object dstValue = ReflectionUtility.getValue(getter, dst);
    Boolean updated = Boolean.FALSE;

    if (propertyValue == null && dstValue != null || propertyValue != null && !propertyValue.equals(dstValue)) {

      ReflectionUtility.setValue(setter, dst, propertyValue);
      updated = Boolean.TRUE;
    }

    return updated;
  }

}
