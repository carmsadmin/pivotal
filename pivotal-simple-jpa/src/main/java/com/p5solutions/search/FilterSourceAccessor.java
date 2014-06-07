package com.p5solutions.search;

public interface FilterSourceAccessor {
  
  String getName();
  
  void setup();

  FilterCriteriaColumn findColumn(String name);
  
  
}
