DROP TABLE IF EXISTS `m_transaction_record`; 
CREATE TABLE `m_transaction_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `service_account_id` bigint(20) NOT NULL,
  `description` text,
  `date` date NOT NULL,
  `type_enum` int(11) NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;