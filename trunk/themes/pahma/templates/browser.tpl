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
	<h1>Concept Browser</h1>
	<p>Youu can use this facet browser to find objects in the Museum's collections
	which are related to any of the listed concepts.
	Click on any concept to see objects related to that concept.
	Click on the '+' button to the left of the concept to see sub-concepts.</p>
	<p>The numbers in parentheses after each concept represent the <em>number of objects</em>
	in that	concept: the first number is for objects <em>with images</em>, and the second
	is for <em>all objects</em> (including those without images).
	</p>
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
