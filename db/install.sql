DROP TABLE IF EXISTS pit;

CREATE TABLE pit(
   pit_id INT NOT NULL AUTO_INCREMENT,
   pit_name VARCHAR(100) NOT NULL,
   PRIMARY KEY ( pit_id )
);

DROP TABLE IF EXISTS block;

CREATE TABLE block(
   block_id INT NOT NULL,
   pit_id INT NOT NULL,
   block_no INT NOT NULL,
   PRIMARY KEY ( block_no, pit_id )
);

DROP TABLE IF EXISTS expressions;

CREATE TABLE expressions (
   id INT NOT NULL  AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   grade TINYINT(1),
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS model_definition;

CREATE TABLE model_definition(
   id INT NOT NULL AUTO_INCREMENT,
   name  VARCHAR(100) NOT NULL,
   field_id INT NOT NULL,
   PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS model_filter;

CREATE TABLE model_filter (
   model_id INT NOT NULL,
   operand_left INT,
   operand_right INT,
   operator INT
);
