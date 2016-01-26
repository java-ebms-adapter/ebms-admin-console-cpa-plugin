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
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.cpa.CPAPage;
import nl.clockwork.ebms.admin.web.cpa.CPAsPage;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ViewCPATemplatesPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	@SpringBean(name="cpaPluginDAO")
	private CPAPluginDAO cpaPluginDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;

	public ViewCPATemplatesPage()
	{
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);

		DataView<CPATemplate> cpaTemplates = new DataView<CPATemplate>("cpaTemplates",new CPATemplateDataProvider(cpaPluginDAO))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public long getItemsPerPage()
			{
				return maxItemsPerPage;
			}

			@Override
			protected void populateItem(final Item<CPATemplate> item)
			{
				final CPATemplate cpaTemplate = item.getModelObject();
				Link<Void> link = new Link<Void>("view")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick()
					{
						//setResponsePage(new CPAPage(ebMSDAO.getCPA(cpa.getId(),CPAsPage.this)));
						setResponsePage(new ViewCPATemplatePage(cpaTemplate,ViewCPATemplatesPage.this));
					}
				};
				link.add(new Label("name",cpaTemplate.getName()));
				item.add(link);
				item.add(AttributeModifier.replace("class",new AbstractReadOnlyModel<String>()
				{
					private static final long serialVersionUID = 1L;
				
					@Override
					public String getObject()
					{
						return (item.getIndex() % 2 == 0) ? "even" : "odd";
					}
				}));
			}
		};
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("viewCPATemplates",this);
	}

}
