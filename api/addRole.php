<?php
// set up env, DB
require_once('apiSetup.php');

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
	$updateQ = "INSERT IGNORE INTO role(name, description, creation_time)"
		." VALUES ('".$rolename."', '".$roledesc."', now())";
	$res =& $db->query($updateQ);
	if (PEAR::isError($res)) {
		header("HTTP/1.0 500 Internal Server Error\n"+$res->getMessage());
	}
	else
		header("HTTP/1.0 200 OK");
	//echo "Query:";
	//echo $updateQ;
	exit();
?>
