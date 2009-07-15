-- ----------------------------------------------------------------------
-- SQL create script for delphi termStats table
-- ----------------------------------------------------------------------

-- Define the concordance table
DROP TABLE IF EXISTS `termStats`;
CREATE TABLE `termStats` (
  `term`     text NOT NULL,
  `count`    INT(10) NOT NULL,
	-- Enable full text (keyword) search on the name and description
  FULLTEXT KEY `terms_fulltext_index` (`term`)
)ENGINE=MyIsam;
SHOW WARNINGS;

-- LOAD DATA LOCAL INFILE 'file.txt' INTO TABLE termStats 
-- CHARACTER SET UTF8 COLUMNS TERMINATED BY '|' (count, term) 
