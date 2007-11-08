<?php

require_once("../../libs/env.php");
require_once("../../libs/utils.php");

if(isset($_POST['submit'])){
	$msg = array();
	
	if(!emailValid($_POST['email'])){
		array_push($msg, "Email address is not valid.");
	}
	
	if(!strlen($_POST['message']) > 0){
		array_push($msg, "You must enter a message.");
	}
	
	if(count($msg) > 0){
		$t->assign('name', stripslashes($_POST['name']));
		$t->assign('email', stripslashes($_POST['email']));
		$t->assign('message', stripslashes($_POST['message']));
		$t->assign('messages', $msg);
	} else {
		$nameTo = "Delphi Feedback";
		$emailTo = $CFG->contactEmail;
		$subj = "New message from Delphi's feedback form";
		$plaintextmsg = $_POST['message'];
		$htmlmsg = $_POST['message'];
		$emailFrom = $_POST['email'];
		$nameFrom = $_POST['name'];

		if(sendDelphiMail($nameTo, $emailTo, $subj, $plaintextmsg, $htmlmsg, $emailFrom, $nameFrom)){
			$t->display('contactSent.tpl');
		} else {
			$t->assign('heading', "Failed to send message");
			$t->assign('message', "<p>We were unable to deliver your message.</p> <p>You can <a href='".$CFG->wwwroot."/modules/about/contact.php'>try again</a> or send us an email directly at <a href='mailto:$emailTo'>$emailTo</a>.</p>");
			$t->display('error.tpl');
			die();
		}
	}
}

$t->display('contact.tpl');

?>