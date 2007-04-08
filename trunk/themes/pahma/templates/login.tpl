{include file="header.tpl"}

<h2>Login</h2>
<h3>{$message}</h3>
<form action="{$wwwroot}/modules/auth/login.php" method="post">
	<table border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td>Username:</td>

			<td><input type="text" name="user" maxlength="40"></td>
		</tr>

		<tr>
			<td>Password:</td>

			<td><input type="password" name="pass" maxlength="40"></td>
		</tr>

		<tr>
			<td colspan="2" align="left"><input type="checkbox" name="remember"> <font size="2">Remember me on this computer</font></td>
		</tr>

		<tr>
			<td colspan="2" align="right"><input type="submit" name="sublogin" value="Login"></td>
		</tr>

		<tr>
			<td colspan="2" align="left"><a href="register.php">Register</a></td>
		</tr>

		<tr>
			<td colspan="2" align="left"><a href="forgotpw.php">Forgot your password?</a></td>
		</tr>
	</table>
</form>

{include file="footer.tpl"}