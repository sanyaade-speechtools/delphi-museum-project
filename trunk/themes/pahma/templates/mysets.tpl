{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/interface_1.2/interface.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.inplace.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>


<div id="content">
	<div id="viewset_nameDiv"><span class="viewset_setName">My Sets</span></div>
	
	<div id="viewset_innerContent">
		
		<div class="viewset_mySetTeaser">
			<img src="{$thumbs_square}/0002.jpg"/>
			<h3><a href="viewset.php?sid=1">Theresa's first set</a> (30)</h3>
			<p>This is the description text for my first set. This set is created for you by default when you register.</p>
			<a href="viewset.php?sid=1">View Set</a> | <a href="shareSet.php?height=270&width=400" class="thickbox" title="">Share this set</a> 
		</div>
		<div class="viewset_mySetTeaser">
			<img src="{$thumbs_square}/0006.jpg"/>
			<h3><a href="viewset.php?sid=1">Craft Inspiration</a> (12)</h3>
			<p>This set is for keeping special things in. This is the description for the set.</p>
			<a href="viewset.php?sid=1">View Set</a> | <a href="shareSet.php?height=270&width=400" class="thickbox" title="">Share this set</a> 
		</div>
		<div class="viewset_mySetTeaser">
			<img src="{$thumbs_square}/0001.jpg"/>
			<h3><a href="viewset.php?sid=1">Favorites</a> (414)</h3>
			<p>This set is for keeping special things in. This is the description for the set.</p>
			<a href="viewset.php?sid=1">View Set</a> | <a href="shareSet.php?height=270&width=400" class="thickbox" title="">Share this set</a> 
		</div>
	</div>
</div>

{include file="footer.tpl"}