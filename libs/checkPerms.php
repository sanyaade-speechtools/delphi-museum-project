<?php
// Report all PHP errors (bitwise 63 may be used in PHP 3)
// Set to 0 for public release.
error_reporting(E_ALL);

// Define the login states
define("DELPHI_LOGGED_OUT", -1);
define("DELPHI_LOGGED_IN", 0);
define("DELPHI_NO_SUCH_USER", 1);
define("DELPHI_PASSWD_WRONG", 2);
define("DELPHI_REG_PENDING", 3);

/**
 * Checks whether or not the given username is in the
 * database, if so it checks if the given password is
 * the same password in the database for that user.
 * If the user doesn't exist or if the passwords don't
 * match up, it returns an error code (DELPHI_NO_SUCH_USER or DELPHI_PASSWD_WRONG). 
 * On success it returns DELPHI_LOGGED_IN.
 */
function checkUser($username, $password){
   global $mysqli;
   /* Add slashes if necessary (for query) */
   if(!get_magic_quotes_gpc()) {
		$username = addslashes($username);
   }

   /* Verify that user is in database */
   $q = "select passwdmd5, pending from user where username = '$username'";
   $result = $mysqli->query($q);
   if(!$result || ($result->num_rows < 1)){
      return DELPHI_NO_SUCH_USER; //Indicates username failure
   }

   $password = stripslashes($password);
   /* Retrieve password from result, strip slashes */
   $dbarray = $result->fetch_array(MYSQLI_ASSOC);
   $dbpw = stripslashes($dbarray['passwdmd5']);
   $dbpend = stripslashes($dbarray['pending']);

   /* Validate that password is correct */
   if($password == $dbpw){
		 if(!$dbpend)
      	return DELPHI_LOGGED_IN; //Success! Username and password confirmed, and not pending
		 else
      	return DELPHI_REG_PENDING; //login is okay, but account is still pending
   }
   else{
      return DELPHI_PASSWD_WRONG; //Indicates password failure
   }
}

/**
 * checkLogin - Checks if the user has already previously
 * logged in, and a session with the user has already been
 * established. Also checks to see if user has been remembered.
 * If so, the database is queried to make sure of the user's 
 * authenticity. Returns true if the user has logged in.
 */
function checkLogin(){
   /* Check if user has been remembered */
   if(isset($_COOKIE['cookname']) && isset($_COOKIE['cookpass'])){
      $_SESSION['username'] = $_COOKIE['cookname'];
      $_SESSION['password'] = $_COOKIE['cookpass'];
   }

   $result = DELPHI_LOGGED_OUT;	// assume not logged in until we find otherwise
   /* Username and password have been set */
   if(isset($_SESSION['username']) && isset($_SESSION['password'])){
      /* Confirm that username and password are valid */
      $result = confirmUser($_SESSION['username'], $_SESSION['password']);
      if($result != DELPHI_LOGGED_IN && $result != DELPHI_REG_PENDING){
         /* Variables are incorrect, user not logged in */
         unset($_SESSION['username']);
         unset($_SESSION['password']);
      }
   }
	 return $result;
}

/**
 * Determines whether or not to display the login
 * form or to show the user that he is logged in
 * based on if the session variables are set.
 */
function displayLogin(){
   global $login_state;
   if($login_state == DELPHI_LOGGED_IN){
      echo "<h2>Logged In!</h2>";
			echo "<p>Welcome <b>".$_SESSION['username']."</b>, you are logged in.</p>"
				."<p>You can <a href=\"profile.php\">check your profile</a> or "
				."<a href=\"logout.php\">logout</a></p>";
   }
   else if( $login_state == DELPHI_REG_PENDING ){
	echo "<h2>Registration still pending</h2>";
	echo "<p>Welcome <b>".$_SESSION['username']."</b>. Your registration is still pending until you confirm via the link sent to you in email.</p>";
   }
   else{
?>

<form action="" method="post">
<table align="left" border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td><input type="text" name="user" maxlength="40"></td></tr>
<tr><td>Password:</td><td><input type="password" name="pass" maxlength="40"></td></tr>
<tr><td colspan="2" align="left"><input type="checkbox" name="remember">
<font size="2">Remember me on this computer</td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="sublogin" value="Login"></td></tr>
<tr><td colspan="2" align="left"><a href="register.php">Register</a></td></tr>
<tr><td colspan="2" align="left"><a href="forgotpw.php">Forgot your password?</a></td></tr>
</table>
</form>

<?php
   }
}


/**
 * Checks to see if the user has submitted his
 * username and password through the login form,
 * if so, checks authenticity in database and
 * creates session.
 */
if(isset($_POST['sublogin'])){
   /* Check that all fields were typed in */
   if(!$_POST['user'] || !$_POST['pass']){
		 die('<p>You didn\'t fill in a required field.</p>'
			 .'<p><a href="main.php">Return to login page.</a></p>');
   }
   /* Spruce up username, check length */
   $_POST['user'] = trim($_POST['user']);
   if(strlen($_POST['user']) > 40){
      die('<p>Sorry, the username is longer than 40 characters, please shorten it.</p>'
			 .'<p><a href="main.php">Return to login page.</a></p>');
   }

   /* Checks that username is in database and password is correct */
   $md5pass = md5($_POST['pass']);
   $result = confirmUser($_POST['user'], $md5pass);

   /* Check error codes */
   if($result == DELPHI_NO_SUCH_USER){
      die('<p>That username does not exist in our database.</p>'
			 .'<p><a href="main.php">Return to login page.</a> '
			 .'or <a href="register.php">Register</a>.</p>');
   }
   else if($result == DELPHI_PASSWD_WRONG){
      die('<p>Incorrect password, please try again.</p>'
			 .'<p><a href="main.php">Return to login page.</a></p>');
   }

   /* Username and password correct, register session variables */
   $_POST['user'] = stripslashes($_POST['user']);
   $_SESSION['username'] = $_POST['user'];
   $_SESSION['password'] = $md5pass;

   /**
    * This is the cool part: the user has requested that we remember that
    * he's logged in, so we set two cookies. One to hold his username,
    * and one to hold his md5 encrypted password. We set them both to
    * expire in 100 days. Now, next time he comes to our site, we will
    * log him in automatically.
    */
   if(isset($_POST['remember'])){
      setcookie("cookname", $_SESSION['username'], time()+60*60*24*100, "/");
      setcookie("cookpass", $_SESSION['password'], time()+60*60*24*100, "/");
   }

   /* Quick self-redirect to avoid resending data on refresh */
   echo "<meta http-equiv=\"Refresh\" content=\"0;url=$HTTP_SERVER_VARS[PHP_SELF]\">";
   return;
}

/* Sets the value of the login_state variable, which can be used in your code */
$login_state = checkLogin();

?>
