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
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.OddOrEvenIndexStringModel;
import nl.clockwork.ebms.admin.web.PageClassLink;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ViewCPATemplatesPage extends BasePage
{
	private class CPATemplateDataView extends DataView<CPATemplate>
	{
		private static final long serialVersionUID = 1L;

		protected CPATemplateDataView(String id, IDataProvider<CPATemplate> dataProvider)
		{
			super(id,dataProvider);
			setOutputMarkupId(true);
		}

		@Override
		public long getItemsPerPage()
		{
			return maxItemsPerPage;
		}

		@Override
		protected void populateItem(final Item<CPATemplate> item)
		{
			final CPATemplate cpaTemplate = item.getModelObject();
			item.add(createViewLink(cpaTemplate));
			item.add(new DownloadCPATemplateLink("downloadCPATemplate",cpaTemplate));
			item.add(createDeleteButton("delete"));
			item.add(AttributeModifier.replace("class",new OddOrEvenIndexStringModel(item.getIndex())));
		}

		private Link<Void> createViewLink(final CPATemplate cpaTemplate)
		{
			Link<Void> link = new Link<Void>("view")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					setResponsePage(new ViewCPATemplatePage(cpaTemplate,ViewCPATemplatesPage.this));
				}
			};
			link.add(new Label("name",cpaTemplate.getName()));
			return link;
		}

		private Button createDeleteButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.delete"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						CPATemplate cpaTemplate = (CPATemplate)getParent().getDefaultModelObject();
						cpaPluginDAO.deleteCPATemplate(cpaTemplate.getId());
						setResponsePage(new ViewCPATemplatesPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			result.add(AttributeModifier.replace("onclick","return confirm('" + getLocalizer().getString("confirm",this) + "');"));
			return result;
		}

	}
	private static final long serialVersionUID = 1L;
	private Log logger = LogFactory.getLog(this.getClass());
	@SpringBean(name="cpaPluginDAO")
	private CPAPluginDAO cpaPluginDAO;
	@SpringBean(name="maxItemsPerPage")
	private Integer maxItemsPerPage;

	public ViewCPATemplatesPage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new ViewCPATemplatesForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("viewCPATemplates",this);
	}

	public class ViewCPATemplatesForm extends Form<Void>
	{
		private static final long serialVersionUID = 1L;

		public ViewCPATemplatesForm(String id)
		{
			super(id);
			WebMarkupContainer container = new WebMarkupContainer("container");
			add(container);
			container.add(new CPATemplateDataView("cpaTemplates",new CPATemplateDataProvider(cpaPluginDAO)));
			add(new PageClassLink("new",RegisterCPATemplatePage.class));
		}
	}
	
}
