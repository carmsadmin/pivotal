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
package com.p5solutions.trackstate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Transient;

/**
 * MapClass:
 * 
 * @author Kasra Rasaee
 * @since 2009-02-24
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
public @interface MapTransient {
  String reason() default "no reason";

  /**
   * Default is <code>true</code>, sometimes we may want to use
   * {@link Transient} annotation such that we do not persist the data, but also
   * do not want to get warning messages about not using {@link MapTransient},
   * because we DO WANT to map the data to the value object; in this scenario,
   * you should set the {@link MapTransient} annotation and set the
   * {@link #ignored()} to <code>false</code>
   * 
   * @return true, if successful
   */
  boolean ignored() default true;
}
