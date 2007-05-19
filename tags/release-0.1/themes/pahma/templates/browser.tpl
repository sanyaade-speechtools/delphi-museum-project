{include file="header.tpl"}

<script type="text/javascript" src="{$themeroot}/scripts/treeview.js"></script>

<code style="display:none;">CountsByCat Query: {$catsByCountQ}</code>

<h1>Collection Browser</h1>


<div id="results_facetTree">
	{$facetTree}
</div>

<div id="results_help">
	<h2>Instructions</h2>
	<p>Click on any concept term to search for objects that match that concept.</p>
	<p>The counts after the concept terms indicate the number of matching objects in the collections.</p>
	<p>PAHMA online has {$objsTotal} total objects{$qual}.</p>
</div>

{include file="footer.tpl"}
