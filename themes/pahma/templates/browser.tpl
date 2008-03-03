{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/treeview/jquery.treeview.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/treeview/jquery.cookie.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/browser.js"></script>

<div id="contentNarrow">
	<h1>Collection Browser</h1>
	<p>You can use the category browser to find all of the categories associated with the museum collection. Click on any category to search for objects in that category. Click on the triangle to the left of the category to see sub-categories.</p>
	<div id="browserDiv">
	
{if !isset($facets) }
	<p>There are no facets defined!</p>
{else}
	<div id="browserTabs">
	{section name=facet loop=$facets}
		<div  style="top:{$smarty.section.facet.index*-1}px"
		    class="browser_topLevelFacet{if $smarty.section.facet.index == 0} browser_firstFacet browser_topLevelFacetSelected{/if}" 
			id="facet_id_{$facets[facet].id}">{$facets[facet].facet}</div>
		{/section}
	</div>
	{section name=facet loop=$facets}
	<div class="browser_browsePane{if $smarty.section.facet.index == 0} browser_browsePaneSelected{/if}" 
			id="facet_id_{$facets[facet].id}_pane">
		<div class="smaller">{$facets[facet].desc}</div>
		<div class="tree">{$facets[facet].items}</div>
	</div>
	{/section}
{/if}
	
	</div>
</div>
{include file="footer.tpl"}
