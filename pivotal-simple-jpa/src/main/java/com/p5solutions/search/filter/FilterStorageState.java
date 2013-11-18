package com.p5solutions.search.filter;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * The Interface SearchCriteriaState.
 * 
 * @author Kasra Rasaee
 * @since 2012-11-27
 */
public interface FilterStorageState extends Serializable {

  Long getStateId();

  /**
   * Sets the search criteria id.
   * 
   * @param stateId
   *          the new search criteria id
   */
  void setStateId(Long stateId);

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

  /**
   * Gets the filter type.
   *
   * @return the filter type
   */
  String getFilterType();

  /**
   * Sets the filter type.
   *
   * @param filterType the new filter type
   */
  void setFilterType(String filterType);

  /**
   * Gets locale resolved description based on the {@link org.springframework.context.i18n.LocaleContextHolder}.
   * 
   * @return the resolved description
   */
  @Transient
  String getResolvedDescription();

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
   * Gets the state data.
   * 
   * @return the state data
   */
  String getStateData();

  /**
   * Sets the state data.
   * 
   * @param stateData
   *          the new state data
   */
  void setStateData(String stateData);
}