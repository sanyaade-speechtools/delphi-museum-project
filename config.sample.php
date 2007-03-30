<?PHP
///////////////////////////////////////////////////////////////////////////
//                                                                       //
// Delphi configuration file                                             //
//                                                                       //
// This file should be renamed "config.php" in the top-level directory   //
//                                                                       //
///////////////////////////////////////////////////////////////////////////
//                                                                       //
// NOTICE OF COPYRIGHT                                                   //
//                                                                       //
//                                                                       //
// This program is free software; you can redistribute it and/or modify  //
// it under the terms of the GNU General Public License as published by  //
// the Free Software Foundation; either version 2 of the License, or     //
// (at your option) any later version.                                   //
//                                                                       //
// This program is distributed in the hope that it will be useful,       //
// but WITHOUT ANY WARRANTY; without even the implied warranty of        //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         //
// GNU General Public License for more details:                          //
//                                                                       //
//          http://www.gnu.org/copyleft/gpl.html                         //
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
$CFG->dbname    = 'delphi';      // database name
$CFG->dbuser    = 'username';    // your database username
$CFG->dbpass    = 'password';    // your database password
$CFG->prefix    = 'delphi_';        // Prefix to use for all table names


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
// 3. SERVER FILES LOCATION
//=========================================================================
// Next, specify the full OS directory path to this same location
// Make sure the upper/lower case is correct.  Some examples:
//
//    $CFG->dirroot = 'c:\program files\easyphp\www\moodle';    // Windows
//    $CFG->dirroot = '/var/www/html/moodle';     // Redhat Linux
//    $CFG->dirroot = '/home/example/public_html/moodle'; // Cpanel host

$CFG->dirroot   = '/home/example/public_html/delphi';


//=========================================================================
// ALL DONE!  To continue installation, visit your main page with a browser
//=========================================================================









if ($CFG->wwwroot == 'http://example.com/delphi') {
    echo "<p>Error detected in configuration file</p>";
    echo "<p>Your server address can not be: \$CFG->wwwroot = 'http://example.com/delphi';</p>";
    die;
}

?>
