package com.p5solutions.trackstate.utils;

import java.util.ArrayList;
import java.util.List;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;

public class ReflectionUtility extends com.p5solutions.core.utils.ReflectionUtility { 
  /**
   * Find all {@link MapClass} annotations for a given class, dig through {@link MapClasses} annotation as well.
   * 
   * @param clazz
   *          the clazz
   * @return the list
   */
  public static List<MapClass> findMapClasses(Class<?> clazz) {
    // TODO cache the result of this method

    List<MapClass> mapClasses = new ArrayList<MapClass>();

    MapClass mc = findAnnotation(clazz, MapClass.class);
    if (mc != null) {
      mapClasses.add(mc);
    }

    MapClasses mcs = findAnnotation(clazz, MapClasses.class);
    if (mcs != null) {
      for (MapClass mapClass : mcs.map()) {
        mapClasses.add(mapClass);
      }
    }

    return mapClasses.size() > 0 ? mapClasses : null;
  }

  public static MapClass findMapClassPrimary(Class<?> clazz) {
    List<MapClass> mapClasses = findMapClasses(clazz);
    if (mapClasses == null) {
      String exc = "There are no " + MapClass.class + " or " + MapClasses.class + " annotations defined on class type "
          + clazz;

      throw new NullPointerException(exc);
    }

    MapClass returnMapClass = null;
    boolean primaryFound = false;
    for (MapClass mc : mapClasses) {
      if (mc.primary()) {
        if (primaryFound) {
          throw new RuntimeException("Cannot define multiple primary map classes on given class type " + clazz);
        }
        primaryFound = true;
        returnMapClass = mc;
      }
    }
    return returnMapClass;
  }

}
