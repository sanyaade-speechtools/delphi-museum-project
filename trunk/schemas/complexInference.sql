-- ----------------------------------------------------------------------
-- SQL create script for delphi complex inference support
--  This allows inferring one concept given several others, plus constraints
-- ----------------------------------------------------------------------

use onto_maint;

-- Define the main object table
-- The image info is denormalized for simplicity and performance,
-- based upon the initial system requirements. Must also
-- provide additional images with a proper normalized table.
DROP TABLE IF EXISTS `inferred_concepts`;
CREATE TABLE `inferred_concepts` (
  `id`            INT(10) UNSIGNED PRIMARY KEY NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `notes`         text NULL,
  `infer`         INT(10) UNSIGNED NULL,
  `reliability`   TINYINT(1) NOT NULL DEFAULT 9,
	-- Default to requiring all required concepts
  `req_all`       TINYINT(1) NOT NULL DEFAULT 1,
	-- Default to excluding any excluded concepts
  `excl_all`      TINYINT(1) NOT NULL DEFAULT 0,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`      timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
  INDEX `infer_id_index` (`infer`),
  CONSTRAINT `infc_ibfk_1` FOREIGN KEY (`infer`)
    REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Provides the required concepts for a complex inference
 * req_id is the required concept id, inf_id is the inferred concept
 */
DROP TABLE IF EXISTS `inf_required`;
CREATE TABLE `inf_required` (
  `id`            INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  `req_id`        INT(10) UNSIGNED NULL,
  `inf_id`        INT(10) UNSIGNED NULL,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`      timestamp NOT NULL default CURRENT_TIMESTAMP 
  INDEX `infrq_cat_index` (`cat_id`),
  INDEX `infrq_inf_index` (`inf_id`),
  CONSTRAINT `infrq_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`),
  CONSTRAINT `infrq_ibfk_2` FOREIGN KEY (`inf_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Provides the required concepts for a complex inference
 */
DROP TABLE IF EXISTS `inf_excluded`;
CREATE TABLE `inf_excluded` (
  `id`            INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  `excl_id`       INT(10) UNSIGNED NULL,
  `inf_id`        INT(10) UNSIGNED NULL,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`      timestamp NOT NULL default CURRENT_TIMESTAMP 
  INDEX `infex_cat_index` (`cat_id`),
  INDEX `infex_inf_index` (`inf_id`),
  CONSTRAINT `infex_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`),
  CONSTRAINT `infex_ibfk_2` FOREIGN KEY (`inf_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;
