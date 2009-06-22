-- ----------------------------------------------------------------------
-- SQL create script for delphi main object info tables
-- ----------------------------------------------------------------------

-- The DBInfo table has a single row and is just used to hold system-wide
-- parameters such as the sizes of alternate image sizes, the version of this
-- DB schema, etc.
DROP TABLE IF EXISTS DBInfo;
CREATE TABLE DBInfo (
  `version`           VARCHAR(16) NOT NULL,
  `lockoutActive`     boolean NOT NULL default false, -- Allows for maintenance lockout
  -- TFacetMaskWidth must agree with the FacetMaskCache definition
  `facetMaskWidth`    TINYINT(2) NULL DEFAULT 32,
  -- cache the number of objects and those with images
  `n_objs_total`      INT(8) NOT NULL DEFAULT 0,
  `n_objs_w_imgs`     INT(8) NOT NULL DEFAULT 0,
  -- Be general about orientation, and store long and short side sizes.
  `thumb_long_side`   SMALLINT(4) UNSIGNED DEFAULT 80,
  `thumb_short_side`  SMALLINT(4) UNSIGNED DEFAULT 60,
  `medium_long_side`  SMALLINT(4) UNSIGNED DEFAULT 640,
  `medium_short_side` SMALLINT(4) UNSIGNED DEFAULT 480,
  `thumb_basepath`    VARCHAR(255) NULL,    
  `medium_basepath`   VARCHAR(255) NULL,    
  `large_basepath`    VARCHAR(255) NULL,    
  `creation_time`     timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`          timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP
); 
SHOW WARNINGS;

INSERT INTO DBInfo( version, creation_time ) 
  VALUES( '0.2 alpha', now() );
SHOW WARNINGS;

-- Define the main object table
-- The image info is denormalized for simplicity and performance,
-- based upon the initial system requirements. Must also
-- provide additional images with a proper normalized table.
DROP TABLE IF EXISTS `objects`;
CREATE TABLE `objects` (
  `id`            INT(10) UNSIGNED PRIMARY KEY NOT NULL,
  `objnum`        VARCHAR(80) NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `description`   text NULL,
  `hiddenNotes`   text NULL,
  -- All paths are relative to the configured image roots
  `img_path`      VARCHAR(255) NULL,    
  `img_ar`        DOUBLE(7,3) UNSIGNED NULL,    
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`      timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
	-- Enable full text (keyword) search on the name and description
  FULLTEXT KEY `obj_fulltext_index` (`name`,`description`,`hiddenNotes`)
)ENGINE=MyIsam;
SHOW WARNINGS;

-- Define the media table
-- This is for media associated to an object. It may be a derivative or view.
-- Note that we have only the relative paths, and prepend base paths both
-- to handle location on the site, and as well to handle various derivative
-- forms (e.g., thumbs, mids, audio-previews, etc.).
-- The media type indicates the type of the set of derivatives. Therefore,
-- if there is an image associated with an audio or video object, the type
-- only refers to the image. In this case, there may be multiple media
-- with different types.
-- For most objects, the default image is denormalized into the objects table
-- to speed up the generation of results views.
DROP TABLE IF EXISTS `media`;
CREATE TABLE `media` (
  `id`            INT(10) UNSIGNED PRIMARY KEY auto_increment NOT NULL,
  `obj_id`        INT(10) UNSIGNED NOT NULL,
	-- name is not for the object but of this surrogate 
	-- (e.g., "Obverse view", "Reverse view")
  `name`          VARCHAR(255) NULL,
	-- description is not for the object but of this surrogate 
	-- (e.g., "Frontal view of object taken during restoration.")
  `description`   text NULL,
  -- All paths are relative to the configured media roots
  `path`          VARCHAR(255) NULL,
	-- If we need to distinguish actual mime-types (e.g., among different image types)
	-- we can expand this, or add a mime-type as well. Seems like overkill.
  `type`          ENUM ('image', 'catCard', 'audio', 'video' ) NOT NULL DEFAULT 'image',
	-- width, height are NULL for audio, or if unknown
  `width`         INT(5) UNSIGNED NULL,    
  `height`        INT(5) UNSIGNED NULL,
  -- aspectR is NULL if width or height are NULL
  `aspectR`       DOUBLE(7,3) UNSIGNED NULL,
  `creation_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`      timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
  INDEX `med_obj_id_index` (`obj_id`,`type`),
  CONSTRAINT `me_ibfk_1` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

-- Sets allow users (or the system) to group together items for
-- a particular reason (or simply as favorites).
DROP TABLE IF EXISTS `sets`;
CREATE TABLE `sets` (
  `id`             int(10) unsigned PRIMARY KEY NOT NULL auto_increment,
  `name`           VARCHAR(255) NOT NULL,
  `description`    text NULL,
  `policy`         enum('public', 'private'), -- later: friends, etc.
  `owner_id`       int(10) unsigned NOT NULL,
  `creation_time`  timestamp NOT NULL default '0000-00-00 00:00:00',
  `mod_time`       timestamp NOT NULL default CURRENT_TIMESTAMP 
        on update CURRENT_TIMESTAMP,
  INDEX `sets_id_owner` (`owner_id`),
  CONSTRAINT `se_ibfk_1` FOREIGN KEY (`owner_id`)
      REFERENCES `user` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

DROP TABLE IF EXISTS `set_objs`;
CREATE TABLE `set_objs` (
  `set_id`        int(10) unsigned NOT NULL,
  `obj_id`        int(10) unsigned NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `notes`         text NULL,			-- why did owner put this in the set?
  `use_as_icon`   boolean NOT NULL default false, -- for individual featured objs
  `order_num`     int(2) NOT NULL default 0,      -- To control presentation order
  INDEX `so_set_index` (`set_id`),
  INDEX `so_obj_index` (`obj_id`),
	-- Ensure we have no dupes in a given set
  UNIQUE INDEX `so_so_index` (`set_id`,`obj_id`),
  CONSTRAINT `seo_ibfk_1` FOREIGN KEY (`set_id`)
      REFERENCES `sets` (`id`),
  CONSTRAINT `seo_ibfk_2` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`)
)ENGINE=MyIsam;
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
  `id`              INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  -- May not have a parent if facet root ; otherwise parent is non-null and >0.
  `parent_id`       INT(10) UNSIGNED NULL,
  -- Note that the names may not be unique, since they can appear in multiple
  -- facets and even in multiple places in one facet.
  `name`            VARCHAR(255) NOT NULL,
  `display_name`    VARCHAR(255) NOT NULL,
  `facet_id`        INT(10) UNSIGNED NULL,
  -- Whether children are exclusive
  `select_mode`     ENUM ('single', 'multiple' ) NOT NULL DEFAULT 'multiple',
  /* This portion must be created algorithmically by the
   * mechanism that denormalizes the tables into masks.
  -- If total nodes in a facet is > ( 255 * maskwidth) then MaskIndexBase becomes a MEDIUMINT
  MaskIndexBase TINYINT(3) UNSIGNED NOT NULL DEFAULT 0, -- which mask segment is this in?
  NMasks TINYINT(3) UNSIGNED NOT NULL DEFAULT 1, -- which mask segment is this in?
  MaskBit TINYINT(2) UNSIGNED NOT NULL,    -- which bit in the mask is this?
 */
  `always_inferred` TINYINT(1) NOT NULL default 0,
  `n_matches`       int(10) NOT NULL default 0,
  `n_matches_w_img` int(10) NOT NULL default 0,
  INDEX `cat_name_index` (`name`),    -- Removed until need clarified
	-- Enable full text (keyword) search on the name and description
  FULLTEXT KEY `cat_fulltext_index` (`display_name`),
  CONSTRAINT `cat_ibfk_1` FOREIGN KEY (`parent_id`)
    REFERENCES `categories` (`id`),
  CONSTRAINT `cat_ibfk_2` FOREIGN KEY (`facet_id`)
      REFERENCES `facets` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Provides all the hooks (matching tokens) for Categories.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `hooks`;
CREATE TABLE `hooks` (
  `id`       INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  `cat_id`   INT(10) UNSIGNED NULL,
  `token`    VARCHAR(255) NOT NULL,
  INDEX `hk_cat_index` (`cat_id`),
  INDEX `hk_token_index` (`token`),
	-- This can be omitted if not supporting api/checkTerms.php
	FULLTEXT KEY `hk_token_fulltext` (token),
  CONSTRAINT `hk_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Provides all the exclusions (countraindications) for Categories.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `exclusions`;
CREATE TABLE `exclusions` (
  `id`       INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  `cat_id`   INT(10) UNSIGNED NULL,
  `token`    VARCHAR(255) NOT NULL,
  INDEX `ex_cat_index` (`cat_id`),
  INDEX `ex_token_index` (`token`),
  CONSTRAINT `ex_ibfk_1` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
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
  `id`             INT(10) UNSIGNED PRIMARY KEY NOT NULL auto_increment,
  `name`           VARCHAR(255) NOT NULL,
-- In UI, if not filtering. E.g.: "All People"
  `display_name`   VARCHAR(255) NOT NULL,
  `description`    text NULL,
  `notes`          text NULL,
  `num_categories` INT(10) UNSIGNED UNSIGNED NULL,
  `num_masks`      TINYINT(3) UNSIGNED NULL
--  `root_cat_id`  INT(10) UNSIGNED NULL,
--  CONSTRAINT `fcat_ibfk_1` FOREIGN KEY (`root_cat_id`)
--      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Facet Masks Cache all the categories from a facet that are associated to
 * a given image, as a sparse list of bitmasks. This is fast and space efficient.
 * If a facet has 200 categories and we have chosen a mask size of 32 (INT)
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
  `obj_id`      int(10) unsigned NOT NULL,
  `cat_id`      int(10) unsigned NOT NULL,
  `inferred`    TINYINT(1) NOT NULL DEFAULT 0,
  `reliability` TINYINT(1) NOT NULL DEFAULT 9,
  INDEX `oc_obj_index` (`obj_id`),
  INDEX `oc_cat_index` (`cat_id`),
  UNIQUE INDEX `oc_obj_cat_index` (`obj_id`,`cat_id`),
  CONSTRAINT `obc_ibfk_1` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`),
  CONSTRAINT `obc_ibfk_2` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Provides associations of categorizations of objects with images.
 * This is a denormalized table to speed queries.
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `obj_cats_wimgs`;
CREATE TABLE `obj_cats_wimgs` (
  `obj_id`      int(10) unsigned NOT NULL,
  `cat_id`      int(10) unsigned NOT NULL,
  `inferred`    TINYINT(1) NOT NULL DEFAULT 0,
  `reliability` TINYINT(1) NOT NULL DEFAULT 9,
  INDEX `ocwi_obj_index` (`obj_id`),
  INDEX `ocwi_cat_index` (`cat_id`),
  UNIQUE INDEX `ocwi_obj_cat_index` (`obj_id`,`cat_id`),
  CONSTRAINT `ocwi_ibfk_1` FOREIGN KEY (`obj_id`)
      REFERENCES `objects` (`id`),
  CONSTRAINT `ocwi_ibfk_2` FOREIGN KEY (`cat_id`)
      REFERENCES `categories` (`id`)
)ENGINE=MyIsam;
SHOW WARNINGS;

/*
 * Defines the tag information
 * TODO add creation and modification times.
 */
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `tag_id` int(11) NOT NULL auto_increment,
  `tag_name` varchar(50) NOT NULL,
  `tag_count` int(11) NOT NULL default '1',
  PRIMARY KEY  (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`),
) ENGINE=MyISAM;

/*
 * Provides associations of tags to objects, by user.
 * TODO add creation time.
 */
DROP TABLE IF EXISTS `tag_user_object`;
CREATE TABLE `tag_user_object` (
  `tag_id` int(11) NOT NULL,
  `tag_user_id` int(11) NOT NULL,
  `tag_object_id` int(11) NOT NULL,
  PRIMARY KEY  (`tag_id`,`tag_user_id`,`tag_object_id`),
  KEY `tuo_id_idx` (`tag_id`),
  KEY `tuo_user_idx` (`tag_user_id`),
  KEY `tuo_object_idx` (`tag_object_id`)
) ENGINE=MyISAM;

