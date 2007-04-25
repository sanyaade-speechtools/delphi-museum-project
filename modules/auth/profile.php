<?php

require_once("../../libs/env.php");
require_once("../../libs/utils.php");

/**
 * Checks for a given user, and returns the info for that user, 
 * or FALSE if user not found.
 */
function getUserInfo(){
	global $db;
	// Get current user info
	$sql = "select * from user where username = '" . $_SESSION['username'] . "'";
	
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
   	// If nothing is found, username is available
	if ( $res->numRows() < 1 ){
		return false;
	} else {
		return $res->fetchRow();
	}
}

/**
 * Updates the password for a given user. Assumes it is
 * already encrypted (via md5).
 */
function updatePassword($username, $newPW){
	global $db;
	$sql = "update user set passwdmd5='$newPW'where username = '$username'";
	$affected =& $db->exec($sql);

	// check that result is not an error
	if (PEAR::isError($affected)) {
	    die($affected->getMessage());
	} else {
		return true;
	}
}

function updateEmail($username, $newEmail){
	global $db;
	$sql = "update user set email='$newEmail'where username = '$username'";
	$res =& $db->query($sql);
	$affected =& $db->exec($sql);

	// check that result is not an error
	if (PEAR::isError($affected)) {
	    die($affected->getMessage());
	} else {
		return true;
	}
}

// Error to show if we find one
$showErr = FALSE;

// If the user isn't logged in, send to the login page.
if(($login_state != DELPHI_LOGGED_IN) && ($login_state != DELPHI_REG_PENDING)){
	header( 'Location: ' . $CFG->wwwroot . '/modules/auth/login.php' );
	die();
}


/* If a request has been submitted, handle it.  */
if(isset($_POST['subreq'])){
	// Fetch an array of user data
	$userData = getUserInfo();
	$t->assign('email', $userData['email']);
	
	if(isset($_POST['pass']) && (strlen($_POST['pass']) > 0)){
		if(strlen($_POST['pass']) < 3 ){
			$showErr = 'Your password must be at least 3 characters. Please try again.';
			$t->assign('message', $showErr);
			$t->display('profile.tpl');
			die();
		}
		if($_POST['pass'] != $_POST['pass2']){
			$showErr = 'Your retyped password did not match the first typed password. Please try again.';
			$t->assign('message', $showErr);
			$t->display('profile.tpl');
			die();
		}
		$md5pass = md5($_POST['pass']);
		if($md5pass != $userData['passwdmd5']) {
			if(!updatePassword($userData['email'], $md5pass)){
				$showErr = 'Error trying to update password.';
				$t->assign('message', $showErr);
				$t->display('profile.tpl');
				die();
			}
		}
	}
	if($_POST['email'] != $userData['email']) {
		if(!emailValid($_POST['email'])){
			$showErr = 'Sorry, the email address: "<strong>'.$_POST['email']
									.'</strong>" is not valid; please enter a valid one.';
			$t->assign('message', $showErr);
			$t->display('profile.tpl');
			die();
		}
		if(!updateEmail($userData['username'], $_POST['email'])){
			$showErr = 'Error trying to update email.';
			$t->assign('message', $showErr);
			$t->display('profile.tpl');
			die();
		}
	}
	$t->assign('email', $_POST['email']);
	$t->assign('message', "Profile Updated");
	$t->display('profile.tpl');
	die();
}

// Fetch an array of user data to get updated values
$userData = getUserInfo();

$t->assign('email', $userData['email']);
$t->display('profile.tpl');
?>

