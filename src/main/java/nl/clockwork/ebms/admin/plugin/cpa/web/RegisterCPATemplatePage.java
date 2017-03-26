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

import java.util.List;

import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;

public class RegisterCPATemplatePage extends BasePage
{
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="cpaPluginDAO")
	private CPAPluginDAO cpaPluginDAO;

	public RegisterCPATemplatePage()
	{
		add(new BootstrapFeedbackPanel("feedback"));
		add(new RegisterCPATemplateForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("registerCPATemplate",this);
	}

	public class RegisterCPATemplateForm extends Form<RegisterCPATemplateFormModel>
	{
		private static final long serialVersionUID = 1L;

		public RegisterCPATemplateForm(String id)
		{
			super(id,new CompoundPropertyModel<RegisterCPATemplateFormModel>(new RegisterCPATemplateFormModel()));
			setMultiPart(true);
			add(new TextField<String>("name").setLabel(new ResourceModel("lbl.name")));
			add(new BootstrapFormComponentFeedbackBorder("cpaFeedback",createCPAFileField("cpaFile")));
			add(createValidateButton("validate"));
			add(createUploadButton("upload"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),RegisterCPATemplatePage.class));
		}

		private FileUploadField createCPAFileField(String id)
		{
			FileUploadField result = new FileUploadField(id);
			result.setLabel(new ResourceModel("lbl.cpa"));
			result.setRequired(true);
			return result;
		}

		private Button createValidateButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.validate"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final List<FileUpload> files = RegisterCPATemplateForm.this.getModelObject().cpaFile;
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaService.validateCPA(new String(file.getBytes()));
						}
						info(getString("cpa.valid"));
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			return result;
		}

		private Button createUploadButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.upload"))
			{
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onSubmit()
				{
					try
					{
						final List<FileUpload> files = RegisterCPATemplateForm.this.getModelObject().cpaFile;
						if (files != null && files.size() == 1)
						{
							FileUpload file = files.get(0);
							//String contentType = file.getContentType();
							//FIXME char encoding
							cpaPluginDAO.insertCPATemplate(RegisterCPATemplateForm.this.getModelObject().getName(),new String(file.getBytes()));
						}
						setResponsePage(new ViewCPATemplatesPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			};
			setDefaultButton(result);
			return result;
		}
	}
	
	public class RegisterCPATemplateFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private String name;
		private List<FileUpload> cpaFile;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public List<FileUpload> getCpaFile()
		{
			return cpaFile;
		}
		
		public void setCpaFile(List<FileUpload> cpaFile)
		{
			this.cpaFile = cpaFile;
		}
		
	}

}
