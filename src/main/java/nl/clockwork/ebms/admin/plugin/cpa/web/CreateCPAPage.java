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
import nl.clockwork.ebms.admin.plugin.cpa.model.PartyInfo;
import nl.clockwork.ebms.admin.plugin.cpa.model.Url;
import nl.clockwork.ebms.admin.web.BasePage;
import nl.clockwork.ebms.admin.web.BootstrapDateTimePicker;
import nl.clockwork.ebms.admin.web.BootstrapFeedbackPanel;
import nl.clockwork.ebms.admin.web.BootstrapFormComponentFeedbackBorder;
import nl.clockwork.ebms.admin.web.ResetButton;
import nl.clockwork.ebms.admin.web.service.cpa.CPAsPage;
import nl.clockwork.ebms.common.util.DOMUtils;
import nl.clockwork.ebms.service.CPAService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IClusterable;
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
			add(new BootstrapFormComponentFeedbackBorder("cpaIdFeedback",new TextField<String>("cpaId").setLabel(new ResourceModel("lbl.cpaId")).setRequired(true)).setOutputMarkupId(true));
			add(new BootstrapFormComponentFeedbackBorder("startDateFeedback",new BootstrapDateTimePicker("startDate","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setLabel(new ResourceModel("lbl.startDate")).setRequired(true)));
			add(new BootstrapFormComponentFeedbackBorder("endDateFeedback",new BootstrapDateTimePicker("endDate","dd-MM-yyyy",BootstrapDateTimePicker.Type.DATE).setLabel(new ResourceModel("lbl.endDate")).setRequired(true)));
			add(createPartyInfosContainer("partyInfosContainer"));
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
							setPartyInfos(model,document,xpath);
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

		private WebMarkupContainer createPartyInfosContainer(String id)
		{
			WebMarkupContainer result = new WebMarkupContainer(id);
			ListView<PartyInfo> partyInfos = new ListView<PartyInfo>("partyInfos",CreateCPAForm.this.getModelObject().getPartyInfos())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(final ListItem<PartyInfo> item)
				{
					item.setModel(new CompoundPropertyModel<PartyInfo>(item.getModelObject()));
					item.add(createEnabledCheckBox("enabled"));
					item.add(new BootstrapFormComponentFeedbackBorder("partyNameFeedback",new TextField<String>("partyName").setLabel(new ResourceModel("lbl.partyName")).setRequired(true))
					{
						private static final long serialVersionUID = 1L;

						@Override
						public boolean isEnabled()
						{
							return item.getModelObject().isEnabled();
						}
					});
					item.add(new BootstrapFormComponentFeedbackBorder("partyIdFeedback",createPartyIdTextField("partyId"))
					{
						private static final long serialVersionUID = 1L;

						@Override
						public boolean isVisible()
						{
							return item.getModelObject().isEnabled();
						}
					});
					item.add(createUrlsContainer("urlsContainer",item));
					item.add(createCertificatesContainer("certificatesContainer",item));
				}
			};
			result.add(partyInfos);
			result.setOutputMarkupId(true);
			return result;
		}

		private CheckBox createEnabledCheckBox(String id)
		{
			CheckBox result = new CheckBox(id);
			result.setLabel(new ResourceModel("lbl.enabled"));
			result.add(new AjaxFormComponentUpdatingBehavior("onchange")
      {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.add(getPage().get("feedback"));
					target.add(getPage().get("form"));
				}
      });
			return result;
		}

		private FormComponent<String> createPartyIdTextField(String id)
		{
			FormComponent<String> result = new TextField<String>(id);
			result.setLabel(new ResourceModel("lbl.partyId"));
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
						target.add(getPage().get("form:cpaIdFeedback"));
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

		private WebMarkupContainer createUrlsContainer(String id, final ListItem<PartyInfo> item)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return item.getModelObject().isEnabled();
				}
			};
			ListView<Url> certificates = new ListView<Url>("urls",item.getModelObject().getURLs())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Url> item)
				{
					item.setModel(new CompoundPropertyModel<Url>(item.getModelObject()));
					item.add(new Label("transportId"));
					item.add(new BootstrapFormComponentFeedbackBorder("urlFeedback",createUrlTextField("url",item.getModelObject().getTransportId())));
				}

			};
			result.add(certificates);
			result.setOutputMarkupId(true);
			return result;
		}

		private FormComponent<String> createUrlTextField(String id, String label)
		{
			return new TextField<String>(id).setLabel(new ResourceModel(label,label)).setRequired(true);
		}

		private WebMarkupContainer createCertificatesContainer(String id, final ListItem<PartyInfo> item)
		{
			WebMarkupContainer result = new WebMarkupContainer(id)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible()
				{
					return item.getModelObject().isEnabled();
				}
			};
			ListView<Certificate> certificates = new ListView<Certificate>("certificates",item.getModelObject().getCertificates())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<Certificate> item)
				{
					item.setModel(new CompoundPropertyModel<Certificate>(item.getModelObject()));
					item.add(new Label("id"));
					item.add(new BootstrapFormComponentFeedbackBorder("fileFeedback",createCertificateFileUploadField("file",item.getModelObject().getId())));
				}
			};
			result.add(certificates);
			result.setOutputMarkupId(true);
			return result;
		}

		private FileUploadField createCertificateFileUploadField(String id, final String label)
		{
			FileUploadField result = new FileUploadField(id);
			result.setLabel(new ResourceModel(label,label));
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
						cpaService.insertCPA(DOMUtils.toString(document),false);
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
					for (PartyInfo partyInfo : modelObject.getPartyInfos())
					{
						if (partyInfo.isEnabled())
						{
							node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfo.getId() + "]/@cpa:partyName",document,XPathConstants.NODE);
							node.setNodeValue(partyInfo.getPartyName());
							node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfo.getId() + "]/cpa:PartyId/text()",document,XPathConstants.NODE);
							node.setNodeValue(partyInfo.getPartyId());
							for (Url url: partyInfo.getURLs())
							{
								node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfo.getId() + "]/cpa:Transport[@cpa:transportId = '" + url.getTransportId() + "']/cpa:TransportReceiver/cpa:Endpoint[1]/@cpa:uri",document,XPathConstants.NODE);
								node.setNodeValue(url.getURL());
							}
							for (Certificate certificate : partyInfo.getCertificates())
							{
								final List<FileUpload> files = certificate.getFile();
								if (files != null && files.size() == 1)
								{
									node = (Node)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + partyInfo.getId() + "]//cpa:Certificate[@cpa:certId = '" + certificate.getId() + "']",document,XPathConstants.NODE);
									Node node1 = (Node)xpath.evaluate("//cpa:Certificate[@cpa:certId = '" + certificate.getId() + "']/xmldsig:KeyInfo",document,XPathConstants.NODE);
									node.removeChild(node1);
									Utils.generateKeyInfo(node,files.get(0).getInputStream());
								}
							}
						}
					}
				}

			};
			setDefaultButton(result);
			return result;
		}

		private void setPartyInfos(CreateCPAFormModel model, Document document, XPath xpath) throws XPathExpressionException
		{
			NodeList nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement//cpa:PartyInfo",document,XPathConstants.NODESET);
			model.getPartyInfos().clear();
			for (int i = 1; i < nodeList.getLength() + 1; i++)
			{
				PartyInfo partyInfo = new PartyInfo();
				partyInfo.setId(i);
				partyInfo.setPartyName(getPartyName(i,document,xpath));
				partyInfo.setPartyId(getPartyId(i,document,xpath));
				partyInfo.setUrls(getURLs(i,document,xpath));
				partyInfo.setCertificates(getCertificateFiles(i,document,xpath));
				model.getPartyInfos().add(partyInfo);
			}
		}

		private void generateCPAId(CreateCPAFormModel model, Document document, XPath xpath) throws XPathExpressionException
		{
			String cpaId = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/@cpa:cpaid",document,XPathConstants.STRING);
			model.setCpaId(cpaId + "_" + toString(model.getPartyInfos()) + UUID.randomUUID());
		}

		private String toString(List<PartyInfo> partyInfos)
		{
			String result = "";
			for (PartyInfo partyInfo : partyInfos)
				if (!StringUtils.isEmpty(partyInfo.getPartyId()))
					result += partyInfo.getPartyId() + "_";
			return result;
		}

		private String getPartyName(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			return (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]/@cpa:partyName",document,XPathConstants.STRING);
		}

		private String getPartyId(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			return (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]/cpa:PartyId/text()",document,XPathConstants.STRING);
		}

		private ArrayList<Url> getURLs(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			ArrayList<Url> result = new ArrayList<Url>();
			NodeList nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]//cpa:Transport/@cpa:transportId",document,XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				String transportId = nodeList.item(i).getNodeValue();
				String url = (String)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]/cpa:Transport[@cpa:transportId = '" + transportId + "']/cpa:TransportReceiver/cpa:Endpoint[1]/@cpa:uri",document,XPathConstants.STRING);
				result.add(new Url(transportId,url));
			}
			return result;
		}

		private ArrayList<Certificate> getCertificateFiles(Integer id, Document document, XPath xpath) throws XPathExpressionException
		{
			ArrayList<Certificate> result = new ArrayList<Certificate>();
			NodeList nodeList = (NodeList)xpath.evaluate("/cpa:CollaborationProtocolAgreement/cpa:PartyInfo[" + id + "]//cpa:Certificate/@cpa:certId",document,XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++)
				result.add(new Certificate(nodeList.item(i).getNodeValue()));
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
		private List<PartyInfo> partyInfos = new ArrayList<PartyInfo>();

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
		public List<PartyInfo> getPartyInfos()
		{
			return partyInfos;
		}
		public void setPartyInfos(List<PartyInfo> partyInfos)
		{
			this.partyInfos = partyInfos;
		}
	}
}
