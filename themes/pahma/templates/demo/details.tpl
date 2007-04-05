{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<div id="content">
	<div id="viewset_nameDiv"><span class="viewset_setName" id="setName___1">{$name}</span></div>
	<div id="viewset_innerContent">
	<div id="detail_imageColumn">
		<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set" style="float:right;">Add to Set</a>
		<div id="detail_image"><img	src="{$mids}/{$img}.jpg" width="398px" height="298px"/></div>
	</div>

	<div id="detail_description">
		<h3>This details page will have more information about the object as well as an interactive image that users can zoom in with. </h3>
		<p>{$description}</p>
	</div>
	</div>
</div>
{include file="footer.tpl"}