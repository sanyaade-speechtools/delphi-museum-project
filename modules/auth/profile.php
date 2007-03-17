<?php
session_start(); 
include("dbconnect.php");
include("utils.php");
include("login.php");
$subtitle = "User Profile page";

/**
 * Checks for a given user, and returns the info for that user, 
 * or FALSE if user not found.
 */
function checkUser($username){
	global $mysqli;
	// Get current user info
	$q = "select email, passwdmd5 from user where username = '$username'";
	$result = $mysqli->query($q);
	if(!$result || ($result->num_rows < 1))
		return FALSE;
	return $result;
}

/**
 * Updates the password for a given user. Assumes it is
 * already encrypted (via md5).
 */
function updatePassword($username, $newPW){
	global $mysqli;
	$q = "update user set passwdmd5='$newPW'where username = '$username'";
	$mysqli->query($q);
	return ($mysqli->affected_rows > 0);
}

function updateEmail($username, $newEmail){
	global $mysqli;
	$q = "update user set email='$newEmail'where username = '$username'";
	$mysqli->query($q);
	return ($mysqli->affected_rows > 0);
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

include("genheader.php");

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

<form action="" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Change Password:</td><td><input type="password" name="pass" maxlength="40"></td></tr>
<tr><td>Repeat New Password:</td><td><input type="password" name="pass2" maxlength="40"></td></tr>
<tr><td>Email:</td><td>
	<input type="text" name="email" maxlength="70" <?php echo 'value="'.$dbemail.'" '; ?> >
</td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subreq" value="Update"></td></tr>
<tr><td style="height:20px;"></td></tr>
<tr><td colspan="2" align="right"><a href="main.php">Return to main page</a></td></tr>
</table>
</form>

<?php include("gentail.php"); ?>
