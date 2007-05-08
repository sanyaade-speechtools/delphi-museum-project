<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="{$themeroot}/style/style.css" type="text/css" />

<title></title>
</head>

<body>

	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>


<div id="headerBar">
	<div id="headerBarContent">
		<img src="{$themeroot}/images/logo.gif" />
		<div id="loginNav">
			Welcome <a href="{$wwwroot}/modules/auth/profile.php"><span>Adam Smith</span></a> | 
			<a href="{$wwwroot}/modules/sets/mysets.php">My Sets</a> | 
			<a href="#">Help</a> | 
			<a href="{$wwwroot}/modules/auth/logout.php">Sign Out</a>
		</div>
	</div>
</div>
<div id="navBar">
	<div id="navBarContent">
		<span class="navLink"><a href="{$wwwroot}/modules/frontpage/frontpage2.php">Home</a> </span>
		<span class="navLink"><a href="{$wwwroot}/modules/frontpage/frontpage2.php">Browse</a></span>
		<span class="navLink"><a href="{$wwwroot}/modules/frontpage/frontpage2.php">Sets</a></span>
		<div id="navSearchBox">
			<form action="{$wwwroot}/modules/facetBrowser/facetBrowse.php" method="get">
				<input type="text" name="kwds" maxlength="40" value="Search the collection" id="navSearchBoxInput">
				<input type="submit" value="Search" id="navSearchBoxButton"> or <a href="#">Browse</a>
			</form>
		</div>
	</div>
</div>
<div id="content">

</div>

<div id="footer"></div>
</body>

</html>