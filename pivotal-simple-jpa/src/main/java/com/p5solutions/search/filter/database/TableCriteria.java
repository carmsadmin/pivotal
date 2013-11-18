package com.p5solutions.search.filter.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.json.JsonTransient;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.RandomCharacterGenerator;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.filter.*;

/**
 * 
 * @User: sophanara
 * @Date: 2013-10-10
 * @Time: 4:34 PM
 */
public class TableCriteria implements Criteria {
  /** The logger. */
  private static Log logger = LogFactory.getLog(TableCriteria.class);
  /** The filter utility. */
  private FilterUtility filterUtility;
  /** The column. */
  private FilterCriteriaColumn column;
  /** The join column. */
  private FilterCriteriaColumn joinColumn;
  /** The value. */
  private FilterCriteriaCondition condition;
  /** The filter source accessor class. */
  private Class<? extends FilterSourceAccessor> filterSourceAccessorClass;
  private String id;

  /**
   * Instantiates a new filter criteria.
   */
  public TableCriteria() {
    super();
  }

  @Override
  public void initialize() {
    // To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * 
   * @return
   */
  public TableCriteria newFilter() {
    @SuppressWarnings("unchecked")
    Class<TableCriteria> clazz = (Class<TableCriteria>) getClass();
    TableCriteria filter = ReflectionUtility.newInstance(clazz);
    copy(filter);
    return filter;
  }

  /**
   * 
   * @param filter
   */
  public void copy(TableCriteria filter) {
    TableCriteria criteria = (TableCriteria) filter;
    criteria.column = column;
    criteria.joinColumn = joinColumn;
    criteria.condition = condition;
    criteria.filterSourceAccessorClass = filterSourceAccessorClass;
    criteria.filterUtility = filterUtility;
  }

  /**
   * 
   * @return
   */
  public FilterCriteriaColumn getColumn() {
    return column;
  }

  /**
   * 
   * @param column
   */
  public void setColumn(FilterCriteriaColumn column) {
    this.column = column;
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public String getColumnName() {
    return column.getName();
  }

  /**
   * Gets the join column.
   * 
   * @return the join column
   */
  public FilterCriteriaColumn getJoinColumn() {
    return joinColumn;
  }

  /**
   * Sets the join column.
   * 
   * @param joinColumn
   *          the new join column
   */
  public void setJoinColumn(FilterCriteriaColumn joinColumn) {
    this.joinColumn = joinColumn;
  }

  /**
   *
   * @return
   */
  public FilterCriteriaCondition getCondition() {
    return condition;
  }

  public void setCondition(FilterCriteriaCondition condition) {
    this.condition = condition;
  }

  /**
   * Gets the filter source accessor name.
   * 
   * @return the filter source accessor name
   */
  public String getFilterSourceAccessorName() {
    return getFilterSourceAccessor().getName();
  }

  /**
   * Gets the filter source accessor.
   * 
   * @return the filter source accessor
   */
  @JsonTransient
  public FilterSourceAccessor getFilterSourceAccessor() {
    return filterUtility.getFilterSourceAccessor(this.filterSourceAccessorClass);
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    return this.filterSourceAccessorClass;
  }

  /**
   * 
   * @param filterSourceAccessorClass
   */
  public void setFilterSourceAccessorClass(Class<? extends FilterSourceAccessor> filterSourceAccessorClass) {
    this.filterSourceAccessorClass = filterSourceAccessorClass;
  }

  /**
   * Gets the filter source name.
   * 
   * @return the filter source name
   */
  public String getFilterSourceName() {
    return this.filterSourceAccessorClass.getName();
  }

  /**
   * Sets the filter source name.
   * 
   * @param filterSourceName
   *          the new filter source name
   */
  @SuppressWarnings("unchecked")
  public void setFilterSourceName(String filterSourceName) {
    try {
      this.filterSourceAccessorClass = (Class<? extends FilterSourceAccessor>) Class.forName(filterSourceName);
    } catch (ClassNotFoundException e) {
      return;
    }
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public String getSourceAlias() {
    Class<?> clazz = (Class<?>) this.filterSourceAccessorClass;
    String aliasName = ReflectionUtility.getStaticValue(clazz, "ALIAS");
    return aliasName;
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public FilterUtility getFilterUtility() {
    return filterUtility;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setFilterUtility(com.p5solutions.search. FilterUtility)
   */
  public void setFilterUtility(FilterUtility filterUtility) {
    this.filterUtility = filterUtility;
  }

  /**
   * 
   * @param value
   * @param condition
   */
  public void setValue(Object value, FilterCriteriaCondition.Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(value);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.condition = c;
  }

  /**
   * 
   * @param value1
   * @param value2
   */
  public void setValueBetween(Object value1, Object value2) {
    FilterCriteriaValue v = new FilterCriteriaValue(value1, value2);
    FilterCriteriaCondition c = new FilterCriteriaCondition(FilterCriteriaCondition.Condition.BETWEEN, v);
    this.condition = c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValues(java.util.List,
   * com.p5solutions.search.FilterCriteriaCondition.Condition)
   */
  public void setValues(List<Object> values, FilterCriteriaCondition.Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(values);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.condition = c;
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public FilterState getFilterState() {
    final List<FilterValue> list = new ArrayList<FilterValue>();
    FilterCriteriaValue value = getCondition().getValue();

    for (final Object v : value.getValues()) {
      list.add(new FilterValue(v));
    }

    FilterCriteriaCondition.Condition condition = getCondition().getCondition();
    FilterState fs = new FilterState(condition, list);

    return fs;
  }

  /**
   * 
   * @return
   */
  @JsonTransient
  public String getFilterType() {
    return this.getClass().getCanonicalName();
  }

  /**
   * Get the id of the criteria.
   * 
   * @return the id
   */
  public String getId() {
    if (Comparison.isEmpty(id)) {
      this.id = RandomCharacterGenerator.generate(4);
    }

    return this.id;
  }

  /**
   * 
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }
}
