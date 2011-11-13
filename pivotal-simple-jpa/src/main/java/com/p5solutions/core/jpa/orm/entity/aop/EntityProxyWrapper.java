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

import java.io.Serializable;

public class EntityProxyWrapper implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant factory. */
  private static final EntityProxyFactoryImpl factory = new EntityProxyFactoryImpl();

  /** The target. */
  private Object target = null;

  /**
   * Read resolve.
   * 
   * @return the object
   * 
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public Object readResolve() throws ClassNotFoundException {
    if (target != null) {
      Class<?> targetClazz = target.getClass();
      EntityProxy proxy = (EntityProxy) factory.createProxy(targetClazz, target);
      proxy.setTarget(target);
      return proxy;
    }

    return new NullPointerException("No target object was defined");
  }

  /**
   * Gets the target.
   * 
   * @return the target
   */
  public Object getTarget() {
    return target;
  }

  /**
   * Sets the target.
   * 
   * @param target
   *          the new target
   */
  public void setTarget(Object target) {
    this.target = target;
  }
}