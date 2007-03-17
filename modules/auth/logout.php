<?php
session_start(); 
include("dbconnect.php");
include("login.php");
$subtitle = "Logout";

/**
 * Delete cookies - the time must be in the past,
 * so just negate what you added when creating the
 * cookie.
 */
if(isset($_COOKIE['cookname']) && isset($_COOKIE['cookpass'])){
   setcookie("cookname", "", time()-60*60*24*100, "/");
   setcookie("cookpass", "", time()-60*60*24*100, "/");
}

include("genheader.php");

if($login_state != DELPHI_LOGGED_IN && $login_state != DELPHI_REG_PENDING){
	echo "<h4 class=\"error\">Error!</h4>"
		. "<p>You are not currently logged in, so logout failed."
	 ."<br /><br /><br /><a href=\"main.php\">Return to main page</a></p>";
}
else{
   /* Kill session variables */
   unset($_SESSION['username']);
   unset($_SESSION['password']);
   $_SESSION = array(); // reset session array
   session_destroy();   // destroy session.

	echo "<h4>Logged Out</h4>"
	 ."<p>You have successfully <b>logged out</b>."
	 ."<br /><br /><br /><a href=\"main.php\">Return to main page</a></p>";
}

include("gentail.php");
?>
