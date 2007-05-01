{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.tooltip.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/themes/pahma/scripts/rollover.js"></script>

<div id="content">
	<div id="front_links">
		<p>Lorem ipsum dolor sit amet, proprius ut patria vel vulputate esse consequat gemino, indoles, blandit veniam ymo, gemino refoveo. Modo abigo transverbero sed quis reprobo dolore. Ille tation sed sudo, ludus pneum. Valde roto, epulae epulae iusto et commodo tamen genitus, quod nisl illum nutus. Commoveo, facilisis nimis volutpat vel ingenium capto roto olim neque iusto praesent letalis fere. Saepius, transverbero nutus ullamcorper hendrerit nisl damnum zelus antehabeo, hendrerit. Ullamcorper obruo wisi te facilisi mara blandit quis vulpes. Luptatum esca duis quia adsum delenit dolor obruo probo suscipere. Jugis capto metuo iustum lenis delenit hendrerit. Duis in ymo immitto scisco nimis, olim lobortis inhibeo sed ullamcorper meus nobis.</p>
		<h2><a href="{$wwwroot}/modules/facetBrowser/browser.php" style="text-decoration: underline;">Browse the Collections</a></h2>
	</div>

	<div id="front_search">
		<form action="{$wwwroot}/modules/facetBrowser/facetBrowse.php" method="get">
			<input type="text" name="kwds" maxlength="40">
			<input type="submit" value="Search">
		</form>
	</div>

	<div id="inner_content">
		<div id="front_featuredSets">
			<div>
				<img src="{$wwwroot}/themes/pahma/images/featuredSet1.jpg" width="608" height="209" alt="FeaturedSet1">
				<img src="{$wwwroot}/themes/pahma/images/featuredSet2.jpg" width="348" height="209" alt="FeaturedSet2">
				<img src="{$wwwroot}/themes/pahma/images/featuredSet3.jpg" width="348" height="209" alt="FeaturedSet3">
			</div>
		</div>

		<div id="front_examples">
			<h2>Featured Objects</h2>
			{section name=examples loop=$objects}
				{$objects[examples]}
			{/section}
		</div>
		<br class="clearbreak" />
	</div>
</div>

{include file="footer.tpl"}
