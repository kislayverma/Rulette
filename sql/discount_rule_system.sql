use rule_system;

CREATE TABLE `discount_rule_system` (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT,
  `brand` varchar(255) DEFAULT NULL,
  `article_type` varchar(255) DEFAULT NULL,
  `fashion_type` varchar(100) DEFAULT NULL,
  `season` char(32) DEFAULT NULL,
  `year` char(4) DEFAULT NULL,
  `style_id` int(11) DEFAULT NULL,
  `valid_date_range` varchar(100) NULL,
  `is_active` TINYINT NOT NULL DEFAULT '1',
  `rule_output_id` int(11) NOT NULL,
  PRIMARY KEY (`rule_id`),
  CONSTRAINT `FK_DRS_D` FOREIGN KEY (`rule_output_id`) REFERENCES `discount` (`discount_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `discount` (
  `discount_id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `on_buy_amount` int(11) NOT NULL DEFAULT '0',
  `on_buy_count` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `get_count` int(11) NOT NULL DEFAULT '0',
  `get_amount` float NOT NULL DEFAULT '0',
  `get_percent` int(11) NOT NULL DEFAULT '0',
  `voucher_id` int(11) NULL,
  `free_item_id` int(11) NULL,  
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `discount_funding` varchar(255) DEFAULT NULL,
  `funding_percentage` decimal(5,2) DEFAULT NULL,
  `funding_tax` tinyint(1) DEFAULT NULL,
  `funding_basis` varchar(255) DEFAULT NULL,
  `discount_limit` decimal(5,2) DEFAULT NULL,
  `is_on_cart` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`discount_id`)
);

INSERT INTO rule_system.discount 
(discount_id, type, on_buy_amount, on_buy_count, name, get_count, get_amount, get_percent, voucher_id, 
 free_item_id, is_on_cart, discount_funding, funding_percentage, funding_tax, funding_basis, discount_limit)

SELECT DISTINCT
  a.discount_id,
  a.type,
  on_buy_amount,
  on_buy_count,
  name,
  get_count,
  get_amount,
  get_percent,
  null AS voucher_id,
  null AS free_item_id,
  a.is_on_cart,
  discount_funding,
  funding_percentage,
  funding_tax,
  funding_basis,
  discount_limit
FROM myntra.discount_rule AS a 
JOIN myntra.discount AS b ON (a.discount_id = b.id);


INSERT INTO discount_rule_system
(brand, article_type, fashion_type, season, year, style_id, valid_date_range, is_active, rule_output_id)

SELECT
  style_brand AS brand,
  style_article_type AS article_type,
  style_fashion_type AS fashion_type,
  style_season AS season,
  style_year AS year,
  style_id,
  concat (b.started_on,'-', b.expired_on) AS valid_date_range,
  is_enabled AS is_active,
  b.id AS rule_output_id
FROM myntra.discount_filter AS a
JOIN myntra.discount AS b ON (a.discount_id = b.id)
JOIN myntra.discount_style_map AS c ON (b.id = c.discount_id)
WHERE is_dynamic = 0

UNION ALL

SELECT
  style_brand AS brand,
  style_article_type AS article_type,
  style_fashion_type AS fashion_type,
  style_season AS season,
  style_year AS year,
  null AS style_id,
  concat (b.started_on,'-', b.expired_on) AS valid_date_range,
  is_enabled AS is_active,
  b.id as rule_output_id
FROM myntra.discount_filter AS a
JOIN myntra.discount AS b ON (a.discount_id = b.id)
WHERE is_dynamic = 1;



INSERT INTO rule_input 
(name, rule_system_id, priority, data_type)
VALUES 
('brand', 1, 1, 'Value'),
('article_type', 1, 2, 'Value'),
('fashion_type', 1, 3, 'Value'),
('season', 1, 4, 'Value'),
('style_id', 1, 5, 'Value'),
('valid_date_range', 1, 6, 'Range'),
('is_active', 1, 7, 'Value');


