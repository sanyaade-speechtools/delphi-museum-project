<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("utils.php");
include("login.php");
/*************************************/
$subtitle = "<a href=\"admin.php\">Admin</a>";
$subtitle2 = "Permission Definitions";

include("genhead1.php");
?>
<style>
td.title { border-bottom: 2px solid black; font-weight:bold; text-align:center; }
td.label { font-weight:bold; }
td.permname { font-weight:bold; }
td.perm { border-bottom: 1px solid black; }
</style>
<script src="setupXMLHttpObj.js" ></script>
<script>

// The ready state change callback method that waits for a response.
function updatePermRSC() {
  if (xmlhttp.readyState==4) {
		if( xmlhttp.status == 200 ) {
			// Maybe this should change the cursor or something
			window.status = "Permission updated.";
	    //alert( "Response: " + xmlhttp.status + " Body: " + xmlhttp.responseText );
		} else {
			alert( "Error encountered when trying to update permission.\nResponse: "
			 				+ xmlhttp.status + "\nBody: " + xmlhttp.responseText );
		}
	}
}

function updatePerm(permName) {
	// Could change cursor and disable button until get response
	var descTextEl = document.getElementById("D_"+permName);
	var desc = descTextEl.value;
	if( desc.length <= 2 )
		alert( "You must enter a description that is at least 3 characters long" );
	else if( !xmlhttp )
		alert( "Cannot update permission - no http obj!\n Please advise Delphi support." );
	else {
		var url = "API/updatePerm.php";
		var args = "p="+permName+"&d="+desc;
		//alert( "Preparing request: POST: "+url+"?"+args );
		xmlhttp.open("POST", url, true);
		xmlhttp.setRequestHeader("Content-Type",
															"application/x-www-form-urlencoded" );
		xmlhttp.onreadystatechange=updatePermRSC;
		xmlhttp.send(args);
		//window.status = "request sent: POST: "+url+"?"+args;
		var el = document.getElementById("U_"+permName);
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
	if(empty($_POST['perm']))
		$opmsg = "Problem deleting perm.";
	else {
		$permname = $_POST['perm'];
		$deleteQ = "DELETE FROM permission WHERE name='".$permname."'";
		if( $mysqli->query($deleteQ) )
			$opmsg = "Permission \"".$permname."\" deleted.";
		else
			$opmsg = "Problem deleting permission \"".$permname."\".<br />".$mysqli->error;
	}
}
else if(isset($_POST['add'])){
	if(empty($_POST['perm']) || empty($_POST['desc']))
		$opmsg = "Problem adding new permission: You must specify both a name and a description.";
	else {
		$permname = $_POST['perm'];
		$permdesc = $_POST['desc'];
		$addQ = "INSERT IGNORE INTO permission(name, description, creation_time)"
			." VALUES ('".$permname."', '".$permdesc."', now())";
		if( $mysqli->query($addQ) )
			$opmsg = "Permission \"".$permname."\" added.";
		else
			$opmsg = "Problem adding permission \"".$permname."\".<br />".$mysqli->error;
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

function getFullPerms(){
	global $mysqli;
   /* Get all the users and their assigned perms */
	$q = "select name, description from permission";
	$result = $mysqli->query($q);
	if(!$result)
		return false;
	else{
		while($row = $result->fetch_assoc()){
			$perms[] = $row;
		}
	}
	return $perms;
}

$perms = getFullPerms();
if(!$perms){
	echo "<p>There are no permissions defined!</p>";
}
else {
	$first = true;
	foreach($perms as $perm){
?>
<form method="post">
<input type="hidden" name="perm" value="<?php echo $perm['name']; ?>" />
<table border="0" cellspacing="0" cellpadding="5">
<?php
		if( $first ) { ?>
	<tr>
		 <td class="title" width="100px">&nbsp;</td>
		 <td class="title 2" width="200px"><em>Permission Name</em></td>
		 <td class="title"><em>Description</em></td>
		 <td class="title" width="100px">&nbsp;</td>
	</tr>
<?php $first = false;
		}
?>
	<tr>
		<td class="perm" width="100px"><input type="submit" name="delete" value="Delete" /></td>
		<td class="perm permname 2" width="200px"><p><?php echo $perm['name']; ?></p></td>
		<td class="perm permdesc">
			<textarea id="D_<?php echo $perm['name']; ?>" cols="40" rows="2"
				onkeyup="enableElement('U_<?php echo $perm['name']; ?>')"
				><?php echo $perm['description']; ?></textarea>
		</td>
		<td class="perm" width="100px">
			<input disabled id="U_<?php echo $perm['name']; ?>" type="button"
				onclick="updatePerm('<?php echo $perm['name']; ?>')" value="Update" />
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
			<td class="title" width="100px">New Permission</td>
			<td class="title 2" width="200px">&nbsp;</td>
			<td class="title">&nbsp;</td>
			<td class="title" width="100px">&nbsp;</td>
		</tr>
		<tr>
			<td class="label" width="100px">New Permission</td>
			<td class="2" width="200px"><input type="text" name="perm" maxlength="40"></td>
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
