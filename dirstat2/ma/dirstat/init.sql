--
-- Ma_Sys.ma DirStat 2.0.0.0 Database Table Specification 1.0.0.1,
-- Copyright (c) 2014 Ma_Sys.ma.
-- For further info send an e-mail to Ma_Sys.ma@web.de
--

-- This is all one transaction because if there are errors creating we want to
-- be able to re-run a modified version without part of the strcture already
-- existing.

BEGIN TRANSACTION;

CREATE TABLE scans (
	id               SERIAL         NOT NULL UNIQUE PRIMARY KEY,
	name             VARCHAR(128)   NOT NULL UNIQUE,
	creation         TIMESTAMP      NOT NULL,
	paths            VARCHAR ARRAY  NOT NULL,
	sep              VARCHAR(4)     NOT NULL,
	conf_exists      BOOLEAN        NOT NULL,
	conf_owner       BOOLEAN        NOT NULL,
	conf_hidden      BOOLEAN        NOT NULL,
	conf_r           BOOLEAN        NOT NULL,
	conf_w           BOOLEAN        NOT NULL,
	conf_x           BOOLEAN        NOT NULL,
	java_version     VARCHAR(64)    NOT NULL,
	java_vm_version  VARCHAR(128)   NOT NULL,
	java_vm_name     VARCHAR(128)   NOT NULL,
	os_name          VARCHAR(64)    NOT NULL,
	os_version       VARCHAR(64)    NOT NULL,
	user_name        VARCHAR(64)    NOT NULL,
	user_home        VARCHAR(256)   NOT NULL
);

CREATE TABLE files (
	scan             INT            NOT NULL REFERENCES scans(id),
	path             VARCHAR        NOT NULL,
	root             VARCHAR(128),  
	visited          TIMESTAMP      NOT NULL,
	name             VARCHAR(256),  
	ext              VARCHAR(128)   DEFAULT 'N_EXT',
	file_key         VARCHAR(256),
	time_creation    TIMESTAMP,
	dir              BOOLEAN,
	reg              BOOLEAN,
	symlink          BOOLEAN,
	time_access      TIMESTAMP,
	time_mod         TIMESTAMP,
	file_size        BIGINT,
	PRIMARY KEY (scan, path)
);

CREATE INDEX file_ext_index ON files(ext);

CREATE TABLE optional_attrs (
	scan             INT            NOT NULL REFERENCES scans(id),
	path             VARCHAR        NOT NULL,
	owner            VARCHAR(128),
	fexists          BOOLEAN,
	hidden           BOOLEAN,
	r                BOOLEAN,
	w                BOOLEAN,
	x                BOOLEAN,
	-- We do not use foreign key constraints because insertions do not
	-- happen in order.
	PRIMARY KEY (scan, path)
);

CREATE TYPE error_type AS ENUM ('owner', 'general', 'visiting');
CREATE TABLE errors (
	scan             INT            NOT NULL REFERENCES scans(id),
	path             VARCHAR        NOT NULL,
	etype            error_type     NOT NULL,
	message          TEXT           NOT NULL,
	PRIMARY KEY (scan, path)
);

END;
