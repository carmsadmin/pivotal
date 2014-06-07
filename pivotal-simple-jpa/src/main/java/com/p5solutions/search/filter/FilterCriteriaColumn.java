package com.p5solutions.search.filter;

import com.p5solutions.core.jpa.orm.Binder;
import com.p5solutions.core.json.JsonTransient;

/**
 * Store the filter criteria column
 */
public class FilterCriteriaColumn {
  public String name;
  public Binder binder;
  public String finalBindingPath;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonTransient
  public Binder getBinder() {
    return binder;
  }

  public void setBinder(Binder binder) {
    this.binder = binder;
  }

  public String getFinalBindingPath() {
    return finalBindingPath;
  }

  public void setFinalBindingPath(String finalBindingPath) {
    this.finalBindingPath = finalBindingPath;
  }
}
