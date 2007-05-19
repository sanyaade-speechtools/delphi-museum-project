<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("utils.php");
include("login.php");
/*************************************/
$subtitle = "<a href=\"admin.php\">Admin</a>";
$subtitle2 = "Role Permissions";

include("genhead1.php");
?>
<style>
td.title { border-bottom: 2px solid black; font-weight:bold; }
td.label { font-weight:bold; }
td.role { border-bottom: 1px solid black; }
</style>
<script src="setupXMLHttpObj.js" ></script>
<script>
function MarkChanged(evt) {
	var evt = evt || window.event; // event object
	var target = evt.target || window.event.srcElement; // event target
	var targetID = target.getAttribute("id"); // event target id
	var iDot = targetID.indexOf('.');
	if(iDot < 1)
		alert( "Error on page - cannot find role to set for item" );
	else {
		var perm = targetID.substr( 0, iDot );
		var role = targetID.substr( iDot+1 );
		var action = target.checked? "set":"unset";
		//alert( "Calling setPermForRole( "+perm+", "+role+", "+action+")" );
		setPermForRole( perm, role, action );
	}
}

// The ready state change callback method that waits for a response.
function setPermForRoleRSC() {
  if (xmlhttp.readyState==4) {
		if( xmlhttp.status == 200 ) {
			// Maybe this should change the cursor or something
			window.status = "Permission for role updated.";
	    //alert( "Response: " + xmlhttp.status + " Body: " + xmlhttp.responseText );
		} else {
			alert( "Error encountered when trying to update role/perms.\nResponse: "
			 				+ xmlhttp.status + "\nBody: " + xmlhttp.responseText );
		}
	}
}

function setPermForRole( perm, role, action ) {
	if( !xmlhttp )
	  alert( "Cannot update role:permission - no http obj!\n Please advise Delphi support." );
	else {
		var url = "API/setRolePerm.php";
		var args = "r="+role+"&p="+perm+"&a="+action;
		//alert( "Preparing request: POST: "+url+"?"+args );
		xmlhttp.open("POST", url, true);
		xmlhttp.setRequestHeader("Content-Type",
															"application/x-www-form-urlencoded" );
 		xmlhttp.onreadystatechange=setPermForRoleRSC;
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

function getPermRoles(){
	global $mysqli;
   /* Get all the permissions and their assigned roles */
	$q = "select p.name, r.name from permission p left join role_perms rp on ( p.id=rp.perm_id )"
	 		." left join role r on (rp.role_id=r.id)";
	$result = $mysqli->query($q);
	if(!$result)
		return false;
	else {
		while ($row = $result->fetch_array()) {
			$permroles[$row[0]][$row[1]] = true;
		}
	}
	return $permroles;
}
$roles = getRoles();
$permroles = getPermRoles();
if(!$roles){
	echo "<p>There are no roles defined!</p>";
}
else if(!$permroles){
	echo "<p>There are no permissions defined!</p>";
}
else {
?>
<table border="0" cellspacing="0" cellpadding="3">
<tr>
   <td class="title"><em>Permission</em></td>
<?php
	foreach( $roles as $role ) {
		echo '<td class="title" width="100px" align="center">'.$role.'</td>';
	}
?>
</tr>
<?php
	foreach( $permroles as $perm => $proles ) {
		echo '<tr><td class="role" ><p><strong>'.$perm.'</td>';
		foreach( $roles as $role ) {
			echo '<td class="role" align="center">'
				.'<input type="checkbox" class="set" id="'.$perm.'.'.$role.'"';
			if(isset($proles[$role])){
				echo ' checked="true"';
			}
			echo ' onclick="MarkChanged(event);" /></td>';
		}
		echo '</tr>';
	}
?>
</table>
</div>
<div style="width:80%; text-align:left; line-height:0.8em; padding-top:15px; padding-left:3px;">
<h5><a href="adminRoles.php">Edit Role Definitions</a></h5>
<h5><a href="adminPerms.php">Edit Permission Definitions</a></h5>
<h5><a href="main.php">Return to main page</a></h5>


<?php
}
include("gentail.php");
?>
