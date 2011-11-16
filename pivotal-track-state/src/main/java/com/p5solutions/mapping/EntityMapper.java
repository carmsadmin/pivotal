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
package com.p5solutions.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.p5solutions.core.jpa.orm.entity.aop.EntityProxy;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.mapping.MapClassTracker.PlaceHolder;
import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;
import com.p5solutions.trackstate.annotation.MapExpand;
import com.p5solutions.trackstate.annotation.MapOrder;
import com.p5solutions.trackstate.annotation.MapProperties;
import com.p5solutions.trackstate.annotation.MapProperty;
import com.p5solutions.trackstate.annotation.MapTransient;
import com.p5solutions.trackstate.aop.TrackStateProxy;

/**
 * EntityMapper: Utility designed specifically to map entities to value objects and value objects to multiple entities.
 * This mapping tool will automatically instantiate entities or value objects when used in conjunction with the
 * {@link MapClass} and {@link MapClasses} type annotations.
 * 
 * @author Kasra Rasaee
 * @since 2009-05-08
 * @see MapClass to define the class type to map the source object to
 * @see MapClasses to define multiple class types to map the source object to.
 * @see MapProperty to define a specific the path a specific field should be mapped to.
 * @see MapProperties to define distinct paths for specific fields within the map to.
 * @note You may specify {@link MapProperty#to()} as part of a hierarchy such as `address.addressLine1` within the
 *       target classes defined as part of {@link MapClass} and {@link MapClasses}
 * @see 'EntityMapperTest' for example on embedded path and reversing of paths using the {@link MapExpand} annotation.
 */
public class EntityMapper {

	/**
	 * The Interface Invoker.
	 */
	protected interface Invoker {
		public void invoke(Class<?> clazz, String path);
	}

	/**
	 * Get a list of {@link MapProperty} annotations defined on a specific method.
	 * 
	 * @param method
	 *          The method in question to search against
	 * 
	 * @return A list of {@link MapProperty} annotations defined on the property as part of the {@link MapProperties}
	 *         annotations. Note that if only {@link MapProperty} is defined then a list with a single annotation will be
	 *         returned.
	 */
	protected static final MapProperty[] properties(Method method) {
		MapProperties properties = ReflectionUtility.findAnnotation(method, MapProperties.class);

		// if properties was not defined, look for a single property.
		if (properties == null) {
			MapProperty property = ReflectionUtility.findAnnotation(method, MapProperty.class);

			if (property != null) {
				// return a list of properties (only one in this case)
				return new MapProperty[] { property };
			}

			// otherwise return null
			return null;
		}

		return properties.value();
	}

	/**
	 * Get a list of {@link MapClass} annotations defined on a specific class type.
	 * 
	 * @param clazz
	 *          The clazz in question to search against
	 * 
	 * @return A list of {@link MapClass} annotations defined on the property as part of the {@link MapClasses}
	 *         annotations. Note that if only {@link MapClass} is defined then a list with a single annotation will be
	 *         returned.
	 */
	protected static final MapClass[] classes(Class<?> clazz) {
		MapClasses classes = ReflectionUtility.findAnnotation(clazz, MapClasses.class);

		// if classes was not defined, look for a single property.
		if (classes == null) {
			MapClass map = ReflectionUtility.findAnnotation(clazz, MapClass.class);

			if (map != null) {
				// return a list of properties (only one in this case)
				return new MapClass[] { map };
			}

			// otherwise return null
			return null;
		}

		return classes.map();
	}

	/**
	 * Run.
	 * 
	 * @param value
	 *          the value
	 * @param target
	 *          the target
	 * @param path
	 *          the path
	 */
	protected static final void run(Object value, Object target, String path) {
		// determine the path and create the
		// necessary objects by the dot separation
		Class<?> clazz = target.getClass();
		Object pushInstance = null;

		int pos = path.indexOf('.', 1);

		String field = path;
		// TODO make sure that we want to ignore creation of embedded objects
		// when the source value is null, for example, address.country == null
		// should we create target.setAddress(new Address())? then
		// set the address.country = null? this could be problematic for persistence
		// if the value of the entire embedded object is null, as such throwing
		// a javax.persistence validation exception and or database null constraint
		if (pos > 0 && value != null) {
			field = path.substring(0, pos);
			String next = path.substring(pos + 1);

			Method push = ReflectionUtility.findGetterMethod(clazz, field);
			if (push != null) {
				Class<?> pushType = push.getReturnType();
				pushInstance = ReflectionUtility.getValue(field, target);

				// if the instance has not been initialized then set it
				if (pushInstance == null) {
					pushInstance = ReflectionUtility.newInstance(pushType);
				}

				// recursion until we set the appropriate value
				run(value, pushInstance, next);

				// set teh instance to the current target
				ReflectionUtility.setValue(field, target, pushInstance);
			}
		} else {
			ReflectionUtility.setValue(field, target, value);
		}
	}

	/**
	 * Iterate each propety and invoke the anonymous method callback. Use the
	 * {@link EntityMapper#iterateProperties(Method, Object, MapClassTracker)} method to invoke this method.
	 * 
	 * @param method
	 *          The source method with the {@link MapProperties} and or {@link MapProperty}
	 * @param value
	 *          The value instance invoke the callback with.
	 * @param invoker
	 *          The callback
	 */
	protected static final void iterateProperties(Method method, Invoker invoker) {

		// get a list of properties if any
		MapProperty[] properties = properties(method);

		// iterate each property
		for (MapProperty property : properties) {
			// get the target field name if any?
			String path = property.to();

			// if the field name is null or empty then build
			// the field name based upon the source method name
			if (Comparison.isEmpty(path)) {
				// build the field name from method name
				path = ReflectionUtility.buildFieldName(method);
			}

			// invoke the anonymous method defined
			invoker.invoke(property.clazz(), path);
		}
	}

	/**
	 * Gather all the {@link MapProperty} annotations from the method and iterate each of them, selectively map to
	 * instances within the tracker the classes that have been defined as part of the {@link MapProperties}
	 * 
	 * @param method
	 *          The source method, used to get the actual annotations
	 * @param value
	 *          The source value.
	 * 
	 * @param tracker
	 *          An instance of the tracker for the selective hierarchy within the source object.
	 * @return <code>true</code> if there were properties and the iteration was successful, otherwise <code>false</code>
	 */
	protected static final boolean iterateProperties(Method method, final Object value, final MapClassTracker tracker) {

		if (iteratable(method)) {
			iterateProperties(method, new Invoker() {
				@Override
				public void invoke(Class<?> clazz, String path) {
					tracker.map(path, value, clazz);
				}
			});
			return true;
		}

		return false;
	}

	/**
	 * Attempt simple map.
	 * 
	 * @param call
	 *          the call
	 * @param source
	 *          the source
	 * @param method
	 *          the method
	 * 
	 * @return true, if successful
	 */
	protected static final boolean attemptSimpleMap(final MapClassTracker tracker, Object source, Method method) {

		// reflectively get the value from the source object
		final Object value = ReflectionUtility.getValue(method, source);

		if (iteratable(method)) {
			iterateProperties(method, new Invoker() {
				@Override
				public void invoke(Class<?> clazz, String path) {
					// get the target object by the clazz
					Object target = tracker.get(clazz);

					if (target == null) {
						// TODO should this really be ignored?
						// something to do with the laundry list and trackstate
						// check for correctness.
						return;
					}
					// assign the value to the target path defined as part of
					// @MapProperty(to=xyz)
					run(value, target, path);
				}
			});
			return true;
		}

		return false;
	}

	/**
	 * Attempt to directly map the value defined by invoking the method on the source object against all fields within all
	 * instances within the tracker instance.
	 * 
	 * @param tracker
	 *          the tracker which holds all instances defined as part of the {@link MapClass} and {@link MapClasses}
	 *          annotation.
	 * @param source
	 *          the source object which is being mapped from
	 * @param method
	 *          the method which holds will be invoked to get the physical object being mapped
	 */
	protected static final void attemptDirectMap(MapClassTracker tracker, Object source, Method method) {
		// build the field name from method name
		String field = ReflectionUtility.buildFieldName(method);

		// reflectively get the value from the source object
		Object value = ReflectionUtility.getValue(method, source);

		// map
		tracker.map(field, value);
	}

	/**
	 * Does the class type have any of the following annotations {@link MapClass} or {@link MapClasses}
	 * 
	 * @param clazz
	 *          the clazz to search against
	 * 
	 * @return <code>true</code> , if successfully found annotations otherwise <code>false</code>
	 */
	@SuppressWarnings("unchecked")
	protected static final boolean mappable(Method method) {

		// get the return value of the method.
		Class<?> clazz = method.getReturnType();

		// if the method has @MapExpand or the return type of the Method has
		// @MapClass or @MapClasses, then the object is expandable or walkable
		return ReflectionUtility.hasAnyAnnotation(method, MapExpand.class)
				|| ReflectionUtility.hasAnyAnnotation(clazz, MapClass.class, MapClasses.class);
	}

	/**
	 * Builds the map class tracker either as a new instance defined by the {@link MapClass} annotations defined on the
	 * clazz type and targets argument, or uses a pre-existing {@link MapClassTracker} defined as part of the first index
	 * of the targets argument.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param targets
	 *          the targets
	 * 
	 * @return the map class tracker instance, either new or pre-existing.
	 */
	protected static final PlaceHolder buildMapClassTracker(Object source, Object... targets) {

		Class<?> originalClazz = source.getClass();
		Class<?> realClazz = originalClazz;
		// get the source clazz type

		MapClassTracker tracker = null;
		if (TrackStateProxy.class.isAssignableFrom(originalClazz)) {
			TrackStateProxy proxy = (TrackStateProxy) source;
			realClazz = proxy.getTarget().getClass();

			List<Class<?>> clazzes = proxy.getAffectedClasses();
			if (Comparison.isEmptyOrNull(clazzes)) {
				return null;
			}

			tracker = new MapClassTracker(clazzes);
			tracker.realSourceClazz = realClazz;
			tracker.originalSourceClazz = originalClazz;

		} else if (targets != null && targets.length == 1 && targets[0] instanceof MapClassTracker) {
			tracker = (MapClassTracker) targets[0];
		} else {
			tracker = new MapClassTracker(originalClazz, realClazz);
			// initialize the tracker
			tracker.init(targets);
		}

		return tracker.newPlaceHolder(realClazz);
	}

	/**
	 * Does the method have any {@link MapProperty} or {@link MapProperties} annotations.
	 * 
	 * @param method
	 *          The method to search against
	 * @return <code>true</code> if method contains the annotations, otherwise <code>false</code>
	 */
	@SuppressWarnings("unchecked")
	protected static final boolean iteratable(Method method) {
		return ReflectionUtility.hasAnyAnnotation(method, MapProperties.class, MapProperty.class);
	}

	/**
	 * Recursively walk an object and call the {@link #map(Object, Object...)} method using the source->value as the
	 * starting point. The value returned as part of the next recursive call to map should result in an end result which
	 * will map back to the target object defined as part of the {@link MapClass} or {@link MapClasses} attribute.
	 * 
	 * @param value
	 *          The source value, most likely a complex object and not a primitive like typed object such as an Integer or
	 *          String. In most cases, objects defined with the {@link MapClass} and or {@link MapClasses} annotation are
	 *          expanded or walked recursively.
	 * @param method
	 *          The source method in question, usually where the source value is going to be walked against.
	 * @param tracker
	 *          The tracker which holds all instances related to the current recursion, unless source method is specified
	 *          with {@link MapExpand}, which in that case the previous tracker will be used against the next recursion
	 *          call.
	 * @return An instance of a complex object defined by the return type defined as part of the target classes method
	 *         return.
	 */
	protected static final Object walkexpand(Object value, Method method, MapClassTracker tracker) {
		// Does the source method have a MapExpand annotation?
		MapExpand expand = ReflectionUtility.findAnnotation(method, MapExpand.class);

		Object ret = null;

		// Check
		if (expand == null) {
			// If MapExpand annotation does exist. Recursively
			// walk the value using a new map tracker instance
			ret = map(value);
		} else {
			// reverse the filter
			tracker.reverseFilter(true);

			// add the classes to use only.
			tracker.addFilters(expand.clazz());

			// If MapExpand annotation does exist, then use the same
			// tracker reference expand the value to the appropriate
			// `tracker.instance`. Recursively walk the value
			ret = map(value, tracker);

			// set the filter back to normal.
			tracker.reverseFilter(false);

			// clear all filters
			tracker.clearFilters();
		}

		// if the return value is still null, use the
		// value, maybe it will map directly?
		if (ret == null) {
			ret = value;
		}

		// return the value returned by the #map(...)
		return ret;
	}

	/**
	 * Use the method name and build a field name from it, using that field name map the value against all instances
	 * defined within the tracker#instances.
	 * 
	 * @param method
	 * @param value
	 * @param tracker
	 */
	protected static final void mapByAutoField(Method method, Object value, MapClassTracker tracker) {

		// build the field name from a method, getAddressLine1 => addressLine1
		String field = ReflectionUtility.buildFieldName(method);

		// map against value against all instances within the tracker.
		tracker.map(field, value);
	}

	/**
	 * Attempt complex map.
	 * 
	 * @param tracker
	 *          the call
	 * @param method
	 *          the method
	 * @param source
	 *          the source
	 * 
	 * @return true, if successful
	 */
	protected static final boolean attemptComplexMap(MapClassTracker tracker, Method method, Object source) {

		// does the class type have MapClass or MapClasses annotation?
		if (!mappable(method)) {
			// if not then return quickly as this is not a walk-able object.
			return false;
		}

		// if above statement is true (mappable = true)
		// then invoke the method getting its value such that
		// we can walk its properties and map them to the destination.
		Object sourceValue = ReflectionUtility.getValue(method, source);

		// Walk and (if needed) expand the results to the appropriate class
		// instance defined within a new or pre-existing (MapExpand0 tracker.
		Object value = walkexpand(sourceValue, method, tracker);

		// iterate each property and set the target value.
		// if no properties were found, then map the value
		// directly by building the field name by the method.
		if (!iterateProperties(method, value, tracker)) {
			mapByAutoField(method, value, tracker);
		}

		return true;
	}

	/**
	 * Ignore.
	 * 
	 * @param method
	 *          the method
	 * 
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	protected static final boolean ignore(Method method) {
		return ReflectionUtility.hasAnyAnnotation(method, MapTransient.class);
	}

	/**
	 * Map. Object source can be a List of objects too.
	 * 
	 * @param source
	 *          the source
	 * 
	 * @return the object
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T map(Object source) {
		if (source instanceof List) {
			List<Object> ret = new ArrayList<Object>();
			for (Object o : (List<Object>) source) {
				ret.add(map(o, (Object[]) null));
			}
			return (T) ret;
		} else {
			Object ret = map(source, (Object[]) null);
			if (ret instanceof List) {
				List<Object> list = (List<Object>) ret;
				Collections.sort(list, ordered);
			}
			return (T) ret;
		}
	}

	static Comparator<Object> ordered = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (o1 == null) {
				return -1;
			}

			if (o2 == null) {
				return 1;
			}

			Class<?> clazz1 = o1.getClass();
			Class<?> clazz2 = o2.getClass();

			MapOrder m1 = ReflectionUtility.findAnnotation(clazz1, MapOrder.class);
			MapOrder m2 = ReflectionUtility.findAnnotation(clazz2, MapOrder.class);

			if (m1 == null && m2 == null) {
				return 0;
			}

			if (m1 == null) {
				return -1;
			}

			if (m2 == null) {
				return 1;
			}

			Short s1 = Short.valueOf(m1.order());
			return s1.compareTo(m2.order());
		}

	};

	/**
	 * Map a source object to the destination object, the objects are usually of the same type.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param src
	 *          the src
	 * @param dst
	 *          the dst
	 * @return the t
	 */
	public static final <T> T mapOneToOne(T src, T dst) {
		return mapOneToOne(src, dst, false);
	}

	/**
	 * Map a source object to the destination object, the objects are usually of the same type. Note that this one-to-one
	 * mapping DOES NOT recursively copy values from the source to destination, but simply iterates through the source
	 * object and sets values within the destination object.
	 * 
	 * @param <T>
	 *          the generic type
	 * @param src
	 *          the src
	 * @param dst
	 *          the dst
	 * @param ignoreMethodNotFound
	 *          if <code>true</code> throws a RuntimeException of the method is not found
	 * @return the destination object of type T
	 */
	public static final <T> T mapOneToOne(T src, T dst, boolean ignoreMethodNotFound) {
		if (src == null) {
			throw new NullPointerException("Source object cannot be null when mapping source to target of same type.");
		}

		if (dst == null) {
			throw new NullPointerException("Destination object cannot be null when mapping source to target of same type.");
		}

		Class<?> srcClazz = src.getClass();
		Class<?> dstClazz = dst.getClass();
		/*
		 * if (!Comparison.isEqual(srcClazz, dstClazz)) { throw new RuntimeException("Class type for source type " +
		 * srcClazz + " and destination type " + dstClazz + "do not match"); }
		 */
		List<Method> methods = ReflectionUtility.findGetMethodsWithNoParams(srcClazz);
		for (Method method : methods) {
			if (ignore(method)) {
				continue;
			}

			Method setter = ReflectionUtility.findSetterMethod(dstClazz, method);

			if (setter == null) {
				if (!ignoreMethodNotFound) {
					throw new RuntimeException("No setter method found for getter method " + method.getName()
							+ " defined in class type " + dstClazz);
				}
				continue;
			}

			Object value = ReflectionUtility.getValue(method, src);
			ReflectionUtility.setValue(setter, dst, value);
		}

		return dst;
	}

	/**
	 * Map a source object to target classes defined as part of the source type defined by {@link MapClass} and
	 * {@link MapClasses}.
	 * 
	 * @param source
	 *          physical instance of the object being mapped from
	 * @param targets
	 *          pre-existing target objects to use to map to if any.
	 * 
	 * @return An instance of the object being mapped to, or a list of objects mapped to based on what was defined as part
	 *         of the annotation {@link MapClasses} defined on the source type.
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T map(Object source, Object... targets) {

		// extract the target object from
		// the proxy if it is proxied.
		if (source instanceof EntityProxy) {
			// the persistence layer is the simple-jpa implementation
			source = ((EntityProxy) source).getTarget();
		} // check if proxied at all ??? there is no hib.

		// if the source object is null then there
		// is no point in walking the graph, exit!
		if (source == null) {
			return null;
		}

		// build an instance tracker based on the @MapClass
		// and @MapClasses or if there are pre-existing targets
		PlaceHolder holder = buildMapClassTracker(source, targets);

		// if the build mapper returned null
		// then exit, as there is nothing to
		// map against.
		if (holder == null) {
			return null;
		}

		MapClassTracker tracker = holder.tracker;

		// get the real source class even if its a proxy class
		Class<?> clazz = holder.clazz;

		// iterate each method with no parameters and starts with `is` or `get`.
		for (Method method : ReflectionUtility.findGetMethodsWithNoParams(clazz)) {
			// should ignore this method?
			if (ignore(method)) {
				continue;
			}

			// attempt a map based on complex type
			if (!attemptComplexMap(tracker, method, source)) {
				// if that complex mapping failed, as in it was not necessary
				// then try to map the property either one to one or walking a path,
				// e.g. `mapping source->addressLine1` => `target->address.addressLine1`
				if (!attemptSimpleMap(tracker, source, method)) {
					// if there were no properties, then attempt to map directly by the
					// method names.
					attemptDirectMap(tracker, source, method);
				}
			}
		}

		return (T) tracker.value();
	}

}
