<?php
session_start(); 
include("dbconnect.php");
include("utils.php");
include("dmail.php");

$subtitle = "Registrtion Page";

/**
 * Confirms a user's registration, clearing the pending flag in the DB
 */
function confirmUser($userid){
   global $mysqli;
   $q = "update user set pending=0 where id= '$userid'";
   $mysqli->query($q);
   return($mysqli->affected_rows > 0);
}

/**
 * Returns true if the username has been taken
 * by another user, false otherwise.
 */
function usernameTaken($username){
   global $mysqli;
   if(!get_magic_quotes_gpc()){
      $username = addslashes($username);
   }
   $q = "select username from user where username = '$username'";
   $result = $mysqli->query($q);
   $row_cnt = $result->num_rows;
   $result->close();
   return ($row_cnt > 0);
}

function sendRegMail($uid, $username, $email){
	$confirmUrl =	'http://' . $_SERVER['SERVER_NAME'] . $_SERVER['PHP_SELF']
								. '?confirm=' . $uid;
	$plaintextmsg = 
		 'Thank you for registering with Delphi! Click on the link below '
		 .'or copy and paste the URL into your browser to complete the registration.

		 ' . $confirmUrl;
	$htmlmsg = 
		 '<p>Thank you for registering with Delphi!<br />Click on the link below '
		 .'or copy and paste the URL into your browser to complete the registration.'
		 .'<br /><br /><a href="'.$confirmUrl.'">'.$confirmUrl.'</a></p>';
	$subj = 'Phoebe A. Hearst Museum: Delphi registration';
	return sendDelphiMail($username, $email, $subj, $plaintextmsg, $htmlmsg);
}

/**
 * Inserts the given (username, password) pair
 * into the database. Returns true on success,
 * false otherwise.
 */
function addNewUser($username, $password, $email){
   global $mysqli;
   $md5pass = md5($password);
	 // Note that 'pending' defaults to true on INSERT
	 $q = 'INSERT INTO user ( username, passwdmd5, email, creation_time )'
          ." VALUES ('$username', '$md5pass', '$email', now() )";
	 if($mysqli->query($q)) {
		 // Only enable this for debugging
		 // echo '<p>Added new user: '.$username.'</p>';
		 $new_uid = $mysqli->insert_id;
		 // Now send email to the new user allowing them to confirm with one-click
		 if( !sendRegMail($new_uid, $username, $email))
			 echo "Could not send email to " . $email . "
				 [".$mailmsg."]";
		 return $new_uid;
	 } else {
		 // Only enable this for debugging
		 echo '<p>Problem adding new user: '.$mysqli->error.'</p>';
		 return 0;
	 }
}

/**
 * Displays the appropriate message to the user
 * after the registration attempt. It displays a 
 * success or failure status depending on a
 * session variable set during registration.
 */
function displayStatus(){
	 // echo '<p>RegUname is: '.$_SESSION['reguname'].'</p>';
	 // echo '<p>Registered is: '.$_SESSION['registered'].'</p>';
	 // echo '<p>Regresult is: '.$_SESSION['reguid'].'</p>';
   $uname = $_SESSION['reguname'];
   if($_SESSION['reguid'] > 0){
?>

<h2>Registered!</h2>
<p>Thank you <b><?php echo $uname; ?></b>, your information has been added to the database.<br />
You will receive an email with a confirmation link. Click on the link, or copy the
URL into your browser to confirm your registration.</p>

<?php
   }
   else{
?>

<h2>Registration Failed</h2>
<p>We're sorry, but an error has occurred and your registration for the username <b><?php echo $uname; ?></b>, could not be completed.<br>
Please try again at a later time.</p>

<?php
   }
   unset($_SESSION['reguname']);
   unset($_SESSION['registered']);
   unset($_SESSION['reguid']);
}

$showRegForm = TRUE;	// Until we find otherwise
$showRegErr = FALSE;			// Error to show if we find one
$showRegUser = FALSE;			// Prefill user name on error
$showRegEmail = FALSE;			// Prefill user name on error

function checkSubmitValues(){
	global $showRegForm;
	global $showRegErr;
	global $showRegUser;
	global $showRegEmail;
	/* Make sure all fields were entered */
	if(!$_POST['user'] || !$_POST['pass'] || !$_POST['pass2'] || !$_POST['email']){
		$showRegErr = 'Please provide a value for each field. All fields are required';
		if($_POST['user'])
			$showRegUser = $_POST['user'];
		return FALSE;
	}
	if(isset($_POST['user']))
		$showRegUser = $_POST['user'];
	if(isset($_POST['email']))
		$showRegEmail = $_POST['email'];

	if(strlen($_POST['pass']) < 3 ){
		$showRegErr = 'Your password must be at least 3 characters. Please try again.';
		return FALSE;
	}
	if($_POST['pass'] != $_POST['pass2']){
		$showRegErr = 'Your retyped password did not match the first typed password. Please try again.';
		return FALSE;
	}

	/* Spruce up username, check length */
	$_POST['user'] = trim($_POST['user']);
	if(strlen($_POST['user']) > 40){
		$showRegErr = "Sorry, the username is longer than 40 characters; please shorten it.";
		return FALSE;
	}

	/* Check if username is already in use */
	if(usernameTaken($_POST['user'])){
		$showRegErr = 'Sorry, the username: "<strong>'.$_POST['user']
								.'</strong>" is already taken; please pick another one.';
		$showRegUser = FALSE;
		return FALSE;
	}

	/* Check if username is already in use */
	if(!emailValid($_POST['email'])){
		$showRegErr = 'Sorry, the email address: "<strong>'.$_POST['email']
								.'</strong>" is not valid; please enter a valid one.';
		$showRegEmail = FALSE;
		return FALSE;
	}
	return TRUE;
}

/**
 * Determines whether or not to show to sign-up form
 * based on whether the form has been submitted, if it
 * has, check the database for consistency and create
 * the new account.
 */
if(isset($_POST['subjoin'])){
	if( checkSubmitValues() ) {
		$showRegForm = FALSE;			// Just for clarity - we return below
		/* Add the new account to the database */
		$_SESSION['reguname'] = $_POST['user'];
		$_SESSION['reguid'] = addNewUser(trim($_POST['user']),
														trim($_POST['pass']), trim($_POST['email']));
		$_SESSION['registered'] = true;
		echo "<meta http-equiv=\"Refresh\" content=\"0;url=$HTTP_SERVER_VARS[PHP_SELF]\">";
		return;
	}
	// Otherwise will fall through to shwo the form, with the error set.
}
else if(isset($_GET['confirm'])){
	if( confirmUser($_GET['confirm']) ) {
include("genheader.php");
	echo "<p>Thank you for confirming your registration.</p>"
		."<p><a href=\"main.php\">Return to the main page.</a></p>";
		$showRegForm = FALSE;
	} else {
		$showRegErr = "This registration is no longer valid. Please begin your registration again.";
	}
}
else if(isset($_SESSION['registered'])){
	$showRegForm = FALSE;
/**
 * This is the page that will be displayed after the
 * registration has been attempted.
 */
include("genheader.php");
displayStatus();
include("gentail.php");
   return;
}

if($showRegForm){
/**
 * This is the page with the sign-up form, the names
 * of the input fields are important and should not
 * be changed.
 */
include("genheader.php");
?>
<h2>Register</h2>
<?php if($showRegErr) echo '<h3 style="color:red;">'.$showRegErr.'</h3>'; ?>
<form action="<?php echo $HTTP_SERVER_VARS['PHP_SELF']; ?>" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td>
<input type="text" name="user" maxlength="40" <?php if($showRegUser) echo ' value="'.$showRegUser.'" '; ?> >
</td></tr>
<tr><td>Password:</td><td><input type="password" name="pass" maxlength="40"></td></tr>
<tr><td>Repeat Password:</td><td><input type="password" name="pass2" maxlength="40"></td></tr>
<tr><td>Email:</td><td>
	<input type="text" name="email" maxlength="70" <?php if($showRegEmail) echo ' value="'.$showRegEmail.'" '; ?> >
</td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subjoin" value="Register"></td></tr>
</table>
</form>
<?php
}
include("gentail.php");
?>
