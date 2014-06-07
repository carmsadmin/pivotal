package com.p5solutions.search.filter;

/**
 *
 * User: sophanara Date: 2013-10-08
 * Time: 12:08 AM
 */
public interface Criteria extends FilterElement {

  /**
   * Initialize
   */
  void initialize();

    /**
     * 
      * @return
     */
  Criteria newFilter();

    /**
   * 
   * @param filterUtility
   */
  void setFilterUtility(FilterUtility filterUtility);
}
