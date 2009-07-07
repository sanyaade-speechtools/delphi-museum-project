<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="shortcut icon" href="/delphi/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="{$themeroot}/style/style.css" type="text/css" />
{if isset($style_block) }
{$style_block}
{/if}
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.2.1.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.overlabel.js"></script>
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var wwwroot = "{$wwwroot}";
//]]>
</script>
<script type="text/javascript" src="{$themeroot}/scripts/header.js"></script>
{if isset($script_block) }
{$script_block}
{/if}
<title>{$page_title}</title>
</head>
<body>
	<div id="contentContainer">
		<div id="headerBar">
			<div id="headerBarLinks" class="smaller">
				{if $currentUser_loggedIn }
					Logged in as <a href="{$wwwroot}/modules/auth/profile.php?uid={$currentUser_id}"><span>{$currentUser_name}</span></a> | 
					<a href="{$wwwroot}/modules/sets/mysets.php">My Sets</a> | 
					<a href="{$wwwroot}/modules/tags/showTags.php?user=curr">My Tags</a> | 
					<a href="{$wwwroot}/modules/help/help.php">Help</a> | 
					{if $currentUser_isAdmin || $currentUser_isAuthStaff }
					<a href="{$wwwroot}/modules/admin/admin.php">Admin</a> |
					{/if}
					<a href="{$wwwroot}/modules/auth/logout.php">Sign Out</a>
				{else}
					<a href="{$wwwroot}/modules/auth/register.php">Register</a> | 
					<a href="{$wwwroot}/modules/help/help.php">Help</a> | 
					<a href="{$wwwroot}/modules/auth/login.php">Sign In</a>
				{/if}
			</div>
		</div>
		<div id="navBar">
			<div id="navBarLinks">
				<a class="navLink" href="{$wwwroot}">Home</a>
				<a class="navLink" href="{$wwwroot}/modules/browser/browser.php">Browse</a>
				<a class="navLink" href="{$wwwroot}/modules/sets/featuredSets.php">Sets</a>
				<a class="navLink" href="{$wwwroot}/modules/tags/showTags.php?user=any">Tags</a>
			</div>
			<div id="navBarSearchBox">
				<form id="navBarSearchBoxForm" action="{$wwwroot}/results/" method="get">
					<div>
						<label for="navSearchBoxInput" class="overlabel">Search the Collection</label>
						<input type="text" name="rawkwds" maxlength="40" title="Search The Collection" id="navSearchBoxInput" class="delphiFormInput"/>
						<input type="submit" value="Search" id="navSearchBoxButton" />
					</div>
				</form>
			</div>
		</div>
		<div id="content">
