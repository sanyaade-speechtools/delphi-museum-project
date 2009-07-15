<?php
require_once("../../libs/env.php");
require_once("../../libs/utils.php");

/*
Handles AJAX calls to send mail to share a set. 

Accepts POST data. 
$_POST['set_id'] -- The ID number of the set involved in the action
$_POST['To'] -- The recipient of the mail
$_POST['Subj'] -- Subject string
$_POST['Msg'] -- User-provided message
 */

if( isset($_POST['set_id']) && isset($_POST['To']) 
	 && isset($_POST['Subj']) && isset($_POST['Msg'])){
	$nameTo = "";
	$emailTo = $_POST['To'];
	$subj = $_POST['Subj'];
	if(!isset($subj) || empty($subj))
		$subj = $_SESSION['username'] . " shared a Delphi set with you";
	$plaintextmsg = $_POST['Msg']."\n\nHere is a link to the set:\n"
								. $CFG->wwwroot . "/modules/sets/viewset.php?sid=" . $_POST['set_id'];
	$htmlmsg = "<p>".$_POST['Msg']."</p>"."<br /><h3><a href='"
	 				. $CFG->wwwroot . "/modules/sets/viewset.php?sid=" . $_POST['set_id'] 
					. "'>Click here to see the set!</a></h3>";
	$emailFrom = $_SESSION['email'];
	$nameFrom = $_SESSION['username'];

	$response = array();
	if (sendDelphiMail($nameTo, $emailTo, $subj, $plaintextmsg, $htmlmsg, $emailFrom, $nameFrom)){
		$response['success'] = true;
	} else {
		$response['success'] = false;
		$response['msg'] = "Failed to send!";
	}
	echo json_encode($response); 
} else {  // Run cheesy test ?>
  <html><body>
		<form method="post" accept-charset="utf-8" id="viewset_setShareForm">
			<table cellspacing="2px" cellpadding="2px">
        <tr>
					<td><p style="text-align:right">To:&nbsp;</p></td><td><input type="text" size="40" name="To" value=""/></td>
        </tr>
        <tr>
					<td><p style="text-align:right">Subject:&nbsp;</p></td><td><input type="text" size="40" name="Subj" id="viewset_setShareSubjInput" value=""/></td>
        </tr>
        <tr>
					<td><p style="text-align:left">Your<br />message:</p></td><td><textarea name="Msg" rows="4" cols="35"></textarea><td>
				</tr>
        <tr>
					<td><p style="text-align:right">Set id:&nbsp;</p></td><td><input type="text" size="40" name="set_id" value=""/></td>
        </tr>
			</table>
			<input type="submit" name="submit" value="Send Message"/>
		</form>
  </body></html>
<?php
	 }
?>
