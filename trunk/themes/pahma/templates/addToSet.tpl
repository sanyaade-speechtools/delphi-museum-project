<script type="text/javascript" src="{$themeroot}/scripts/addToSet.js"></script>

<img src="{$mids}/0006.jpg" width="200px" height="200px" style="float:left;margin-right:25px;">
<div id="chooseSet">
	<form action="#" method="get" accept-charset="utf-8">
	<h3>Choose a set</h3>
	<ul>
		<li><a href="#" class="setLink" >Theresa's first list</a></li>
		<li><a href="#" class="setLink">Craft Inspiration</a></li>
	</ul>
	or create a <a href="#" class="createNewSet">new set</a><br/>
	
		<p><input class="closer" type="button" value="Cancel"></p>
	</form>
</div>

<div id="createNewSet" style="display:none;">
	<form action="#" method="get" accept-charset="utf-8">
	<h3>New set name:</h3>
	<input type="text" name="newSetName" value="" id="newSetName">
	<input type="button" class="setLink" name="newSetSubmit" value="Add to new set" id="newSetSubmit">
	<p>or go back to your <a href="#" class="backToExistingSets">existing sets</a>.</p>
	</form>
</div>

<div id="addingToSet" style="display:none;">
	
	<h3>Adding to set...</h3>
	<img src="{$themeroot}/images/loadingAnimation.gif" width="100" height="100" alt="LoadingAnimation">
	
</div>