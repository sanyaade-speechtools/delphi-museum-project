<?php

require_once("../../libs/env.php");
require_once("../../libs/utils.php");


/**
 * Confirms a user's registration, clearing the pending flag in the DB
 */
function confirmRegistration($userid){
   global $db;
	$sql =	"	UPDATE user 
				SET pending = 0
				WHERE id = '$userid'
			";

	$affected =& $db->exec($sql);

	// check that result is not an error
	if (PEAR::isError($affected)) {
	    die($affected->getMessage());
	} else {
		return true;
	}

}

/**
 * Returns true if the username has been taken
 * by another user, false otherwise.
 */
function usernameTaken($username){
   global $db;

   if(!get_magic_quotes_gpc()){
      $username = addslashes($username);
   }

	$sql = "	SELECT username 
				FROM user 
				WHERE username = '$username'
			";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
   	// If nothing is found, username is available
	if ( $res->numRows() < 1 ){
		return false;
	} else {
		return true;
	}
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
   global $db;
   $md5pass = md5($password);
	 // Note that 'pending' defaults to true on INSERT
	 $sql = 'INSERT INTO user ( username, passwdmd5, email, creation_time )'
          ." VALUES ('$username', '$md5pass', '$email', now() )";

	$affected =& $db->exec($sql);

	 if($affected) {
		$sql = "SELECT *
				FROM user
				WHERE username = '$username'";
		
		$res =& $db->query($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
		
		$row = $res->fetchRow();
		$new_uid = $row['id'];
		/*
			TODO get email working
		*/
		sendRegMail($new_uid, $username, $email);
		return $new_uid;
		
	 }
	return;
}

/**
 * Displays the appropriate message to the user
 * after the registration attempt. It displays a 
 * success or failure status depending on a
 * session variable set during registration.
 */
function displayStatus(){
	global $t;
	if ( $_SESSION['reguid'] > 0 ) {
		$t->assign('header','Registered!');	
		$t->assign('message','Thank you, your information has been added to the database. You will receive an email with a confirmation link. Click on the link, or copy the URL into your browser to confirm your registration.');
		$t->display('registerConfirm.tpl');
		die();
	} else {
		unset($_SESSION['reguname']);
		unset($_SESSION['registered']);
		unset($_SESSION['reguid']);
		$t->assign('header','Registration Failed');	
		$t->assign('message','We are sorry, but an error has occurred and your registration could not be completed. Please try again at a later time.');
		$t->display('registerConfirm.tpl');
		die();
	}

}


function checkSubmitValues(){
	global $t;
	/* Make sure all fields were entered */
	if(!$_POST['user'] || !$_POST['pass'] || !$_POST['pass2'] || !$_POST['email']){
		$t->assign('message','Please provide a value for each field. All fields are required');
		$t->display('register.tpl');
		die();
	}

	if(strlen($_POST['pass']) < 3 ){
		$t->assign('message','Your password must be at least 3 characters. Please try again.');
		$t->display('register.tpl');
		die();
	}
	if($_POST['pass'] != $_POST['pass2']){
		$t->assign('message','Your retyped password did not match the first typed password. Please try again.');
		$t->display('register.tpl');
		die();
	}

	/* Spruce up username, check length */
	$_POST['user'] = trim($_POST['user']);
	if(strlen($_POST['user']) > 40){
		$t->assign('message','Sorry, the username is longer than 40 characters; please shorten it.');
		$t->display('register.tpl');
		die();
	}

	/* Check if username is already in use */
	if(usernameTaken($_POST['user'])){
		$t->assign('message','Sorry, that username is already taken. Please pick another one.');
		$t->display('register.tpl');
		die();	
	}

	/* Check if email is valid */
	if(!emailValid($_POST['email'])){
		$t->assign('message','Sorry, the email address is not valid; please enter a valid one.');
		$t->display('register.tpl');
		die();
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
		/* Add the new account to the database */
		$_SESSION['username'] = $_POST['user'];
		$_SESSION['password'] = md5($_POST['pass']);
		$_SESSION['reguid'] = addNewUser(trim($_POST['user']), trim($_POST['pass']), trim($_POST['email']));
		$_SESSION['registered'] = true;
		header( 'Location: ' . $CFG->wwwroot . '/modules/frontpage/frontpage.php' );
		return;
	}
	// Otherwise will fall through to show the form, with the error set.
}
else if(isset($_GET['confirm'])){
	if( confirmRegistration($_GET['confirm']) ) {
		$t->assign('message','Thank you for confirming your registration.');
		$t->display('registerConfirm.tpl');
		die();
	} else {
		$t->assign('message','This registration is no longer valid. Please begin your registration again.');
		$t->display('registerConfirm.tpl');
		die();
	}
}
else if(isset($_SESSION['registered'])){
	/**
	 * This is the page that will be displayed after the
	 * registration has been attempted.
	 * This isn't being used at the moment. Rather user is just redirected.
	 */
	displayStatus();
}

// Display template
$t->display('register.tpl');
die();

?>
