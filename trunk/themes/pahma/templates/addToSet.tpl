<script type="text/javascript" src="{$themeroot}/scripts/addToSet.js"></script>
<div style="float:left; margin-right:25px;">
	<img src="{$mids}/0006.jpg" width="200px" height="200px">	
<h3 style="margin-left:15px;">Red Basket</h3>
</div>
<div id="chooseSet">
	<form action="#" method="get" accept-charset="utf-8">
	<h3>Choose a set</h3>
	<select name="setList">
		<option>Theresa's first list</option>
		<option>Craft Inspiration</option>
	</select>

	<blockquote>or</blockquote>
	<h3>Create a new set</h3>
	<input type="text" name="newSetName" value="" id="newSetName">
	<br/><br/><br/>
	<p><input class="closer" type="button" value="Cancel"> <input class="setLink" type="button" value="OK"></p>
	</form>
</div>

<div id="addingToSet" style="display:none;">
	
	<h3>Adding to set...</h3>
	<img src="{$themeroot}/images/loadingAnimation.gif" width="100" height="100" alt="LoadingAnimation">
	
</div>