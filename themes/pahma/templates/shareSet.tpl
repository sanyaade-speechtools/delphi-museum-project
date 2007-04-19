<script type="text/javascript" src="{$themeroot}/scripts/shareSet.js"></script>

<div id="viewset_sendSetForm">
	<p>To share this set, just fill out this form and we'll email them a link</p>
	<form action="#" method="get" accept-charset="utf-8">
		To:<br/><input type="text" name="email" value="" id="emailTo" size="26"><br/><br/>
		From:<br/><input type="text" name="email" value="youemail@wouldBeHere.com" id="emailFrom" size="26"><br/><br/>
		Your personal message (optional):<br/><textarea name="message" rows="5" cols="25"></textarea>
		<center>
			<!--
				TODO The shareSetSendButton is not working :(
			-->
		<p><a class="closer" href="#" style="float:left;">Cancel</a> <input type="button" value="Send Email" class="shareSetSendButton" /></p>
		</center>
	</form>
</div>

<div id="viewset_confirmSendForm" style="display:none;">
	<p>We just emailed a link to this set to "alexsmith@hotmail.com".</p>
	<p><a href="#" class="closer">Close</a></p>
</div>