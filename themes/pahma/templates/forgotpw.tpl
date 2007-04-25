{include file="header.tpl"}

<h2>Request a new password</h2>
<h4>{$message}</h4>
<form method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td><input type="text" name="user" maxlength="40" ></td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subreq" value="Submit"></td></tr>
</table>
</form>

{include file="footer.tpl"}