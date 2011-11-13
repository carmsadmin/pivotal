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
package com.p5solutions.core.jpa.orm;

import java.util.List;

/**
 * The Class InterceptorUtility.
 * 
 * @see
 */
public class InterceptorUtility {

  /** The interceptors. */
  private List<Interceptor> interceptors;

  /**
   * Before save.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  public <T> T beforeSave(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        entity = interceptor.beforeSave(entity);
      }
    }
    return entity;
  }

  /**
   * After save.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  public <T> T afterSave(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        entity = interceptor.afterSave(entity);
      }
    }
    return entity;
  }

  /**
   * Before update.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  public <T> T beforeUpdate(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        entity = interceptor.beforeUpdate(entity);
      }
    }
    return entity;
  }

  /**
   * After update.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the t
   */
  public <T> T afterUpdate(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        entity = interceptor.afterUpdate(entity);
      }
    }
    return entity;
  }

  /**
   * Before delete.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the int
   */
  public <T> void beforeDelete(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        interceptor.beforeDelete(entity);
      }
    }
  }

  /**
   * After delete.
   * 
   * @param <T>
   *          the generic type
   * @param entity
   *          the entity
   * @return the int
   */
  public <T> void afterDelete(T entity) {
    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        interceptor.afterDelete(entity);
      }
    }
  }

  /**
   * Gets the interceptors.
   * 
   * @return the interceptors
   */
  public List<Interceptor> getInterceptors() {
    return interceptors;
  }

  /**
   * Sets the interceptors.
   * 
   * @param interceptors
   *          the new interceptors
   */
  public void setInterceptors(List<Interceptor> interceptors) {
    this.interceptors = interceptors;
  }
}
