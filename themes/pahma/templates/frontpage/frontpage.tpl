{include file="header.tpl"}
<div id="front_tongue">
	<h1>Welcome to the Collection!</h1>
	<p>You can explore by location, by how items were used, or even how they were made or decorated. You can save items you like to your own sets and then share these sets with others. To get started, type keywords in the "Search" box, or click on "Browse" to see the various categories to start from. Or you can explore some of the sets featured to the right or any of the objects below.</p>
	<!--
	<div class="front_tongueLink">
		<a href="{$wwwroot}/modules/facetBrowser/browser.php">Browse the Collection</a>
	</div>
	-->
</div>
<div id="front_featuredObjects">
	<h2>Featured Objects</h2>
	{section name=obj loop=$objects}
            {$objects[obj]}
    {/section}
</div>
<div id="front_greyCol">
	<h2>Featured Sets</h2>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
	<div class="front_featuredSetBox">
		<img src="{$themeroot}/images/featuredSetTestImage.jpg" alt="Featured set image" />
		<div class="front_featuredSetName">
			New Baskets from South America
		</div>
		<div class="front_featuredSetCreator">
			Created by <span>HearstMuseum</span>
		</div>
	</div>
</div>

{include file="footer.tpl"}