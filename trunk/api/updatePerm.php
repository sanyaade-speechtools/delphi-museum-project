<?php
//Bring in the user's config file
require_once('../config.php');

	$badarg = false;
	if(empty($_POST['p']))
		$badarg = true;
	else {
		$permname = $_POST['p'];
		$permdesc = $_POST['d'];
	}
	if( $badarg ) {
		header("HTTP/1.0 400 Bad Request");
		echo "Bad Arg: p[".$permname."] d[".$permdesc."]";
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
	$updateQ = "UPDATE permission set description='".$permdesc."' where name='".$permname."'";
	if( $mysqli->query($updateQ) )
		header("HTTP/1.0 200 OK");
	else
		header("HTTP/1.0 500 Internal Server Error");
	//echo "Query:";
	//echo $updateQ;
	exit();
?>
