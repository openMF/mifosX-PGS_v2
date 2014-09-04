DROP TABLE IF EXISTS `m_service_account`;
CREATE TABLE `m_service_account` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `pgs_client` bigint(20) NOT NULL,
  `service_account_id` bigint(20) NOT NULL,
  `current_account_information` bigint(20) DEFAULT NULL,
  `usage_record` bigint(20) DEFAULT NULL,
  `transaction_record` bigint(20) DEFAULT NULL,
  `amount` decimal(19,6) NOT NULL,
  `is_activated` tinyint(1) NOT NULL,
  `service_account_status_history` bigint(20) DEFAULT NULL,
  `service_offering` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;