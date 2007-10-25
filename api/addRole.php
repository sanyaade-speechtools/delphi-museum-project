<?php
//Bring in the user's config file
require_once('../config.php');

	$badarg = false;
	if(empty($_POST['r']))
		$badarg = true;
	else {
		$rolename = $_POST['r'];
		$roledesc = $_POST['d'];
	}
	if( $badarg ) {
		header("HTTP/1.0 400 Bad Request");
		echo "Bad Arg: r[".$rolename."]";
		echo "POST: ";
		print_r( $_POST );
		exit();
	}
// Connect to the mysql database.
  $mysqli = new mysqli("$CFG->dbhost", "$CFG->dbuser", "$CFG->dbpass", "$CFG->dbname");
	// verify connection
	if (mysqli_connect_errno()) {
		header("HTTP/1.0 503 Service Unavailable");
		exit();
	}
	$updateQ = "INSERT IGNORE INTO role(name, description, creation_time)"
		." VALUES ('".$rolename."', '".$roledesc."', now())";
	if( $mysqli->query($updateQ) )
		header("HTTP/1.0 200 OK");
	else
		header("HTTP/1.0 500 Internal Server Error");
	//echo "Query:";
	//echo $updateQ;
	exit();
?>
