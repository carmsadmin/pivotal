package com.p5solutions.search;

/**
 * The Interface FilterDisplay.
 * 
 * @author Kasra Rasaee
 * @since 2012-11-27
 */
public interface FilterDisplay {
  
  /**
   * Gets the filter id.
   *
   * @return the filter id
   */
  Long getFilterId();
  
  /**
   * Sets the filter id.
   *
   * @param id the new filter id
   */
  void setFilterId(Long id);
  
  /**
   * Gets the filter group id.
   *
   * @return the filter group id
   */
  Long getFilterGroupId();
  
  /**
   * Sets the filter group id.
   *
   * @param filterGroupId the new filter group id
   */
  void setFilterGroupId(Long filterGroupId);
  
  /**
   * Gets the presentation name.
   *
   * @return the presentation name
   */
  String getPresentationName();
  
  /**
   * Sets the presentation name.
   *
   * @param name the new presentation name
   */
  void setPresentationName(String name);
  
  /**
   * Gets the resolved description.
   *
   * @return the resolved description
   */
  String getResolvedDescription();
}
