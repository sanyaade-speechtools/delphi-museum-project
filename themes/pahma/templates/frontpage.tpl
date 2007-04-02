{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.tooltip.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/themes/pahma/scripts/rollover.js"></script>

<div id="content">
	<div id="front_links"><h2><a href="./browser.html">Browse Collections</a></h2></div>

	<h2>Featured Sets</h2>
	<div id="front_featuredSets">
		<div>
			<h3><a href="#">Minotaur</a></h3>
			<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo.</p>
		</div>
		<div>
			<h3><a href="#">Coins</a></h3>
			<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo.</p>
		</div>
	</div>

	<h2>Some objects from the collection</h2>
	<div id="front_examples">
		{section name=examples loop=$objects}
		<div class="front_example">
			<h3 class="front_exampleName">{$objects[examples].name}</h3>
			<div class="front_exampleThumbnail">
				<img src="{$wwwroot}/objimages/{$objects[examples].thumb_path}"/>
			</div>
		</div>
		{/section}
	</div>
	<br style="clear:both;" />
</div>

{include file="footer.tpl"}