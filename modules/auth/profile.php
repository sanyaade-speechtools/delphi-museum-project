<?php
require_once("../../libs/env.php");
require_once("../../libs/utils.php");

include("login.php");

/**
 * Checks for a given user, and returns the info for that user, 
 * or FALSE if user not found.
 */
function checkUser($username){
	global $db;
	// Get current user info
	$sql = "select email, passwdmd5 from user where username = '$username'";
	
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
   	// If nothing is found, username is available
	if ( $res->numRows() < 1 ){
		return false;
	} else {
		return $res;
	}
}

/**
 * Updates the password for a given user. Assumes it is
 * already encrypted (via md5).
 */
function updatePassword($username, $newPW){
	global $db;
	$sql = "update user set passwdmd5='$newPW'where username = '$username'";
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	} else {
		return true;
	}
}

function updateEmail($username, $newEmail){
	global $db;
	$sql = "update user set email='$newEmail'where username = '$username'";
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	} else {
		return true;
	}
}

$showErr = FALSE;			// Error to show if we find one

function checkAndUpdateValues(){
	global $showErr;
	global $user;
	global $dbpwmd5;
	global $dbemail;
	/* Make sure all fields were entered */
	if(((!$_POST['pass'] || !$_POST['pass2']) && !$_POST['email'])){
		$showErr = 'Enter either a new password or a new email address';
		return FALSE;
	}

	if(isset($_POST['pass']) && (strlen($_POST['pass']) > 0)){
		if(strlen($_POST['pass']) < 3 ){
			$showErr = 'Your password must be at least 3 characters. Please try again.';
			return FALSE;
		}
		if($_POST['pass'] != $_POST['pass2']){
			$showErr = 'Your retyped password did not match the first typed password. Please try again.';
			return FALSE;
		}
		$md5pass = md5($_POST['pass']);
		if($md5pass != $dbpwmd5) {
			if(!updatePassword($user, $md5pass)){
				$showErr = 'Error trying to update password.';
				return FALSE;
			}
		}
	}
	if($_POST['email'] != $dbemail) {
		if(!emailValid($_POST['email'])){
			$showErr = 'Sorry, the email address: "<strong>'.$_POST['email']
									.'</strong>" is not valid; please enter a valid one.';
			return FALSE;
		}
		if(!updateEmail($user, $_POST['email'])){
			$showErr = 'Error trying to update email.';
			return FALSE;
		}
		$dbemail = $_POST['email'];
	}
	return TRUE;
}

$showErr = FALSE;			// Error to show if we find one


if(($login_state != DELPHI_LOGGED_IN) && ($login_state != DELPHI_REG_PENDING)){
	echo "<h2 class=\"error\">You are not logged in!</h2>";
	displayLogin();
	include("gentail.php");
	return;
}

$user = $_SESSION['username'];
$result = checkUser($user);
if(!$result){
	echo "<h2 class=\"error\">There is problem with your profile. "
		." Please log out and log back in, or contact Delphi support.</h2>";
	include("gentail.php");
	return;
}

$dbarray = $result->fetch_array(MYSQLI_ASSOC);
$result->close();
$dbpwmd5 = stripslashes($dbarray['passwdmd5']);
$dbemail = stripslashes($dbarray['email']);
echo "<h1>Profile information for <b>".$user."</b></h1>";

/* If a request has been submitted, handle it.  */
if(isset($_POST['subreq'])){
	if(checkAndUpdateValues()){
		echo "<p class=\"notice\">Your profile has been updated.</p>";
	}
	else if($showErr) {
		echo "<p class=\"error\">".$showErr."</p>";
	}
	else{
		// This should not obtain - here for safety
		echo "<p class=\"error\">Error encountered with values - please try again.</p>";
	}
}
?>

