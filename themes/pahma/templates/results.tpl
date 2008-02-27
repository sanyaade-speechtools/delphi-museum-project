{include file="header.tpl"}

<script type="text/javascript" src="{$themeroot}/scripts/results.js"></script>
<div id="results_menuCol">
	<div id="results_filterContainer">
		<h2>Search Filters</h2>
		<form method="post" accept-charset="utf-8">
			<input type="text" name="keyword" value="" size="15" class="delphiFormInput"/>
			<input type="submit" value="Add keyword"/>
		</form>
		<a href="?cats=123,23,123&amp;keywords=sldkj,sasdf,ASjdfkjsdf" script="return false;">New search</a>
	</div>
	<div id="results_categoriesContainer">
		<h2>Categories</h2>
		{section name=facet loop=$facets}
			<h4>{$facets[facet].facet}</h4>
			{$facets[facet].items}
	    {/section}
	</div>	
</div>
<div id="results_resultsCol">
	<div id="results_pagerCount">
		<h2>{$results_start} - {$results_end} of {$results_total} Results</h2>
	</div>
	<div id="results_pagerLinks">
		Previous 1 2 3 4 5 6 Next
	</div>
	<div id="results_thumbnails">
		{section name=object loop=$objects}
			{$objects[object].name}<br/>
	    {/section}
	</div>
</div>



{include file="footer.tpl"}