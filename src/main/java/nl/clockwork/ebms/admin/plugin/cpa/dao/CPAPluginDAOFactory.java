/**
 * Copyright 2016 Ordina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.clockwork.ebms.admin.plugin.cpa.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import nl.clockwork.ebms.dao.AbstractDAOFactory.DefaultDAOFactory;

public class CPAPluginDAOFactory extends DefaultDAOFactory<CPAPluginDAO>
{
	protected TransactionTemplate transactionTemplate;
	protected JdbcTemplate jdbcTemplate;

	@Override
	public Class<CPAPluginDAO> getObjectType()
	{
		return nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO.class;
	}

	@Override
	public CPAPluginDAO createHSqlDbDAO()
	{
		return new nl.clockwork.ebms.admin.plugin.cpa.dao.hsqldb.CPAPluginDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public CPAPluginDAO createMySqlDAO()
	{
		return new nl.clockwork.ebms.admin.plugin.cpa.dao.mysql.CPAPluginDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public CPAPluginDAO createPostgresDAO()
	{
		return new nl.clockwork.ebms.admin.plugin.cpa.dao.postgresql.CPAPluginDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public CPAPluginDAO createOracleDAO()
	{
		return new nl.clockwork.ebms.admin.plugin.cpa.dao.oracle.CPAPluginDAOImpl(transactionTemplate,jdbcTemplate);
	}

	@Override
	public CPAPluginDAO createMsSqlDAO()
	{
		return new nl.clockwork.ebms.admin.plugin.cpa.dao.mssql.CPAPluginDAOImpl(transactionTemplate,jdbcTemplate);
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate)
	{
		this.transactionTemplate = transactionTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}

}
