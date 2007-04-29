<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="{$themeroot}/style/delphi_style.css" type="text/css" />

<title></title>
</head>

<body>
<div id="header">
	<div id="logo"><h1><a href="{$wwwroot}/modules/frontpage/frontpage.php">Delphi</a></h1></div>
	
	<div id="search">
		<form action="{$wwwroot}/modules/facetBrowser/facetBrowse.php" method="get">
			<input type="text" name="kwds" maxlength="40">
			<input type="submit" value="Search">
		</form>
	</div>
	
	<div id="user">
		{if $currentUser_loggedIn }
			Welcome <strong>{$currentUser_name}</strong> | <a href="{$wwwroot}/modules/auth/profile.php">My Account</a> &middot; <a href="{$wwwroot}/modules/sets/mysets.php">My Sets</a> &middot; <a href="{$wwwroot}/modules/auth/logout.php">Sign Out</a>
		{else}
			<a href="{$wwwroot}/modules/auth/login.php">Sign In</a> or <a href="{$wwwroot}/modules/auth/register.php">Register</a>
		{/if}
	</div>
	<br class="clearbreak" />
</div>
