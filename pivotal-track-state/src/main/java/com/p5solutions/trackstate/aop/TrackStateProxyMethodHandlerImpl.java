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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.aop.AbstractMethodHandler;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * TrackStateProxyMethodCallbackImpl: This callback should be used with the {@link TrackStateProxyFactoryImpl}
 * implementation. It allows tracking of state changes for a given {@link TrackState} annotated pojo.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-06
 * @see TrackState for annotation details on class type
 * @see Track for annotation details on class method level
 * @see TrackStateProxy for castable proxy interface.
 * @see TrackStateLaundry for details on affected classes by method
 * @see TrackStateProxyAspect for details on how to implement Auto {@link TrackStateProxy} on spring beans with methods
 *      with the {@link WrapTrackStateProxy} annotation.
 */
public class TrackStateProxyMethodHandlerImpl extends AbstractMethodHandler implements TrackStateProxyMethodHandler,
		Serializable {

	/** Serialized UID. */
	private static final long serialVersionUID = -5976925854341471575L;

	/** The logger. */
	private static Log logger = LogFactory.getLog(TrackStateProxyFactoryImpl.class);

	/** The initialized. */
	private boolean initialized = false;

	/** The target. */
	private Object target = null;

	/** The dirties. */
	private Map<String, TrackStateLaundry> trackStateLaundryMap = new HashMap<String, TrackStateLaundry>();

	private class State {
		Object value;
		boolean local;
	}

	/**
	 * Local method invocation handling
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return the state
	 */
	@SuppressWarnings("unchecked")
	private State local(Method method, Object[] args) {
		String name = method.getName();

		State state = new State();
		state.local = true;
		state.value = null;

		if ("setTarget".equals(name)) {
			setTarget(args[0]);
		} else if ("finalize".equals(name)) {
			try {
				super.finalize();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else if ("writeReplace".equals(name)) {
			state.value = writeReplace();
		} else if ("setTrackStateLaundryMap".equals(name)) {
			setTrackStateLaundryMap((Map<String, TrackStateLaundry>) args[0]);
		} else if ("setInitialized".equals(name)) {
			setInitialized((Boolean) args[0]);
		} else if ("getTrackStateLaundryMap".equals(name)) {
			state.value = getTrackStateLaundryMap();
		} else if ("getAffectedClasses".equals(name)) {
			state.value = getAffectedClasses();
		} else if ("getTrackStateLaundryListForClass".equals(name)) {
			state.value = getTrackStateLaundryListForClass((Class<?>) args[0]);
		} else if ("getTrackStateLaundryList".equals(name)) {
			state.value = getTrackStateLaundryList();
		} else if ("getTarget".equals(name)) {
			state.value = getTarget();
		} else if ("isInitialized".equals(name)) {
			state.value = isInitialized();
		} else if ("reset".equals(name)) {
			reset();
		} else {
			state.local = false;
		}

		return state;
	}

	@Override
	public Object writeReplace() {
		TrackStateProxyWrapper wrapper = new TrackStateProxyWrapper();
		wrapper.setTarget(this.target);
		wrapper.setInitialized(this.initialized);
		wrapper.setTrackStateLaundryMap(this.trackStateLaundryMap);
		return wrapper;
	}

	/**
	 * @see com.p5solutions.core.aop.AbstractMethodHandler#invoke(java.lang.Object, java.lang.reflect.Method,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object object, Method method, Method proceed, Object[] args) throws Throwable {
		State state = local(method, args);
		if (!state.local) {
			// mark laundry list
			mark(target, method, args);
			// call the method on the target object with arguments
			return ReflectionUtility.invoke(target, method, args);
		}
		// return local method value;
		return state.value;
	}

	/**
	 * Do laundry. essentially checks to see if the values being set are different than the values of the current, as well
	 * as checks against values which were once dirty but now have gone back to its original state, making it no longer
	 * dirty.
	 * 
	 * @param methodName
	 *          the method name to track against.
	 * @param trackClazzez
	 *          list of clazzes taken from the {@link Track} annotation
	 * @param oldV
	 *          the old value represented by invoking the get method
	 * @param newV
	 *          the new value passed in upon {@link #intercept(Object, Method, Object[], MethodProxy)}
	 */
	private void doLaundry(String methodName, Class<?>[] trackClazzez, Object oldV, Object newV) {

		// check for changes
		if (!Comparison.isEqual(oldV, newV)) {
			// get laundry list if already one for this method name
			TrackStateLaundry laundry = trackStateLaundryMap.get(methodName);

			// if no laundry was found then create one
			if (laundry == null) {
				// new tracker for the method
				laundry = new TrackStateLaundry();
				laundry.setOriginal(oldV); // set the original value
			} else if (Comparison.isEqual(newV, laundry.getOriginal())) {
				// if the new value is equal to the original value
				// then remove the dirt instance from the laundry list
				trackStateLaundryMap.remove(methodName);
				return;
			}

			// set it to dirty
			laundry.setDirty(true);
			laundry.setMethod(methodName);

			// put it in the laundry list
			trackStateLaundryMap.put(methodName, laundry);

			// Add any tracked clazz if not already tracked before
			for (Class<?> trackClazz : trackClazzez) {
				trackClasses(laundry, trackClazz);
			}
		}
	}

	/**
	 * Track classes.
	 * 
	 * @param laundry
	 *          the laundry
	 * @param trackClazz
	 *          the track clazz
	 * 
	 * @return the list< class<?>>
	 */
	private void trackClasses(TrackStateLaundry laundry, Class<?> trackClazz) {
		// get the list of current affected clazzes
		List<Class<?>> classes = laundry.getAffected();
		// if its null then create a new list.
		if (classes == null) {
			classes = new ArrayList<Class<?>>();
			laundry.setAffected(classes);
		}

		// already contains the tracked class
		if (!classes.contains(trackClazz)) {
			// if not then add it to the list
			classes.add(trackClazz);
		}
	}

	/**
	 * Check whether the data within the target is dirty or not, if so, then set the dirty flag for later persistence.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * @param target
	 *          the target
	 * 
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 * @throws InvocationTargetException
	 *           the invocation target exception
	 */
	@SuppressWarnings("unchecked")
  private boolean mark(Object target, Method method, Object[] args) throws IllegalAccessException,
			InvocationTargetException {

		// quick return if there are no arguments nor is the
		// argument length equal to 1 this suggests that we
		// are not calling a typical getter/setter method
		if (args == null || args.length != 1) {
			return false;
		}

		// if the method does not start with set, then ignore it.
		//if (!method.getName().startsWith("set")) {
		//	return false;
		//}
		
		// quick return if the proxy has not finished initializing.
		if (!isInitialized()) {
			return false;
		}

		// if the method is marked with a TrackStateTransient annotation, then ignore it. 
		if (ReflectionUtility.hasAnyAnnotation(method, TrackStateTransient.class)) {
			return false;
		}
		
		Class<?> targetClazz = target.getClass();

		// get the get-method name
		String name = ReflectionUtility.buildGetterName(method);

		// extract the actual get method by using the method name which was invoked
		Method get = ReflectionUtility.findGetterMethod(targetClazz, method);

		// if a get method exists then attempt tracking
		if (get != null) {
			// see if there is an annotation on the method with Track.class
			Track track = ReflectionUtility.findAnnotation(get, Track.class);

			// if a tracker does exist then get the list of classes it should track.
			if (track != null) {
				// get list of clazzes from the get-method annotation
				// @Track(clazz={???})
				Class<?>[] trackClazzez = track.clazz();

				// the target objects current value
				Object oldV = ReflectionUtility.invoke(target, get);
				Object newV = args[0];

				// do laundry on the value and classes
				doLaundry(name, trackClazzez, oldV, newV);

				return true;
			}
		} else {
			// get the get method name from the set method

			logger.warn("No get method [" + name + " defined for set method " + method.getName() + " under class "
					+ targetClazz + " with " + TrackState.class + " annotation");

		}

		return false;
	}

	/**
	 * After.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if after
	 * 
	 * @see com.p5solutions.core.aop.TargetInterceptor#after(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public boolean after(Method method, Object[] args) {
		return true;
	}

	/**
	 * After finally.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if after finally
	 * 
	 * @see com.p5solutions.core.aop.TargetInterceptor#afterFinally(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public boolean afterFinally(Method method, Object[] args) {
		return true;
	}

	/**
	 * After throwing.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * @param throwable
	 *          the throwable
	 * 
	 * @return true, if after throwing
	 * 
	 * @see com.p5solutions.core.aop.TargetInterceptor#afterThrowing(java.lang.reflect.Method, java.lang.Object[],
	 *      java.lang.Throwable)
	 */
	@Override
	public boolean afterThrowing(Method method, Object[] args, Throwable throwable) {
		return true;
	}

	/**
	 * Before.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if before
	 * 
	 * @see com.p5solutions.core.aop.TargetInterceptor#before(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public boolean before(Method method, Object[] args) {
		return true;
	}

	/**
	 * Checks if is initialized.
	 * 
	 * @return true, if checks if is initialized
	 * 
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#isInitialized()
	 */
	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#setInitialized(boolean)
	 */
	@Override
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#getTrackStateLaundryMap()
	 */
	@Override
	public Map<String, TrackStateLaundry> getTrackStateLaundryMap() {
		return this.trackStateLaundryMap;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#getTrackStateLaundryList()
	 */
	@Override
	public Collection<TrackStateLaundry> getTrackStateLaundryList() {
		return this.trackStateLaundryMap.values();
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#getTrackStateLaundryList(java.lang.Class)
	 */
	@Override
	public List<TrackStateLaundry> getTrackStateLaundryListForClass(Class<?> forClazz) {

		List<TrackStateLaundry> list = new ArrayList<TrackStateLaundry>();
		for (TrackStateLaundry laundry : getTrackStateLaundryList()) {
			if (hasClass(laundry, forClazz)) {
				list.add(laundry);
			}
		}

		return list.size() == 0 ? null : list;
	}

	/**
	 * Checks for class existence in a {@link TrackStateLaundry} instance.
	 * 
	 * @param laundry
	 *          the laundry
	 * @param hasClazz
	 *          the has clazz
	 * 
	 * @return true, if successful
	 */
	private boolean hasClass(TrackStateLaundry laundry, Class<?> hasClazz) {
		// return laundry.affected.contains(hasClass);
		if (laundry != null && laundry.affected != null) {
			for (Class<?> clazz : laundry.affected) {
				if (clazz.equals(hasClazz)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reset.
	 * 
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#reset()
	 */
	@Override
	public void reset() {
		trackStateLaundryMap.clear();
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#getTarget()
	 */
	@Override
	public Object getTarget() {
		return this.target;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#setTrackStateLaundryMap(java.util.Map)
	 */
	@Override
	public void setTrackStateLaundryMap(Map<String, TrackStateLaundry> trackStateLaundryMap) {
		this.trackStateLaundryMap = trackStateLaundryMap;
	}

	/**
	 * @see com.p5solutions.trackstate.aop.TrackStateProxy#getAffectedClasses()
	 */
	@Override
	public List<Class<?>> getAffectedClasses() {
		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		for (TrackStateLaundry laundry : getTrackStateLaundryList()) {
			List<Class<?>> affected = laundry.getAffected();
			for (Class<?> clazz : affected) {
				if (!clazzes.contains(clazz)) {
					clazzes.add(clazz);
				}
			}
		}

		return clazzes.size() == 0 ? null : clazzes;
	}

}