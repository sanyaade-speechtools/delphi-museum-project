{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>
<script type="text/javascript" src="{$themeroot}/scripts/mysets.js"></script>

<!--
	TODO Add view count
-->
<div id="contentNarrow">
	<h1>My Sets</h1>
	<p>Sets allow you to organize objects into collections. You can make sets private so that only you can see them, or you can make them public and share your set with others. You can add your own titles and captions for each object within a set to tell a story with your set.</p>
	<p><a href="#" id="mysets_createNewSet">Create a new set</a></p>
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