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

import org.apache.wicket.util.io.IClusterable;

public class CPATemplate implements IClusterable
{
	private static final long serialVersionUID = 1L;
	private long id;
	private String cpaId;
	private String cpa;
	public CPATemplate(long id, String cpaId, String cpa)
	{
		this.id = id;
		this.cpaId = cpaId;
		this.cpa = cpa;
	}
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getCpaId()
	{
		return cpaId;
	}
	public void setCpaId(String cpaId)
	{
		this.cpaId = cpaId;
	}
	public String getCpa()
	{
		return cpa;
	}
	public void setCpa(String cpa)
	{
		this.cpa = cpa;
	}
}
