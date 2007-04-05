{include file="header.tpl"}
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<script type="text/javascript" src="{$themeroot}/scripts/results_demo.js"></script>

<div id="content">

	<div id="viewset_innerContent">
		<h2>Results (343)</h2>
		<div class="results_pagination">
			<ul>
				<li>First</li>
				<li>Previous</li>
				<li> &middot; </li>
				<li><strong>1</strong></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li><a href="#">6</a></li>
				<li>...</li>
				<li><a href="#">10</a></li>
				<li> &middot; </li>
				<li><a href="#">Next</a></li>
				<li><a href="#">Last</a></li>
			</ul>
		</div>

		<div id="results_resultsGrid">
			{section name=results loop=$objects}
			<div class="results_result">
				<div class="results_resultThumbnail">
					<a href="details_demo.php?id={$objects[results].id}&img=0003"><img src="{$thumbs}/4.jpg" /></a>
				</div>
				<h3 class="results_resultName">{$objects[results].name}</h3>
				
				<ul>
					<li><a href="details_demo.php?id={$objects[results].id}&img=0003">View Details</a></li>
					<li><a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set">Add to Set</a></li>
				</ul>
			</div>
			{/section}
		</div>

		<div class="results_pagination">
			<ul>
				<li>First</li>
				<li>Previous</li>
				<li> &middot; </li>
				<li><strong>1</strong></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li><a href="#">6</a></li>
				<li>...</li>
				<li><a href="#">10</a></li>
				<li> &middot; </li>
				<li><a href="#">Next</a></li>
				<li><a href="#">Last</a></li>
			</ul>
		</div>
	</div>
	<br style="clear:both;" />
</div>


{include file="footer.tpl"}