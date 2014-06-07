package com.p5solutions.search;

import org.springframework.core.type.filter.AssignableTypeFilter;

import com.p5solutions.core.utils.ComponentClassScanner;

public class FilterSourceScanner extends ComponentClassScanner<FilterSource> {
  public FilterSourceScanner() {
    super();
    addIncludeFilter(new AssignableTypeFilter(FilterSource.class));
  }

}