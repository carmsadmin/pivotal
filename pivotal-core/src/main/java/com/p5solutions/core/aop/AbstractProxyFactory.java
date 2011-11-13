/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
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
package com.p5solutions.core.aop;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.aopalliance.intercept.MethodInterceptor;

import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * AbstractProxyFactory: An abstract factory for creating proxy objects. Use this as a base for all proxy factories.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-06
 * @see ProxyFactory interface for definition details
 */
public abstract class AbstractProxyFactory implements com.p5solutions.core.aop.ProxyFactory {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new abstract proxy factory.
	 */
	public AbstractProxyFactory() {
		super();
	}

	/**
	 * Gets the handler instance.
	 * 
	 * @param handlerClazz
	 *          the handler clazz
	 * 
	 * @return the handler instance
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MethodHandler> T getHandlerInstance(Class<T> handlerClazz) {

		/* if the callback class it null then throw an exception */
		if (handlerClazz == null) {
			throw new RuntimeException("Method handler class cannot be null");
		}

		MethodHandler handler = null;

		try {
			/* create a new instance of the handler (interceptor) */
			handler = handlerClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return (T) handler;
	}

	/**
	 * Check for similar method signatures
	 * 
	 * @param proxyInterfaceClazz
	 * @param pojoClazz
	 */
	protected void checkForSameMethods(Class<?> proxyInterfaceClazz, Class<?> pojoClazz) {

		// throw exception if not an interface class
		throwIsNotInterface(proxyInterfaceClazz);

		// iterate each method within the interface class
		for (Method method : ReflectionUtility.findAllMethods(proxyInterfaceClazz)) {

			// get some of the method attributes
			String methodName = method.getName();
			Class<?>[] paramClazzes = method.getParameterTypes();

			// check the pojo class for the same method signature
			Method pojoMethod = ReflectionUtility.findMethod( //
					pojoClazz, //
					methodName, //
					paramClazzes);

			// if the pojo method is returned then throw an exception
			if (pojoMethod != null) {
				throwSameMethod(proxyInterfaceClazz, //
						pojoClazz, //
						methodName, //
						paramClazzes);
			}
		}
	}

	private void throwIsNotInterface(Class<?> proxyInterfaceClazz) {
		if (!proxyInterfaceClazz.isInterface()) {
			throw new RuntimeException("Argument proxyInterfaceClazz must be of "
					+ "type interface not concrete or abstract class!");
		}
	}

	/**
	 * Throw method exists exception
	 * 
	 * @param proxyInterfaceClazz
	 * @param pojoClazz
	 * @param methodName
	 * @param paramClazzes
	 */
	private void throwSameMethod(Class<?> proxyInterfaceClazz, Class<?> pojoClazz, String methodName,
			Class<?>[] paramClazzes) {

		// exception message
		String ex = "Similar signature Method [" + methodName + "( ";

		// append the parameter types
		if (!Comparison.isEmptyOrNull(paramClazzes)) {
			for (Class<?> paramClazz : paramClazzes) {
				ex += ", " + paramClazz.getCanonicalName();
			}
		}

		// append the classes the method is defined in.
		ex += ")] cannot be defined in both " + pojoClazz + " and in " + proxyInterfaceClazz;

		// throw a runtime exception.
		throw new RuntimeException(ex);
	}

	/**
	 * Creates a new AbstractProxy object.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param handlerClazz
	 *          the handler clazz
	 * 
	 * @return the T
	 */
	protected <T> T createProxy(Class<T> clazz, Class<? extends MethodHandler> handlerClazz) {
		MethodHandler handler = getHandlerInstance(handlerClazz);
		try {
			return createProxy(clazz, handler);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create a proxy (handler) for " + clazz, e);
		}
	}

	/**
	 * Creates a new proxy object by the class type, and register the method handler of choice.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param handler
	 *          the handler
	 * 
	 * @return the T representing the proxy instance subclassed from the clazz passed in
	 */
	@SuppressWarnings("unchecked")
	protected <T> T createProxy(Class<T> clazz, MethodHandler handler) throws IllegalAccessException,
			InstantiationException {
		// check for duplicate methods within the proxy interface
		// and the pojo class (the class type being sub-classed)
		checkForSameMethods(proxyClass(), clazz);

		// instantiate new javassist proxy factory (equivalent to cglib's enhancer)
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(clazz);
		proxyFactory.setInterfaces(new Class[] { proxyClass() });

		// set the method handler/invoker (equivalent to cglib's callback/interceptor)
		Class<ProxyObject> proxyClass = proxyFactory.createClass();
		ProxyObject proxy = proxyClass.newInstance();
		proxy.setHandler(handler);

		return (T) proxy;
	}

	/**
	 * Creates a new AbstractProxy object.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param callback
	 *          the callback
	 * 
	 * @return the class< t>
	 */
	@SuppressWarnings("unchecked")
	protected <T> Class<T> createProxyClass(Class<T> clazz, MethodInterceptor callback) {
		Class<T> newClazz = clazz;

		try {
			// TODO: what is this in javassist? Enhancer.registerCallbacks(newClazz, new Callback[] { callback });
			ProxyFactory proxyFactory = new ProxyFactory();
			proxyFactory.setSuperclass(clazz);
			proxyFactory.setInterfaces(new Class[] { proxyClass() });
			newClazz = proxyFactory.createClass();

		} finally {
			// TODO: what is this in javassist? Enhancer.registerCallbacks(newClazz, null);
		}

		return newClazz;
	}

}
