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
package nl.clockwork.ebms.admin.plugin.cpa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PartyInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String partyName;
	private String partyId;
	private List<Url> urls = new ArrayList<Url>();
	private List<Certificate> certificates = new ArrayList<Certificate>();
	private boolean enabled;

	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
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
	public List<Url> getURLs()
	{
		return urls;
	}
	public void setUrls(List<Url> urls)
	{
		this.urls = urls;
	}
	public List<Certificate> getCertificates()
	{
		return certificates;
	}
	public void setCertificates(List<Certificate> certificates)
	{
		this.certificates = certificates;
	}
	public boolean isEnabled()
	{
		return enabled;
	}
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
