<?php
//Bring in the user's config file
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
		echo "Bad Arg: r[".$rolename."] d[".$roledesc."]";
		echo "POST: ";
		print_r( $_POST );
		exit();
	}
	$updateQ = "UPDATE role set description='".$roledesc."' where name='".$rolename."'";
	$res =& $db->query($updateQ);
	if (PEAR::isError($res)) {
		header("HTTP/1.0 500 Internal Server Error\n"+$res->getMessage());
	}
	else
		header("HTTP/1.0 200 OK");

	exit();
?>
