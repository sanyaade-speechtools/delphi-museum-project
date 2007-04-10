<?php
include("opendb.php");
	// verify connection
	if (mysqli_connect_errno()) {
		 printf("Connection to Database failed: %s\n", mysqli_connect_error());
		 exit();
	}
?>
