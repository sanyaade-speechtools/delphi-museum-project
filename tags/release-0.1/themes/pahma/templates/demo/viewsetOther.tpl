{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/interface_1.2/interface.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.inplace.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<script type="text/javascript" src="{$themeroot}/scripts/viewset.js"></script>


<div id="content">

	<span class="viewset_setName" id="setName___1">{$setName}</span> 
	<div id="viewset_createdBy">Created by <a href="#">Theresa Conant</a></div>
	<a href="shareSet.php?height=340&width=400" class="thickbox viewset_shareLink" title="Share this set">Share this set</a>

	<div id="inner_content">
	<div id="viewset_leftCol">
		<br/>
		{include file="viewsetDetails.tpl"}
		<div id="viewset_objectDescription" class="viewset_box">
			<div class="viewset_boxHeader">
				<span class="viewset_setName">Set Description</span>
			</div>
			<div class="viewset_boxContent">
				<p id="setDescription___1" class="">{$setDescription}</p>
			</div>
		</div>
	</div>

	<div id="viewset_rightCol">
		<div id="viewset_thumbnails" class="viewset_box">
			<div class="viewset_boxHeader">
				<br/>
			</div>
			<div class="viewset_boxContent">
				<div id="viewset_sortableThumbs">

					<div class="viewset_thumbnail" id="thumb_4">
						<img src="{$thumbs_square}/0002.jpg" onclick="loadObjDetails(4,'0002'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_123">
						<img src="{$thumbs_square}/0007.jpg" onclick="loadObjDetails(123,'0007'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>

					<div class="viewset_thumbnail" id="thumb_346">
						<img src="{$thumbs_square}/0009.jpg" onclick="loadObjDetails(346,'0009'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>

					<div class="viewset_thumbnail" id="thumb_46"><img src="{$thumbs_square}/0011.jpg" onclick="loadObjDetails(46,'0011'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_113"><img src="{$thumbs_square}/0012.jpg" onclick="loadObjDetails(113,'0012'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_13514"><img src="{$thumbs_square}/0013.jpg" onclick="loadObjDetails(13514,'0013'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_11824"><img src="{$thumbs_square}/0014.jpg" onclick="loadObjDetails(11824,'0014'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_11534"><img src="{$thumbs_square}/0015.jpg" onclick="loadObjDetails(11534,'0015'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_17444"><img src="{$thumbs_square}/0016.jpg" onclick="loadObjDetails(17444,'0016'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_12"><img src="{$thumbs_square}/0017.jpg" onclick="loadObjDetails(12,'0017'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_186"><img src="{$thumbs_square}/0018.jpg" onclick="loadObjDetails(186,'0018'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_177"><img src="{$thumbs_square}/0019.jpg" onclick="loadObjDetails(177,'0019'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_3818"><img src="{$thumbs_square}/0020.jpg" onclick="loadObjDetails(3818,'0020'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_68519"><img src="{$thumbs_square}/0021.jpg" onclick="loadObjDetails(68519,'0021'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_1520"><img src="{$thumbs_square}/0022.jpg" onclick="loadObjDetails(1520,'0022'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>

					<div class="viewset_thumbnail" id="thumb_422"><img src="{$thumbs_square}/0024.jpg" onclick="loadObjDetails(422,'0024'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
					<div class="viewset_thumbnail" id="thumb_8235"><img src="{$thumbs_square}/0025.jpg" onclick="loadObjDetails(8235,'0025'); return false;" />
						<div class="addButton">
							<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set"><img src="{$themeroot}/images/addButton.gif"/></a>
						</div>
					</div>
				</div>
				
			</div>
		</div>
	</div>
	<br style="clear:both;" />
	</div>
</div>

{include file="footer.tpl"}