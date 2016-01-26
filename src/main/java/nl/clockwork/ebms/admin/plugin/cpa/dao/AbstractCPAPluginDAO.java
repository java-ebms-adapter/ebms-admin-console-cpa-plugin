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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import nl.clockwork.ebms.admin.plugin.cpa.model.CPAElement;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractCPAPluginDAO implements CPAPluginDAO
{
	public static class CPATemplateRowMapper implements ParameterizedRowMapper<CPATemplate>
	{
		public static String getBaseQuery()
		{
			return "select id, cpa_id, cpa from cpa";
		}

		@Override
		public CPATemplate mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			return new CPATemplate(rs.getLong("id"),rs.getString("cpa_id"),rs.getString("cpa"));
		}
	}
	
	protected TransactionTemplate transactionTemplate;
	protected JdbcTemplate jdbcTemplate;

	public AbstractCPAPluginDAO(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate)
	{
		this.transactionTemplate = transactionTemplate;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public CPATemplate findCPATemplate(long id)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				CPATemplateRowMapper.getBaseQuery() +
				" where id = ?",
				new CPATemplateRowMapper(),
				id
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	@Override
	public CPATemplate findCPATemplateByName(String name)
	{
		try
		{
			return jdbcTemplate.queryForObject(
				CPATemplateRowMapper.getBaseQuery() +
				" where name = ?",
				new CPATemplateRowMapper(),
				name
			);
		}
		catch (EmptyResultDataAccessException e)
		{
			return null;
		}
	}

	@Override
	public int countCPATemplates()
	{
		return jdbcTemplate.queryForInt("select count(id) from cpa");
	}
	
	@Override
	public List<String> selectCPAIds()
	{
		return jdbcTemplate.queryForList(
			"select cpa_id" +
			" from cpa" +
			" order by cpa_id",
			String.class
		);
	}
	
	public abstract String selectCPATemplatesQuery(long first, long count);
	
	@Override
	public List<CPATemplate> selectCPATemplates(long first, long count)
	{
		return jdbcTemplate.query(
			selectCPATemplatesQuery(first,count),
			new CPATemplateRowMapper()
		);
	}

	@Override
	public List<CPAElement> selectCPAElements(long cpaTemplateId)
	{
		return jdbcTemplate.query(
			"select id, name, xpath_query" +
			" from cpa_element" +
			" where cpa_template_id = ?" +
			" order by order_nr asc",
			new ParameterizedRowMapper<CPAElement>()
			{
				@Override
				public CPAElement mapRow(ResultSet rs, int rowNum) throws SQLException
				{
					return new CPAElement(rs.getLong("id"),rs.getString("name"),rs.getString("xpath_query"));
				}
			}
		);
	}
}