package com.p5solutions.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.jpa.orm.Binder;
import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.utils.Comparison;

/**
 * The Class FilterUtility.
 */
public class FilterUtility {

  /** The logger. */
  private static Log logger = LogFactory.getLog(EntityUtility.class);

  /** The filters. */
  private Map<Class<? extends Filter>, Filter> filters;

  /** The filter source accessors. */
  private Map<Class<? extends FilterSourceAccessor>, FilterSourceAccessor> filterSourceAccessors;

  /** The entity utility. */
  private EntityUtility entityUtility;

  /** The conversion utility. */
  private ConversionUtility conversionUtility;

  // todo should be a list of types, e.g. package scanner, hbase scanner, and so
  // on and so forth.
  /** The packages. */
  private List<String> packages;

  /** The generators. */
  private Map<Class<? extends FilterGenerator>, FilterGenerator> generators;

  public FilterUtility() {
    super();
  }

  /**
   * Gets the packages.
   * 
   * @return the packages
   */
  public List<String> getPackages() {
    return packages;
  }

  /**
   * Sets the packages.
   * 
   * @param packages
   *          the new packages
   */
  public void setPackages(List<String> packages) {
    this.packages = packages;
  }

  /**
   * Initialize.
   */
  public void initialize() {
    if (packages == null) {
      logger.warn("No packages defined in " + FilterUtility.class + " bean, skipping search for " + FilterSourceAccessor.class);
      return;
    }

    // scan for filter sources
    FilterSourceScanner scanner = new FilterSourceScanner();
    for (String entityPackage : packages) {
      processPackage(scanner, entityPackage);
    }

    // initialize all filter criterias
    for (Filter filter : this.filters.values()) {
      filter.initialize();
    }

  }

  /**
   * Process package.
   * 
   * @param scanner
   *          the scanner
   * @param entityPackage
   *          the entity package
   */
  protected void processPackage(FilterSourceScanner scanner, String entityPackage) {
    try {

      // TODO needs to be non table specific, should be injected?
      for (Class<? extends FilterSource> type : scanner.getComponentClasses(entityPackage)) {
        EntityDetail<?> detail = entityUtility.getEntityDetail(type);
        TableFilterSourceAccessor source = new TableFilterSourceAccessor(detail);

        source.setup();
        addFilterSourceAccessor(type.getName(), source);
      }

    } catch (Exception e) {
      logger.fatal(e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Find filter.
   * 
   * @param <T>
   *          the generic type
   * @param filterClass
   *          the filter class
   * @return the t
   */
  @SuppressWarnings("unchecked")
  public <T> T findFilter(Class<T> filterClass) {
    // TODO log?
    Class<?> clazz = (Class<?>) filterClass;
    return (T) filters.get(clazz);
  }

  /**
   * New filter.
   * 
   * @param <T>
   *          the generic type
   * @param filterClass
   *          the filter class
   * @return the t
   */
  @SuppressWarnings("unchecked")
  public <T extends Filter> T newFilter(Class<T> filterClass) {
    T filter = findFilter(filterClass);
    return (T) filter.newFilter();
  }

  /**
   * Gets the filters map.
   * 
   * @return the filters map
   */
  public Map<Class<? extends Filter>, Filter> getFiltersMap() {
    return filters;
  }

  /**
   * Gets the filters.
   * 
   * @return the filters
   */
  public List<Filter> getFilters() {
    return new ArrayList<Filter>(filters.values());
  }

  /**
   * Sets the filters.
   * 
   * @param filters
   *          the new filters
   */
  public void setFilters(List<Filter> filters) {
    // TODO log?
    if (Comparison.isEmptyOrNull(filters)) {
      return;
    }

    this.filters = new HashMap<Class<? extends Filter>, Filter>();
    for (Filter filter : filters) {
      filter.setFilterUtility(this);
      this.filters.put(filter.getClass(), filter);
    }
  }

  /**
   * Find generator.
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the filter generator
   */
  public <G extends FilterGenerator<T>, T extends FilterGeneratorResult> G findGenerator(Class<? extends FilterGenerator<T>> clazz) {
    G generator = (G)this.generators.get(clazz);
    return generator;
  }
  
  /**
   * Generate filter result to be used at a later point in getting datasets from
   * the appropriate datasource, whatever that may be, RDBMS, HBASE, PIVOTS, so
   * forth.
   * 
   * 
   * @param chain
   *          the chain
   * @param clazz
   *          the clazz
   * @return the filter generator result
   */
  public <T extends FilterGeneratorResult> T generateResult(FilterChain chain, Class<? extends FilterGenerator<T>> clazz) {
    if (Comparison.isEmptyOrNull(generators)) {
      throw new NullPointerException("No generators defined, cannot access generator of type " + clazz + " when nothing has been defined!");
    }

    if (!this.generators.containsKey(clazz)) {
      // TODO dump all generator classes for debugging
      throw new RuntimeException("No generator of type " + clazz + " found in list of generators with size " + generators.size());
    }

    FilterGenerator<T> generator = findGenerator(clazz);
    T result = generator.generateResult(chain);

    return result;
  }

  /**
   * Gets the filter source accessor.
   * 
   * @param <T>
   *          the generic type
   * @param filterSourceClass
   *          the filter source class
   * @return the filter source accessor
   */
  public <T> FilterSourceAccessor getFilterSourceAccessor(Class<T> filterSourceClass) {
    return filterSourceAccessors.get(filterSourceClass);
  }

  /**
   * Adds the filter source accessor.
   * 
   * @param filterSourceClassName
   *          the filter source class name
   * @param source
   *          the source
   */
  public void addFilterSourceAccessor(String filterSourceClassName, FilterSourceAccessor source) {
    if (filterSourceAccessors == null) {
      filterSourceAccessors = new HashMap<Class<? extends FilterSourceAccessor>, FilterSourceAccessor>();
    }

    try {
      @SuppressWarnings("unchecked")
      Class<? extends FilterSourceAccessor> clazz = (Class<? extends FilterSourceAccessor>) Class.forName(filterSourceClassName);
      filterSourceAccessors.put(clazz, source);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Unable to find class name " + filterSourceClassName + " when caching filter sources in " + this.getClass());
    }
  }

  /**
   * Gets the filter source accessors.
   * 
   * @return the filter source accessors
   */
  public Map<Class<? extends FilterSourceAccessor>, FilterSourceAccessor> getFilterSourceAccessors() {
    return filterSourceAccessors;
  }

  /**
   * Sets the filter source accessorss.
   * 
   * @param filterSourceAccessors
   *          the filter source accessors
   */
  public void setFilterSourceAccessorss(Map<Class<? extends FilterSourceAccessor>, FilterSourceAccessor> filterSourceAccessors) {
    this.filterSourceAccessors = filterSourceAccessors;
  }

  /**
   * Sets the entity utility.
   * 
   * @param entityUtility
   *          the new entity utility
   */
  public void setEntityUtility(EntityUtility entityUtility) {
    this.entityUtility = entityUtility;
  }

  /**
   * Gets the conversion utility.
   * 
   * @return the conversion utility
   */
  public ConversionUtility getConversionUtility() {
    return conversionUtility;
  }

  /**
   * Sets the conversion utility.
   * 
   * @param conversionUtility
   *          the new conversion utility
   */
  public void setConversionUtility(ConversionUtility conversionUtility) {
    this.conversionUtility = conversionUtility;
  }

  /**
   * Gets the generators.
   * 
   * @return the generators
   */
  public Map<Class<? extends FilterGenerator>, FilterGenerator> getGeneratorsMap() {
    return generators;
  }

  /**
   * Sets the generators.
   * 
   * @param generators
   *          the generators
   */
  public void setGenerators(List<FilterGenerator> generators) {
    this.generators = new HashMap<Class<? extends FilterGenerator>, FilterGenerator>();
    for (FilterGenerator generator : generators) {
      this.generators.put(generator.getClass(), generator);
    }
  }
}
