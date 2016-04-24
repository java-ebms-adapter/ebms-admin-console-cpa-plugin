CREATE TABLE cpa_template
(
	id			INT						IDENTITY(1,1)	PRIMARY KEY,
	name		VARCHAR(256)	NOT NULL UNIQUE,
	content	TEXT					NOT NULL
);
