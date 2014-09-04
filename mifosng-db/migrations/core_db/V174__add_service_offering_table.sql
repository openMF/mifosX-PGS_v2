DROP TABLE IF EXISTS `m_service_offering`;
CREATE TABLE `m_service_offering` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET latin1,
  `description` text CHARACTER SET latin1,
  `fee_structure_enum` tinyint(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;