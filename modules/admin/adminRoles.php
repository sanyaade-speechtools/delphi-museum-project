<?php 
/* Include Files *********************/
require_once("../../libs/env.php");
/*************************************/
// If the user isn't logged in, send to the login page.
if(($login_state != DELPHI_LOGGED_IN) && ($login_state != DELPHI_REG_PENDING)){
	header( 'Location: ' . $CFG->wwwroot . '/modules/auth/login.php' );
	die();
}

// This needs to verify perms. 

$style_block = "<style>
td.title { border-bottom: 2px solid black; font-weight:bold; text-align:left; 
		font-style:italic; color:#777777; }
td.label { font-weight:bold; }
td.rolename { font-weight:bold; }
td.role { border-bottom: 1px solid black; }
td.roledesc textarea { font-family: Arial, Helvetica, sans-serif; }
form.form_row  { padding:0px; margin:0px;}
</style>";

$t->assign("style_block", $style_block);

$themebase = $CFG->wwwroot.'/themes/'.$CFG->theme;

$script_block = '
<script type="text/javascript" src="'.$themebase.'/scripts/setupXMLHttpObj.js"></script>
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
		var url = "../../api/updateRole.php";
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
	//alert( "enableElement" );
	var el = document.getElementById(elID);
	el.disabled = false;
	//window.status = "Element ["+elID+"] enabled.";
}

</script>';

$t->assign("script_block", $script_block);

$opmsg = "";

if(isset($_POST['delete'])){
	if(empty($_POST['role']))
		$opmsg = "Problem deleting role.";
	else {
		$rolename = $_POST['role'];
		$deleteQ = "DELETE FROM role WHERE name='".$rolename."'";
		$res =& $db->query($deleteQ);
		if (PEAR::isError($res)) {
			$opmsg = "Problem deleting role \"".$rolename."\".<br />".$res->getMessage();
		} else {
			$opmsg = "Role \"".$rolename."\" deleted.";
		}
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
		$res =& $db->query($addQ);
		if (PEAR::isError($res)) {
			$opmsg = "Problem adding role \"".$rolename."\".<br />".$res->getMessage();
		} else {
			$opmsg = "Role \"".$rolename."\" added.";
		}
	}
}

function getFullRoles(){
	global $db;
   /* Get all the users and their assigned roles */
	$q = "select name, description from role";
	$res =& $db->query($q);
	if (PEAR::isError($res))
		return false;
	$roles = array();
	while ($row = $res->fetchRow()) {
		$role = array(	'name' => $row['name'], 
						'description' => $row['description']);
		
		array_push($roles, $role);
	}
	// Free the result
	$res->free();
	return $roles;
}

$roles = getFullRoles();
if($roles){
	$t->assign('roles', $roles);
}
if($opmsg!="")
	$t->assign('opmsg', $opmsg);

$t->display('adminRoles.tpl');
?>
