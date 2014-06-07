package com.p5solutions.search.filter;

import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.json.JsonProperty;
import com.p5solutions.core.json.JsonTransient;
import com.p5solutions.search.filter.FilterCriteriaCondition.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FilterState.
 */
public class FilterState {

  /** The condition. */
  private Condition condition;
  
  /** The values. */
  private List<FilterValue> values;
  
  /**
   * Instantiates a new filter state.
   */
  public FilterState() {
    super();
  }
  /**
   * Instantiates a new filter state.
   *
   * @param condition the condition
   * @param values the values
   */
  public FilterState(Condition condition, List<FilterValue> values) {
    this.condition = condition;
    this.values = values;
  }
  
  /**
   * Gets the condition.
   *
   * @return the condition
   */
  public Condition getCondition() {
    return condition;
  }
  
  /**
   * Sets the condition.
   *
   * @param condition the new condition
   */
  public void setCondition(Condition condition) {
    this.condition = condition;
  }
  
  /**
   * Gets the values.
   *
   * @return the values
   */
  @JsonProperty(FilterValue.class)
  public List<FilterValue> getValues() {
    return values;
  }
  
  /**
   * Sets the values.
   *
   * @param values the new values
   */
  public void setValues(List<FilterValue> values) {
    this.values = values;
  }
   
  
  @JsonTransient
  public List<Object> toCriteriaValues(ConversionUtility conversionUtility) {
    // reinitialize values // TODO clean this up, should be done via the fs??
    List<Object> values = new ArrayList<Object>();
    for (FilterValue fv : this.getValues()) {
      Object converted = conversionUtility.convert(fv.getValue(), fv.getType());
      values.add(converted);
    }
    
    return values;
  }
}