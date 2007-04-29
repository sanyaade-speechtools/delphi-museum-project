{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/interface_1.2/interface.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.inplace.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>

<div id="content">

	<span class="viewset_setName{if $username == $currentUser_name} editSetName{/if}" id="setName___{$setId}">{$setName} {if $username == $currentUser_name}<span class="viewset_editLink">(<a href="#">edit</a>)</span>{/if}</span> 
	<div id="viewset_createdBy">Created by {$username}</div>
	<a href="shareSet.php?height=340&width=400&sid={$setId}" class="thickbox viewset_shareLink" title="Share this set">Share this set</a>

	<div id="inner_content">
	<div id="viewset_leftCol">
		<br/>
		{include file="viewsetDetails.tpl"}
	</div>

	<div id="viewset_rightCol">
		<div id="viewset_thumbnails" class="viewset_box">
			<br/>
			<div class="viewset_boxContent">
				<div id="viewset_sortableThumbs">
					{section name=setObj loop=$objects}
					<div class="viewset_thumbnail{if $smarty.section.setObj.first} viewset_thumbnailSelected{/if}" id="thumb_{$objects[setObj].id}">
						<img src="{$thumbs}/{$objects[setObj].img_path}" onclick="loadObjDetails({$objects[setObj].id}); return false;" width="70px" height="70px"/>
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					{/section}
				</div>
				<br style="clear:both;" />
				<div id="viewset_objectDescription" class="viewset_box">
					<div class="viewset_boxHeader">
						<span class="viewset_setName">Set Description</span>
					</div>
					<div class="viewset_boxContent">
						<p id="setDescription___{$setId}" class="{if $username == $currentUser_name}editSetDescription{/if}">{$setDescription} {if $username == $currentUser_name}<span class="viewset_editLink">(<a href="#">edit</a>)</span>{/if}</p>
					</div>
				</div>
			</div>
		</div>
	</div>
	<br style="clear:both;" />
	</div>
</div>

{include file="footer.tpl"}