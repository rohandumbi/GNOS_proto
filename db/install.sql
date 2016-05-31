DROP TABLE IF EXISTS project;

CREATE TABLE project (
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   description VARCHAR(400),
   fileName VARCHAR(200) NOT NULL,
   created_date	DATETIME,
   modified_date DATETIME,
   PRIMARY KEY ( id ),
   UNIQUE (name)
);

DROP TABLE IF EXISTS scenario; 

CREATE TABLE scenario (
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100),
   start_year INT,
   time_period INT,
   discount FLOAT,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS fields;

CREATE TABLE fields(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   data_type TINYINT,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS required_field_mapping;

CREATE TABLE required_field_mapping(
   project_id INT NOT NULL,
   field_name VARCHAR(100) NOT NULL,
   mapped_field_name VARCHAR(100) NOT NULL,
   UNIQUE KEY ( project_id, field_name )
);

DROP TABLE IF EXISTS pit;

CREATE TABLE pit(
   pit_id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   pit_name VARCHAR(100) NOT NULL,
   UNIQUE KEY ( pit_id, project_id )
);

DROP TABLE IF EXISTS process;

CREATE TABLE process (
   model_id INT NOT NULL,
   process_no INT NOT NULL,
   project_id INT NOT NULL,
   UNIQUE ( project_id, model_id )
);

DROP TABLE IF EXISTS block;

CREATE TABLE block(
   block_id INT NOT NULL,
   pit_id INT NOT NULL,
   project_id INT NOT NULL,
   block_no INT NOT NULL,
   UNIQUE KEY ( block_no, pit_id, project_id )
);

DROP TABLE IF EXISTS expressions;

CREATE TABLE expressions (
   id INT NOT NULL  AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   is_grade TINYINT(1),
   is_complex TINYINT(1),
   expr_value VARCHAR(200) NOT NULL,
   filter VARCHAR(400),
   PRIMARY KEY ( id, project_id ),
   UNIQUE (project_id, name)
);

DROP TABLE IF EXISTS models;

CREATE TABLE models(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   expr_id INT NOT NULL,
   filter_str VARCHAR(400),
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS process_route_defn; 

CREATE TABLE process_route_defn(
   project_id INT NOT NULL,
   model_id INT NOT NULL,
   parent_model_id INT
);

DROP TABLE IF EXISTS process_join_defn; 

CREATE TABLE process_join_defn(
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   child_model_id INT,
   unique key(project_id, name, child_model_id)
);

DROP TABLE IF EXISTS opex_defn; 

CREATE TABLE opex_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   model_id INT NOT NULL,
   expression_id INT,
   in_use TINYINT NOT NULL default 1,
   is_revenue TINYINT NOT NULL default 1,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS process_constraint_defn; 

CREATE TABLE process_constraint_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   selector_name VARCHAR(50),
   selector_type TINYINT,
   coefficient_name VARCHAR(50),
   coefficient_type TINYINT,
   in_use TINYINT NOT NULL default 1,
   is_max TINYINT NOT NULL default 1,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS model_year_mapping; 

CREATE TABLE model_year_mapping(
   opex_id INT NOT NULL,
   year INT NOT NULL,
   value FLOAT NOT NULL,
   unique key( opex_id, year)
);

DROP TABLE IF EXISTS process_constraint_year_mapping; 

CREATE TABLE process_constraint_year_mapping(
   process_constraint_id INT NOT NULL,
   year INT NOT NULL,
   value FLOAT NOT NULL,
   unique key( process_constraint_id, year)
);

DROP TABLE IF EXISTS fixedcost_year_mapping; 

CREATE TABLE fixedcost_year_mapping(
   scenario_id INT NOT NULL,
   cost_head float NOT NULL,
   year INT NOT NULL,
   value FLOAT NOT NULL,
   unique key(scenario_id, cost_head, year)
);

DROP TABLE IF EXISTS product_defn; 

CREATE TABLE product_defn(
  project_id INT NOT NULL,
  name  VARCHAR(100) NOT NULL,
  associated_model_id INT,
  child_expression_id INT
);

DROP TABLE IF EXISTS product_join_defn; 

CREATE TABLE product_join_defn(
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   child_product_name VARCHAR(100),
   child_product_join_name VARCHAR(100),
   unique key(project_id, name, child_product_name, child_product_join_name)
);