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

import java.util.Iterator;

import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

public class CPATemplateDataProvider implements IDataProvider<CPATemplate>
{
	private static final long serialVersionUID = 1L;
	private CPAPluginDAO cpaPluginDAO;

	public CPATemplateDataProvider(CPAPluginDAO cpaPluginDAO)
	{
		this.cpaPluginDAO = cpaPluginDAO;
	}
	
	@Override
	public Iterator<? extends CPATemplate> iterator(long first, long count)
	{
		return cpaPluginDAO.selectCPATemplates(first,count).iterator();
	}

	@Override
	public IModel<CPATemplate> model(CPATemplate cpa)
	{
		return new CPATemplateDataModel(cpaPluginDAO,cpa);
	}

	@Override
	public long size()
	{
		return (int)cpaPluginDAO.countCPATemplates();
	}

	@Override
	public void detach()
	{
	}

}
