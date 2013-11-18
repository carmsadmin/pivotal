package com.p5solutions.search.filter.database;

import java.lang.reflect.Array;
import java.util.*;

import com.p5solutions.core.jpa.orm.Binder;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.search.filter.*;

/**
 * @User: sophanara Min
 * @Date: 2013-10-10 Time: 4:04 PM
 *        <p/>
 *        Store the total result of all the query and join column.
 */
public class TableFilterGeneratorResult implements FilterResult {

  // private String mainTableName;
  private List<String> whereClauseList = new ArrayList<String>();
  private Map<String, JoinTable> joinCriteriaMap = new HashMap<String, JoinTable>();

  // list the columns that we are interest in.
  // todo Should be move some where else does not belong here
  private String[] returnColumns;
  private TableCriteria criteria;

  /**
   * The query.
   */
  private String query;

  /**
   * The parameters.
   */
  private Map<String, Object> parameters;

  /**
   * Wrapped the criteria into the filter result. Since the processor can only store a stack of FilterResult.
   * 
   * @param criteria
   */
  public TableFilterGeneratorResult(TableCriteria criteria) {
    this.criteria = criteria;
    // generateFrom(criteria);
    whereClause(null, this);
  }

  /**
   * 
   * @param operator
   * @param operand2
   * @return
   */
  @Override
  public FilterResult applyOperation(Operator operator, FilterResult operand2) {

    if (operand2 != null) {
      TableFilterGeneratorResult tableFilterGeneratorResult = ((TableFilterGeneratorResult) operand2);
      whereClause(operator, tableFilterGeneratorResult);
      tableDefinitions(tableFilterGeneratorResult);

    } else {
      // NOT operation
      whereClause(operator, this);
      tableDefinitions(this);
    }
    return this;
  }

  /**
   * Gets the query.
   * 
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the query.
   * 
   * @param query
   *          the new query
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public Map<String, Object> getParameters() {
    return parameters;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the parameters
   */
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }

  /**
   * Adds a parameter for binding
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public void add(String name, Object value) {
    if (this.parameters == null) {
      this.parameters = new HashMap<String, Object>();
    }

    this.parameters.put(name, value);
  }

  /**
   * Add a list of parameters
   * 
   * @param newParameters
   */
  public void addAll(Map<String, Object> newParameters) {
    if (newParameters == null) {
      return;
    }

    if (this.parameters == null) {
      this.parameters = new HashMap<String, Object>();
    }

    this.parameters.putAll(newParameters);
  }

  /**
   * Gets the a parameter for binding
   * 
   * @param name
   *          the name
   * @return the object
   */
  public Object get(String name) {
    return this.parameters.get(name);
  }

  /**
   * 
   * @return
   */
  public TableCriteria getCriteria() {
    return criteria;
  }

  /**
   * 
   * @param criteria
   */
  public String generateFrom(TableCriteria criteria) {
    StringBuilder tableSQL = new StringBuilder();
    tableSQL.append(criteria.getFilterSourceAccessorName());
    tableSQL.append(" ");
    tableSQL.append(criteria.getSourceAlias());
    return tableSQL.toString();
  }

  /**
   * 
   * @return
   */
  public String generateQueryResult() {
    StringBuilder finalSQL = new StringBuilder();
    // compile the final sql statement
    finalSQL.append(generateStart());
    finalSQL.append(generateTableSQL());
    finalSQL.append("\n WHERE ");
    finalSQL.append(generateWhereSQL());
    finalSQL.append(generateEnding());

    setQuery(finalSQL.toString());

    return getQuery();
  }

  /**
   * 
   * @return
   */
  private String generateWhereSQL() {
    StringBuilder result = new StringBuilder();

    for (String s : whereClauseList) {
      result.append(s).append(" ");
    }
    return result.toString();
  }

  /**
   * 
   * @param joinCriterias
   * @param criteria
   * @return
   */
  private List<JoinTable> sortJoinCriteria(Collection<JoinTable> joinCriterias, TableCriteria criteria) {
    List<JoinTable> resultList = new ArrayList<JoinTable>();
    String srcAlias = criteria.getSourceAlias();
    sortJoinCriteriaWithAlias(srcAlias, joinCriterias, resultList);

    return resultList;
  }

  /**
   *
   * @param alias
   * @param joinCriterias
   * @param resultList
   */
  private void sortJoinCriteriaWithAlias(String alias, Collection<JoinTable> joinCriterias, List<JoinTable> resultList) {
    List<JoinTable> leftOverList = new ArrayList<JoinTable>();
    Set<String> aliasList = new HashSet<String>();

    for (JoinTable joinTable : joinCriterias) {
      if (joinTable.getSrcCriteria().getSourceAlias().equals(alias)) {
        aliasList.add(joinTable.getTargetCriteria().getSourceAlias());
        resultList.add(joinTable);
      } else {
        leftOverList.add(joinTable);
      }
    }

    for (String srcAlias : aliasList) {
      sortJoinCriteriaWithAlias(srcAlias, leftOverList, resultList);
    }
  }

  /**
   * 
   * @return
   */
  private String generateTableSQL() {
    StringBuilder result = new StringBuilder();
    result.append(generateFrom(this.criteria));
    for (JoinTable joinTable : sortJoinCriteria(getJoinCriteriaMap().values(), criteria)) {
      generateJoin(result, joinTable);
    }
    return result.toString();
  }

  /**
   * Gets the return column definitions.
   * 
   * @return the return column definitions
   */
  public String getReturnColumnDefinitions() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < returnColumns.length; i++) {
      String column = returnColumns[i];
      if (i > 0) {
        sb.append(",");
      }
      sb.append(column);
    }
    return sb.toString();
  }

  /**
   * Generate start.
   * 
   * @return the string
   */
  protected String generateStart() {
    StringBuilder sb = new StringBuilder("SELECT ");
    sb.append(getReturnColumnDefinitions());
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
   * Junction clause.
   * 
   * @param sb
   *          the sb
   * @param op
   *          the op
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
   * Generate join definition for table structure.
   * 
   * @param tables
   *          the tables
   */
  public void generateJoin(StringBuilder tables, JoinTable joinTable) {
    TableCriteria srcCriteria = joinTable.getSrcCriteria();
    TableCriteria targetCriteria = joinTable.getTargetCriteria();

    // TODO re-think the joining of these tables together, there needs to be a
    // map
    // of sorts such that we know what join filters to apply in the list of all
    // filters within the chain. for most cases, a single join element will do,
    // however, more complex joins are very much a reality.
    tables.append("\n INNER JOIN ");
    tables.append(targetCriteria.getFilterSourceAccessor().getName());
    tables.append(" ");
    tables.append(targetCriteria.getSourceAlias());
    tables.append(" ON ");
    tables.append(targetCriteria.getSourceAlias());
    tables.append(".");
    tables.append(srcCriteria.getJoinColumn().getName());
    tables.append(" = "); // TODO configurable?
    tables.append(srcCriteria.getSourceAlias());
    tables.append(".");
    tables.append(srcCriteria.getJoinColumn().getName());
  }

  protected void whereClause(Operator operator, TableFilterGeneratorResult tableFilterGeneratorResult) {

    StringBuilder whereSQL = new StringBuilder();
    if (operator != null) {
      junctionClause(whereSQL, operator);
    }

    TableCriteria operand1 = tableFilterGeneratorResult.getCriteria();

    // The criteria that we are trying to join is already a complex data.
    // ie already have a join method select * from TAB_A a inner join TAB_B b

    if (!tableFilterGeneratorResult.getWhereClauseList().isEmpty()) {
      // add the operator in front of the first where clause
      //
      whereSQL.append(tableFilterGeneratorResult.getWhereClauseList().get(0));
      tableFilterGeneratorResult.getWhereClauseList().set(0, whereSQL.toString());
      this.getWhereClauseList().addAll(tableFilterGeneratorResult.getWhereClauseList());
      this.addAll(tableFilterGeneratorResult.getParameters());
      return;
    }

    FilterSourceAccessor accessor = operand1.getFilterSourceAccessor();

    // TODO what if there are multiple accessors of the same type,
    // alias' would be similar, add a counter??
    String tableAlias = operand1.getSourceAlias();
    FilterCriteriaCondition condition = operand1.getCondition();

    // TODO should be list compatible...
    FilterCriteriaValue criteriaValue = condition.getValue();

    // table alias appender to column name
    whereSQL.append(tableAlias);
    whereSQL.append(".");
    whereSQL.append(operand1.getColumnName());

    // get binding path, in this scenario, for a SQL statement, e.g. :name.
    FilterCriteriaColumn column = criteria.getColumn();
    Binder binder = column.getBinder();
    String path = binder.getBindingPathForStatement();
    path += "_" + operand1.getId();

    String basicCondition = condition.hasBasicCondition();
    if (Comparison.isNotNull(basicCondition)) {
      whereDefinition(basicCondition, whereSQL, path, criteriaValue.getActualValue());
    } else if (condition.isNull()) {
      whereDefinitionNull(whereSQL);
    } else if (condition.isNotNull()) {
      whereDefinitionNotNull(whereSQL);
    } else if (condition.isIn()) {
      whereDefinitionIN(whereSQL, path, criteriaValue.getValues());
    } else if (condition.isNotIn()) {
      whereDefinitionNotIN(whereSQL, path, criteriaValue.getValues());
    } else if (condition.isBetween()) {
      whereDefinitionBetween(whereSQL, path, criteriaValue.getValue(0), criteriaValue.getValue(1));
    }
    this.getWhereClauseList().add(whereSQL.toString());
  }

  /**
   * Appends to the WHERE definition a condition and value, e.g. column_nm = :value
   * 
   * @param whereSQL
   *          the where sql
   * @param bindingPath
   *          the binding path
   * @param value
   *          the value
   */
  protected void whereDefinition(String condition, StringBuilder whereSQL, String bindingPath, Object value) {

    whereSQL.append(condition);
    whereSQL.append(getSQLParameterIdentifier());
    whereSQL.append(bindingPath);

    add(bindingPath, value);
  }

  /**
   * Where definition null.
   * 
   * @param whereSQL
   *          the where sql
   */
  protected void whereDefinitionNull(StringBuilder whereSQL) {
    whereSQL.append(" IS NULL");
  }

  /**
   * Where definition not null.
   * 
   * @param whereSQL
   *          the where sql
   */
  protected void whereDefinitionNotNull(StringBuilder whereSQL) {
    whereSQL.append(" IS NOT NULL");
  }

  /**
   * Append to the WHERE definition the IN (. , .) keyword
   * 
   * @param whereSQL
   *          the where sql
   * @param bindingPath
   *          the binding path
   * @param values
   *          the values
   */
  protected void whereDefinitionNotIN(StringBuilder whereSQL, String bindingPath, List<Object> values) {

    // NOT IN (...)
    whereDefinitionINorNOT(true, whereSQL, bindingPath, values);
  }

  /**
   * Append to the WHERE definition the IN (. , .) keyword
   * 
   * @param whereSQL
   *          the where sql
   * @param bindingPath
   *          the binding path
   * @param values
   *          the values
   */
  protected void whereDefinitionIN(StringBuilder whereSQL, String bindingPath, List<Object> values) {

    // IN (...)
    whereDefinitionINorNOT(false, whereSQL, bindingPath, values);
  }

  /**
   * Where definition IN (...) or NOT IN (...)
   * 
   * @param isNot
   *          the is not
   * @param whereSQL
   *          the where sql
   * @param bindingPath
   *          the binding path
   * @param values
   *          the values
   */
  protected void whereDefinitionINorNOT(boolean isNot, StringBuilder whereSQL, String bindingPath, List<Object> values) {

    if (isNot) {
      whereSQL.append(" NOT");
    }

    whereSQL.append(" IN (");
    for (int i = 0; i < values.size(); i++) {
      Object value = values.get(i);
      if (i > 0) {
        whereSQL.append(",");
      }
      String bind = bindingPath + "_" + i;
      whereSQL.append(getSQLParameterIdentifier() + bind);
      add(bind, value);
    }
    whereSQL.append(")");
  }

  /**
   * Append to the WHERE definition the between keyword.
   * 
   * @param whereSQL
   *          the where sql
   * @param bindingPath
   *          the binding path
   * @param value1
   *          the value1
   * @param value2
   *          the value2
   */
  protected void whereDefinitionBetween(StringBuilder whereSQL, String bindingPath, Object value1, Object value2) {
    whereSQL.append(" BETWEEN ");
    whereSQL.append(getSQLParameterIdentifier() + bindingPath + "_1");
    whereSQL.append(" AND ");
    whereSQL.append(getSQLParameterIdentifier() + bindingPath + "_2");

    add(bindingPath + "_1", value1);
    add(bindingPath + "_2", value2);
  }

  /**
   * Gets the sQL parameter identifier.
   * 
   * @return the sQL parameter identifier
   */
  protected String getSQLParameterIdentifier() {
    return ":";
  }

  protected void tableDefinitions(TableFilterGeneratorResult tableFilterGeneratorResult) {

    TableCriteria operand1 = tableFilterGeneratorResult.getCriteria();

    // The criteria that we are trying to join is already a complex data.
    // ie already have a join method select * from TAB_A a inner join TAB_B b

    if (!tableFilterGeneratorResult.getJoinCriteriaMap().isEmpty()) {
      for (JoinTable joinTable : tableFilterGeneratorResult.getJoinCriteriaMap().values()) {
        String name = joinTable.getTargetTableName();

        // if it not in the current source and not already add
        if (!name.equals(this.criteria.getFilterSourceAccessorName())) {
          if (!this.getJoinCriteriaMap().keySet().contains(name)) {
            this.getJoinCriteriaMap().put(name, joinTable);
          }
        }

        // add the current source.
        if (!this.getJoinCriteriaMap().keySet().contains(operand1.getFilterSourceAccessorName())) {
          this.getJoinCriteriaMap().put(operand1.getFilterSourceAccessorName(), new JoinTable(this.criteria, operand1));
        }
      }
      return;
    }

    String name = operand1.getFilterSourceAccessorName();
    if (!name.equals(this.criteria.getFilterSourceAccessorName())) {
      if (!this.getJoinCriteriaMap().keySet().contains(name)) {
        this.getJoinCriteriaMap().put(name, new JoinTable(this.criteria, operand1));
      }
    }

  }

  public void setReturnColumns(String[] returnColumns) {
    this.returnColumns = returnColumns;
  }

  public List<String> getWhereClauseList() {
    return whereClauseList;
  }

  public Map<String, JoinTable> getJoinCriteriaMap() {
    return joinCriteriaMap;
  }

  public void setJoinCriteriaMap(Map<String, JoinTable> joinCriteriaMap) {
    this.joinCriteriaMap = joinCriteriaMap;
  }
}
