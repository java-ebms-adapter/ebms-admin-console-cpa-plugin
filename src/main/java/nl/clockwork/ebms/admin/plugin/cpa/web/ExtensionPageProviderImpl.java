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

import java.util.ArrayList;
import java.util.List;

import nl.clockwork.ebms.admin.web.ExtensionPageProvider;
import nl.clockwork.ebms.admin.web.menu.MenuItem;
import nl.clockwork.ebms.admin.web.menu.MenuLinkItem;

public class ExtensionPageProviderImpl extends ExtensionPageProvider
{

	@Override
	public List<MenuItem> getMenuItems()
	{
		List<MenuItem> result = new ArrayList<MenuItem>();
		result.add(new MenuLinkItem("cpa_1","register CPA Template",RegisterCPATemplatePage.class));
		result.add(new MenuLinkItem("cpa_2","create CPA",CreateCPAPage.class));
		return result;
	}

}