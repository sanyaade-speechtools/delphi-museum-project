{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
	//var templateVarsJSON = JSON.parse({$templateVarsJSON});
	var templateVarsJSON = eval({$templateVarsJSON});
</script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.tabs.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/details.js"></script>
		<div id="detail_imageCol">
			<div id="detail_image">
				{if isset($zoom_path) }
		  	  <OBJECT CLASSID="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
					   CODEBASE="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0"
						 WIDTH="400" HEIGHT="400" ID="theMovie">
						<PARAM NAME="FlashVars" VALUE="zoomifyImagePath={$zoom_path}/&amp;zoomifyMaxZoom=125">
						<PARAM NAME="MENU" VALUE="FALSE">
						<PARAM NAME="SRC" VALUE="{$zoomer}">
						<EMBED FlashVars="zoomifyImagePath={$zoom_path}/&amp;zoomifyMaxZoom=125"
							SRC="{$zoomer}" MENU="false"
							PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"
							WIDTH="400" HEIGHT="400" NAME="theMovie"></EMBED>
					</OBJECT>
				{else}
					<code class="hidden">{$bad_zoom_path}</code>
					<code class="hidden">{$bad_img_path}</code>
					{if isset($img_html) }
						{$img_html}
					{else}
						<img src="{$img_path}" />
					{/if}
				{/if}
			</div>
			<div id="detail_thumbnails">

			</div>
			<div id="detail_tabBox">
	            <ul>
	                <li class="detail_setsTabLI"><a href="#detail_setsTab"><span>Sets</span></a></li>
	                <li class="detail_tagsTabLI"><a href="#detail_tagsTab"><span>Tags</span></a></li>
	            </ul>
	            <div id="detail_setsTab">
<!--
	TODO Create a new set
	TODO See all sets containing this object
	TODO link to owner of public set
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
								<span class="smaller">Created by <a href="#">{$otherSets[set].owner_name}</a></span>
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
						<div class="detail_personalSetletThumb">
							{$personalSets[set].thumb}
						</div>
						<div class="detail_personalSetletDetails">
							{if $personalSets[set].contains_obj}
								{$personalSets[set].set_name}<br/>
								<a href="{$wwwroot}/modules/sets/addToSet.php?oid={$id}&amp;set_id={$personalSets[set].set_id}" id="addToSet{$personalSets[set].set_id}" class="ajaxLink" title="Remove object from this set">[remove]</a>
							{else}
								{$personalSets[set].set_name}<br/>
								<a href="{$wwwroot}/modules/sets/addToSet.php?oid={$id}&amp;set_id={$personalSets[set].set_id}" id="addToSet{$personalSets[set].set_id}" class="ajaxLink" title="Add object to this set">[add to set]</a>
								<img src="{$themeroot}/images/indicator_s.gif" style="display:none;" id="addToSet{$personalSets[set].set_id}Indicator" alt="Spinning indicator"/>
							{/if}
						</div>
					</div>
					{/section}
					<p class="smaller" id="detail_personalSetletLinks"><a href="#">Create a new set with this object</a><br/><a href="/delphi/mysets/">Manage your sets</a></p>
				</div>
	            <div id="detail_tagsTab">
					<p class="smaller">Tags are short labels that you can apply to museum objects.</p>
					<form action="{$wwwroot}/modules/tags/addTag.php" method="post" accept-charset="utf-8" id="tagAddForm">
						<input type="text" name="tagInput" value="" id="tagAddForm_input" size="20" maxlength="45"/>
						<input type="submit" value="add"/> <img src="{$themeroot}/images/indicator_s.gif" style="display:none;" id="addTagFormIndicator" alt="Spinning indicator"/>
						<input type="hidden" name="obj_id" value="{$id}" id="tagAddForm_obj_id"/>
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
			<h1>{$name}</h1>
			<h3>Description</h3>
			{if $description != ""}
				<p>{$description}</p>
			{else}
				<p>No description available for this item.</p>
			{/if}
			<h1>Categories</h1>
			{$facetTree}
		</div>
		<br class="clear" />


{include file="footer.tpl"}
