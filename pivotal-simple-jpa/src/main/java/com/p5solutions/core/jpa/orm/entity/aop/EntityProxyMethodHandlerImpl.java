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
import java.lang.reflect.Method;

import com.p5solutions.core.aop.AbstractMethodHandler;
import com.p5solutions.core.utils.ReflectionUtility;

public class EntityProxyMethodHandlerImpl extends AbstractMethodHandler implements EntityProxyMethodHandler, Serializable {

  /** Serialized UID. */
  private static final long serialVersionUID = 5956188733857302089L;

  /** The target. */
  private Object target = null;

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
    } else if ("getTarget".equals(name)) {
      state.value = getTarget();
    } else if ("reset".equals(name)) {
      reset();
    } else {
      state.local = false;
    }

    return state;
  }

  @Override
  public Object writeReplace() {
    EntityProxyWrapper wrapper = new EntityProxyWrapper();
    wrapper.setTarget(this.target);
    return wrapper;
  }

  @Override
  public Object invoke(Object object, Method method, Method proceed, Object[] args) throws Throwable {

    State state = local(method, args);

    if (!state.local) {
      // mark(target, method, args);
      // call the method on the target object with arguments
      return ReflectionUtility.invoke(target, method, args);
    }

    // return local method value;
    return state.value;
  }

  @Override
  public boolean after(Method method, Object[] args) {
    return true;
  }

  @Override
  public boolean afterFinally(Method method, Object[] args) {
    return true;
  }

  @Override
  public boolean afterThrowing(Method method, Object[] args, Throwable throwable) {
    return true;
  }

  @Override
  public boolean before(Method method, Object[] args) {
    return true;
  }

  @Override
  public void reset() {
    // TODO
  }

  @Override
  public Object getTarget() {
    return this.target;
  }

  @Override
  public void setTarget(Object target) {
    this.target = target;
  }

}