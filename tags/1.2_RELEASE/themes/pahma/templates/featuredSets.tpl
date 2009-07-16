{include file="header.tpl"}

<!--
	TODO Add view count
-->
<div id="contentNarrow">
	<h1>Featured Sets</h1>
	<p>These sets have been selected by Museum staff as being particularly  
	interesting, thoughtful, informative, and/or imaginative.  Some of  
	these have been made by Museum staff, but most of these sets were made  
	by members of our online community. If you have a set that you would  
	like to have considered as a featured set, you can nominate your set  
	by using the "Share this set" feature on your "My Sets" page and  
	address the set to delphi_feedback@lists.berkeley.edu.</p>
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
