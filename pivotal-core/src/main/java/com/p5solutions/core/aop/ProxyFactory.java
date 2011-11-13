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

import java.io.Serializable;

/**
 * ProxyFactory: The Interface ProxyFactory, used as part of all proxy factories
 * which inherit the {@link AbstractProxyFactory}.
 * 
 * @author Kasra Robert Rasaee
 * @see AbstractProxyFactory for implementation details
 */
public interface ProxyFactory extends Serializable {

  /**
   * Create a new proxy for the given Class type and use the appropriate
   * MethodInterceptor for the given proxy.
   * 
   * @param clazz
   *          the clazz
   * 
   * @return the T
   */
  public abstract <T> T createProxy(Class<T> clazz);

  /**
   * Creates a new Proxy object with a target object.
   * 
   * @param clazz the clazz
   * @param target the target
   * 
   * @return the T
   */
  public abstract <T> T createProxy(Class<T> clazz, Object target);
  
  /**
   * The class type which the proxy factory creates.
   * 
   * @return the class<?>
   */
  public abstract Class<?> proxyClass();
}