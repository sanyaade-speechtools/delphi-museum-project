{include file="header.tpl"}

<script type="text/javascript" src="{$themeroot}/scripts/treeview.js"></script>

<code style="display:none;">CountsByCat Query: {$catsByCountQ}</code>
<h3>PAHMA online has {$objsTotal} total objects{$qual}.</h3>

<div id="content">
	<h2>Collection Browser</h2>

	<div id="inner_content">
		<div id="browser_facetTree">
			{$facetTree}
		</div>

		<div id="browser_help">
			<h2>Instructions</h2>
			<p>Click on any concept term to search for objects that match that concept.</p>
			<p>You can also click on a&nbsp;&nbsp;
				<img src="images/expandbtn.gif" width="9px" height="9px" />
				&nbsp;&nbsp;icon to the left of a concept term to expand
				the sub-concepts for that concept term.</p>
		<p>The counts after the concept terms indicate the number of matching objects in the 
		collections.</p>

		</div>

		<br style="clear:both;" />
	</div>
</div>

{include file="footer.tpl"}
