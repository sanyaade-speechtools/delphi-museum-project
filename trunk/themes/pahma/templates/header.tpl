<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="{$themeroot}/style/style.css" type="text/css" />
{if isset($style_block) }
{$style_block}
{/if}
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.2.1.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.overlabel.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/header.js"></script>
{if isset($script_block) }
{$script_block}
{/if}

<title>{$page_title}</title>

</head>

<body>

<div id="headerBar">
	<div id="headerBarContent">
		<img src="{$themeroot}/images/logo.gif" alt="Logo Image"/>
		<div id="loginNav">
			{if $currentUser_loggedIn }
				Welcome <a href="{$wwwroot}/modules/auth/profile.php"><span>{$currentUser_name}</span></a> | 
				<a href="{$wwwroot}/modules/sets/mysets.php">My Sets</a> | 
				<a href="{$wwwroot}/modules/help/help.php">Help</a> | 
				{if $currentUser_isAdmin || $currentUser_isAuthStaff }
				<a href="{$wwwroot}/modules/admin/admin.php">Admin</a> |
				{/if}
				<a href="{$wwwroot}/modules/auth/logout.php">Sign Out</a>
			{else}
				<a href="{$wwwroot}/modules/auth/login.php">Sign In</a> | 
				<a href="{$wwwroot}/modules/auth/register.php">Register</a>
			{/if}
		</div>
	</div>
</div>
<div id="navBar">
	<div id="navBarContent">
		<span class="navLink"><a href="{$wwwroot}/modules/frontpage/frontpage.php">Home</a> </span>
		<span class="navLink"><a href="{$wwwroot}/modules/facetBrowser/browser.php">Browse</a></span>
		<span class="navLink"><a href="#">Sets</a></span>
		<div id="navSearchBox">
			<form action="{$wwwroot}/modules/facetBrowser/facetBrowse.php" method="get">
				<div>
					<label for="navSearchBoxInput" class="overlabel">Search the Collection</label>
					<input type="text" name="kwds" maxlength="40" title="Search The Collection" id="navSearchBoxInput" />
					<input type="submit" value="Search" id="navSearchBoxButton" /> or <a href="{$wwwroot}/modules/facetBrowser/browser.php">Browse</a>
				</div>
			</form>
		</div>
	</div>
</div>
<div id="contentContainer">
	<div id="content">
