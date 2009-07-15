{include file="header.tpl"}
<div id="front_tongue">
	<h1>{$greetingHdr}</h1>
	<p class="smaller">{$greetingTxt}</p>
	<!--
	<div class="front_tongueLink">
		<a href="{$wwwroot}/modules/facetBrowser/browser.php">Browse the Collection</a>
	</div>
	-->
</div>
<div id="front_featuredObjects">
	<h2>Featured Objects</h2>
	{section name=obj loop=$objects}
            {$objects[obj]}
    {/section}
</div>
<div id="front_greyCol">
	<h2>Featured Sets</h2>
	{section name=set loop=$sets}
		<div class="setlet_Large smaller">
			<div class="setletThumb_Large">
				{$sets[set].thumb}
			</div>
			<div class="setletDetails_Large">
				<a href="/delphi/set/{$sets[set].set_id}">{$sets[set].set_name}</a>
				<p><strong>{$sets[set].total_objects}</strong> objects</p>
			</div>
		</div>
	{/section}
</div>

{include file="footer.tpl"}
