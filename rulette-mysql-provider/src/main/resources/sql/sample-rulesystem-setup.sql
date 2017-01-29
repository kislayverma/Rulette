CREATE DATABASE tax;

USE tax;

CREATE TABLE rule_system (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `table_name` varchar(100) NOT NULL,
  `output_column_name` varchar(256) DEFAULT NULL,
  `unique_id_column_name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `rule_input` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `rule_system_id` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  `rule_type` varchar(45) NOT NULL,
  `data_type` varchar(45) NOT NULL,
  `range_lower_bound_field_name` varchar(256) NOT NULL,
  `range_upper_bound_field_name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `vat_rule_system` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `source_state` varchar(100) NULL,
  `item_type` int NULL,
  `material` varchar(100) NULL,
  `mrp_threshold` varchar(100) NULL,
  `rule_output_id` VARCHAR(256) NOT NULL,  
  PRIMARY KEY (`id`)
);

CREATE TABLE govt_vat_value (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tax_type` VARCHAR(20) NOT NULL,
  `tax_rate` DECIMAL(12,3) NOT NULL,
  PRIMARY KEY (`id`)
);

GRANT ALL PRIVILEGES ON tax.* TO 'taxUser'@'%';

FLUSH PRIVILEGES;

INSERT INTO rule_system 
  (`name`, `table_name`, `output_column_name`, `unique_id_column_name`)
VALUES 
  ('vat_rule_system', 'tax.vat_rule_system', 'rule_output_id', 'id');

INSERT INTO rule_input 
  (`name`, `rule_system_id`, `priority`, `rule_type`, `data_type`, `range_lower_bound_field_name`, `range_upper_bound_field_name`)
VALUES 
  ('source_state', 1, 1, 'VALUE', 'STRING', NULL, NULL);
INSERT INTO rule_input 
  (`name`, `rule_system_id`, `priority`, `rule_type`, `data_type`, `range_lower_bound_field_name`, `range_upper_bound_field_name`)
VALUES 
  ('item_type', 1, 2, 'VALUE', 'NUMBER', NULL, NULL);
INSERT INTO rule_input 
  (`name`, `rule_system_id`, `priority`, `rule_type`, `data_type`, `range_lower_bound_field_name`, `range_upper_bound_field_name`)
VALUES 
  ('material', 1, 3, 'VALUE', 'STRING', NULL, NULL);
INSERT INTO rule_input 
  (`name`, `rule_system_id`, `priority`, `rule_type`, `data_type`, `range_lower_bound_field_name`, `range_upper_bound_field_name`)
VALUES 
  ('mrp_threshold', 1, 4, 'RANGE', 'NUMBER', 'min_mrp', 'max_mrp');

INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 0.0);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 1.0);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 1.2);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 5.0);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 5.25);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 5.5);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 12.5);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 13.125);
INSERT INTO govt_vat_value (`tax_type`, `tax_rate`) VALUES ('VAT', 14.5);

INSERT INTO `vat_rule_system` VALUES (1,NULL,1,NULL,NULL,'1'),(5,'DEL',1,NULL,NULL,'1'),(9,'HAR',1,NULL,NULL,'9'),(13,'KAR',1,NULL,NULL,'3'),(17,'MAH',1,NULL,NULL,'1'),(21,'TEL',1,NULL,NULL,'3'),(25,'WES',1,NULL,NULL,'3'),(29,NULL,10,NULL,NULL,'7'),(41,'DEL',10,NULL,NULL,'7'),(42,'DEL',10,'gold',NULL,'5'),(43,'DEL',10,'platinum',NULL,'5'),(44,'DEL',10,'silver',NULL,'5'),(54,'HAR',10,NULL,NULL,'2'),(55,'HAR',10,'gold',NULL,'5'),(56,'HAR',10,'platinum',NULL,'5'),(57,'HAR',10,'silver',NULL,'5'),(67,'KAR',10,NULL,NULL,'8'),(68,'KAR',10,'gold',NULL,'5'),(69,'KAR',10,'platinum',NULL,'5'),(70,'KAR',10,'silver',NULL,'5'),(80,'MAH',10,NULL,NULL,'8'),(81,'MAH',10,'gold',NULL,'6'),(82,'MAH',10,'platinum',NULL,'6'),(83,'MAH',10,'silver',NULL,'6'),(93,'TEL',10,NULL,NULL,'7'),(94,'TEL',10,'gold',NULL,'5'),(95,'TEL',10,'platinum',NULL,'5'),(96,'TEL',10,'silver',NULL,'5'),(106,'WES',10,NULL,NULL,'7'),(107,'WES',10,'gold',NULL,'5'),(108,'WES',10,'platinum',NULL,'5'),(109,'WES',10,'silver',NULL,'5'),(118,NULL,11,NULL,NULL,'4'),(122,'DEL',11,NULL,NULL,'7'),(126,'HAR',11,NULL,NULL,'4'),(130,'KAR',11,NULL,NULL,'4'),(134,'MAH',11,NULL,NULL,'4'),(138,'TEL',11,NULL,NULL,'4'),(142,'WES',11,NULL,NULL,'4'),(145,NULL,2,NULL,NULL,'7'),(149,'DEL',2,NULL,NULL,'7'),(153,'HAR',2,NULL,NULL,'2'),(157,'KAR',2,NULL,NULL,'8'),(161,'MAH',2,NULL,NULL,'8'),(165,'TEL',2,NULL,NULL,'7'),(169,'WES',2,NULL,NULL,'7'),(172,NULL,3,NULL,NULL,'4'),(176,'DEL',3,NULL,NULL,'4'),(180,'HAR',3,NULL,NULL,'4'),(184,'KAR',3,NULL,NULL,'4'),(188,'MAH',3,NULL,NULL,'4'),(192,'WES',3,NULL,NULL,'4'),(196,'WES',3,NULL,NULL,'4'),(199,NULL,4,NULL,NULL,'7'),(203,'DEL',4,NULL,NULL,'5'),(207,'HAR',4,NULL,NULL,'5'),(211,'KAR',4,NULL,NULL,'5'),(215,'MAH',4,NULL,NULL,'5'),(219,'TEL',4,NULL,NULL,'5'),(223,'WES',4,NULL,NULL,'5'),(226,NULL,5,NULL,NULL,'1'),(230,'DEL',5,NULL,NULL,'1'),(231,'DEL',5,'herbal',NULL,'7'),(238,'HAR',5,NULL,NULL,'9'),(239,'HAR',5,'herbal',NULL,'2'),(246,'KAR',5,NULL,NULL,'3'),(247,'KAR',5,'herbal',NULL,'8'),(254,'MAH',5,NULL,NULL,'1'),(255,'MAH',5,'herbal',NULL,'8'),(262,'TEL',5,NULL,NULL,'3'),(263,'TEL',5,'herbal',NULL,'7'),(270,'WES',5,NULL,NULL,'3'),(271,'WES',5,'herbal',NULL,'7'),(277,NULL,6,NULL,NULL,'1'),(281,'DEL',6,NULL,NULL,'1'),(282,'DEL',6,'fabric',NULL,'7'),(289,'HAR',6,NULL,NULL,'9'),(290,'HAR',6,'fabric',NULL,'2'),(297,'KAR',6,NULL,NULL,'3'),(298,'KAR',6,'fabric',NULL,'8'),(305,'MAH',6,NULL,NULL,'1'),(306,'MAH',6,'fabric',NULL,'8'),(313,'TEL',6,NULL,NULL,'3'),(314,'TEL',6,'fabric',NULL,'7'),(321,'WES',6,NULL,NULL,'3'),(322,'WES',6,'fabric',NULL,'7'),(327,NULL,7,NULL,NULL,'1'),(331,'DEL',7,NULL,'500.01-999999999','7'),(335,'HAR',7,NULL,'500.01-999999999','2'),(339,'KAR',7,NULL,'500.01-999999999','3'),(343,'MAH',7,NULL,NULL,'1'),(347,'TEL',7,NULL,NULL,'3'),(351,'WES',7,NULL,NULL,'3'),(354,NULL,8,NULL,NULL,'1'),(358,'DEL',8,NULL,NULL,'1'),(362,'HAR',8,NULL,NULL,'9'),(366,'KAR',8,NULL,NULL,'3'),(370,'MAH',8,NULL,NULL,'8'),(380,NULL,9,NULL,NULL,'5'),(383,'DEL',9,NULL,NULL,'5'),(387,'HAR',9,NULL,NULL,'5'),(391,'KAR',9,NULL,NULL,'5'),(395,'MAH',9,NULL,NULL,'6'),(399,'TEL',9,NULL,NULL,'5'),(403,'WES',9,NULL,NULL,'5'),(406,NULL,NULL,NULL,NULL,'7'),(409,'DEL',NULL,NULL,NULL,'7'),(410,'HAR',NULL,NULL,NULL,'2'),(411,'KAR',NULL,NULL,NULL,'8'),(412,'MAH',NULL,NULL,NULL,'8'),(413,'TEL',NULL,NULL,NULL,'7'),(414,'WES',NULL,NULL,NULL,'7');
