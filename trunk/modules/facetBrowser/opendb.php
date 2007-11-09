<?php

require_once("../../libs/env.php");

    // Connect to the mysql database.
    $mysqli = new mysqli("sierra.ist.berkeley.edu", "$CFG->dbuser", "$CFG->dbpass", "$CFG->dbname", "3031");
?>	