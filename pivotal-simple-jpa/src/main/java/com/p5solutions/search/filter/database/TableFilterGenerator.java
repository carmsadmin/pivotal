package com.p5solutions.search.filter.database;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.p5solutions.core.jpa.orm.AbstractEntity;
import com.p5solutions.core.jpa.orm.transaction.TransactionTemplate;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.ReflectionUtility;
import com.p5solutions.search.filter.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @User: sophanara
 * @Date: 2013-10-14
 * @Time: 11:03 PM
 */
public class TableFilterGenerator implements FilterGenerator<TableFilterGeneratorResult> {

  // TODO should not be specific to simpleJPA transaction template, or at the
  // very least extend from the Spring Template.
  private TransactionTemplate transactionTemplate;
  /** The logger. */
  private static Log logger = LogFactory.getLog(TableFilterGenerator.class);

  /**
   * Generate the result
   * 
   * @param chain
   * @return
   */
  @Override
  public TableFilterGeneratorResult generateResult(FilterChain chain) {
    // todo should be move to the filter element with the criteria for now just add to the list
    String[] returnColumns = chain.getReturnColumns();
    // make sure it valid
    FilterBNFValidator bnf = new FilterBNFValidator(chain);
    bnf.filter();

    // convert into postfix logical expression
    List<FilterElement> filters = FilterPostfixConverter.convert(bnf.getFilterElementResults());

    // evaluate the postfix logical expression
    TableFilterProcessor processor = new TableFilterProcessor();
    FilterResult result = FilterPostfixEvaluator.evaluate(filters, processor);
    TableFilterGeneratorResult tableResult = (TableFilterGeneratorResult) result;
    tableResult.setReturnColumns(returnColumns);
    return tableResult;
  }

  /**
   * Execute the result in the database
   * 
   * @param result
   * @return
   */
  public List<?> processResult(TableFilterGeneratorResult result) {
    String query = result.getQuery();
    Map<String, Object> keyValue = result.getParameters();
    List<?> dataset = transactionTemplate.findResultsAsListByQuery(query, keyValue);
    return dataset;
  }

  /**
   * 
   * @param returnEntityClass
   * @param filterColumns
   * @param result
   * @param <T>
   * @return
   */
  public <T extends AbstractEntity> List<T> processResult(Class<T> returnEntityClass, String[] filterColumns,
      TableFilterGeneratorResult result) {

    long start  = new Date().getTime();

    if (Comparison.isEmptyOrNull(filterColumns)) {
      throw new NullPointerException(
          "Filter columns cannot be blank or null, you need to specify which columns are used when filtering out the final view, example, USR_ID, or a composite of columns, such as WHERE (x, y) IN (SELECT x, y FROM z)");
    }

    Entity entity = ReflectionUtility.findAnnotation(returnEntityClass, Entity.class);
    if (entity == null) {
      throw new NullPointerException("Class of type " + returnEntityClass + " is not annotated with type "
          + Entity.class);
    }

    Table table = ReflectionUtility.findAnnotation(returnEntityClass, Table.class);
    if (table == null) {
      throw new NullPointerException("Class of type " + returnEntityClass + " is not annotated with type "
          + Table.class + ", thus no table name can be retrieved to join against the filter chains resultset.");
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

    String query = result.generateQueryResult();

    finalSQL.append(query);
    finalSQL.append(")");

    // Build query for transaction template.
    Map<String, Object> keyValue = result.getParameters();
    List<T> dataset = transactionTemplate.findResultsByQuery(returnEntityClass, finalSQL.toString(), keyValue);
    long end  = new Date().getTime();

    long duration = end - start;
    //query take more then 5 sconds
    //log it.
    if ( duration / 1000  > 5 ) {
        logger.warn("**************************************** Query running more then 5 seconds: **********************************");
        logger.warn("Time in millisecond to run the query: " + duration);
        logger.warn(finalSQL.toString());
    }

    return dataset;
  }

  /**
   * 
   * @return
   */
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  /**
   * 
   * @param transactionTemplate
   */
  public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }
}
