<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox2.js"></script>

<div id="viewset_objectDetails" class="viewset_box">
	<div id="viewset_objectDetailsContent" class="viewset_boxContent">
		<a href="details_demo.php?id={$id}&img={$img}" title="View more details on this object"><img src="{$mids}/{$img}.jpg" width="350px" height="250px" style="border:1px solid #e96a03;"></a>
		<div style="position:relative;width=350px;">
			<a href="details_demo.php?id={$id}&img={$img}" style="position:absolute;left:0px;"title="View more details on this object">View Details</a>
			<a href="addToSet.php?height=270&width=400" class="thickbox2" style="position:absolute;right:18px;" title="Add this object to a set">Add to Set</a>
		</div>
		<br/>
		<p><b style="color:#000;">Object Name:</b><br/> {$name}</p>
		<p><b style="color:#000;">Description:</b><br/> {$description}</p>
	</div>
</div>