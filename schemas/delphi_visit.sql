
--
-- Table structure for table `visit_request`
-- The when field is in the timezone of the museum.
--
DROP TABLE IF EXISTS `visit_request`;
SHOW WARNINGS;
CREATE TABLE `visit_request` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `requester_id` bigint(20) unsigned NOT NULL,
--  `set_id` bigint(20) unsigned NOT NULL,
  `when` DATETIME NOT NULL default '0000-00-00 00:00:00',
  `notes` text,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time` timestamp NOT NULL default CURRENT_TIMESTAMP
				on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  CONSTRAINT `vr_ibfk_1` FOREIGN KEY (`requester_id`)
			  REFERENCES `user` (`id`),
--  CONSTRAINT `vr_ibfk_2` FOREIGN KEY (`set_id`)
--			  REFERENCES `set` (`id`),
  KEY `requester` (`requester_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SHOW WARNINGS;


