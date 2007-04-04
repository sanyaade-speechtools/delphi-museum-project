{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.tabs.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/themes/pahma/scripts/tabs.js"></script>

<div id="content">
	<h2 id="detail_name">{$name}</h2>
	<div id="detail_imageColumn">
		<div id="detail_image"><img	src="{$mids}/{$img_path}" width="398px" height="298px"/></div>
		<div id="detail_thumbnails">
			<div id="detail_thumbnailSelectedContainer">
				<div id="detail_thumbnailSelected"><img	src="{$thumbs}/{$img_path}" width="115px" height="85px"/></div>
			</div>
			<div class="detail_thumbnail">alt view 1</div>
			<div class="detail_thumbnail">alt view 2</div>
			<div class="detail_thumbnail">alt view 3</div>
			<div class="detail_thumbnail">alt view 4</div>
			<div class="detail_thumbnail">alt view 5</div>
		</div>
	</div>

	<div id="detail_information">
		<ul class="tabs-nav">
			<li class="tabs-selected"><a href="#tab-1">Info</a></li>
			<li class=""><a href="#tab-2">More ...</a></li>
		</ul>

		<div style="display: block;" class="tabs-container" id="tab-1">
			<p>First tab is active by default:</p>
			<h3>Description</h3>
			<p>{$description}</p>
			<h3>info type 2</h3>
			<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo.</p>
			<h3>info type 3</h3>
			<ul>
				<li>point 1</li>
				<li>point 2</li>
				<li>point 3</li>
			</ul>
		</div>

		<div style="" class="tabs-container tabs-hide" id="tab-2">
			<h3>info type 1</h3>
			<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo.</p>
			<h3>info type 2</h3>
			<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo.</p>
		</div>
	</div>

	<div id="detail_facetPath">
		<h2>facet 1</h2>
		<ul>
			<li>root</li>
			<ul>
				<li>one-deep</li>
				<ul>
					<li>two-deep</li>
				</ul>
			</ul>
		</ul>

		<h2>facet 2</h2>
		<ul>
			<li>root</li>
			<ul>
				<li>one-deep</li>
				<ul>
					<li>two-deep</li>
				</ul>
			</ul>
		</ul>

		<h2>facet 3</h2>
		<ul>
			<li>root</li>
			<ul>
				<li>one-deep</li>
				<ul>
					<li>two-deep</li>
				</ul>
			</ul>
		</ul>
	</div>
</div>
{include file="footer.tpl"}