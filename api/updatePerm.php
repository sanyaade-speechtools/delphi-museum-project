<?php
//Bring in the user's config file
require_once('apiSetup.php');

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
	$updateQ = "UPDATE permission set description='".$permdesc."' where name='".$permname."'";
	$res =& $db->query($updateQ);
	if (PEAR::isError($res)) {
		header("HTTP/1.0 500 Internal Server Error\n"+$res->getMessage());
	}
	else
		header("HTTP/1.0 200 OK");

	exit();
?>
