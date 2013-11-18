package com.p5solutions.search.filter;

public interface FilterSourceAccessor {
  
  String getName();
  
  void setup();

  FilterCriteriaColumn findColumn(String name);

}
