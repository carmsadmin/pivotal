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

import java.util.HashMap;
import java.util.Map;

import com.p5solutions.core.jpa.orm.EntityPersisterImpl;

/**
 * The Class PersistenceContext. A plain old java object that can hold
 * information about which entities have been persisted within a given
 * transaction context.
 * 
 * @author Kasra Rasaee
 * @since 2011-02-04
 * 
 * @see PersistenceProvider
 * @see SimpleJPATransactionManager
 * @see EntityPersisterImpl#saveOrUpdate(Object)
 */
public class PersistenceContext {

  /**
   * The Enum EntityState: Defines the last action of the entity within the
   * transaction context
   */
  public enum EntityState {

    /** The UPDATE. */
    UPDATE,
    /** The SAVE. */
    SAVE,
    /** The DELETE. */
    DELETE,
    /** The MERGE. */
    MERGE,
    /** The SELECTED. */
    SELECTED
  }

  /**
   * The Class EntityPresence: class that holds information about a given entity
   * that was persisted
   */
  protected class EntityPresence {

    /** The entity. */
    public Object entity;

    /** The entity state. */
    public EntityState entityState;
  }

  /** The entity presence. */
  private Map<String, EntityPresence> entityPresence;

  /**
   * Gets the entity presence.
   * 
   * @return the entity presence
   */
  public Map<String, EntityPresence> getEntityPresence() {
    return entityPresence;
  }

  /**
   * Sets the states.
   * 
   * @param states
   *          the states
   */
  public void setStates(Map<String, EntityPresence> states) {
    this.entityPresence = states;
  }

  /**
   * Exists.
   * 
   * @param entityKey
   *          the entity key
   * @return true, if successful
   */
  public boolean exists(String entityKey) {
    if (entityPresence != null) {
      return entityPresence.containsKey(entityKey);
    }
    return false;
  }

  /**
   * Gets the presence.
   * 
   * @param entityKey
   *          the entity key
   * @return the presence
   */
  protected EntityPresence getPresence(String entityKey) {
    if (entityPresence != null) {
      return this.entityPresence.get(entityKey);
    }
    return null;
  }

  /**
   * Update a given entity within an instance of {@link PersistenceContext}.
   * 
   * @param entityKey
   *          the entity key
   * @param entity
   *          the entity
   * @param entityState
   *          the entity state
   */
  public void update(String entityKey, Object entity, EntityState entityState) {
    if (exists(entityKey)) {
      EntityPresence presence = getPresence(entityKey);
      presence.entity = entity;
      presence.entityState = entityState;
    } else {
      if (entityPresence == null) {
        entityPresence = new HashMap<String, PersistenceContext.EntityPresence>();
      }
      EntityPresence presence = new EntityPresence();
      presence.entity = entity;
      presence.entityState = entityState;

      // add the presence
      getEntityPresence().put(entityKey, presence);
    }
  }

  /**
   * Gets the entity state. For example the last state of the entity might have
   * been {@link EntityState#SAVE}.
   * 
   * @param entityKey
   *          the entity key
   * @return the entity state
   */
  public EntityState getEntityState(String entityKey) {
    if (exists(entityKey)) {
      EntityPresence presence = getEntityPresence().get(entityKey);
      return presence.entityState;
    }
    return null;
  }
}