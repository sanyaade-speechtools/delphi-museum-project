{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
	var requested_cats = "{$cats}";
	var requested_kwds = "{$kwds}";
//]]>
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/getParams.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/results.js"></script>
<div id="results_menuCol">
	<div id="results_filterContainer">
		<h2>Found Objects where...</h2>
		<div id="results_searchFilters">
			{$filters}
		</div>
		<!-- <a href="#">New search</a> -->
	</div>
	{if $results_total > 0}
	<div id="results_categoriesContainer">
		<h2>Narrow with Categories</h2>
		{section name=facet loop=$facets}
			<h3>{$facets[facet].facet}</h3>
			{$facets[facet].items}
	    {/section}
	</div>
	<div id="results_keywordContainer">
		<h2>Narrow with Keywords</h2>
		<form method="post" accept-charset="utf-8" id="results_keywordForm">
			<div>
				<input type="text" name="keyword" value="" size="15" class="delphiFormInput" id="results_keywordInput"/>
				<input type="submit" value="Add"/>
			</div>
		</form>
		<br/><br/>
		{if $toggleImages}
			<a href="#" id="toggleImagesLink">Include objects without images</a>
		{else}
			<a href="#" id="toggleImagesLink">Exclude objects without images</a>
		{/if}
	</div>
	{else}
		{if $toggleImages}
			<div id="results_keywordContainer">
				<br/><br/>
				<p><a href="#" id="toggleImagesLink">Include objects without images</a></p>
			</div>
		{/if}
	{/if}
</div>
<div id="results_resultsCol">
	{if $results_total > 0}
	<div id="results_pagerCount">
		<h2>{$results_start} - {$results_end} of {$results_total} Results</h2>
	</div>
	<div id="results_thumbnails">
		{section name=object loop=$objects}
		<div class="results_object smaller">
			<div class="results_objectThumb">
				{$objects[object].thumb}
			</div>
			<div class="resuts_objectDetails">
				<a href="{$shortbase}/object/{$objects[object].id}">{$objects[object].name}</a>
			</div>
		</div>
	    {/section}
	</div>
	<div id="results_pagerLinks">
		{$pager}
	</div>
	{else}
	<h2>No results!</h2>
	{/if}
</div>



{include file="footer.tpl"}
