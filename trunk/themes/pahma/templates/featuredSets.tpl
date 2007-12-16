{include file="header.tpl"}

<!--
	TODO Add view count
-->
<div id="contentNarrow">
	<h1>Featured Sets</h1>
	<p>Featured sets are selected by the staff at the museum. Featured sets are selected by the staff at the museum.</p>
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