{include file="header.tpl"}

<h2>Profile information for {$username} </h2>
<h3>{$message}</h3>
<form action="" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Change Password:</td><td><input type="password" name="pass" maxlength="40"></td></tr>
<tr><td>Repeat New Password:</td><td><input type="password" name="pass2" maxlength="40"></td></tr>
<tr><td>Email:</td><td>
	<input type="text" name="email" maxlength="70" value="{$email}" >
</td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subreq" value="Update"></td></tr>
<tr><td style="height:20px;"></td></tr>
</table>
</form>

{include file="footer.tpl"}