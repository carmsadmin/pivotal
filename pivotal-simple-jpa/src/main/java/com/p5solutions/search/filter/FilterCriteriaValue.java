package com.p5solutions.search.filter;

import com.p5solutions.core.json.JsonTransient;
import com.p5solutions.core.utils.Comparison;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * FilterCriteriaValue:.
 * 
 * @author krasaee
 */
public class FilterCriteriaValue {

  /** The values. */
  private List<Object> values;

  /**
   * Instantiates a new filter criteria value.
   *
   * @param values
   *          the values
   */
  public FilterCriteriaValue(List<Object> values) {
    this.values = values;
  }

  /**
   * Instantiates a new filter criteria value.
   *
   * @param values
   *          the values
   */
  public FilterCriteriaValue(Object... values) {
    this.values = new ArrayList<Object>();
    for (Object value : values) {
      this.values.add(value);
    }
  }

  /**
   * Gets the actual value.
   *
   * @return the actual value
   */
  @JsonTransient
  public Object getActualValue() {
    if (Comparison.isEmptyOrNull(values)) {
      return null;
    }

    if (values.size() > 1) {
      // TODO output all values
      throw new RuntimeException("There are too many results within the array, expected only one value, got " + values.size() + " on filter " + this.getClass());
    }

    return values.get(0);
  }

  /**
   * Gets the values.
   *
   * @return the values
   */
  public List<Object> getValues() {
    return values;
  }

  /**
   * Sets the values.
   *
   * @param values
   *          the new values
   */
  public void setValues(List<Object> values) {
    this.values = values;
  }

  /**
   * Gets the value.
   *
   * @param index
   *          the index
   * @return the value
   */
  public Object getValue(int index) {
    return this.values.get(index);
  }
}