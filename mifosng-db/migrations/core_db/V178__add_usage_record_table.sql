DROP TABLE if EXISTS `m_usage_record`;
CREATE TABLE `m_usage_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `service_account_id` int(20) NOT NULL,
  `date` date NOT NULL,
  `metric_type_enum` int(11) NOT NULL,
  `number_of_units` decimal(19,6) NOT NULL,
  `start_date` datetime NOT NULL,	  
  `end_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;