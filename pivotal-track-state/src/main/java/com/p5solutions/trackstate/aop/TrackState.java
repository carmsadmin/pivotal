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
 * TrackState: Annotation to be used on a class type, such that
 * {@link TrackStateProxyAspect} can recognize it as being a proxiable class
 * type. Must be used in conjunction with the {@link TrackState},
 * {@link TrackStateProxyStategy} and {@link WrapTrackStateProxy} annotations.
 * 
 * Note: If auto proxing is required via {@link TrackStateProxyAspect} and
 * {@link WrapTrackStateProxy} then the method in question, must be in a Spring
 * managed bean. Such that target invocation can be captured.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-05
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {
  ElementType.TYPE
})
@Inherited
public @interface TrackState {
  //
}
