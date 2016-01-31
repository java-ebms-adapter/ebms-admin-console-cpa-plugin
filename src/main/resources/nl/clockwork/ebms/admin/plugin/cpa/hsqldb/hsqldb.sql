--
-- Copyright 2016 Ordina
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE cpa_template
(
	id			INT						NOT NULL PRIMARY KEY,
	name		VARCHAR(256)	NOT NULL UNIQUE,
	content	CLOB					NOT NULL
);

CREATE TABLE cpa_element
(
	id							INT						NOT NULL PRIMARY KEY,
	cpa_template_id	INT						NOT NULL,
	name						VARCHAR(64) 	NOT NULL,
	xpath_query			CLOB					NOT NULL,
	order_nr				SMALLINT			NOT NULL,
	FOREIGN KEY (cpa_template_id) REFERENCES cpa(id)
);
