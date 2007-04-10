-- ----------------------------------------------------------------------
-- SQL create script for delphi main object info tables
-- The image info is denormalized for simplicity and performance,
-- since the system requirements are fixed for now. Can later
-- Provide multiple images with a proper normalized table if need be.
-- ----------------------------------------------------------------------

USE delphi;

-- The DBInfo table has a single row and is just used to hold system-wide
-- parameters such as the sizes of alternate image sizes, the version of this
-- DB schema, etc.
DROP TABLE IF EXISTS DBInfo;
CREATE TABLE DBInfo (
  `version`           VARCHAR(16) NOT NULL,
  -- TFacetMaskWidth must agree with the FacetMaskCache definition
  `facetMaskWidth`    TINYINT(2) NULL DEFAULT 32,
  -- Be general about orientation, and store long and short side sizes.
  `thumb_long_side`   SMALLINT(4) UNSIGNED DEFAULT 80,
  `thumb_short_side`  SMALLINT(4) UNSIGNED DEFAULT 60,
  `medium_long_side`  SMALLINT(4) UNSIGNED DEFAULT 640,
  `medium_short_side` SMALLINT(4) UNSIGNED DEFAULT 480,
  `thumb_basepath`    VARCHAR(255) NULL,    
  `medium_basepath`   VARCHAR(255) NULL,    
  `large_basepath`    VARCHAR(255) NULL,    
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time` timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP
);
SHOW WARNINGS;

INSERT INTO DBInfo( version, creation_time ) 
  VALUES( '0.1 alpha', now() );
SHOW WARNINGS;

-- Define the main object table
DROP TABLE IF EXISTS `objects`;
CREATE TABLE `objects` (
  `id`            BIGINT(10) PRIMARY KEY NOT NULL,
  `objnum`        VARCHAR(80) NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `description`   text NULL,
  -- All paths are relative to the configured image roots`
  `img_path`  VARCHAR(255) NULL,    
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time` timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
  INDEX `obj_id_index` (`id`)
)ENGINE=InnoDB;
SHOW WARNINGS;

--Sets allow users (or the system) to group together items for
-- a particular reason (or simply as favorites).
DROP TABLE IF EXISTS `sets`;
CREATE TABLE `sets` (
  `id` bigint(20) PRIMARY KEY NOT NULL auto_increment,
  `name` VARCHAR(255) NOT NULL,
  `description` text NULL,
  `policy` enum('public', 'private'), -- later: friends, etc.
  `owner_id` bigint(20) NOT NULL,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time` timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
  INDEX `sets_id_index` (`id`),
  CONSTRAINT `se_ibfk_1` FOREIGN KEY (`owner_id`)
      REFERENCES `user` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

DROP TABLE IF EXISTS `set_objs`;
CREATE TABLE `set_objs` (
  `set_id` bigint(20) NOT NULL,
  `obj_id` bigint(20) NOT NULL,
  INDEX `so_set_index` (`set_id`),
  INDEX `so_obj_index` (`obj_id`),
  CONSTRAINT `seo_ibfk_1` FOREIGN KEY (`set_id`)
      REFERENCES `sets` (`id`) ON DELETE CASCADE,
  CONSTRAINT `seo_ibfk_2` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

/*
 * Categories are the nodes in the taxonomy.
 * Note that we tie each category to a facet explicitly.
 * We also specify how it is represented in the caches.
 * Note that categories that are tied to ranges, etc. do not have
 * the value in the cache - the category is assigned IFF the range
 * condition is met, and then the category acts as a boolean attribute.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id`        BIGINT(10) PRIMARY KEY NOT NULL auto_increment,
  -- May not have a parent if facet root ; otherwise parent is non-null and >0.
  `parent_id` BIGINT(10) NULL,
  -- Note that the names may not be unique, since they can appear in multiple
  -- facets and even in multiple places in one facet.
  `name`          VARCHAR(255) NOT NULL,
  `display_name`  VARCHAR(255) NOT NULL,
  `facet_id`  BIGINT(10) NULL,
  -- Whether children are exclusive
  `select_mode` ENUM ('single', 'multiple' ) NOT NULL DEFAULT 'multiple',
  /* This portion must be created algorithmically by the
   * mechanism that denormalizes the tables into masks.
  -- If total nodes in a facet is > ( 255 * maskwidth) then MaskIndexBase becomes a MEDIUMINT
  MaskIndexBase TINYINT(3) UNSIGNED NOT NULL DEFAULT 0, -- which mask segment is this in?
  NMasks TINYINT(3) UNSIGNED NOT NULL DEFAULT 1, -- which mask segment is this in?
  MaskBit TINYINT(2) UNSIGNED NOT NULL,    -- which bit in the mask is this?
 */
  `always_inferred` BIT(1) NULL,
  INDEX `cat_id_index` (`id`),
  INDEX `cat_name_index` (`name`),    -- Removed until need clarified
  CONSTRAINT `cat_ibfk_1` FOREIGN KEY (`parent_id`)
    REFERENCES `categories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `cat_ibfk_2` FOREIGN KEY (`facet_id`)
      REFERENCES `facets` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

/*
 * Provides all the hooks (matching tokens) for Categories.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `hooks`;
CREATE TABLE `hooks` (
  `id`        BIGINT(10) PRIMARY KEY NOT NULL auto_increment,
  `cat_id` BIGINT(10) NULL,
  `token`      VARCHAR(255) NOT NULL,
  INDEX `hk_cat_index` (`cat_id`),
  INDEX `hk_token_index` (`token`),
  CONSTRAINT `hk_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

/*
 * Provides all the exclusions (countraindications) for Categories.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `exclusions`;
CREATE TABLE `exclusions` (
  `id`        BIGINT(10) PRIMARY KEY NOT NULL auto_increment,
  `cat_id` BIGINT(10) NULL,
  `token`      VARCHAR(255) NOT NULL,
  INDEX `ex_cat_index` (`cat_id`),
  INDEX `ex_token_index` (`token`),
  CONSTRAINT `ex_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

/*
 * Facets define how we set up the caches and the annotations.
 * A facet includes the facet name as well as policies for adding
 *  annotations using the facet categories.
 * Certain features are key in the UI binding, including the description of:
 *  - geographic placenames and relations (e.g., regional containment)
 *  - people names and relations (e.g., familial containment)
 * We mark these in the facets so we can pick the right place to hunt for
 *  the home towns and family placenames of Creators.
 * This script is here for documentation only; it is created from the facetmap
 * imported at index time. The column sizes for the maskIndex and maskSize
 * must match the optimized definitions in the FacetCache table.
 */
DROP TABLE IF EXISTS `facets`;
CREATE TABLE `facets` (
  `id`             BIGINT(10) PRIMARY KEY NOT NULL auto_increment,
  `name`           VARCHAR(255) NOT NULL,
-- In UI, if not filtering. E.g.: "All People"
  `display_name`   VARCHAR(255) NOT NULL,
  `num_categories` BIGINT(5) UNSIGNED NULL,
  `num_masks`      TINYINT(3) UNSIGNED NULL
--  `root_cat_id`  INT(10) UNSIGNED NULL,
--  CONSTRAINT `fcat_ibfk_1` FOREIGN KEY (`root_cat_id`)
--      REFERENCES `categories` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

/*
 * Facet Masks Cache all the categories from a facet that are associated to
 * a given image, as a sparse list of bitmasks. This is fast and space efficient.
 * If we a facet has 200 categories and we have chosen a mask size of 32 (BIGINT)
 * then there will be 8 potential masks for each image. In practice, each image
 * will tend to have only a few masks at most, and so we get caching with little cost.
 * Each category knows which mask segment it is in, and which bit in that segment.
 * The app will hold this info for efficiency at query time.
 * An analysis app will build this at index time - we consider the type of facet,
 * and other features of the facet and the frequency and cooccurrence of categories
 * within the facet, and pick an optimal mask size and bit assignment to minimize
 * the space used by the caches and maximize the query-time efficiency.
 * Because of that, the following is just an indicator of the FacetCache table
 * definition - it must be defined at index time based upon the analysis, and then
 * constructed from the existing category associations (and facetmask inferrence info).

CREATE TABLE NewPhotoDB.FacetCaches (
  ImageID INT(10) PRIMARY KEY NOT NULL,    -- Which image is this bound to?
  -- If total nodes in a facet is > ( 255 * maskwidth) then MaskIndex becomes a MEDIUMINT
  MaskIndex TINYINT(3) NOT NULL DEFAULT 0,  -- Which part of the mask is this?
  -- Code will decide whether mask is a BIGINT, an INT or a MEDIUMINT
  mask BIGINT(16) NOT NULL,      -- holds the top facet mask
  UNIQUE INDEX FacetIDIndex (ImageID, MaskIndex),
);
 */

/*
 * Provides associations of categorizations of objects.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `obj_cats`;
CREATE TABLE `obj_cats` (
  `obj_id`      bigint(20) NOT NULL,
  `cat_id`      bigint(20) NOT NULL,
  `inferred`    BIT(1) NOT NULL DEFAULT 0,
  `reliability` TINYINT(1) NOT NULL DEFAULT 9,
  INDEX `oc_obj_index` (`obj_id`),
  INDEX `oc_cat_index` (`cat_id`),
  CONSTRAINT `obc_ibfk_1` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`) ON DELETE CASCADE,
  CONSTRAINT `obc_ibfk_2` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB;
SHOW WARNINGS;

