DROP TABLE IF EXISTS `m_service_account_status_history`;
CREATE TABLE `m_service_account_status_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `service_account_id` bigint(20) NOT NULL,
  `date` date DEFAULT NULL,
  `change_enum` int(11) NOT NULL,
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;