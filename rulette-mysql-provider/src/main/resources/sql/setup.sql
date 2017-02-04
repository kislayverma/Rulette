
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
