<?php

require_once("../../libs/env.php");
require_once("../../libs/utils.php");


/**
 * Checks for a given user, and returns the email for that user, 
 * or FALSE if user not found.
 */
function checkUser($username){
	global $db;
	$sql = "select email from user where username = '$username'";
	
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
		return FALSE;
	    die($res->getMessage());
	}

	$row = $res->fetchRow();

	return stripslashes($row['email']);
}

/**
 * Updates the password for a given user.
 * Returns the new cleartext password, or FALSE if user not found.
 */
function synthesizeAndUpdatePassword($username){
	global $db;
	$chars='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
	$nchars=strlen($chars);
	$newPWClear = '';
	// Generate a random string of 16 chars for the new password.
	for($i = 0; $i <= 15; $i++) 
		$newPWClear .= $chars[rand(0,$nchars-1)]; 
	$newPWmd5 = md5($newPWClear);
	$sql = "update user set passwdmd5='$newPWmd5'where username = '$username'";
	
	$affected =& $db->exec($sql);

	// check that result is not an error
	if (PEAR::isError($affected)) {
		return FALSE;
	    die($affected->getMessage());
	} else {
		return $newPWClear;
	}
}

function sendPWMail($username, $email, $newPW){
	$plaintextmsg = 
		'Your password has been reset to: "'.$newPW
			.'". You should reset this the next time you log in.';
	$htmlmsg = 
		'<p>Your password has been reset to: "'.$newPW
			.'". You should reset this the next time you log in.</p>';
	// The message already mentions the password, but advertising it on the
	// subject line seems even more unsafe.
	$subj = 'Phoebe A. Hearst Museum';
	return sendDelphiMail($username, $email, $subj, $plaintextmsg, $htmlmsg);
}

$showErr = FALSE;			// Error to show if we find one

function checkSubmitValues(){
	global $showErr;
	/* Make sure at least one field was entered */
	if(!$_POST['user']){
		$showErr = 'Please provide a valid username.';
		return FALSE;
	}

	/* Check if username is valid */
	$email = checkUser($_POST['user']);
	if(!$email){
		$showErr = 'Sorry, the username: "<strong>'.$_POST['user']
								.'</strong>" could not be found.';
		return FALSE;
	}
	// If we get here, username is valid. Return the email address.
	return $email;
}

/* If a request has been submitted, handle it.  */
if(isset($_POST['subreq'])){
	$email = checkSubmitValues();
	if($email){
		$newPW = synthesizeAndUpdatePassword($_POST['user']);
		sendPWMail($_POST['user'], $email, $newPW);
?>
<p>Your password has been updated, and the new password will be mailed
to the email account associated to your account.</p>
<p><a href="main.php">Return to main page</a></p>
<?php
		return;
	}
	// Otherwise will fall through to show the form, with the error set.
}

/**
 * This is the form to enter info to get a new password
 */
?>
<h2>Request a new password</h2>
<?php if($showErr) echo '<h3 style="color:red;">'.$showErr.'</h3>'; ?>
<form action="" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td><input type="text" name="user" maxlength="40" ></td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subreq" value="Submit"></td></tr>
</table>
</form>
