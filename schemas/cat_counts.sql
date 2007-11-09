--
-- Table structure for table category counts
-- The when field is in the timezone of the museum.
--
DROP TABLE IF EXISTS `cat_counts` \p;
SHOW WARNINGS;
CREATE TABLE `cat_counts` (
  `id` bigint(20) NOT NULL,
	`count` int(10) NOT NULL
) ENGINE=MyIsam DEFAULT CHARSET=latin1;
SHOW WARNINGS;

-- Calculate counts overall and put in temp table
INSERT INTO cat_counts( id, count )
SELECT cat_id, count(*) FROM obj_cats group by cat_id \p;

-- Add counts to categories table
UPDATE categories, cat_counts SET categories.n_matches=cat_counts.count
WHERE categories.id=cat_counts.id \p;

-- Clear temp table to start over
TRUNCATE TABLE cat_counts \p;

-- Calculate counts with images and put in temp table
INSERT INTO cat_counts( id, count )
SELECT oc.cat_id, count(*) FROM obj_cats oc, objects o 
where oc.obj_id=o.id and NOT o.img_path IS NULL 
group by oc.cat_id \p;

-- Add counts with images to categories table
UPDATE categories, cat_counts SET categories.n_matches_w_img=cat_counts.count
WHERE categories.id=cat_counts.id \p;

-- And clean up after ourselves
DROP TABLE cat_counts \p;
