{include file="header.tpl"}

<h1>Register</h1>
{if $message}
<div class="formError">
	{$message}
</div>
{/if}
<form action="{$wwwroot}/modules/auth/register.php" method="post" class="delphiForm">
	<label for="user">Username <span class="requiredFieldIndicator">*</span></label>
	<input type="text" name="user" maxlength="40">
	
	<label for="pass">Password <span class="requiredFieldIndicator">*</span></label>
	<input type="password" name="pass" maxlength="40"/>
	
	<label for="pass2">Retype your password <span class="requiredFieldIndicator">*</span></label>
	<input type="password" name="pass2" maxlength="40"/>
	
	<label for="email">Email <span class="requiredFieldIndicator">*</span></label>
	<input type="text" name="email" maxlength="70"/>
	<div class="buttonRow">
		<input type="submit" name="subjoin" value="Register"/>
	</div>
</form>

{include file="footer.tpl"}