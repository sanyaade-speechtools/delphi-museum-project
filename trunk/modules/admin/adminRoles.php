<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("utils.php");
include("login.php");
/*************************************/
$subtitle = "<a href=\"admin.php\">Admin</a>";
$subtitle2 = "Role Definitions";

include("genhead1.php");
?>
<style>
td.title { border-bottom: 2px solid black; font-weight:bold; text-align:center; }
td.label { font-weight:bold; }
td.rolename { font-weight:bold; }
td.role { border-bottom: 1px solid black; }
</style>
<script src="setupXMLHttpObj.js" ></script>
<script>

// The ready state change callback method that waits for a response.
function updateRoleRSC() {
  if (xmlhttp.readyState==4) {
		if( xmlhttp.status == 200 ) {
			// Maybe this should change the cursor or something
			window.status = "Role updated.";
	    //alert( "Response: " + xmlhttp.status + " Body: " + xmlhttp.responseText );
		} else {
			alert( "Error encountered when trying to update role.\nResponse: "
			 				+ xmlhttp.status + "\nBody: " + xmlhttp.responseText );
		}
	}
}

function updateRole(roleName) {
	// Could change cursor and disable button until get response
	var descTextEl = document.getElementById("D_"+roleName);
	var desc = descTextEl.value;
	if( desc.length <= 2 )
		alert( "You must enter a description that is at least 3 characters long" );
	else if( !xmlhttp )
		alert( "Cannot update role - no http obj!\n Please advise Delphi support." );
	else {
		var url = "API/updateRole.php";
		var args = "r="+roleName+"&d="+desc;
		//alert( "Preparing request: POST: "+url+"?"+args );
		xmlhttp.open("POST", url, true);
		xmlhttp.setRequestHeader("Content-Type",
															"application/x-www-form-urlencoded" );
		xmlhttp.onreadystatechange=updateRoleRSC;
		xmlhttp.send(args);
		//window.status = "request sent: POST: "+url+"?"+args;
		var el = document.getElementById("U_"+roleName);
		el.disabled = true;
	}
}
// This should go into a utils.js - how to include?
function enableElement( elID ) {
	//alert( 'enableElement' );
	var el = document.getElementById(elID);
	el.disabled = false;
	//window.status = 'Element ['+elID+'] enabled.';
}

</script>

<?php
$opmsg = "";

if(isset($_POST['delete'])){
	if(empty($_POST['role']))
		$opmsg = "Problem deleting role.";
	else {
		$rolename = $_POST['role'];
		$deleteQ = "DELETE FROM role WHERE name='".$rolename."'";
		if( $mysqli->query($deleteQ) )
			$opmsg = "Role \"".$rolename."\" deleted.";
		else
			$opmsg = "Problem deleting role \"".$rolename."\".<br />".$mysqli->error;
	}
}
else if(isset($_POST['add'])){
	if(empty($_POST['role']) || empty($_POST['desc']))
		$opmsg = "Problem adding new role. You must define both a name and a description.";
	else {
		$rolename = $_POST['role'];
		$roledesc = $_POST['desc'];
		$addQ = "INSERT IGNORE INTO role(name, description, creation_time)"
			." VALUES ('".$rolename."', '".$roledesc."', now())";
		if( $mysqli->query($addQ) )
			$opmsg = "Role \"".$rolename."\" added.";
		else
			$opmsg = "Problem adding role \"".$rolename."\".<br />".$mysqli->error;
	}
}

include("genbanner.php");

if($login_state != DELPHI_LOGGED_IN){
	echo "<h2 class=\"error\">You are not logged in!</h2>";
	displayLogin();
  include("gentail.php");
	return;
}
// This needs to verify the admin perms as well...

function getFullRoles(){
	global $mysqli;
   /* Get all the users and their assigned roles */
	$q = "select name, description from role";
	$result = $mysqli->query($q);
	if(!$result)
		return false;
	else{
		while($row = $result->fetch_assoc()){
			$roles[] = $row;
		}
	}
	return $roles;
}

$roles = getFullRoles();
if(!$roles){
	echo "<p>There are no roles defined!</p>";
}
else {
	$first = true;
	foreach($roles as $role){
?>
<form method="post">
<input type="hidden" name="role" value="<?php echo $role['name']; ?>" />
<table border="0" cellspacing="0" cellpadding="5">
<?php
		if( $first ) { ?>
	<tr>
		 <td class="title" width="100px">&nbsp;</td>
		 <td class="title 2" width="200px"><em>Role Name</em></td>
		 <td class="title"><em>Description</em></td>
		 <td class="title" width="100px">&nbsp;</td>
	</tr>
<?php $first = false;
		}
?>
	<tr>
		<td class="role" width="100px"><input type="submit" name="delete" value="Delete" /></td>
		<td class="role rolename 2" width="200px"><p><?php echo $role['name']; ?></p></td>
		<td class="role roledesc">
			<textarea id="D_<?php echo $role['name']; ?>" cols="40" rows="2"
				onkeyup="enableElement('U_<?php echo $role['name']; ?>')"
				><?php echo $role['description']; ?></textarea>
		</td>
		<td class="role" width="100px">
			<input disabled id="U_<?php echo $role['name']; ?>" type="button"
				onclick="updateRole('<?php echo $role['name']; ?>')" value="Update" />
		</td>
	</tr>
</table>
</form>
<?php
	}
?>
<div style="height:30px"></div>
<form method="post">
	<table border="0" cellspacing="0" cellpadding="5">
		<tr>
			<td class="title" width="100px">New Role</td>
			<td class="title 2" width="200px">&nbsp;</td>
			<td class="title">&nbsp;</td>
			<td class="title" width="100px">&nbsp;</td>
		</tr>
		<tr>
			<td class="label" width="100px">New Role</td>
			<td class="2" width="200px"><input type="text" name="role" maxlength="40"></td>
			<td><textarea name="desc" rows="2" cols="40"></textarea></td>
			<td><input type="submit" name="add" value="Add" /></td>
		</tr>
	</table>
</form>
<?php
if($opmsg!="")
	echo "<p>".$opmsg."</p>";
?>
</div>
<div style="width:80%; text-align:left; padding:20px;">
<h5><a href="main.php">Return to main page</a></h5>
<?php
}
include("gentail.php");
?>
