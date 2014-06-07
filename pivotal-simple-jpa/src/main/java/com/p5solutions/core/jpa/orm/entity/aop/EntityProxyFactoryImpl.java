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
package com.p5solutions.core.jpa.orm.entity.aop;

import javax.persistence.Entity;

import com.p5solutions.core.aop.AbstractProxyFactory;

public class EntityProxyFactoryImpl extends AbstractProxyFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new entity proxy factory impl.
   */
  public EntityProxyFactoryImpl() {
    super();
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#createProxy(java.lang.Class)
   */
  @Override
  public <T> T createProxy(Class<T> clazz) {
    Entity entity = clazz.getAnnotation(Entity.class);
    T proxy = null;
    if (entity != null) {
      proxy = createProxy(clazz, EntityProxyMethodHandlerImpl.class);
    } else {
      throw new RuntimeException("Cannot create proxy on a pojo which does not define an @TrackState annotation");
    }

    /* return an instance of clazz<t> */
    return proxy;
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#createProxy(java.lang.Class,
   *      java.lang.Object)
   */
  @Override
  public <T> T createProxy(Class<T> clazz, Object target) {
    T proxy = createProxy(clazz);
    EntityProxy entityProxy = (EntityProxy) proxy;
    entityProxy.setTarget(target);
    return proxy;
  }

  /**
   * @see com.p5solutions.core.aop.ProxyFactory#proxyClass()
   */
  @Override
  public Class<?> proxyClass() {
    return EntityProxy.class;
  }
}