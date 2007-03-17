<?php
session_start(); 
include("dbconnect.php");
include("dmail.php");
$subtitle = "Forgotten Password";

/**
 * Checks for a given user, and returns the email for that user, 
 * or FALSE if user not found.
 */
function checkUser($username){
	global $mysqli;
	$q = "select email from user where username = '$username'";
	$result = $mysqli->query($q);
	if(!$result || ($result->num_rows < 1))
		return FALSE;
	$dbarray = $result->fetch_array(MYSQLI_ASSOC);
	$dbemail = stripslashes($dbarray['email']);
	return $dbemail;
}

/**
 * Updates the password for a given user.
 * Returns the new cleartext password, or FALSE if user not found.
 */
function synthesizeAndUpdatePassword($username){
	global $mysqli;
	$chars='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
	$nchars=strlen($chars);
	$newPWClear = '';
	// Generate a random string of 16 chars for the new password.
	for($i = 0; $i <= 15; $i++) 
		$newPWClear .= $chars[rand(0,$nchars-1)]; 
	$newPWmd5 = md5($newPWClear);
	$q = "update user set passwdmd5='$newPWmd5'where username = '$username'";
	$mysqli->query($q);
	if($mysqli->affected_rows > 0)
		return $newPWClear;
	else
		return FALSE;
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
include("genheader.php");
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
include("gentail.php");
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
<form action="<?php echo $HTTP_SERVER_VARS['PHP_SELF']; ?>" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td><input type="text" name="user" maxlength="40" ></td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subreq" value="Submit"></td></tr>
</table>
</form>
<?php include("gentail.php"); ?>
