{include file="header.tpl"}
<!-- <script type="text/javascript" src="{$themeroot}/scripts/treeview.js"></script> -->
<script type="text/javascript" src="{$themeroot}/scripts/browser.js"></script>

<div id="contentNarrow">
	<h1>Collection Browser</h1>
	<p>You can use the category browser to find all of the categories associated with the museum collection. Click on any category to search for objects in that category. Click on the triangle to the left of the category to see sub-categories.</p>
	
	<div id="browserTabs">
		<div style="top:0px" class="browser_topLevelFacet browser_topLevelFacetSelected" id="facet_id_10000">Use or Context</div>
		<div style="top:34px" class="browser_topLevelFacet" id="facet_id_20000">Site or Provenience</div>
		<div style="top:68px" class="browser_topLevelFacet" id="facet_id_30000">Cultural Group</div>
		<div style="top:102px" class="browser_topLevelFacet" id="facet_id_40000">Material</div>
		<div style="top:136px" class="browser_topLevelFacet" id="facet_id_50000">Technique</div>
		<div style="top:170px" class="browser_topLevelFacet" id="facet_id_60000">Color</div>
	</div>
	<div class="browser_browsePane browser_browsePaneSelected" id="facet_id_10000_pane">
		<div class="smaller">Use or Context describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_10000}
	</div>
	<div class="browser_browsePane" id="facet_id_20000_pane">
		<div class="smaller">Site or Provenience describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_20000}
	</div>
	<div class="browser_browsePane" id="facet_id_30000_pane">
		<div class="smaller">Cultural Group describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_30000}
	</div>
	<div class="browser_browsePane" id="facet_id_40000_pane">
		<div class="smaller">Material describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_40000}
	</div>
	<div class="browser_browsePane" id="facet_id_50000_pane">
		<div class="smaller">Technique describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_50000}
	</div>
	<div class="browser_browsePane" id="facet_id_60000_pane">
		<div class="smaller">Color describes the blah abd blah of an object. It something for the of something and the other thing.</div>
		{$cats_60000}
	</div>
	
</div>
{include file="footer.tpl"}
