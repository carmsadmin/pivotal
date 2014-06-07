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

import com.p5solutions.core.aop.AbstractProxyFactory;

/**
 * TrackStateProxyFactoryImpl: Track state proxy factory implementation. This
 * factory essentially uses the {@link TrackStateProxyMethodHandlerImpl}
 * implementation to dynamically capture method calls.
 * 
 * @author Kasra Rasaee
 * @since 2009-02-11
 * 
 * @see Track annotation for method level tracking
 * @see TrackState annotation for details on how to wrap an object for tracking
 * @see TrackStateProxy for safe proxy casting to get access to proxy special
 *      methods
 * @see TrackStateProxyMethodHandler interface for implementation definition.
 * @see TrackStateProxyMethodHandlerImpl for actual implementation details.
 */
public class TrackStateProxyFactoryImpl extends AbstractProxyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7275690361584986964L;

  /**
   * Instantiates a new track state proxy factory impl.
   */
  public TrackStateProxyFactoryImpl() {
    super();
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#createProxy(java.lang.Class)
   */
  public <T> T createProxy(Class<T> clazz) {
    TrackState entity = clazz.getAnnotation(TrackState.class);
    T proxy = null;
    if (entity != null) {
      proxy = createProxy(clazz, TrackStateProxyMethodHandlerImpl.class);

    } else {
      throw new RuntimeException("Cannot create proxy on a pojo which "
          + "does not define an @TrackState annotation");
    }

    /* return an instance of clazz<t> */
    return proxy;
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#createProxy(java.lang.Class,
   *      java.lang.Object)
   */
  public <T> T createProxy(Class<T> clazz, Object target) {
    T proxy = createProxy(clazz);
    TrackStateProxy trackStateProxy = (TrackStateProxy) proxy;
    trackStateProxy.setTarget(target);
    return proxy;
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#proxyClass()
   */
  @Override
  public Class<?> proxyClass() {
    return TrackStateProxy.class;
  }
}