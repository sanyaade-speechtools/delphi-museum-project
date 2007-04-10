{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.tabs.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/themes/pahma/scripts/tabs.js"></script>

<div id="content">
	<h2 id="detail_name">{$name}</h2>

	<div id="inner_content">
		<div id="detail_imageColumn">
			<div id="detail_image">
				<img src="{$mids}/{$img_path}" />
			</div>
			<div id="detail_thumbnails">
				<div id="detail_thumbnailSelectedContainer">
					<div id="detail_thumbnailSelected">
						<img src="{$thumbs}/{$img_path}" />
					</div>
				</div>
				<div class="detail_thumbnail">view</div>
				<div class="detail_thumbnail">view</div>
				<div class="detail_thumbnail">view</div>
				<div class="detail_thumbnail">view</div>
				<div class="detail_thumbnail">view</div>
			</div>
		</div>


		<div id="detail_information">
			<ul class="tabs-nav">
				<li class="tabs-selected"><a href="#">Description</a></li>
				<li class=""><a href="#">Details</a></li>
			</ul>

			<div style="display: block;" class="tabs-container">
				<!--<h3>Description</h3>-->
				<p>{$description}</p>
			</div>

			<div style="" class="tabs-container tabs-hide">
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
		
					<h2>...</h2>
		
					<h2>facet n</h2>
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
		</div>
		<br class="clearbreak" />
	</div>
</div>

{include file="footer.tpl"}
