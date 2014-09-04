DROP TABLE IF EXISTS `m_current_account_information`;
CREATE TABLE `m_current_account_information` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `service_account_id` bigint(20) DEFAULT NULL, 
  `pgs_client_id` bigint(20) NOT NULL,
  `savings_id` bigint(20) DEFAULT NULL,
  `balance` decimal(19,6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;