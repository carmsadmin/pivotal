package com.p5solutions.search.filter.database;

/**
 * Created with IntelliJ IDEA. User: sophanara Date: 2013-10-21 Time: 9:36 AM
 * 
 * Store the join information. The class will encapsulate the source table and the target table to be join with.
 * 
 */
public class JoinTable {
  private TableCriteria targetCriteria;
  private TableCriteria srcCriteria;

  public JoinTable(TableCriteria srcCriteria, TableCriteria targetCriteria) {
    this.srcCriteria = srcCriteria;
    this.targetCriteria = targetCriteria;
  }

  public TableCriteria getSrcCriteria() {
    return srcCriteria;
  }

  public TableCriteria getTargetCriteria() {
    return targetCriteria;
  }

  public String getTargetTableName() {
    return this.getTargetCriteria().getFilterSourceName();
  }
}
