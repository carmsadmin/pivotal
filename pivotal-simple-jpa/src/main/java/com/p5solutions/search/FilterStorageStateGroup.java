package com.p5solutions.search;

import java.io.Serializable;

/**
 * The Interface FilterStorageStateGroup.
 * 
 * @author Kasra Rasaee
 * @since 2012-11-27
 */
public interface FilterStorageStateGroup extends Serializable {

  /**
   * Gets the state group id.
   * 
   * @return the state group id
   */
  Long getStateGroupId();

  /**
   * Sets the state group id.
   * 
   * @param stateGroupId
   *          the new state group id
   */
  void setStateGroupId(Long stateGroupId);

  /**
   * Gets the name.
   * 
   * @return the name
   */
  String getName();

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  void setName(String name);
}