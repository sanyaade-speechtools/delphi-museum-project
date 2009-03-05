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

	if(!strlen($_POST['subject']) > 0) {
		$_POST['subject'] = 'Feedback on Delphi';
	}
	
	if(count($msg) > 0){
		$t->assign('name', cleanFormData($_POST['name']));
		$t->assign('email', cleanFormData($_POST['email']));
		$t->assign('message', cleanFormData($_POST['message']));
		$t->assign('subject', cleanFormData($_POST['subject']));
		$t->assign('messages', $msg);
	} else {
		$nameTo = "Delphi Feedback";
		$emailTo = $CFG->contactEmail;
		$subj = cleanFormData($_POST['subject']);
		$plaintextmsg = cleanFormData($_POST['message']);
		$htmlmsg = cleanFormData($_POST['message']);
		$emailFrom = $_POST['email'];
		$nameFrom = cleanFormData($_POST['name']);

		if(sendDelphiMail($nameTo, $emailTo, $subj, $plaintextmsg, $htmlmsg, $emailFrom, $nameFrom)){
			$t->display('contactSent.tpl');
			die();
		} else {
			$t->assign('heading', "Failed to send message");
			$t->assign('message', "<p>We were unable to deliver your message.</p> <p>You can <a href='".$CFG->wwwroot."/modules/about/contact.php'>try again</a> or send us an email directly at <a href='mailto:$emailTo'>$emailTo</a>.</p>");
			$t->display('error.tpl');
			die();
		}
	}
}
if( isset($_SESSION['id']) ) {
	$q = "select username, real_name, email from user where id=".$_SESSION['id'];
	$res =& $db->query($q);
	if (!(PEAR::isError($res))) {
		if($row = $res->fetchRow()) {
			if( isset( $row['real_name'] ))
				$t->assign('name', $row['real_name']);
			else if( isset( $row['username'] ))
				$t->assign('name', $row['username']);
			if( isset( $row['email'] ))
				$t->assign('email', $row['email']);
		}
		// Free the result
		$res->free();
	}
}

if( isset( $_GET['objNum'] ) ) {
	$t->assign('subject', 'Feedback on Object: '.$_GET['objNum']);
} else {
	$t->assign('subject', 'Feedback on Delphi');
}
$t->display('contact.tpl');

?>
