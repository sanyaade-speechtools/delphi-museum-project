-- ----------------------------------------------------------------------
-- SQL create script for delphi news content table
-- ----------------------------------------------------------------------

-- Define the news content table
DROP TABLE IF EXISTS `newsContent`;
CREATE TABLE `newsContent` (
  `header`      VARCHAR(255) NOT NULL,
  `content`     text NOT NULL,
  `start_time`  timestamp NULL,
	`end_time`    timestamp NULL
)ENGINE=MyIsam;
SHOW WARNINGS;
