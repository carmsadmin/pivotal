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

import com.p5solutions.trackstate.utils.TrackStateUtility;

/**
 * TrackStateIgnoreOnIsNull: Annotation used on a methods which should be
 * skipped when value is null. The utility method
 * {@link TrackStateUtility#areValuesNull(Object)} checks for this annotation,
 * if it exists, it will ignore the method and assume an automatic null.
 * 
 * @author Kasra Rasaee
 * @since 2009-10-01
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface TrackStateIgnoreOnIsNull {
	// TODO rename this to something that makes more sense.
	// Essentially when using the mapPersist, and values of a 
	// specific Track(clazz=>target.class) are null, the class
	// persistence should be skipped.
	
	// This happens when a Value Object is mapped to more than
	// one entity target, for example. UserVO -> User.class, UserDetail.class, and UserLogin.class
	// .. when UserVO is populated with only User and UserLogin information and not 
	// UserDetails (first, middle, and last names), the userId within UserDetails is still set when 
	// using the EntityMapper utility via the @MapClass, @MapClassses annotations.
	// Now, since the value is required for the User and UserLogin entities, a false-positive
	// set of entities are generated. In this case, a UserDetail with only userId set.
	// As such, to ignore entities that are completely null (with the exception of the id field),
	// we need to mark the field/methods that are not to be considered as part of 
	// the validity of the entity, when persisting.
}
