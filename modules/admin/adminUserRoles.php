<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("utils.php");
include("login.php");
/*************************************/
$subtitle = "<a href=\"admin.php\">Admin</a>";
$subtitle2 = "User Roles";

include("genhead1.php");
?>
<style>
td.title { border-bottom: medium solid #000000; font-weight:bold; }
td.label { font-weight:bold; }
td.user { border-bottom: 1px solid black; }
</style>
<script src="setupXMLHttpObj.js" ></script>
<script>
var sess_login_id = -1;
<?php 
	if(isset($_SESSION['login_id'])){
		echo "sess_login_id = ".$_SESSION['login_id'].";
		";
	}
?>
function MarkChanged(evt) {
	var evt = evt || window.event; // event object
	var target = evt.target || window.event.srcElement; // event target
	var targetID = target.getAttribute("id"); // event target id
	var iDot = targetID.indexOf('.');
	if(iDot < 1)
		alert( "Error on page - cannot find role to set for item" );
	else {
		var user = targetID.substr( 0, iDot );
		var role = targetID.substr( iDot+1 );
		var action = target.checked? "set":"unset";
		//alert( "Calling setRoleForUser( "+role+", "+user+", "+action+")" );
		setRoleForUser( role, user, action );
	}
}
// The ready state change callback method that waits for a response.
function setRoleForUserRSC() {
  if (xmlhttp.readyState==4) {
		if( xmlhttp.status == 200 ) {
			// Maybe this should change the cursor or something
			window.status = "Role for user updated.";
	    //alert( "Response: " + xmlhttp.status + " Body: " + xmlhttp.responseText );
		} else {
			alert( "Error encountered when trying to update user/roles.\nResponse: "
			 				+ xmlhttp.status + "\nBody: " + xmlhttp.responseText );
		}
	}
}

function setRoleForUser( role, user, action ) {
	if( !xmlhttp )
	  alert( "Cannot update role:permission - no http obj!\n Please advise Delphi support." );
	else {
		var url = "API/setUserRole.php";
		var args = "r="+role+"&u="+user+"&a="+action;
		if(sess_login_id >= 0)
			args += "&ap="+sess_login_id;
		//alert( "Preparing request: POST: "+url+"?"+args );
		xmlhttp.open("POST", url, true);
		xmlhttp.setRequestHeader("Content-Type",
															"application/x-www-form-urlencoded" );
 		xmlhttp.onreadystatechange=setRoleForUserRSC;
		xmlhttp.send(args);
		//window.status = "request sent: POST: "+url+"?"+args;
	}
}
</script>
<?php
include("genbanner.php");

if($login_state != DELPHI_LOGGED_IN){
	echo "<h2 class=\"error\">You are not logged in!</h2>";
	displayLogin();
  include("gentail.php");
	return;
}
// This needs to verify the admin perms as well...

function getUserRoles(){
	global $mysqli;
   /* Get all the users and their assigned roles */
	$q = "select u.username, r.name from user u left join user_roles ur on ( u.id=ur.user_id )"
	 		." left join role r on (ur.role_id=r.id)";
	$result = $mysqli->query($q);
	if(!$result)
		return false;
	else {
		while ($row = $result->fetch_array()) {
			$userroles[$row[0]][$row[1]] = true;
		}
	}
	return $userroles;
}
$roles = getRoles();
$userroles = getUserRoles();
if(!$roles){
	echo "<p>There are no roles defined!</p>";
}
else if(!$userroles){
	echo "<p>There are no users defined!</p>";
}
else {
?>
<table border="0" cellspacing="0" cellpadding="3">
<tr>
	 <td class="title"><em>User</em></td>
<?php
	foreach( $roles as $role ) {
		echo '<td class="title" width="100px" align="center">'.$role.'</td>';
	}
?>
</tr>
<?php
	foreach( $userroles as $user => $uroles ) {
		echo '<tr><td class="user" ><p><strong>'.$user.'</td>';
		foreach( $roles as $role ) {
			echo '<td class="user" align="center">'
				.'<input type="checkbox" class="set" id="'.$user.'.'.$role.'"';
			if(isset($uroles[$role])){
				echo ' checked="true"';
			}
			echo ' onclick="MarkChanged(event);" /></td>';
		}
		echo '</tr>';
	}
?>
<tr><td height="20px"></td></tr>
<tr><td colspan="3" align="left"><a href="main.php">Return to main page</a></td></tr>
</table>
<?php
}
include("gentail.php");
?>
