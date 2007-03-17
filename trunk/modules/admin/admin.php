<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("login.php");
/*************************************/
$subtitle = "Admin";

include("genheader.php");
if($login_state != DELPHI_LOGGED_IN){
	echo "<h2 class=\"error\">You are not logged in!</h2>";
	displayLogin();
  include("gentail.php");
	return;
}
// This needs to verify the admin perms as well...
?>
<h3><a href="adminRoles.php">Manage Role Definitions</a></h3>
<h3><a href="adminPermissions.php">Manage Permission Definitions</a></h3>
<h3><a href="adminUserRoles.php">Manage User Roles</a></h3>
<h3><a href="adminRolePerms.php">Manage Role Permissions</a></h3>
<p></p>
<p></p>
<p><a href="main.php">Return to main page</a></p>
<?php
include("gentail.php");
?>
