CREATE TABLE cpa_template
(
	id						INT						NOT NULL PRIMARY KEY,
	name					VARCHAR(256)	NOT NULL UNIQUE,
	cpa_template	CLOB					NOT NULL
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
