{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/interface_1.2/interface.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.inplace.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>


<div id="content">
	<div id="viewset_nameDiv"><span class="editSetName viewset_setName" id="setName___1">MY FAVORITE BASKETS</span>Created by <a href="#">JohnDoe</a></div>
	<div id="viewset_innerContent">
	<div id="viewset_leftCol">
		{include file="viewsetDetails.tpl"}
		<div id="viewset_objectDescription" class="viewset_box">
			<div class="viewset_boxHeader">
				<h3>Set Description</h3>
			</div>
			<div class="viewset_boxContent">
				<p id="setDescription___1" class="editSetDescription">This is the set description</p>
			</div>
		</div>
	</div>

	<div id="viewset_rightCol">
		<div id="viewset_thumbnails" class="viewset_box">
			<div class="viewset_boxHeader">
				<a href="#" title="Add this object to a set">Share this set</a>
			</div>
			<div class="viewset_boxContent">
				<div id="viewset_sortableThumbs">

					<img id="thumb_4" src="{$thumbs_square}/0002.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(4,'0002'); return false;" />
					<img id="thumb_123" src="{$thumbs_square}/0007.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(123,'0007'); return false;" />

					<img id="thumb_346" src="{$thumbs_square}/0009.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(346,'0009'); return false;" />

					<img id="thumb_46" src="{$thumbs_square}/0011.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(46,'0011'); return false;" />
					<img id="thumb_113" src="{$thumbs_square}/0012.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(113,'0012'); return false;" />
					<img id="thumb_13514" src="{$thumbs_square}/0013.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(13514,'0013'); return false;" />
					<img id="thumb_11824" src="{$thumbs_square}/0014.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(11824,'0014'); return false;" />
					<img id="thumb_11534" src="{$thumbs_square}/0015.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(11534,'0015'); return false;" />
					<img id="thumb_17444" src="{$thumbs_square}/0016.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(17444,'0016'); return false;" />
					<img id="thumb_12" src="{$thumbs_square}/0017.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(12,'0017'); return false;" />
					<img id="thumb_186" src="{$thumbs_square}/0018.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(186,'0018'); return false;" />
					<img id="thumb_177" src="{$thumbs_square}/0019.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(177,'0019'); return false;" />
					<img id="thumb_3818" src="{$thumbs_square}/0020.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(3818,'0020'); return false;" />
					<img id="thumb_68519" src="{$thumbs_square}/0021.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(68519,'0021'); return false;" />
					<img id="thumb_1520" src="{$thumbs_square}/0022.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(1520,'0022'); return false;" />

					<img id="thumb_422" src="{$thumbs_square}/0024.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(422,'0024'); return false;" />
					<img id="thumb_8235" src="{$thumbs_square}/0025.jpg" class="viewset_sortableitem viewset_thumbnail" onclick="loadObjDetails(8235,'0025'); return false;" />
				</div>
				
			</div>
		</div>
	</div>
	<br style="clear:both;" />
	</div>
</div>

{include file="footer.tpl"}