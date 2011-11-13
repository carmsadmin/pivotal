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
 * TargetInterceptor: Convenient interface to be implemented as part of the all subclass of
 * {@link AbstractMethodHandler}, used to handle method interceptors in a more sequential approach.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-06
 * @see AbstractMethodHandler for implementation
 * @see MethodHandler
 */
public interface TargetInterceptor {

	/**
	 * Before.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if successful
	 */
	public boolean before(Method method, Object[] args);

	/**
	 * After.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if successful
	 */
	public boolean after(Method method, Object[] args);

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
	 * @return true, if successful
	 */
	public boolean afterThrowing(Method method, Object[] args, Throwable throwable);

	/**
	 * After finally.
	 * 
	 * @param method
	 *          the method
	 * @param args
	 *          the args
	 * 
	 * @return true, if successful
	 */
	public boolean afterFinally(Method method, Object[] args);
}