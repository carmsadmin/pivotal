/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  Zvjezdan Guzijan
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
package com.p5solutions.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ClassUtils;

/**
 * Component Scanner, for given annotation type.
 * 
 * @author Zvjezdan Guzijan
 */
public class ComponentClassScanner<T> extends ClassPathScanningCandidateComponentProvider {

  public ComponentClassScanner() {
    super(false);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final Collection<Class<? extends T>> getComponentClasses(String basePackage) {
    basePackage = basePackage == null ? "" : basePackage;
    List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
    for (BeanDefinition candidate : findCandidateComponents(basePackage)) {
      try {
        Class cls = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
        classes.add(cls);
      } catch (Throwable e) {
        throw new RuntimeException("Cannot perform scanning for the package " + basePackage, e);
      }
    }
    return classes;
  }

}
