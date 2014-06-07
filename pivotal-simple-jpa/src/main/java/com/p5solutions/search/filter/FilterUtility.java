package com.p5solutions.search.filter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.p5solutions.core.jpa.orm.ConversionUtility;
import com.p5solutions.core.jpa.orm.EntityDetail;
import com.p5solutions.core.jpa.orm.EntityUtility;
import com.p5solutions.core.json.JsonDeserializer;
import com.p5solutions.core.json.JsonSerializer;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.search.filter.database.TableFilterSourceAccessor;

/**
 * Created with IntelliJ IDEA. User: sophanara Date: 2013-10-09 Time: 2:38 PM To change this template use File |
 * Settings | File Templates.
 */
public class FilterUtility {
  /** The logger. */
  private static Log logger = LogFactory.getLog(EntityUtility.class);
  /** The default charset. */
  private Charset defaultCharset = Charset.forName("UTF-8");
  /** The filters. */
  private Map<Class<? extends Criteria>, Criteria> filtersCriteria;
  /** The filter source accessors. */
  private Map<Class<? extends FilterSourceAccessor>, FilterSourceAccessor> filterSourceAccessors;
  /** The entity utility. */
  private EntityUtility entityUtility;
  /** The conversion utility. */
  private ConversionUtility conversionUtility;
  /** The json serializer. */
  private JsonSerializer jsonSerializer;
  /** The json deserializer. */
  private JsonDeserializer jsonDeserializer;
  /** The filter storage. */

  private FilterChainStorage<FilterChainStorageState, FilterChainStorageStateGroup> filterChainStorage;

  // todo should be a list of types, e.g. package scanner, hbase scanner, and so
  // on and so forth.
  /** The packages. */
  private List<String> packages;
  /** The generators. */
  private Map<Class<? extends FilterGenerator>, FilterGenerator> generators;

  /**
   * Instantiates a new filter utility.
   */
  public FilterUtility() {
    super();
  }

  public static boolean isRightBracket(FilterElement filterElement) {
    if (filterElement instanceof Bracket) {
      return filterElement.equals(Bracket.RIGHT);
    }
    return false;
  }

  public static boolean isLeftBracket(FilterElement filterElement) {
    if (filterElement instanceof Bracket) {
      return filterElement.equals(Bracket.LEFT);
    }
    return false;
  }

  public static boolean isOperator(FilterElement operator) {
    return operator instanceof Operator;
  }

  public static String getPrintFilterList(List<FilterElement> filterElementList) {
    StringBuilder buffer = new StringBuilder();

    if (filterElementList != null) {
      for (FilterElement filterElement : filterElementList) {
        buffer.append(filterElement).append(" ");
      }
    }

    return buffer.toString();
  }

  public static boolean isOperand(FilterElement filterElement) {
    if (filterElement instanceof Criteria) {
      return true;
    }
    if (filterElement instanceof Values) {
      return true;
    }
    return false;
  }

  public static boolean isOperatorNOT(FilterElement filterElement) {
    if (filterElement instanceof Operator) {
      return filterElement.equals(Operator.NOT);
    }
    return false;
  }

  public static boolean isOperatorAND(FilterElement filterElement) {
    if (filterElement instanceof Operator) {
      return filterElement.equals(Operator.AND);
    }
    return false;
  }

  public static boolean isOperatorOR(FilterElement filterElement) {
    if (filterElement instanceof Operator) {
      return filterElement.equals(Operator.OR);
    }
    return false;
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
      logger.warn("No packages defined in " + FilterUtility.class + " bean, skipping search for "
          + FilterSourceAccessor.class);
      return;
    }

    // scan for filter sources
    FilterSourceScanner scanner = new FilterSourceScanner();
    for (String entityPackage : packages) {
      processPackage(scanner, entityPackage);
    }

    // initialize all filter criterias
    for (Criteria criteria : this.filtersCriteria.values()) {
      criteria.initialize();
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
  protected <T extends Criteria> T findFilterCriteria(Class<T> filterClass) {
    // TODO log?
    Class<T> clazz = (Class<T>) filterClass;
    if (clazz != null) {
      return (T) filtersCriteria.get(filterClass);
    }

    throw new NullPointerException("Filter of " + filterClass + " not found in list of injected filter types");
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
  public <T extends Criteria> T newCriteria(Class<T> filterClass) {
    T filter = findFilterCriteria(filterClass);
    return (T) filter.newFilter();
  }

  /**
   * Gets the filters map.
   * 
   * @return the filters map
   */
  public Map<Class<? extends Criteria>, Criteria> getFiltersCriteriaMap() {
    return filtersCriteria;
  }

  /**
   * Gets the filters.
   * 
   * @return the filters
   */
  public List<Criteria> getFiltersCriteria() {
    return new ArrayList<Criteria>(filtersCriteria.values());
  }

  /**
   * Sets the filters.
   * 
   * @param filtersCriteria
   *          the new filters
   */
  public void setFiltersCriteria(List<Criteria> filtersCriteria) {
    // TODO log?
    if (Comparison.isEmptyOrNull(filtersCriteria)) {
      return;
    }

    this.filtersCriteria = new HashMap<Class<? extends Criteria>, Criteria>();
    for (Criteria filter : filtersCriteria) {
      filter.setFilterUtility(this);
      this.filtersCriteria.put(filter.getClass(), filter);
    }
  }

  public <T extends FilterChainStorageStateGroup> T saveFilterChainGroup(T group) {
    return (T) getFilterChainStorage().saveGroup(group);
  }

  @SuppressWarnings("unchecked")
  public <T extends FilterChain, S extends FilterChainStorageState> S saveFilterChain(T filter) {
    return (S) getFilterChainStorage().save(filter);
  }

  /**
   * Load filter.
   * 
   * @param <T>
   *          the generic type
   * @param stateId
   *          the state id
   * @return the t
   */
  @SuppressWarnings("unchecked")
  public <T extends FilterChain> T loadFilterChain(Long stateId) {
    return (T) getFilterChainStorage().load(stateId);
  }

  public <T extends FilterChain> Map<Object, T> loadFilterChains(Long stateGroupId) {
    return (Map<Object, T>) getFilterChainStorage().loadAll(stateGroupId);
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
      Class<? extends FilterSourceAccessor> clazz = (Class<? extends FilterSourceAccessor>) Class
          .forName(filterSourceClassName);
      filterSourceAccessors.put(clazz, source);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Unable to find class name " + filterSourceClassName
          + " when caching filter sources in " + this.getClass());
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
  public void setFilterSourceAccessorss(
      Map<Class<? extends FilterSourceAccessor>, FilterSourceAccessor> filterSourceAccessors) {
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
   * Gets the default charset.
   * 
   * @return the default charset
   */
  public Charset getDefaultCharset() {
    return defaultCharset;
  }

  /**
   * Sets the default charset.
   * 
   * @param defaultCharset
   *          the new default charset
   */
  public void setDefaultCharset(Charset defaultCharset) {
    this.defaultCharset = defaultCharset;
  }

  /**
   * Gets the json serializer.
   * 
   * @return the json serializer
   */
  public JsonSerializer getJsonSerializer() {
    return jsonSerializer;
  }

  /**
   * Sets the json serializer.
   * 
   * @param jsonSerializer
   *          the new json serializer
   */
  public void setJsonSerializer(JsonSerializer jsonSerializer) {
    this.jsonSerializer = jsonSerializer;
  }

  /**
   * Gets the json deserializer.
   * 
   * @return the json deserializer
   */
  public JsonDeserializer getJsonDeserializer() {
    return jsonDeserializer;
  }

  /**
   * Sets the json deserializer.
   * 
   * @param jsonDeserializer
   *          the new json deserializer
   */
  public void setJsonDeserializer(JsonDeserializer jsonDeserializer) {
    this.jsonDeserializer = jsonDeserializer;
  }

  /**
   * Gets the filter storage.
   * 
   * @return the filter storage
   */
  public FilterChainStorage<FilterChainStorageState, FilterChainStorageStateGroup> getFilterChainStorage() {
    return filterChainStorage;
  }

  /**
   * Sets the filter storage.
   * 
   * @param filterChainStorage
   *          the new filter storage
   */
  public void setFilterChainStorage(
      FilterChainStorage<FilterChainStorageState, FilterChainStorageStateGroup> filterChainStorage) {
    this.filterChainStorage = filterChainStorage;
    if (filterChainStorage != null) {
      filterChainStorage.setFilterUtility(this);
    }
  }

  /**
   * Find generator.
   * 
   * @param <G>
   *          the generic type
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @return the filter generator
   */
  public <G extends FilterGenerator<T>, T extends FilterResult> G findGenerator(
      Class<? extends FilterGenerator<T>> clazz) {
    G generator = (G) this.generators.get(clazz);
    return generator;
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
