package com.p5solutions.search;

public class FilterCriteriaCondition {
  enum Condition {
    IN,
    LIKE,
    EQ,
    NEQ,
    LESS,
    LESS_EQ,
    GREATER,
    GREATER_EQ,
    REGEXP,
    BETWEEN
  }
  
  private Condition condition;
  
  private FilterCriteriaValue value;
  
  public FilterCriteriaCondition(Condition condition) {
    this.condition = condition;
  }
  
  public FilterCriteriaCondition(Condition condtition, FilterCriteriaValue value) {
    this.condition = condtition;
    this.value = value;
  }
  
  public Condition getCondition() {
    return condition;
  }
  
  public void setCondition(Condition condtition) {
    this.condition = condtition;
  }
 
  public FilterCriteriaValue getValue() {
    return value;
  }
  
  public void setValue(FilterCriteriaValue value) {
    this.value = value;
  }
}
