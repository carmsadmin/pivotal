package com.p5solutions.search.filter;

import com.p5solutions.core.utils.ComponentClassScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class FilterSourceScanner extends ComponentClassScanner<FilterSource> {
  public FilterSourceScanner() {
    super();
    addIncludeFilter(new AssignableTypeFilter(FilterSource.class));
  }

}