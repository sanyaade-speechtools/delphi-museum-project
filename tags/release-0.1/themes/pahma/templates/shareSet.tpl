<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">

<html>
<head>

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.form.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.validate.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/metadata.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/shareSet.js"></script>
<link rel="stylesheet" href="{$themeroot}/style/style.css" type="text/css" />


	<title></title>
</head>

<body>
	<div id="viewset_sendSetForm">
		<p>To share this set, just fill out this form and we'll email them a link</p>

		<form id="shareSetForm" action="{$wwwroot}/modules/sets/shareSet.php" method="post" accept-charset="utf-8" name="shareSetForm">
			<!--
			TODO Add prompt text in the to field that goes away on focus and returns on blur if nothing was entered.
		-->
			<label for="emailTo">To:</label><br>
			<input type="text" name="emailTo" value="" id="emailTo" size="40" class="{ldelim}required:true,email:true{rdelim}"><br>
			<br>
			<label for="emailFrom">From:</label><br>
			<input type="text" name="emailFrom" value="{$currentUser_email}" id="emailFrom" size="40" class="{ldelim}required:true,email:true{rdelim}"><br>
			<br>
			<label for="message">Your personal message (optional):</label><br>
			<textarea id="message" name="message" rows="3" cols="25">
</textarea><br>
			<br>
			<a class="closer" href="#" style="float:left;margin-right:30px">Cancel</a> <input type="submit" value="Send Email"> <input type="hidden" name="submitted" value="True"> <input type="hidden" name="setId" value="{$setId}">
		</form>
	</div>

	<div id="viewset_confirmSendForm" style="display:none;">
		<p>We just emailed a link to this set to "<span id='toEmailConfirm'></span>".</p>

		<p><a href="#" class="closer">Close</a></p>
	</div>
</body>
</html>