{include file="header.tpl"}
	<script type="text/javascript" charset="utf-8">
	//<![CDATA[
		var templateVarsJSON = eval({$templateVarsJSON});
	//]]>
	</script>
{if $owner_id == $currentUser_id}
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.form.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/jquery.dimensions.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.mouse.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.draggable.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.droppable.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.ui-1.0/ui.sortable.js"></script>
	<script type="text/javascript" src="{$wwwroot}/libs/json.js"></script>
{/if}
	<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.flash.js"></script>
	<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>
	<div id="viewset_leftCol">
		<div id="viewset_setDetailsDisplay">
			<h1 class="viewset_setTitle">{$setName}</h1>
			<div id="viewset_createdBy" class="smaller">Created by <a href="{$wwwroot}/modules/auth/profile.php?uid={$owner_id}">{$username}</a><br/>This set is <span id="viewset_policy">{$policy}</span>.</div>

			{if $setDescription}
				<div class="viewset_setDescription">{$setDescription}</div>
			{else}
				<div class="viewset_setDescription"></div>
			{/if}
			<div class="smaller">
				
			</div>		
		</div>
		{if $owner_id == $currentUser_id}
			<div id="viewset_setDetailsEdit" style="display:none;">
				<form method="post" accept-charset="utf-8" id="viewset_setDetailsEditForm">
					<h3>Title</h3>
					<h1 class="viewset_setTitle"><input type="text" name="setTitle" id="viewset_setTitleInput" value="" class="viewset_titleInput"/></h1>
					
					<h3>Description</h3>
					<div class="viewset_setDescription">
						<textarea name="setDesc" id="viewset_setDescTextarea" class="viewset_descTextarea"></textarea>
					</div>
					<h3>Policy</h3>
					<p>
						<input type="radio" name="policy" value="private" {if $policy == "private"}checked="checked"{/if}/> Private <input type="radio" name="policy" value="public" {if $policy == "public"}checked="checked"{/if}/> Public
					</p>
					
					
					<input type="submit" name="submit" value="Save changes"/> or <a href="#" id="viewset_cancelSetEditLink">Cancel</a>
					<input type="hidden" name="set_id" value="{$setId}"/>
				</form>
			</div>
			<div id="viewset_setShare" style="display:none;">
				<form method="post" accept-charset="utf-8" id="viewset_setShareForm">
					<table cellspacing="2px" cellpadding="2px">
						<tr>
							<td><p style="text-align:right">To:&nbsp;</p></td>
							<td><input type="text" size="32" name="To" id="viewset_setShareToInput" value=""/></td>
						</tr>
						<tr>
							<td><p style="text-align:right">Subject:&nbsp;</p></td><td><input type="text" size="32" name="Subj" id="viewset_setShareSubjInput" value=""/></td>
						</tr>
						<tr>
							<td><p style="text-align:left">Your<br />message:</p></td>
							<td><textarea name="Msg" id="viewset_setShareMsgTextarea" rows="4" cols="30"></textarea><td>
						</tr>
						<tr><td colspan="2" height="5px"></td></tr>
						<tr>
							<td></td>
							<td><input type="submit" name="submit" value="Send Message"/>
							  or <a href="#" id="viewset_cancelSetShareLink">Cancel</a></td>
						</tr>
					</table>					
					<input type="hidden" name="set_id" value="{$setId}"/>
				</form>
			</div>
			<div id="viewset_setDetailLinks">
				<a href="#" id="viewset_editSetDetailsLink" class="smaller">Edit set details</a><br/>
				<a href="#" id="viewset_deleteSetLink" class="smaller">Delete this set</a><br/>
				<a href="#" id="viewset_shareSetLink" class="smaller">Share this set</a>
			</div>
			<p id="viewset_setShareResponse" style="display:none;font-style:italic;"></p>
		{/if}
		
		{if $setHasObjects}
			<div id="viewset_thumbnails">
				{section name=obj loop=$objects}
				<div class="viewset_objectThumb" id="objectThumb_{$objects[obj].id}">
					{$objects[obj].thumb}
				</div>
				{/section}
			</div>
		{/if}
		
	</div>
	
	
	<div id="viewset_rightCol">
		{if $setHasObjects}
		<img src="{$themeroot}/images/viewset_prev.png" id="viewset_prev" alt="View the previous item" title="View the previous item" class="viewset_prevIcon"/>
		<img src="{$themeroot}/images/viewset_prev_click.png" id="viewset_prev_click" alt="View the previous item" title="View the previous item" class="viewset_prevIcon" style="display:none;"/>

		<img src="{$themeroot}/images/viewset_next.png" id="viewset_next" alt="View the next item" title="View the next item" class="viewset_nextIcon"/>
		<img src="{$themeroot}/images/viewset_next_click.png" id="viewset_next_click" alt="View the next item" title="View the next item" class="viewset_nextIcon" style="display:none;"/>
		
		<div id="viewset_detailsBox">
			<div id="viewset_objectImage">Flash is required to see object images.</div>
			<div id="viewset_objectDetailsDisplay">
				<h2 id="viewset_objectName"></h2>
				<div id="viewset_objectDescription"></div>
			</div>
			{if $owner_id == $currentUser_id}
			<div id="viewset_objectDetailsEdit" style="display:none;">
				<form method="post" accept-charset="utf-8" id="viewset_objectDetailsEditForm">
					<h3>My name for object</h3>
					<h2 class="viewset_objectName">
						<input type="text" name="objectName" id="viewset_objectNameInput" class="viewset_titleInput" value=""/>
					</h2>
					
					<h3>My notes for object</h3>
					<div class="viewset_objectDescription">
						<textarea name="objectDesc" id="viewset_objectDescTextarea" class="viewset_descTextarea"></textarea>
					</div>
					<input type="submit" name="submit" value="Save changes"/> or <a href="#" id="viewset_cancelObjectEditLink">Cancel</a>
					<input type="hidden" name="set_id" value="{$setId}"/>
					<input type="hidden" name="obj_id" value="" id="viewset_objectIdFormField"/>
				</form>
			</div>
			{/if}
			<div id="viewset_objectDetailLinks">
				{if $owner_id == $currentUser_id}
					<a href="#" id="viewset_objectEditLink" class="smaller">Edit my name and notes for object</a><br/>
					<a href="#" id="viewset_objectDeleteLink" class="smaller">Remove object from this set</a><br/>
				{/if}
				<a href="#" id="viewset_objectDetailLink" class="smaller">View object details</a>
			</div>
		</div>
		{else}
			<div id="viewset_noThumbnails">There are no objects in this set</div>
		{/if}
	</div>
{include file="footer.tpl"}
