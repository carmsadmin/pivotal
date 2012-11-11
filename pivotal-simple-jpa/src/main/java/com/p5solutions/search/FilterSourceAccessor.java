package com.p5solutions.search;

public interface FilterSourceAccessor {
  void setup();

  FilterCriteriaColumn findColumn(String name);
}
