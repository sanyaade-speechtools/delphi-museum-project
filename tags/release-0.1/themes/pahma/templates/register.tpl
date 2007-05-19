{include file="header.tpl"}

<h2>Register</h2>
<h3>{$message}</h3>
<form action="{$wwwroot}/modules/auth/register.php" method="post">
<table border="0" cellspacing="0" cellpadding="3">
<tr><td>Username:</td><td>
<input type="text" name="user" maxlength="40">
</td></tr>
<tr><td>Password:</td><td><input type="password" name="pass" maxlength="40"></td></tr>
<tr><td>Repeat Password:</td><td><input type="password" name="pass2" maxlength="40"></td></tr>
<tr><td>Email:</td><td>
	<input type="text" name="email" maxlength="70">
</td></tr>
<tr><td colspan="2" align="right"><input type="submit" name="subjoin" value="Register"></td></tr>
</table>
</form>

{include file="footer.tpl"}