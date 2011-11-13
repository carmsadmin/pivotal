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
package com.p5solutions.core.jpa.orm.transaction;

import java.util.Stack;

import com.p5solutions.core.jpa.orm.EntityPersisterImpl;

/**
 * The Class PersistenceProvider: Simple class defining a thread local which
 * will hold a single instance of {@link PersistenceContext}, in turn holds
 * information about all persisted entities within a single Transaction. This
 * works in conjunction with the {@link SimpleJPATransactionManager}
 * 
 * @author Kasra Rasaee
 * @since 2011-02-04
 * 
 * @see EntityPersisterImpl#saveOrUpdate(Object)
 * @see SimpleJPATransactionManager
 * @see PersistenceContext
 */
public class PersistenceProvider {

  /** The local. */
  protected static volatile ThreadLocal<Stack<PersistenceContext>> local = new ThreadLocal<Stack<PersistenceContext>>();

  /**
   * Reset.
   */
  public static void reset() {
    if (local.get() != null) {
      local.get().pop();
      if (local.get().isEmpty()) {
        local.remove();
      }
    }
  }

  /**
   * Gets the.
   * 
   * @return the persistence context
   */
  public static PersistenceContext get() {
    return local.get() != null ? local.get().lastElement() : null;
  }

  /**
   * Sets the.
   * 
   * @param context
   *          the context
   */
  public synchronized static void set(PersistenceContext context) {
    if (local.get() == null) {
      local.set(new Stack<PersistenceContext>());
    }
    local.get().push(context);
  }

}
