/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  KASRA RASAEE
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.p5solutions.core.jpa.orm.transaction;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;

@Deprecated
// REMOVE
public class TransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager, BeanFactoryAware, InitializingBean {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private DataSource dataSource;

  public TransactionManager() {
    super();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // TODO Auto-generated method stub
  }

  @Override
  public void setBeanFactory(BeanFactory arg0) throws BeansException {
    // TODO Auto-generated method stub

  }

  @Override
  public Object getResourceFactory() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Object doGetTransaction() throws TransactionException {

    return null;
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
    // TODO Auto-generated method stub

  }

  @Override
  protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
    // TODO Auto-generated method stub

  }

  @Override
  protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
    // TODO Auto-generated method stub

  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    if (dataSource instanceof TransactionAwareDataSourceProxy) {
      // If we got a TransactionAwareDataSourceProxy, we need to perform
      // transactions
      // for its underlying target DataSource, else data access code won't see
      // properly exposed transactions (i.e. transactions for the target
      // DataSource).
      this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
    } else {
      this.dataSource = dataSource;
    }
  }
}
