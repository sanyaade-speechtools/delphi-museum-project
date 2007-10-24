-- ----------------------------------------------------------------------
-- SQL create script for delphi main object info tables
-- Assumes main DB tables have already been created.
-- ----------------------------------------------------------------------

USE delphi;

-- Featured sets are shown on the first page to invite people in.
-- We denormalize for speed - this should be built out of the other tables.
-- We may adjust the name and username for presentation, which breaks
-- the pure denormalization model...
-- There should be about 6 of these
-- featured objects support
DROP TABLE IF EXISTS `featured_sets` \p;
CREATE TABLE `featured_sets` (
  `set_id`      INT(10) unsigned NOT NULL,
  `name`        VARCHAR(255) NOT NULL,          -- From the sets table
  `img_path`    VARCHAR(255) NOT NULL,          -- From the sets table
  `aspectR`     DOUBLE(7,3) UNSIGNED NULL,
  `owner_name`  VARCHAR(40) NOT NULL,           -- From the users table
  `porder`      INT(2) NOT NULL default 0,      -- To control presentation order
  CONSTRAINT `fese_ibfk_1` FOREIGN KEY (`set_id`)
      REFERENCES `sets` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

