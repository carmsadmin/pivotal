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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Track: Annotation used on a specific method to track its changes.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-05
 * @see TrackState annotation for proxiable objects
 * @see TrackStateProxyStategy for strategy to be used in conjunction with the
 *      {@link WrapTrackStateProxy} annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {
  ElementType.METHOD
})
@Inherited
public @interface Track {

  /**
   * List of classes to track changes for.
   * 
   * @return the class<?>[]
   */
  Class<?>[] clazz();
}
