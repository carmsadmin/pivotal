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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.p5solutions.core.aop.ProxyFactory;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * TrackStateProxyAspect: TrackStateProxy AspectJ handling for target method call. All resulting objects with the
 * {@link WrapTrackStateProxy} annotation will result in proxy based object derived from the
 * {@link TrackStateProxyFactoryImpl}
 * 
 * @author Kasra Rasaee
 * @since 2009-02-11
 * @see TrackStateProxyStategy for strategy on the method result
 * @see TrackStateProxy interface for definition
 * @see TrackStateProxyFactoryImpl for factory implementation on proxying via {@link javassist.util.proxy.ProxyFactory}
 * @see TrackState annotation on proxable objects
 */
@Aspect
public class TrackStateProxyAspect {

	private ProxyFactory trackStateProxyFactory;

	protected final TrackState getTrackState(Object target) {
		// if the target is not null, then search
		// for the appropriate annotation.
		if (target != null) {
			Class<?> clazz = target.getClass();
			return ReflectionUtility.findAnnotation( //
					clazz, TrackState.class);
		}

		// return null if no TrackState annotation was found.
		return null;
	}

	/**
	 * Checks for track state proxy on a given target object.
	 * 
	 * @param target
	 *          the target, must either be instance of {@link TrackStateProxy}
	 * 
	 * @return true, if instance is of type {@link TrackStateProxy}
	 */
	protected final boolean hasTrackStateProxy(Object target) {
		if (target instanceof TrackStateProxy) {
			return true;
		}
		return false;
	}

	/**
	 * Auto proxy returning object.
	 * 
	 * @param target
	 *          the target
	 * @return Instance of the returning object form the proxied.
	 * @throws Throwable
	 *           the throwable
	 */
	protected final Object autoTrack(Object target) throws Throwable {
		TrackState trackState = null;

		if (target != null) {
			Class<?> clazz = target.getClass();
			trackState = ReflectionUtility.findAnnotation( //
					clazz, TrackState.class);
		}

		// if the TrackState annotation exists on the class type
		// and the target object has not already been enhanced via
		// the TrackStateProxyFactory implementation, then proxy it.
		if (target instanceof List<?>) {
			target = listProxy(target);
		} else if (trackState != null && !(target instanceof TrackStateProxy)) {
			// if (target instanceof List<?>) {
			// target = listProxy(target);
			// } else {
			target = proxy(target);
			// }
		}

		return target;
	}

	/**
	 * Proxy a list of objects and return a list of proxies.
	 * 
	 * @param target
	 * @return
	 */
	protected final Object listProxy(Object target) {
		List<?> items = (List<?>) target;
		// if there are objects iterate them
		if (!Comparison.isEmptyOrNull(items)) {
			List<Object> newItems = new ArrayList<Object>();
			// iterate and proxy the objects
			for (Object item : items) {
				// proxy the object
				Object proxy = proxy(item);
				// TODO: fix me... need to check for proxy state first.
				// add the proxy to the list
				newItems.add(proxy);
			}
			// return the proxied list
			return newItems;
		}

		return target;
	}

	/**
	 * Proxy an object via the {@link #trackStateProxyFactory}.
	 * 
	 * @param target
	 *          the target
	 * @return an instance of {@link TrackStateProxy} enhanced.
	 */
	protected final Object proxy(Object target) {
		TrackStateProxy proxy = null;

		// if the target object is already proxied
		if (target instanceof TrackStateProxy) {
			proxy = (TrackStateProxy) target;
		} else if (target != null) {
			// if the target is not ap roxy and target is not null

			// If the target object is a collection
			// then proxy each object within it.
			if (target instanceof Collection<?>) {
				return listProxy(target);
			}

			// Search for track state annotation
			TrackState trackState = getTrackState(target);

			// if TrackState annotation exists on the
			// type, then attempt to proxy the target
			if (trackState != null) {
				// proxy the target if it already hasn't been proxied
				Class<?> clazz = target.getClass();
				proxy = (TrackStateProxy) trackStateProxyFactory.createProxy(clazz, target);
			}
		}

		// set the proxy as being initialized
		if (proxy != null) {
			proxy.setInitialized(true);
			target = proxy;
		}

		// return the proxied object, or existing proxy
		return target;
	}

	/**
	 * Reset proxy dirty flags.
	 * 
	 * @param target
	 *          the target
	 * @return true, if successful
	 */
	protected final boolean resetProxy(Object target) {

		if (target instanceof TrackStateProxy) {
			TrackStateProxy proxy = (TrackStateProxy) target;
			proxy.reset();
			proxy.setInitialized(true);
			return true;
		}

		return false;
	}

	/**
	 * Auto reset proxy state after saving.
	 * 
	 * @param target
	 *          the target
	 * @param args
	 *          the args
	 * @return the object
	 * @throws Throwable
	 *           the throwable
	 */
	protected final Object autoReset(Object target, Object[] args) throws Throwable {
		try {
			reset(target);
			// return the return value from the invoked method
			return target;
		} catch (Exception e) {
			// catch any exceptions and throw it back down the callstack
			throw e;
		}
	}

	/**
	 * Reset join point method arguments and returning target.
	 * 
	 * @param target
	 *          the target
	 * @return true, if successful
	 */
	private boolean reset(Object target) {

		// iterate each method argument and reset if its a proxy
		if (hasTrackStateProxy(target)) {
			TrackStateProxy proxy = (TrackStateProxy) target;
			proxy.reset();
			proxy.setInitialized(true);
			return true;
		}

		return false;
	}

	/**
	 * Auto proxy or reset.
	 * 
	 * @param target
	 *          the target
	 * @param args
	 *          the args
	 * 
	 * @return the object
	 * 
	 * @throws Throwable
	 *           the throwable
	 */
	protected Object autoProxyOrReset(Object target, Object[] args) throws Throwable {
		try {
			if (!reset(target)) {
				target = proxy(target);
			}

			// reset all the arguments
			if (!Comparison.isEmptyOrNull(args)) {
				for (Object arg : args) {
					if (arg != target) {
						reset(arg);
					}
				}
			}

			// return the return value from the invoked method
			return target;
		} catch (Exception e) {
			// catch any exceptions and throw it back down the callstack
			throw e;
		}
	}

	/**
	 * Wrap around a method with the annotation of {@link WrapTrackStateProxy}. If {@link TrackStateProxyStategy#PROXY},
	 * the returning value of this method will be proxied via the {@link #trackStateProxyFactory}.
	 * 
	 * @param pjp
	 *          The proceeding join point, representing the method in question.
	 * @param wrap
	 *          The {@link WrapTrackStateProxy} instance on annotated on the method.
	 * @return Instance of the returning object form the <code>pjp.proceed()</code> proxied via the
	 *         {@link #autoTrack(ProceedingJoinPoint)} if {@link TrackStateProxyStategy#PROXY}
	 * @throws Throwable
	 *           the throwable
	 */
	@Around("@annotation(wrap)")
	public Object trackState(ProceedingJoinPoint pjp, WrapTrackStateProxy wrap) throws Throwable {

		Object target = pjp.proceed();
		Object[] args = pjp.getArgs();

		target = wrap(target, args, wrap);

		return target;
	}

	protected Object wrap(Object target, Object[] args, WrapTrackStateProxy wrap) throws Throwable {

		TrackStateProxyStategy strategy = wrap.value();

		switch (strategy) {
			case PROXY:
				// call the method and proxy the results
				return autoTrack(target);

			case RESET_WHEN_NO_EXCEPTION:
				// call the method and reset the proxy laundry list
				return autoReset(target, args);

			case PROXY_OR_RESET:
				// call the method and proxy the results or reset the proxy if
				// present
				return autoProxyOrReset(target, args);
			case CONTAINER:
				doContainer(target);
		}

		return target;
	}

	/**
	 * Do container. Runs through an object looking for {@link WrapTrackStateProxy} annotions on methods.
	 * 
	 * @param target
	 *          the target
	 * 
	 * @throws Throwable
	 *           the throwable
	 */
	private void doContainer(Object target) throws Throwable {
		if (target == null) {
			return;
		}

		// In case the target is a collection of objects that need to be tracked
		if (target instanceof Collection<?>) {
			for (Object collectionObject : (Collection<?>) target) {
				doContainer(collectionObject);
			}
		}

		/* Find all methods with WrapTrackStateProxy and call #wrap on the object */

		Class<?> clazz = target.getClass();
		List<Method> methods = ReflectionUtility.findMethodsWithAnnotation(clazz, WrapTrackStateProxy.class);

		// iterate each method and recursively walk the containers
		for (Method method : methods) {
			Object innerTarget = ReflectionUtility.getValue(method, target);

			// skip if target object within container is null
			if (innerTarget == null) {
				continue;
			}

			// find the wrap annotation
			WrapTrackStateProxy innerWrap = ReflectionUtility.findAnnotation(method, WrapTrackStateProxy.class);

			// invoke wrap method and set the value back into the cointainer object
			innerTarget = wrap(innerTarget, null, innerWrap);

			// find the setter method based on the getter method
			Method set = ReflectionUtility.findSetterMethod(clazz, method);

			// set the proxied object back into the container object
			ReflectionUtility.setValue(set, target, innerTarget);
		}
	}

	/**
	 * Sets the track state proxy factory.
	 * 
	 * @param trackStateProxyFactory
	 *          the new track state proxy factory
	 */
	public void setTrackStateProxyFactory(ProxyFactory trackStateProxyFactory) {
		this.trackStateProxyFactory = trackStateProxyFactory;
	}
}