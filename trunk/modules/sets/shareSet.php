<?php
require_once("../../libs/env.php");
require_once("../../libs/utils.php");

if ( isset( $_POST['submitted'] ) ) {
	$nameTo = "";
	$emailTo = $_POST['emailTo'];
	$subj = $_SESSION['username'] . " shared a set with you";
	/*
		TODO Make a real message when sharing a set.
	*/
	$plaintextmsg = $CFG->wwwroot . "/modules/sets/viewset.php?sid=" . $_POST['setId'];
	$htmlmsg = "<a href='" . $CFG->wwwroot . "/modules/sets/viewset.php?sid=" . $_POST['setId'] . "'>Click here to see!</a>";
	$emailFrom = $_SESSION['email'];
	$nameFrom = $_SESSION['username'];
	if (sendDelphiMail($nameTo, $emailTo, $subj, $plaintextmsg, $htmlmsg, $emailFrom, $nameFrom)){
		echo $_POST['emailTo'];		
	} else {
		echo "Failed to send!";
	}


} else {
	// If there is no id param in the url...
	if( isset( $_GET['sid'] ) ) {
		$setId = $_GET['sid'];
	} else {
		$t->assign('heading', "Error");
		$t->assign('message', "I don't know what set you want to share because there was no sid in the URL");	
		$t->display('error.tpl');
		die;
	}
	$t->assign('setId', $setId );
	$t->display('shareSet.tpl');
}
?>
