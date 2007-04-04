{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/interface_1.2/interface.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.inplace.js"></script>
<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>


<div id="content">
	<div id="viewset_leftCol">
		<h2 id="setName___1" class="editSetName">Set name!</h2>
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
				<h3>Objects (59)</h3>
			</div>
			<div class="viewset_boxContent">
				<div id="viewset_sortableThumbs">

					<img src="{$thumbs_square}/0002.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0007.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0008.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0009.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />

					<img src="{$thumbs_square}/0010.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0011.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0012.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0013.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0014.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0015.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0016.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0017.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0018.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />

					<img src="{$thumbs_square}/0019.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0020.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0021.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0022.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0023.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0024.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
					<img src="{$thumbs_square}/0025.jpg" class="viewset_sortableitem" onclick="loadObjDetails(4); return false;" />
				</div>

				
			</div>
		</div>
	</div>
	<br style="clear:both;" />
</div>

{include file="footer.tpl"}