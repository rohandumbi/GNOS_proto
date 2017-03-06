/* Table backup */

CREATE TABLE product_join_defn_bak  AS (SELECT * FROM product_join_defn);

CREATE TABLE pitgroup_pit_mapping_bak  AS (SELECT * FROM pitgroup_pit_mapping);

CREATE TABLE grade_bak  AS (SELECT * FROM grade);

/*  Drop table statements */
DROP TABLE IF EXISTS process;

/* alter table statements */

ALTER TABLE fields ADD COLUMN weighted_unit VARCHAR(100) AFTER data_type;

ALTER TABLE expressions ADD COLUMN weighted_field VARCHAR(100) AFTER filter; 

/* Recreate statements */

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

/* Create table statements */

CREATE TABLE cycletime_field_mapping(
   project_id INT NOT NULL,
   field_name VARCHAR(100) NOT NULL,
   mapping_type TINYINT,
   mapped_field_name VARCHAR(100) NOT NULL,
   UNIQUE KEY ( project_id, field_name, mapping_type )
);

/* Copy data from backup tables */


 insert into product_join_defn select distinct project_id, name, 1, child_product_name FROM product_join_defn_bak where child_product_join_name is null;
 
 insert into product_join_defn select distinct project_id, name, 2, child_product_join_name FROM product_join_defn_bak where child_product_name is null;
 
 insert into pitgroup_pit_mapping select distinct project_id, name, 1, child_pit_name FROM pitgroup_pit_mapping_bak where child_pitgroup_name is null;
 
 insert into pitgroup_pit_mapping select distinct project_id, name, 2, child_pitgroup_name FROM pitgroup_pit_mapping_bak where child_pit_name is null;
 
 insert into grade select id, project_id, product_name, name, 2, value from grade_bak;
 
 insert into cycletime_field_mapping select project_id, field_name, 1, mapped_field_name from cycletime_fixed_field_mapping;
 
 insert into cycletime_field_mapping select project_id, field_name, 2, mapped_field_name from cycletime_process_field_mapping;
  
 insert into cycletime_field_mapping select project_id, field_name, 3, mapped_field_name from cycletime_process_field_mapping;
   
 insert into cycletime_field_mapping select project_id, field_name, 4, mapped_field_name from cycletime_stockpile_field_mapping;
 
 /* drop backup tables */
DROP TABLE IF EXISTS product_join_defn_bak;

DROP TABLE IF EXISTS pitgroup_pit_mapping_bak;

DROP TABLE IF EXISTS grade_bak;

/* drop tables */

DROP TABLE IF EXISTS cycletime_fixed_field_mapping;

DROP TABLE IF EXISTS cycletime_process_field_mapping;

DROP TABLE IF EXISTS cycletime_dump_field_mapping;

DROP TABLE IF EXISTS cycletime_stockpile_field_mapping;

