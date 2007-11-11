{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.flash.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>


	<div id="viewset_leftCol">
		<h1>{$setName}</h1>
		
		<div id="viewset_createdBy" class="smaller">Created by <a href="{$wwwroot}/modules/auth/profile.php?uid={$owner_id}">{$username}</a></div>
		
		<h3>Description</h3>
		{if $setDescription}
			<div id="viewset_setDescription">{$setDescription}</div>
		{else}
			<div id="viewset_setNoDescription">No description</div>
		{/if}
		<h3>Objects</h3>
		
			{if $setHasObjects}
				<div id="viewset_thumbnails">
					{section name=obj loop=$objects}
					<div class="viewset_objectThumb" id="objectThumb_{$objects[obj].id}">
						{$objects[obj].thumb}
					</div>
					{/section}
				</div>
			{else}
				<div id="viewset_noThumbnails">
					There are no objects in this set
				</div>
			{/if}
		
	</div>

	{if $setHasObjects}
	<div id="viewset_rightCol">
		<div id="viewset_detailsBox">
			<div id="viewset_objectImage">Replaced by flash</div>
			<h2 id="viewset_objectName"></h2>
			<div id="viewset_objectDescription"></div>
			<a href="#" id="viewset_objectDetaiLink" class="smaller">View object details</a>
		</div>
	</div>
	<img src="{$themeroot}/images/viewset_prev.png" id="viewset_prev" alt="View the previous item" title="View the previous item" class="viewset_prevIcon"/>
	<img src="{$themeroot}/images/viewset_prev_click.png" id="viewset_prev_click" alt="View the previous item" title="View the previous item" class="viewset_prevIcon" style="display:none;"/>

	<img src="{$themeroot}/images/viewset_next.png" id="viewset_next" alt="View the next item" title="View the next item" class="viewset_nextIcon"/>
	<img src="{$themeroot}/images/viewset_next_click.png" id="viewset_next_click" alt="View the next item" title="View the next item" class="viewset_nextIcon" style="display:none;"/>
	{/if}
{include file="footer.tpl"}