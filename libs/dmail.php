<?php
// path to smtp.php from XPM2 package
require_once 'XPM/smtp.php';

function sendDelphiMail($username, $email, $subj, $plaintextmsg, $htmlmsg){
	$mail = new SMTP;
	$mail->Delivery('local');
	$mail->From('delphi@hearstmuseum.berkeley.edu', 'Delphi');
	$mail->AddTo($email, $username);
	$mail->Text($plaintextmsg);
	$mail->Html($htmlmsg);
	return $mail->Send($subj);
}
?>
