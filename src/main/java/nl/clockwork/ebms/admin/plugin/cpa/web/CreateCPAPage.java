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

import java.io.IOException;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.MarshalException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import nl.clockwork.ebms.admin.plugin.cpa.common.Utils;
import nl.clockwork.ebms.admin.plugin.cpa.dao.CPAPluginDAO;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;
import nl.clockwork.ebms.admin.plugin.cpa.model.Certificate;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.LocalizedStringResource;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.TextField;
import nl.clockwork.ebms.admin.web.service.cpa.CPAsPage;
import nl.clockwork.ebms.common.util.DOMUtils;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CreateCPAPage extends BasePage
{
	private class LoadableDetachableCPATemplatesModel extends LoadableDetachableModel<List<CPATemplate>>
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected List<CPATemplate> load()
		{
			return cpaPluginDAO.selectCPATemplates();
		}
	}
	private static final long serialVersionUID = 1L;
	protected transient Log logger = LogFactory.getLog(getClass());
	@SpringBean(name="cpaService")
	private CPAService cpaService;
	@SpringBean(name="cpaPluginDAO")
	private CPAPluginDAO cpaPluginDAO;

	public CreateCPAPage()
	{
		add(new BootstrapFeedbackPanel("feedback").setOutputMarkupId(true));
		add(new CreateCPAForm("form"));
	}

	@Override
	public String getPageTitle()
	{
		return getLocalizer().getString("createCPA",this);
	}

	public class CreateCPAForm extends Form<CreateCPAFormModel>
	{
		private static final long serialVersionUID = 1L;

		public CreateCPAForm(String id)
		{
			super(id,new CompoundPropertyModel<CreateCPAFormModel>(new CreateCPAFormModel()));
			setMultiPart(true);
			add(new BootstrapFormComponentFeedbackBorder("cpaTemplateFeedback",createCPATemplateChoice("cpaTemplate")));
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",new TextField<String>("cpaId",new LocalizedStringResource("lbl.cpaId",CreateCPAForm.this)).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("startDateFeedback",new BootstrapDateTimePicker("startDate",new LocalizedStringResource("lbl.startDate",CreateCPAForm.this),"dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("endDateFeedback",new BootstrapDateTimePicker("endDate",new LocalizedStringResource("lbl.endDate",CreateCPAForm.this),"dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("partyNameFeedback",new TextField<String>("partyName",new LocalizedStringResource("lbl.partyName",CreateCPAForm.this)).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("partyIdFeedback",createPartyIdTextField("partyId")));
			add(new BootstrapFormComponentFeedbackBorder("urlFeedback",new TextField<String>("url",new LocalizedStringResource("lbl.url",CreateCPAForm.this)).setRequired(true)));
			add(createCertificatesListView("certificates"));
			add(createGenerateButton("generate"));
			add(new ResetButton("reset",new ResourceModel("cmd.reset"),CreateCPAPage.class));
		}

		private DropDownChoice<CPATemplate> createCPATemplateChoice(String id)
		{
			DropDownChoice<CPATemplate> result = new DropDownChoice<CPATemplate>(id,new LoadableDetachableCPATemplatesModel(),new ChoiceRenderer<CPATemplate>("name","id"));
			result.setLabel(new ResourceModel("lbl.cpaTemplate"));
			result.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						CreateCPAFormModel model = CreateCPAForm.this.getModelObject();
						if (model.getCpaTemplate() != null)
						{
							Document document = DOMUtils.read(model.getCpaTemplate().getContent());
							XPath xpath = Utils.createXPath();
							generateCPAId(model,document,xpath);
							addCertificateFiles(model,document,xpath);
						}
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});
			return result;
		}

		private FormComponent<String> createPartyIdTextField(String id)
		{
			FormComponent<String> result = new TextField<String>(id,new LocalizedStringResource("lbl.partyId",CreateCPAForm.this));
			result.setRequired(true);
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					try
					{
						CreateCPAFormModel model = CreateCPAForm.this.getModelObject();
						if (model.getCpaTemplate() != null)
						{
							Document document = DOMUtils.read(model.getCpaTemplate().getContent());
							XPath xpath = Utils.createXPath();
							generateCPAId(model,document,xpath);
						}
						target.add(getPage().get("feedback"));
						target.add(getPage().get("form"));
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(e.getMessage());
					}
				}
			});
			return result;
		}

		private void generateCPAId(CreateCPAFormModel model, Document document, XPath xpath) throws XPathExpressionException
		{
			String cpaId = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/@cpa:cpaid",document,XPathConstants.STRING);
			String fromPartyId = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[1]/cpa:PartyId/text()",document,XPathConstants.STRING);
			model.setCpaId(cpaId + "_" + (fromPartyId != null ? fromPartyId + "_" : "") + (model.partyId != null ? model.partyId + "_" : "") + UUID.randomUUID());
		}

		private void addCertificateFiles(CreateCPAFormModel model, Document document, XPath xpath) throws XPathExpressionException
		{
			NodeList nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[2]//cpa:Certificate/@cpa:certId",document,XPathConstants.NODESET);
			model.getCertificates().clear();
			for (int i = 0; i < nodeList.getLength(); i++)
				model.getCertificates().add(new Certificate(nodeList.item(i).getNodeValue()));
		}

		private ListView<Certificate> createCertificatesListView(String id)
		{
			ListView<Certificate> result = new ListView<Certificate>(id,CreateCPAForm.this.getModelObject().certificates)
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Certificate> item)
				{
					item.setModel(new CompoundPropertyModel<Certificate>(item.getModelObject()));
					item.add(new Label("id"));
					item.add(new BootstrapFormComponentFeedbackBorder("fileFeedback",createCertificateFileField("file",item.getModelObject().getId())));
				}
			};
			return result;
		}

		private FileUploadField createCertificateFileField(String id, final String label)
		{
			FileUploadField result = new FileUploadField(id);
			result.setLabel(new ResourceModel(label));
			result.setRequired(true);
			return result;
		}

		private Button createGenerateButton(String id)
		{
			Button result = new Button(id,new ResourceModel("cmd.generate"))
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit()
				{
					try
					{
						CPATemplate cpaTemplate = CreateCPAForm.this.getModelObject().cpaTemplate;
						Document document = DOMUtils.read(cpaTemplate.getContent());
						processDocument(document,CreateCPAForm.this.getModelObject());
						cpaService.insertCPA(DOMUtils.toString(document),null,false);
						setResponsePage(new CPAsPage());
					}
					catch (Exception e)
					{
						logger.error("",e);
						error(StringUtils.isEmpty(e.getMessage()) ? e : e.getMessage());
					}
				}

				private void processDocument(Document document, CreateCPAFormModel modelObject) throws XPathExpressionException, CertificateException, KeyException, MarshalException, IOException
				{
					XPath xpath = Utils.createXPath();
					Node node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/@cpa:cpaid",document,XPathConstants.NODE);
					node.setNodeValue(modelObject.cpaId);
					node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:Start/text()",document,XPathConstants.NODE);
					node.setNodeValue(Utils.toXSDDate(modelObject.startDate));
					node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:End/text()",document,XPathConstants.NODE);
					node.setNodeValue(Utils.toXSDDate(modelObject.endDate));
					node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[2]/@cpa:partyName",document,XPathConstants.NODE);
					node.setNodeValue(modelObject.partyName);
					node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[2]/cpa:PartyId/text()",document,XPathConstants.NODE);
					node.setNodeValue(modelObject.partyId);
					NodeList nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[2]/cpa:Transport/cpa:TransportReceiver/cpa:Endpoint/@cpa:uri",document,XPathConstants.NODESET);
					for (int i = 0; i < nodeList.getLength(); i++)
						nodeList.item(i).setNodeValue(modelObject.url);
					for (Certificate certificate : modelObject.getCertificates())
					{
						final List<FileUpload> files = certificate.getFile();
						if (files != null && files.size() == 1)
						{
							node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[2]/cpa:Certificate[1]",document,XPathConstants.NODE);
							node = (Node)xpath.evaluate("//cpa:Certificate[@cpa:certId = '" + certificate.getId() + "']",document,XPathConstants.NODE);
							Node node1 = (Node)xpath.evaluate("//cpa:Certificate[@cpa:certId = '" + certificate.getId() + "']/xmldsig:KeyInfo",document,XPathConstants.NODE);
							node.removeChild(node1);
							Utils.generateKeyInfo(node,files.get(0).getInputStream());
						}
					}
				}

			};
			setDefaultButton(result);
			return result;
		}
	}

	public class CreateCPAFormModel implements IClusterable
	{
		private static final long serialVersionUID = 1L;
		private CPATemplate cpaTemplate;
		private String cpaId;
		private Date startDate = new Date();
		private Date endDate = new Date(new Date().getTime() + (365L * 24 * 60 * 60 * 1000));
		private String partyName;
		private String partyId;
		private String url;
		private List<Certificate> certificates = new ArrayList<Certificate>();
		private List<FileUpload> clientCertificateFile;

		public CPATemplate getCpaTemplate()
		{
			return cpaTemplate;
		}
		public void setCpaTemplate(CPATemplate cpaTemplate)
		{
			this.cpaTemplate = cpaTemplate;
		}
		public String getCpaId()
		{
			return cpaId;
		}
		public void setCpaId(String cpaId)
		{
			this.cpaId = cpaId;
		}
		public Date getStartDate()
		{
			return startDate;
		}
		public void setStartDate(Date startDate)
		{
			this.startDate = startDate;
		}
		public Date getEndDate()
		{
			return endDate;
		}
		public void setEndDate(Date endDate)
		{
			this.endDate = endDate;
		}
		public String getPartyName()
		{
			return partyName;
		}
		public void setPartyName(String partyName)
		{
			this.partyName = partyName;
		}
		public String getPartyId()
		{
			return partyId;
		}
		public void setPartyId(String partyId)
		{
			this.partyId = partyId;
		}
		public String getUrl()
		{
			return url;
		}
		public void setUrl(String url)
		{
			this.url = url;
		}
		public List<Certificate> getCertificates()
		{
			return certificates;
		}
		public void setCertificates(List<Certificate> certificates)
		{
			this.certificates = certificates;
		}
		public List<FileUpload> getClientCertificateFile()
		{
			return clientCertificateFile;
		}
		public void setClientCertificateFile(List<FileUpload> clientCertificateFile)
		{
			this.clientCertificateFile = clientCertificateFile;
		}
	}
}
