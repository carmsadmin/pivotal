package com.p5solutions.search;

import java.util.ArrayList;
import java.util.List;

import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.FilterChain.Operator;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterCriteria.
 */
public class FilterCriteria implements Filter {

  /** The filter utility. */
  private FilterUtility filterUtility;

  /** The column. */
  private FilterCriteriaColumn column;

  /** The join column. */
  private FilterCriteriaColumn joinColumn;

  /** The value. */
  private FilterCriteriaCondition value;

  // TODO switch these to class types vs. names.
  /** The filter name. */
  private String filterName;

  /** The filter source accessor class. */
  private Class<? extends FilterSourceAccessor> filterSourceAccessorClass;

  /** The junctions. */
  private List<FilterJunctionComposite> filterJunctions;

  /**
   * Instantiates a new filter criteria.
   */
  public FilterCriteria() {
    super();
  }

  /**
   * @see com.p5solutions.search.Filter#getFilterJunctions()
   */
  @Override
  public List<FilterJunctionComposite> getFilterJunctions() {
    return this.filterJunctions;
  }

  /**
   * @see com.p5solutions.search.Filter#setFilterJunctions(java.util.List)
   */
  @Override
  public void setFilterJunctions(List<FilterJunctionComposite> junctions) {
    this.filterJunctions = junctions;
  }

  /**
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.Filter,
   *      com.p5solutions.search.FilterChain.Operator)
   */
  @Override
  public void addFilter(Filter filter, Operator op) {
    FilterJunctionComposite composite = new FilterJunctionComposite(filter, op);
    addFilter(composite);
  }

  /**
   * @see com.p5solutions.search.Filter#addFilter(com.p5solutions.search.FilterJunctionComposite)
   */
  @Override
  public void addFilter(FilterJunctionComposite composite) {
    if (filterJunctions == null) {
      filterJunctions = new ArrayList<FilterJunctionComposite>();
    }

    filterJunctions.add(composite);
  }

  /**
   * @see com.p5solutions.search.Filter#newFilter()
   */
  @Override
  public Filter newFilter() {
    @SuppressWarnings("unchecked")
    Class<FilterCriteria> clazz = (Class<FilterCriteria>) getClass();
    FilterCriteria filter = ReflectionUtility.newInstance(clazz);
    copy(filter);
    return filter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#copy(com.p5solutions.search.Filter)
   */
  @Override
  public void copy(Filter filter) {
    FilterCriteria criteria = (FilterCriteria) filter;
    criteria.column = column;
    criteria.value = value;
    criteria.filterName = filterName;
    criteria.filterSourceAccessorClass = filterSourceAccessorClass;
    criteria.filterUtility = filterUtility;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#initialize()
   */
  @Override
  public void initialize() {
    // TODO logger.debug("nothing to initialize on base filter criteria");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getColumn()
   */
  public FilterCriteriaColumn getColumn() {
    return column;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setColumn(com.p5solutions.search.
   * FilterCriteriaColumn)
   */
  public void setColumn(FilterCriteriaColumn column) {
    this.column = column;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getColumnName()
   */
  @Override
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

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getValue()
   */
  public FilterCriteriaCondition getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValue(com.p5solutions.search.
   * FilterCriteriaCondition)
   */
  public void setValue(FilterCriteriaCondition value) {
    this.value = value;
  }

  /**
   * Gets the filter source accessor name.
   * 
   * @return the filter source accessor name
   */
  @Override
  public String getFilterSourceAccessorName() {
    return getFilterSourceAccessor().getName();
  }

  /**
   * Gets the filter source accessor.
   * 
   * @return the filter source accessor
   */
  public FilterSourceAccessor getFilterSourceAccessor() {
    return filterUtility.getFilterSourceAccessor(this.filterSourceAccessorClass);
  }

  /**
   * Gets the filter name.
   * 
   * @return the filter name
   */
  public String getFilterName() {
    return filterName;
  }

  /**
   * Sets the filter name.
   * 
   * @param filterName
   *          the new filter name
   */
  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterSourceAccessorClass()
   */
  public Class<? extends FilterSourceAccessor> getFilterSourceAccessorClass() {
    return this.filterSourceAccessorClass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.p5solutions.search.Filter#setFilterSourceAccessorClass(java.lang.Class)
   */
  @Override
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
   * @see com.p5solutions.search.Filter#getSourceAlias()
   */
  @Override
  public String getSourceAlias() {
    Class<?> clazz = (Class<?>) this.filterSourceAccessorClass;
    String aliasName = ReflectionUtility.getStaticValue(clazz, "ALIAS");
    return aliasName;
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

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#getFilterUtility()
   */
  public FilterUtility getFilterUtility() {
    return filterUtility;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setFilterUtility(com.p5solutions.search.
   * FilterUtility)
   */
  public void setFilterUtility(FilterUtility filterUtility) {
    this.filterUtility = filterUtility;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValue(java.lang.Object,
   * com.p5solutions.search.FilterCriteriaCondition.Condition)
   */
  public void setValue(Object value, Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(value);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.value = c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValueBetween(java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void setValueBetween(Object value1, Object value2) {
    FilterCriteriaValue v = new FilterCriteriaValue(value1, value2);
    FilterCriteriaCondition c = new FilterCriteriaCondition(Condition.BETWEEN, v);
    this.value = c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#setValues(java.util.List,
   * com.p5solutions.search.FilterCriteriaCondition.Condition)
   */
  public void setValues(List<Object> values, Condition condition) {
    FilterCriteriaValue v = new FilterCriteriaValue(values);
    FilterCriteriaCondition c = new FilterCriteriaCondition(condition, v);
    this.value = c;
  }

}