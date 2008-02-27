<?php
///////////////////////////////////////////////////////////////////////////
//                                                                       //
// Delphi configuration file                                             //
//                                                                       //
// This file should be renamed "config.php" in the top-level directory   //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

unset($CFG);  // Ignore this line

//=========================================================================
// 1. DATABASE SETUP
//=========================================================================
// First, you need to configure the database where all Delphi data       //
// will be stored.  This database must already have been created         //
// and a username/password created to access it.                         //
//                                                                       //
//   mysql      - the prefix is optional, but useful when installing     //
//                into databases that already contain tables.            //
//                                                                       //
//

$CFG->dbtype    = 'mysql';       // mysql
$CFG->dbhost    = 'localhost';   // eg localhost or db.isp.com
$CFG->dbname    = '';      // database name
$CFG->dbuser    = '';    // your database username
$CFG->dbpass    = '';    // your database password
$CFG->prefix    = '';        // Prefix to use for all table names (not working yet)


//=========================================================================
// 2. WEB SITE LOCATION
//=========================================================================
// Now you need to tell Delphi where it is located. Specify the full
// web address to where Delphi has been installed.  If your web site
// is accessible via multiple URLs then choose the most natural one.
//
// Do not include a trailing slash!

$CFG->wwwroot   = 'http://example.com/delphi';


//=========================================================================
// 2.5 SHORT URL BASE
//=========================================================================
// shortbase allows delphi to construct short URLs in conjunction with Apace rewite rules.
//
// Do not include a trailing slash!

$CFG->shortbase   = '/delphi';

//=========================================================================
// 3. SERVER FILES LOCATION
//=========================================================================
// Next, specify the full OS directory path to this same location
// Make sure the upper/lower case is correct.  Some examples:
//
//    $CFG->dirroot = 'c:\program files\easyphp\www\delphi';    // Windows
//    $CFG->dirroot = '/var/www/html/delphi';     // Redhat Linux
//    $CFG->dirroot = '/home/example/public_html/delphi'; // Cpanel host

$CFG->dirroot           = '/home/example/public_html/delphi';
$CFG->dir_image_thumb   = '/home/example/public_html/objimages/thumbs';
$CFG->dir_image_medium  = '/home/example/public_html/objimages/mids';
$CFG->dir_image_zoom    = '/home/example/public_html/objimages/zooms';


//=========================================================================
// 4. DEFAULT THEME
//=========================================================================
// Next, specify which theme should be used by default

$CFG->theme   = 'pahma';


//=========================================================================
// 5. IMAGE PATHS
//=========================================================================
// Next, specify the full web address to the location where object
// images can be accessed. 
//
// Do not include a trailing slash!


$CFG->image_thumb        = 'http://example.com/delphi/objimages/thumbs';
$CFG->image_thumb_square = 'http://example.com/delphi/objimages/thumbs_square';
$CFG->image_medium       = 'http://example.com/delphi/objimages/mids';
$CFG->image_full         = 'http://example.com/delphi/objimages/fulls';
$CFG->image_zoom         = 'http://example.com/delphi/trunk/objimages/zooms';
$CFG->no_image_thumb     = 'http://example.com/delphi/trunk/objimages/thumbs/noimg.jpg';
$CFG->no_image_thumb     = 'http://example.com/objimages/thumbs/noimg.jpg';
$CFG->no_image_thumb_square = 'http://example.com/delphi/trunk/objimages/thumbs_square/noimg.jpg';
$CFG->no_image_medium    = 'http://example.com/delphi/trunk/objimages/mids/noimg.jpg';

//=========================================================================
// 6. Zoomify location
//=========================================================================
// The zoomify swf file must be in the same domain as the the tiled images. 
//
// Do not include a trailing slash!
$CFG->zoomer	= 'http://example.com/delphi/zoomifyDynamicViewer.swf';


//=========================================================================
// 7. IMAGE CONSTANTS
//=========================================================================
// Next, specify the size of derivative images, etc.
//

$CFG->thumbSize          = 120;


//=========================================================================
// 8. CONTACT EMAIL
//=========================================================================
// Specify an email address to recieve messages from the contact form
//

$CFG->contactEmail          = "somebody@example.com";



//=========================================================================
// ALL DONE!  To continue installation, visit your main page with a browser
//=========================================================================









if ($CFG->wwwroot == 'http://example.com/delphi') {
    echo "<p>Error detected in configuration file</p>";
    echo "<p>Your server address can not be: \$CFG->wwwroot = 'http://example.com/delphi';</p>";
    die;
}

?>
