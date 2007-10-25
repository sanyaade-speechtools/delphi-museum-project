<?php 
/* Include Files *********************/
require_once("../../libs/env.php");
/*************************************/
// If the user isn't logged in, send to the login page.
if(($login_state != DELPHI_LOGGED_IN) && ($login_state != DELPHI_REG_PENDING)){
	header( 'Location: ' . $CFG->wwwroot . '/modules/auth/login.php' );
	die();
}

// This need not verify perms as it does not expose anything directly. 

$t->display('admin.tpl');
?>
