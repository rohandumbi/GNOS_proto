DROP TABLE IF EXISTS project;

CREATE TABLE project (
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   description VARCHAR(400),
   created_date	DATETIME,
   modified_date DATETIME,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS data_columns;

CREATE TABLE data_columns(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS pit;

CREATE TABLE pit(
   pit_id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   pit_name VARCHAR(100) NOT NULL,
   PRIMARY KEY ( pit_id, project_id )
);

DROP TABLE IF EXISTS block;

CREATE TABLE block(
   block_id INT NOT NULL,
   pit_id INT NOT NULL,
   project_id INT NOT NULL,
   block_no INT NOT NULL,
   PRIMARY KEY ( block_no, pit_id, project_id )
);

DROP TABLE IF EXISTS expressions;

CREATE TABLE expressions (
   id INT NOT NULL  AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   grade TINYINT(1),
   value_type TINYINT(1),
   value INT NOT NULL,
   filter_str VARCHAR(400),
   PRIMARY KEY ( id, project_id )
);

DROP TABLE IF EXISTS model_definition;

CREATE TABLE model_definition(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   field_id INT NOT NULL,
   PRIMARY KEY ( id )
);
