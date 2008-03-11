{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/getParams.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/results.js"></script>
<div id="results_menuCol">
	<div id="results_filterContainer">
		<h3>Find Objects where:</h3>
		<div id="results_searchFilters">
			{$filters}
		</div>
		<!-- <a href="#">New search</a> -->
	</div>
	{if $results_total > 0}
	<hr />
	<div id="results_categoriesContainer">
		<h3>Narrow with Categories:</h3>
		{section name=facet loop=$facets}
			<h4>{$facets[facet].facet}</h4>
			{$facets[facet].items}
	    {/section}
	</div>
	<hr />
	<div id="results_categoriesContainer">
		<h3>Narrow with Keywords:</h3>
		<form method="post" accept-charset="utf-8" id="results_keywordForm">
			<div>
				<input type="text" name="keyword" value="" size="15" class="delphiFormInput" id="results_keywordInput"/>
				<input type="submit" value="Add"/>
			</div>
		</form>
	</div>
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
				{$objects[object].name}
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
