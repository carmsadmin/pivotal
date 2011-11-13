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

/**
 * AbstractMethodHandler: abstraction for method handler/invokers (equivalent to cglib's callback/interceptors). Use
 * this as a base to be implemented in all concrete level byte code method handlers using {@link ProxyFactory}
 * 
 * @author Kasra Rasaee
 * @see Enhancer for details on bytecode enhancement
 * @see AbstractProxyFactory for implementation details
 */
public abstract class AbstractMethodHandler implements MethodHandler, TargetInterceptor {

	/**
	 * Instantiates a new abstract method handler.
	 */
	public AbstractMethodHandler() {
		super();
	}

	/**
	 * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object object, Method method, Method proceed, Object[] args) throws Throwable {
		try {
			Object returnValue = null;
			before(method, args);
			returnValue = method.invoke(object, args);
			after(method, args);
			return returnValue;
		} catch (Throwable t) {
			afterThrowing(method, args, t);
			throw t;
		} finally {
			afterFinally(method, args);
		}
	}

}