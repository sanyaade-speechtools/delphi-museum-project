<?php

require_once("../../libs/env.php");

    // Connect to the mysql database.
    $mysqli = new mysqli("$CFG->dbhost", "$CFG->dbuser", "$CFG->dbpass", "$CFG->dbname");
?>