{include file="header.tpl"}
<div id="front_tongue">
	<h1>Welcome to Delphi</h1>
	<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint</p>
	<div class="front_tongueLink">
		<a href="{$wwwroot}/modules/facetBrowser/browser.php">Browse the Collection</a>
	</div>
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