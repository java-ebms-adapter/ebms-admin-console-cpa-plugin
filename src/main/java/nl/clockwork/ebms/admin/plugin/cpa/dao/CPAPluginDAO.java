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

import java.util.List;

import nl.clockwork.ebms.admin.plugin.cpa.model.CPAElement;
import nl.clockwork.ebms.admin.plugin.cpa.model.CPATemplate;

public interface CPAPluginDAO
{
	CPATemplate findCPATemplate(long id);
	int countCPATemplates();
	List<CPATemplate> selectCPATemplates();
	List<CPATemplate> selectCPATemplates(long first, long count);
	void insertCPATemplate(String name, String cpa);
	int deleteCPATemplate(long id);

	List<CPAElement> selectCPAElements(long cpaTemplateId);
}
