<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox2.js"></script>

<div id="viewset_objectDetails" class="viewset_box">
	<div class="viewset_boxHeader">
		<a style="float:left;" href="details_demo.php?id={$id}&img={$img}" title="View more details on this object">View Details</a>
		<a href="addToSet.php?height=270&width=400" class="thickbox2" style="float:right;" title="Add this object to a set">Add to Set</a>
	</div>
	<div id="viewset_objectDetailsContent" class="viewset_boxContent">
		<img src="{$mids}/{$img}.jpg" width="200px" height="200px">
		<p>Object Name: {$name}</p>
		<p>Description: {$description}</p>
	</div>
</div>