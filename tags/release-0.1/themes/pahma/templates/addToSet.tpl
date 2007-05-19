<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.form.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.validate.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/metadata.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/addToSet.js"></script>

<div style="float:left; margin-right:25px;">
	<img src="{$thumbs}/{$obj_img_path}">	
<h3 style="margin-left:15px;">{$obj_name}</h3>
</div>

<div id="chooseSet">
	<form id="addToSetForm" action="{$wwwroot}/modules/sets/addToSet.php" method="post" accept-charset="utf-8">
		<label for="setList">Choose a set:</label><br/>
		<select name="setList" id="setList">
			{section name=set loop=$sets}
				<option value="{$sets[set].id}">{$sets[set].name}</option>
			{/section}
		</select>

		<p>or</p>
		
		<!--
			TODO Add prompt text in the set name field that goes away on focus and returns on blur if nothing was entered.
		-->
		<label for="newSetName">Create a new set:</label><br/>
		<input type="text" name="newSetName" value="" id="newSetName">
		<br/><br/><br/>
		<input type="hidden" name="submitted" value="True" id="submitted">
		<input type="hidden" name="obj_id" value="{$obj_id}" id="obj_id">
		<a class="closer" href="#">Cancel</a> <input type="submit" value="Add">
	</form>
</div>

<div id="addingToSet" style="display:none;">
	
	<h3>Adding to set...</h3>
	<img src="{$themeroot}/images/loadingAnimation.gif" width="100" height="100" alt="LoadingAnimation">
	
</div>