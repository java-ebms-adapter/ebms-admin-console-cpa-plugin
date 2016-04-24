CREATE TABLE cpa_template
(
	id			SERIAL				PRIMARY KEY,
	name		VARCHAR(256)	NOT NULL UNIQUE,
	content	CLOB					NOT NULL
);
