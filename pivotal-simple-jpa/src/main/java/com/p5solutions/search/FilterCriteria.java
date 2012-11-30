package com.p5solutions.search;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.FilterChain.Operator;
import com.p5solutions.search.FilterCriteriaCondition.Condition;

/**
 * The Class FilterCriteria.
 */
public class FilterCriteria<STATE extends FilterStorageState> implements Filter<STATE> {

  /** The logger. */
  private static Log logger = LogFactory.getLog(FilterCriteria.class);

  /** The filter id. */
  private Long filterId;

  /** The filter group id. */
  private Long filterGroupId;

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

  /** The presentation name. */
  private String presentationName;

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
  public void addFilter(Filter<? extends STATE> filter, Operator op) {
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
  public Filter<STATE> newFilter() {
    @SuppressWarnings("unchecked")
    Class<FilterCriteria<STATE>> clazz = (Class<FilterCriteria<STATE>>) getClass();
    FilterCriteria<STATE> filter = ReflectionUtility.newInstance(clazz);
    copy(filter);
    return filter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.p5solutions.search.Filter#copy(com.p5solutions.search.Filter)
   */
  @Override
  public void copy(Filter<STATE> filter) {
    FilterCriteria<STATE> criteria = (FilterCriteria<STATE>) filter;
    criteria.column = column;
    criteria.joinColumn = joinColumn;
    criteria.filterId = filterId;
    criteria.filterGroupId = filterGroupId;
    criteria.presentationName = presentationName;
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

  /**
   * @see com.p5solutions.search.Filter#retrieveFilterStorageState()
   */
  @Override
  public STATE retrieveFilterStorageState() {
    throw new NotImplementedException("There is no implementation available for generic "
        + "filter criteria's, pleas emake sure to implement this method on the sub-class of " + FilterCriteria.class);
  }

  /**
   * Gets the filter storage state.
   * 
   * @param state
   *          the state
   * @return the filter storage state
   */
  public STATE retrieveFilterStorageState(STATE state) {
    state.setStateId(getFilterId());
    state.setStateGroupId(getFilterGroupId());
    state.setName(getPresentationName());
    state.setFilterType(this.getClass().getCanonicalName());

    // / serialize;
    try {
      FilterState fs = this.getFilterState();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      getFilterUtility().getJsonSerializer().serialize(fs, baos);
      String json = baos.toString();
      state.setStateData(json);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return state;
  }
  
  /**
   * @see com.p5solutions.search.Filter#initializeFromFilterStorageState(com.p5solutions.search.FilterStorageState)
   */
  public void initializeFromFilterStorageState(STATE state) {
    logger.debug(state);
    
    this.setFilterId(state.getStateId());
    this.setFilterGroupId(state.getStateGroupId());
    this.setPresentationName(state.getName());

    // / serialize;
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(state.getStateData().getBytes());
      FilterState fs = getFilterUtility().getJsonDeserializer().deserialize(FilterState.class, bais);

      ConversionUtility conversionUtility = getFilterUtility().getConversionUtility();
      List<Object> values = fs.toCriteriaValues(conversionUtility);
      this.setValues(values, fs.getCondition());
     
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Gets the filter storage state.
   * 
   * @return the filter storage state
   */
  @Override
  public FilterState getFilterState() {
    final List<FilterValue> list = new ArrayList<FilterValue>();
    FilterCriteriaValue value = getValue().getValue();

    for (final Object v : value.getValues()) {
      list.add(new FilterValue(v));
    }

    Condition condition = getValue().getCondition();
    FilterState fs = new FilterState(condition, list);

    return fs;
  }

  @Override
  public String getFilterType() {
    return this.getClass().getCanonicalName();
  }

  /**
   * @see com.p5solutions.search.FilterDisplay#getFilterId()
   */
  public Long getFilterId() {
    return filterId;
  }

  /**
   * @see com.p5solutions.search.FilterDisplay#setFilterId(java.lang.Long)
   */
  public void setFilterId(Long filterId) {
    this.filterId = filterId;
  }

  /**
   * @see com.p5solutions.search.FilterDisplay#getFilterGroupId()
   */
  public Long getFilterGroupId() {
    return filterGroupId;
  }

  /**
   * @see com.p5solutions.search.FilterDisplay#setFilterGroupId(java.lang.Long)
   */
  public void setFilterGroupId(Long filterGroupId) {
    this.filterGroupId = filterGroupId;
  }

  @Override
  public String getPresentationName() {
    return this.presentationName;
  }

  @Override
  public void setPresentationName(String name) {
    this.presentationName = name;
  }

}