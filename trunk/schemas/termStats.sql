-- ----------------------------------------------------------------------
-- SQL create script for delphi termStats table
-- ----------------------------------------------------------------------

-- Define the concordance table
-- Need to take care that maximum tokenlength produced by tool 
-- does not exceed 255 or we need to change from VARCHAR to text.
DROP TABLE IF EXISTS `termStats`;
CREATE TABLE `termStats` (
  `term`     VARCHAR(255) NOT NULL,
  `count`    INT(10) NOT NULL,
  INDEX `termStats_index` (`term`),
	-- Enable full text (keyword) search on the name and description
  FULLTEXT KEY `terms_fulltext_index` (`term`)
)ENGINE=MyIsam;
SHOW WARNINGS;

-- LOAD DATA LOCAL INFILE 'file.txt' INTO TABLE termStats 
-- CHARACTER SET UTF8 COLUMNS TERMINATED BY '|' (count, term) 
