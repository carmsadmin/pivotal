package com.p5solutions.search;

/**
 * The Class FilterValue.
 */
public class FilterValue {
  
  /**
   * Instantiates a new filter value.
   */
  public FilterValue() {
    super();
  }
  
  /** The value. */
  private Object value;
  
  /** The type. */
  private String type;

  /**
   * Instantiates a new filter value.
   *
   * @param value the value
   */
  public FilterValue(Object value) {
    this.value = value;
    this.type = value.getClass().getCanonicalName();
  }
  
  /**
   * Gets the value.
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(Object value) {
    this.value = value;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(String type) {
    this.type = type;
  }
}