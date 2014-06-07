package com.p5solutions.search;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class TableFilterGeneratorResult.
 */
public class TableFilterGeneratorResult implements FilterGeneratorResult {
  
  /** The query. */
  private String query;
  
  /** The parameters. */
  private Map<String, Object> parameters;
  
  /**
   * Gets the query.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }
  
  /**
   * Sets the query.
   *
   * @param query the new query
   */
  public void setQuery(String query) {
    this.query = query;
  }
  
  /**
   * Gets the parameters.
   *
   * @return the parameters
   */
  public Map<String, Object> getParameters() {
    return parameters;
  }
  
  /**
   * Sets the parameters.
   *
   * @param parameters the parameters
   */
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }
  
  /**
   * Adds a parameter for binding
   *
   * @param name the name
   * @param value the value
   */
  public void add(String name, Object value) {
    if (this.parameters == null) {
      this.parameters = new HashMap<String, Object>();
    }
    
    this.parameters.put(name, value);
  }
  
  /**
   * Gets the a parameter for binding
   *
   * @param name the name
   * @return the object
   */
  public Object get(String name) {
    return this.parameters.get(name);
  }
  
}
