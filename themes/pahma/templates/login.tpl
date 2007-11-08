{include file="header.tpl"}
<div id="contentNarrow">
	

<h1>Login</h1>
This site is currently not open to the public.
If you need access, <a href="mailto:mtblack@berkeley.edu?subject=Delphi">email us</a>.
{if $message}
<div class="formError">
	{$message}
</div>
{/if}
<form action="{$wwwroot}/modules/auth/login.php" method="post" class="delphiForm">
	<label for="user">Username <span class="requiredFieldIndicator">*</span></label>
	<input type="text" name="user" maxlength="40"/>
	
	<label for="pass">Password <span class="requiredFieldIndicator">*</span></label>
	<input type="password" name="pass" maxlength="40"/>
	<br/><br/>
	<input type="checkbox" name="remember"/> Remember me on this computer
	<div class="buttonRow">
		<input type="submit" name="sublogin" value="Login"/>
	</div>
</form>
<p><!-- <a href="register.php">Register</a> --><br/>
<a href="forgotpw.php">Forgot your password?</a></p>
</div>
{include file="footer.tpl"}
