{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.flash.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.tabs.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/details.js"></script>
		<div id="detail_imageCol">
			<div id="detail_mediaBox">
				{if $noImage}
					<img src="{$themeroot}/images/noObjectImage.png" alt="No image"/>
				{else}
					<div id="detail_image">
						Flash zoomer goes here.
					</div>
					<div id="detail_thumbnails">
						{if $hasAdditionalMedia}
							<h3>Additional Media</h3>
							{section name=item loop=$additionalMediaItems}
								<div class="detail_thumbnail">
									{$additionalMediaItems[item].thumb}
								</div>
							{/section}
						{/if}
					</div>
				{/if}
			</div>
			<div id="detail_tabBox">
	            <ul>
	                <li class="detail_setsTabLI"><a href="#detail_setsTab"><span>Sets</span></a></li>
	                <li class="detail_tagsTabLI"><a href="#detail_tagsTab"><span>Tags</span></a></li>
	            </ul>
	            <div id="detail_setsTab">
<!--
	TODO See all sets containing this object
	TODO truncate long set names
-->
	                <p class="smaller">Sets are collections of objects made by users. You can view sets that contain this object or add this object to one of your own sets.</p>
	            	{if $containingSets}
						<h3>Public sets containing this object</h3>
						{section name=set loop=$otherSets}
						<div class="detail_publicSetlet">
							<div class="detail_publicSetletThumb">
								{$otherSets[set].thumb}
							</div>
							<div class="detail_publicSetletDetails">
								<a href="/delphi/set/{$otherSets[set].set_id}">{$otherSets[set].set_name}</a><br/>
								<span class="smaller">Created by <a href="{$wwwroot}/modules/auth/profile.php?uid={$otherSets[set].owner_id}">{$otherSets[set].owner_name}</a></span>
							</div>
						</div>
						{/section}
						{if $moreSetsLink}
						<p class="smaller" id="detail_publicSetletAllLink"><a href="#">See all sets containing this object</a></p>
						{else}
						<br class="clear"/>
						{/if}
					{/if}
					<br class="clear"/>
					<h3>Add this object to one of your sets</h3>
					{section name=set loop=$personalSets}
					<div class="detail_personalSetlet smaller">
						<div class="detail_personalSetletThumb" id="set_thumb_{$personalSets[set].set_id}">
							{$personalSets[set].thumb}
						</div>
						<div class="detail_personalSetletDetails">
							{if $personalSets[set].contains_obj}
								{$personalSets[set].set_name}<br/>
								<a href="{$wwwroot}/modules/sets/api_addToSet.php?obj_id={$id}&amp;set_id={$personalSets[set].set_id}" id="addToSet{$personalSets[set].set_id}" class="ajaxLink" title="Remove object from this set">[remove]</a>
							{else}
								{$personalSets[set].set_name}<br/>
								<a href="{$wwwroot}/modules/sets/api_addToSet.php?obj_id={$id}&amp;set_id={$personalSets[set].set_id}" id="addToSet{$personalSets[set].set_id}" class="ajaxLink" title="Add object to this set">[add to set]</a>
								<img src="{$themeroot}/images/indicator_s.gif" style="display:none;" id="addToSet{$personalSets[set].set_id}Indicator" alt="Spinning indicator"/>
							{/if}
						</div>
					</div>
					{/section}
					<p class="smaller" id="detail_personalSetletLinks">
						<a href="{$wwwroot}/modules/sets/api_createNewSet.php?obj_id={$id}&amp;owner_id={$currentUser_id}" class="ajaxLink" id="" title="Create a new set with this object">Create a new set with this object</a><br/>
						<a href="/delphi/mysets/">Manage your sets</a>
					</p>
				</div>
	            <div id="detail_tagsTab">
					<p class="smaller">Tags are short labels that you can apply to museum objects.</p>
					<form action="{$wwwroot}/modules/tags/addTag.php" method="post" accept-charset="utf-8" id="tagAddForm">
						<div>
						<input type="text" name="tagInput" value="" id="tagAddForm_input" size="20" maxlength="45"/>
						<input type="submit" value="add"/> <img src="{$themeroot}/images/indicator_s.gif" style="display:none;" id="addTagFormIndicator" alt="Spinning indicator"/>
						<input type="hidden" name="obj_id" value="{$id}" id="tagAddForm_obj_id"/>
						</div>
					</form>
					<br/>
					<h3>Tags you associate with this object</h3>
					<div id="detail_tagList">			
						{if $objectTags}
							{section name=tag loop=$tags}
								<div class="detail_tag">{$tags[tag].tag_name} 
									<a href="{$wwwroot}/modules/tags/removeTag.php?tag_id={$tags[tag].tag_id}&amp;obj_id={$id}" class="ajaxLink" id="tag{$tags[tag].tag_id}" title="Remove this tag">[x]</a>
									<img src="{$themeroot}/images/indicator_s.gif" style="display:none;" id="tag{$tags[tag].tag_id}Indicator" alt="Spinning indicator"/>
								</div>
							{/section}
							<div id="detail_noTagsMessage" style="display:none;">
								You have no tags associated with this object.
							</div>
						{else}
							<div id="detail_noTagsMessage">
								You have no tags associated with this object.
							</div>
						{/if}
					</div>
			</div>
			</div>
		</div>
		<div id="detail_infoCol">
			<h1 class="name">{$name}</h1>
			<h3 id="desc_label">Description</h3>
			{if $description != ""}
				<p>{$description}</p>
			{else}
				<p>No description available for this item.</p>
			{/if}
			{if $showObjNum }
				<h3>Object Number</h3>
				<p>{$objnum}</p>
			{/if}
			<h2 id="cats_label">Categories</h2>
			{section name=facet loop=$facetinfo}
				<h3 class="facet_name">{$facetinfo[facet].facet}</h3>
				{$facetinfo[facet].items}
			{/section}
		</div>
		<br class="clear" />


{include file="footer.tpl"}
