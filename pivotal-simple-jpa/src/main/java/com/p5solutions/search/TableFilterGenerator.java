package com.p5solutions.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.p5solutions.core.jpa.orm.AbstractEntity;
import com.p5solutions.core.jpa.orm.Binder;
import com.p5solutions.core.jpa.orm.Query;
import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.FilterChain.Operator;

/**
 * The Class TableFilterGenerator.
 * 
 * @author Kasra Rasaee
 * @since 2012 - OCT
 */
public class TableFilterGenerator implements FilterGenerator<TableFilterGeneratorResult> {

  // TODO should not be specific to simpleJPA transaction template, or at the very least extend from the Spring Template.
  private TransactionTemplate transactionTemplate;
  
  /**
   * The Class ProcessorResult.
   */
  protected class ProcessorResult {
    public FilterJunction previousTable;
    
  }
  
  /**
   * Generate start.
   *
   * @return the string
   */
  protected String generateStart(FilterChain chain) {
    StringBuilder sb = new StringBuilder("SELECT ");
    sb.append(chain.getReturnColumnDefinitions());
    sb.append(" FROM ");
    return sb.toString();
  }

  /**
   * Generate ending.
   *
   * @return the string
   */
  protected String generateEnding() {
    return "";
  }

  /**
   * Table defintions.
   *
   * @param chain the chain
   * @param duplicateFilter the duplicate filter
   * @param tableSQL the table sql
   * @param junction the junction
   * @param previous the previous
   * @param processorResult the processor result
   * @param result the result
   * @return the processor result
   */
  protected ProcessorResult tableDefintions(
      FilterChain chain,
      Map<String, FilterJunction> duplicateFilter,
      StringBuilder tableSQL,
      FilterJunction junction,
      FilterJunction previous,
      ProcessorResult processorResult,
      FilterGeneratorResult result) {

    Filter filter = junction.getFilter();
    String name = filter.getFilterSourceAccessorName();
    if (Comparison.isNotNull(duplicateFilter) && duplicateFilter.containsKey(name)) {
      return null;
    }
    
    duplicateFilter.put(name, junction);

    if (tableSQL.length() > 0) {
      generateJoin(tableSQL, junction, processorResult);
    } else {
      generateFrom(tableSQL, junction);
    }

    processorResult.previousTable = junction;
    
    return processorResult;
  }


  /**
   * Appends to the WHERE definition a condition and value, e.g. column_nm = :value
   *
   * @param result the result
   * @param operator the operator
   * @param whereSQL the where sql
   * @param bindingPath the binding path
   * @param value the value
   */
  protected void whereDefinition(TableFilterGeneratorResult result, 
      String condition, StringBuilder whereSQL, String bindingPath, 
      Object value) {
    
    whereSQL.append(condition);
    whereSQL.append(getSQLParameterIdentifier());
    whereSQL.append(bindingPath);
    
    result.add(bindingPath, value);
  }

  /**
   * Where definition null.
   *
   * @param result the result
   * @param whereSQL the where sql
   * @param bindingPath the binding path
   */
  protected void whereDefinitionNull(StringBuilder whereSQL) {
    whereSQL.append(" IS NULL");
  }
  /**
   * Append to the WHERE definition the IN (. , .) keyword
   *
   * @param result the result
   * @param whereSQL the where sql
   * @param bindingPath the binding path
   * @param values the values
   */
  protected void whereDefinitionIN(TableFilterGeneratorResult result,
      StringBuilder whereSQL, String bindingPath, 
      List<Object> values) {
    
    whereSQL.append(" IN (");
    for (int i = 0; i < values.size(); i++) {
      Object value = values.get(i);  
      if (i > 0) {
        whereSQL.append(",");
      }
      String bind = bindingPath + "_" + i;
      whereSQL.append(getSQLParameterIdentifier() + bind);
      result.add(bind, value);
    }
    whereSQL.append(")");
  }
  
  /**
   * Append to the WHERE definition the between keyword.
   *
   * @param result the result
   * @param whereSQL the where sql
   * @param bindingPath the binding path
   * @param value1 the value1
   * @param value2 the value2
   */
  protected void whereDefinitionBetween(TableFilterGeneratorResult result, 
      StringBuilder whereSQL, String bindingPath, Object value1, Object value2) {
    whereSQL.append(" BETWEEN ");
    whereSQL.append(getSQLParameterIdentifier() + bindingPath + "_1");
    whereSQL.append(" AND ");
    whereSQL.append(getSQLParameterIdentifier() + bindingPath + "_2");
    
    result.add(bindingPath + "_1", value1);
    result.add(bindingPath + "_2", value2);
  }
  
  /**
   * Where clause.
   *
   * @param chain the chain
   * @param finalSQL the final sql
   * @param tableSQL the table sql
   * @param whereSQL the where sql
   * @param junction the junction
   * @param previous the previous
   * @param processorResult the processor result
   * @param result the result
   */
  protected void whereClause(
      FilterChain chain,
      StringBuilder finalSQL,
      StringBuilder tableSQL,
      StringBuilder whereSQL, 
      FilterJunction junction,
      FilterJunction previous,
      ProcessorResult processorResult,
      TableFilterGeneratorResult result) {
    
    Filter filter = junction.getFilter();
    FilterSourceAccessor accessor = filter.getFilterSourceAccessor();

    // TODO what if there are multiple accessors of the same type, 
    // alias' would be similar, add a counter??
    String tableAlias = filter.getSourceAlias();
    FilterCriteriaCondition condition = filter.getValue();

    // TODO should be list compatible...
    FilterCriteriaValue criteriaValue = condition.getValue();
    
    // table alias appender to column name
    whereSQL.append(tableAlias);
    whereSQL.append(".");
    whereSQL.append(filter.getColumnName());
    
    // get binding path, in this scenario, for a SQL statement, e.g. :name.
    FilterCriteriaColumn column = filter.getColumn();
    Binder binder = column.getBinder();
    String path = binder.getBindingPathForStatement();
           path += "_" + junction.getId();

    String basicCondition = condition.hasBasicCondition();
    if (Comparison.isNotNull(basicCondition)) {
      whereDefinition(result, basicCondition, whereSQL, path, criteriaValue.getActualValue());
    } else if (condition.isNull()) {
      whereDefinitionNull(whereSQL);
    } else if (condition.isIn()) {
      whereDefinitionIN(result, whereSQL, path, criteriaValue.getValues());
    } else if (condition.isBetween()) {
      whereDefinitionBetween(result, whereSQL, path, 
          criteriaValue.getValue(0), 
          criteriaValue.getValue(1));
    }

    if (Comparison.isNotEmptyOrNull(filter.getFilterJunctions())) {
      iterateComposites(chain, finalSQL, tableSQL, whereSQL, filter.getFilterJunctions(), processorResult, result);
    }
  }
  
  /**
   * Iterate junctions.
   *
   * @param chain the chain
   * @param duplicateFilter the duplicate filter
   * @param finalSQL the final sql
   * @param tableSQL the table sql
   * @param whereSQL the where sql
   * @param junctions the junctions
   * @param previousComposite the previous composite
   * @param processorResult the processor result
   * @param result the result
   * @return the processor result
   */
  protected ProcessorResult iterateJunctions(
      FilterChain chain,
      Map<String, FilterJunction> duplicateFilter,
      StringBuilder finalSQL,
      StringBuilder tableSQL,
      StringBuilder whereSQL,
      List<FilterJunction> junctions,
      FilterJunctionComposite previousComposite,
      ProcessorResult processorResult,
      TableFilterGeneratorResult result) {
    
    // TODO determine if this is the best method to figure out the Operator to use
    // in the next set of junctions,.. e.g. if there was in-fact a previous iteration
    // of junctions, then the first junction->operator within this set of Junctions
    // should be used to determine the Operator, e.g. (previous->junctions) AND? (next->junctions)
    if (previousComposite != null && Comparison.isNotEmptyOrNull(junctions)) {
      FilterJunction junction = junctions.get(0);
      junctionClause(whereSQL, junction.getOp());
    }
    
    // open new composite
    whereSQL.append("(");
    
    FilterJunction previous = null;
    for (FilterJunction junction : junctions) {

      // only appends it if there was a previous junction
      // this is not used in pairs of composite junctions
      // but rather within the set of junctions within the
      // composite, only.
      junctionClause(whereSQL, junction, previous);
      
      // create the where clause.
      whereClause(chain, finalSQL, tableSQL, whereSQL, junction, previous, processorResult, result);

      // create the table definitions
      tableDefintions(chain, duplicateFilter, tableSQL, junction, previous, processorResult, result);
      
      previous = junction;
    }
    
    // close previously opened composite
    whereSQL.append(")");
    
    return processorResult;
  }

  /**
   * Iterate composites.
   *
   * @param chain the chain
   * @param finalSQL the final sql
   * @param tableSQL the table sql
   * @param whereSQL the where sql
   * @param composites the composites
   * @param processorResult the processor result
   * @param result the result
   * @return the processor result
   */
  protected ProcessorResult iterateComposites(
      FilterChain chain,
      StringBuilder finalSQL, //
      StringBuilder tableSQL, //
      StringBuilder whereSQL,
      List<FilterJunctionComposite> composites, //
      ProcessorResult processorResult,
      TableFilterGeneratorResult result) {
    
    if (Comparison.isEmptyOrNull(composites)) {
      throw new NullPointerException("No filters defined within filter chain, cannot generate ouptut on empty chain [" + this.getClass() + "]");
    }

    Map<String, FilterJunction> filtering = new HashMap<String, FilterJunction>();
    FilterJunctionComposite previousComposite = null;
    for (FilterJunctionComposite composite : composites) {
      List<FilterJunction> junctions = composite.getFilterJunctions();
      
      processorResult = iterateJunctions(chain, filtering, 
          finalSQL, tableSQL, whereSQL, 
          junctions, previousComposite, 
          processorResult, result);

      previousComposite = composite;
    }
    
    return processorResult;
  }
  

  /**
   * @see com.p5solutions.search.FilterGenerator#generateResult(com.p5solutions.search.FilterChain)
   */
  @Override
  public TableFilterGeneratorResult generateResult(FilterChain chain) {
    if (Comparison.isNull(chain)) {
      throw new NullPointerException("Cannot pass an unitialized " + FilterChain.class + ". Nothing to select!");
    }
    StringBuilder finalSQL = new StringBuilder();
    StringBuilder whereSQL = new StringBuilder();
    StringBuilder tableSQL = new StringBuilder();

    TableFilterGeneratorResult result = new TableFilterGeneratorResult();
    ProcessorResult processorResult = new ProcessorResult();
    
    iterateComposites(chain, finalSQL, tableSQL, whereSQL, chain.getFilterJunctions(), processorResult, result);

    // compile the final sql statement
    finalSQL.append(generateStart(chain));
    finalSQL.append(tableSQL);
    finalSQL.append("\n WHERE ");
    finalSQL.append(whereSQL);
    finalSQL.append(generateEnding());

    result.setQuery(finalSQL.toString());

    return result;
  }
  
  public void processResult(FilterChain chain) {
    TableFilterGeneratorResult result = generateResult(chain);
   // return processResult(chain);
  }
  
  public List<?> processResult(FilterChain chain, TableFilterGeneratorResult result) {
    String query = result.getQuery();
    Map<String, Object> keyValue = result.getParameters();
    List<?> dataset = transactionTemplate.findResultsAsListByQuery(query, keyValue);
    return dataset;
  }
  
  public <T extends AbstractEntity> List<T> processResult(
      Class<T> returnEntityClass, String[] filterColumns,
      FilterChain chain, TableFilterGeneratorResult result) {
    
    if (Comparison.isEmptyOrNull(filterColumns)) {
      throw new NullPointerException("Filter columns cannot be blank or null, you need to specify which columns are used when filtering out the final view, example, USR_ID, or a composite of columns, such as WHERE (x, y) IN (SELECT x, y FROM z)");
    }
    
    Entity entity = ReflectionUtility.findAnnotation(returnEntityClass, Entity.class);
    if (entity == null) {
      throw new NullPointerException("Class of type " + returnEntityClass + " is not annotated with type " + Entity.class);
    }
    
    Table table = ReflectionUtility.findAnnotation(returnEntityClass, Table.class);
    if (table == null) {
      throw new NullPointerException("Class of type " + returnEntityClass + " is not annotated with type " + Table.class + ", thus no table name can be retrieved to join against the filter chains resultset.");
    }
    
    StringBuilder finalSQL = new StringBuilder("SELECT * FROM ");
    finalSQL.append(table.name());
    finalSQL.append(" WHERE (");
    
    // TODO join instead of IN ? or have the option.
    
    for (int i = 0; i < filterColumns.length; i++) {
      if (i > 0) {
        finalSQL.append(",");
      }
      finalSQL.append(filterColumns[i]);
    }
    
    finalSQL.append(") IN (");
    
    String query = result.getQuery();
    
    finalSQL.append(query);
    finalSQL.append(")");
    
    // Build query for transaction template.
    Map<String, Object> keyValue = result.getParameters();
    List<T> dataset = transactionTemplate.findResultsByQuery(
        returnEntityClass, 
        finalSQL.toString(), 
        keyValue);
    
    return dataset;
  }

  public void generateFrom(StringBuilder tableSQL, FilterJunction junction) {
    Filter filter = junction.getFilter();
    tableSQL.append(filter.getFilterSourceAccessorName());
    tableSQL.append(" ");
    tableSQL.append(filter.getSourceAlias());
  }
  
  /**
   * Generate join definition for table structure.
   *
   * @param tables the tables
   * @param junction the junction
   * @param processorResult the processor result
   */
  public void generateJoin(StringBuilder tables, FilterJunction junction, ProcessorResult processorResult) {
    // TODO re-think the joining of these tables together, there needs to be a
    // map
    // of sorts such that we know what join filters to apply in the list of all
    // filters within the chain. for most cases, a single join element will do,
    // however, more complex joins are very much a reality.

    Filter filter = junction.getFilter();
    FilterJunction previousJunction = processorResult.previousTable;
    Filter previousFilter = previousJunction.getFilter();
    
    tables.append("\n INNER JOIN ");
    tables.append(filter.getFilterSourceAccessor().getName());
    tables.append(" ");
    tables.append(filter.getSourceAlias());
    tables.append(" ON ");
    tables.append(filter.getSourceAlias());
    tables.append(".");
    tables.append(previousFilter.getJoinColumn().getName());
    tables.append(" = "); // TODO configurable?
    tables.append(previousFilter.getSourceAlias());
    tables.append(".");
    tables.append(filter.getJoinColumn().getName());
  }

  /**
   * Junction clause.
   *
   * @param sb the sb
   * @param junction the junction
   * @param previous the previous
   */
  protected void junctionClause(StringBuilder sb, FilterJunction junction, FilterJunction previous) {
    if (previous == null) {
      return;
    }
    
    junctionClause(sb, junction.getOp());
  }
  
  /**
   * Junction clause.
   *
   * @param sb the sb
   * @param op the op
   */
  protected void junctionClause(StringBuilder sb, Operator op) {
    if (Operator.OR.equals(op)) {
      sb.append("\n OR ");
    } else if (Operator.AND.equals(op)) {
      sb.append("\n AND ");
    } else if (Operator.NOT.equals(op)) { // TODO questionable??
      sb.append("\n NOT ");
    }
  }

  /**
   * Gets the sQL parameter identifier.
   *
   * @return the sQL parameter identifier
   */
  protected String getSQLParameterIdentifier() {
    return ":";
  }
  
  /**
   * Gets the transaction template.
   *
   * @return the transaction template
   */
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }
  
  /**
   * Sets the transaction template.
   *
   * @param transactionTemplate the new transaction template
   */
  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }
}
