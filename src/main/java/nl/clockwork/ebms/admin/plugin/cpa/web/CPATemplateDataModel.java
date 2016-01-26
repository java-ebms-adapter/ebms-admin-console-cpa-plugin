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
package nl.clockwork.ebms.admin.plugin.cpa.web;

import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;

import org.apache.wicket.model.LoadableDetachableModel;

public class CPATemplateDataModel extends LoadableDetachableModel<CPATemplate>
{
	private static final long serialVersionUID = 1L;
	private CPAPluginDAO cpaPluginDAO;
	private final String name;

	public CPATemplateDataModel(CPAPluginDAO ebMSDAO, CPATemplate cpaTemplate)
	{
		this(ebMSDAO,cpaTemplate.getName());
	}
	public CPATemplateDataModel(CPAPluginDAO cpaPluginDAO, String name)
	{
		if (name == null || "".equals(name))
			throw new IllegalArgumentException("name is empty!");
		this.cpaPluginDAO = cpaPluginDAO;
		this.name = name;
	}

	protected CPAPluginDAO getCpaPluginDAO()
	{
		return cpaPluginDAO;
	}

	@Override
	protected CPATemplate load()
	{
		return getCpaPluginDAO().findCPATemplateByName(name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		else if (obj == null)
			return false;
		else if (obj instanceof CPATemplateDataModel)
		{
			CPATemplateDataModel other = (CPATemplateDataModel)obj;
			return name.equals(other.name);
		}
		return false;
	}
}
