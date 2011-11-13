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

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * The Class SimpleJPATransactionManager. A sub typed
 * {@link DataSourceTransactionManager} that implements a very simple
 * persistence context via the implementation of {@link PersistenceContext} and
 * {@link PersistenceProvider}
 * 
 * @author Kasra Rasaee
 * @since 2011-02-04
 * 
 * @see PersistenceProvider
 * @see DataSourceTransactionManager
 */
public class SimpleJPATransactionManager extends DataSourceTransactionManager {

	private static final long serialVersionUID = 1L;

	/**
	 * Begin a transaction, issue a new {@link PersistenceContext} within the
	 * local {@link PersistenceProvider}.
	 * 
	 * @param transaction
	 *          the transaction
	 * @param definition
	 *          the definition
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#doBegin(java.lang.Object,
	 *      org.springframework.transaction.TransactionDefinition)
	 */
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		// reset the local thread variable if any
		// PersistenceProvider.reset(); no point resetting as each transaction will
		// stack its own context

		// create a new persistence context for this transaction
		PersistenceContext context = new PersistenceContext();
		PersistenceProvider.set(context);

		super.doBegin(transaction, definition);
	}

	/**
	 * Do commit of a transaction, then reset the {@link PersistenceContext} for
	 * the given {@link ThreadLocal} within the {@link PersistenceProvider}
	 * 
	 * @param status
	 *          the status
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
	 */
	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		super.doCommit(status);
		PersistenceProvider.reset();
	}

	/**
	 * Do rollback of a transaction, then reset the {@link PersistenceContext} for
	 * the given {@link ThreadLocal} within the {@link PersistenceProvider}
	 * 
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#doRollback(org.springframework.transaction.support.DefaultTransactionStatus)
	 */
	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		super.doRollback(status);
		PersistenceProvider.reset();
	}
}