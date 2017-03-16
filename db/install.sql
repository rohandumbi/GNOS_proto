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
   PRIMARY KEY ( id ),
   unique (project_id, name)
);

DROP TABLE IF EXISTS fields;

CREATE TABLE fields(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   data_type TINYINT,
   weighted_unit VARCHAR(100),
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
   weighted_field VARCHAR(100),
   PRIMARY KEY ( id, project_id ),
   UNIQUE (project_id, name)
);

DROP TABLE IF EXISTS models;

CREATE TABLE models(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   unit_type TINYINT(1),
   unit_id INT NOT NULL,
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
   unit_type TINYINT,
   unit_id INT,
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
  child_unit_type TINYINT,
  child_unit_id INT
);

DROP TABLE IF EXISTS product_join_defn; 

CREATE TABLE product_join_defn(
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   child_type TINYINT,
   child VARCHAR(100),
   unique key(project_id, name, child_type, child)
);

DROP TABLE IF EXISTS pitgroup_pit_mapping; 

CREATE TABLE pitgroup_pit_mapping(
   project_id INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   child_type TINYINT,
   child VARCHAR(100),
   unique (project_id, name, child_type, child)
);

DROP TABLE IF EXISTS dump; 

CREATE TABLE dump(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   type INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   condition_str VARCHAR(200),
   mapped_to VARCHAR(50) NOT NULL,
   mapping_type TINYINT,
   has_capacity TINYINT,
   capacity INT,
   primary key (id)
);

DROP TABLE IF EXISTS stockpile; 

CREATE TABLE stockpile(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   type INT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   condition_str VARCHAR(200),
   mapped_to VARCHAR(50) NOT NULL,
   mapping_type TINYINT,
   has_capacity TINYINT,
   capacity INT,
   is_reclaim TINYINT,
   primary key (id)
);

DROP TABLE IF EXISTS grade; 

CREATE TABLE grade(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   product_name VARCHAR(100) NOT NULL,
   name VARCHAR(100) NOT NULL,
   type TINYINT,
   mapped_name VARCHAR(100),
   PRIMARY KEY ( id ),
   unique key(project_id, product_name, name)
);


DROP TABLE IF EXISTS grade_constraint_defn; 

CREATE TABLE grade_constraint_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   grade VARCHAR(50) NOT NULL,
   product_join_name  VARCHAR(50),
   selector_name VARCHAR(50),
   selector_type TINYINT,
   in_use TINYINT NOT NULL default 1,
   is_max TINYINT NOT NULL default 1,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS grade_constraint_year_mapping; 

CREATE TABLE grade_constraint_year_mapping(
   grade_constraint_id INT NOT NULL,
   year INT NOT NULL,
   value FLOAT NOT NULL,
   unique key( grade_constraint_id, year)
);

DROP TABLE IF EXISTS product_join_grade_name_mapping; 

CREATE TABLE product_join_grade_name_mapping(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   product_join_name VARCHAR(50) NOT NULL,
   name VARCHAR(50),
   PRIMARY KEY ( id ),
   unique key(project_id, name, product_join_name)
);

DROP TABLE IF EXISTS bench_constraint_defn; 

CREATE TABLE bench_constraint_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   pit_name  VARCHAR(50),
   in_use TINYINT NOT NULL default 1,
   PRIMARY KEY ( id ),
   unique key(scenario_id, pit_name)
);

DROP TABLE IF EXISTS bench_constraint_year_mapping; 

CREATE TABLE bench_constraint_year_mapping(
   bench_constraint_id INT NOT NULL,
   year INT NOT NULL,
   value FLOAT NOT NULL,
   unique key( bench_constraint_id, year)
);

DROP TABLE IF EXISTS pit_dependency_defn; 

CREATE TABLE pit_dependency_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   in_use TINYINT NOT NULL default 1,
   first_pit_name  VARCHAR(50) NOT NULL,
   first_pit_bench_name  VARCHAR(50),
   dependent_pit_name  VARCHAR(50) NOT NULL,
   dependent_pit_bench_name  VARCHAR(50),
   min_lead INT,
   max_lead INT,
   PRIMARY KEY ( id ),
   unique key(scenario_id, first_pit_name, first_pit_bench_name, dependent_pit_name, dependent_pit_bench_name, min_lead, max_lead)
);

DROP TABLE IF EXISTS dump_dependency_defn; 

CREATE TABLE dump_dependency_defn(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   in_use TINYINT NOT NULL default 1,
   first_pit_name  VARCHAR(50),
   first_dump_name  VARCHAR(50),
   dependent_dump_name  VARCHAR(50) NOT NULL,
   PRIMARY KEY ( id ),
   unique key(scenario_id, id)
);


DROP TABLE IF EXISTS capex_data; 

CREATE TABLE capex_data(
   id INT NOT NULL AUTO_INCREMENT,
   scenario_id INT NOT NULL,
   name  VARCHAR(50) NOT NULL,
   PRIMARY KEY ( id ),
   unique key(scenario_id, name)
);

DROP TABLE IF EXISTS capex_instance; 

CREATE TABLE capex_instance(
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(50) NOT NULL,
   capex_id INT NOT NULL,
   group_name VARCHAR(50),
   group_type INT NOT NULL,
   capex BIGINT,
   expansion_capacity BIGINT,
   PRIMARY KEY ( id ),
   unique key(name, capex_id)
);

DROP TABLE IF EXISTS cycle_time_fields;

CREATE TABLE cycle_time_fields(
   id INT NOT NULL AUTO_INCREMENT,
   project_id INT NOT NULL,
   name VARCHAR(100) NOT NULL,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS cycletime_field_mapping;

CREATE TABLE cycletime_field_mapping(
   project_id INT NOT NULL,
   field_name VARCHAR(100) NOT NULL,
   mapping_type TINYINT,
   mapped_field_name VARCHAR(100) NOT NULL,
   UNIQUE KEY ( project_id, field_name, mapping_type )
);

DROP TABLE IF EXISTS truckparam_material_payload_mapping;

CREATE TABLE truckparam_material_payload_mapping(
   project_id INT NOT NULL,
   material_name VARCHAR(100) NOT NULL,
   payload INT NOT NULL,
   UNIQUE KEY ( project_id, material_name )
);

DROP TABLE IF EXISTS truckparam_fixed_time;

CREATE TABLE truckparam_fixed_time(
   project_id INT NOT NULL,
   fixed_time FLOAT NOT NULL,
   UNIQUE KEY ( project_id )
);

DROP TABLE IF EXISTS truckparam_cycle_time; 

CREATE TABLE truckparam_cycle_time(
   project_id INT NOT NULL,
   stockpile_name VARCHAR(100) NOT NULL,
   process_name VARCHAR(100) NOT NULL,
   value FLOAT NOT NULL,
   unique key(project_id, stockpile_name, process_name)
);

DROP TABLE IF EXISTS scenario_config; 

CREATE TABLE scenario_config(
   scenario_id INT NOT NULL,
   reclaim TINYINT,
   unique key(scenario_id)
);
