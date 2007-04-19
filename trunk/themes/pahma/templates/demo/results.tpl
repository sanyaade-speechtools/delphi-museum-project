{include file="header.tpl"}
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<script type="text/javascript" src="{$themeroot}/scripts/results_demo.js"></script>

<div id="content">

	<div id="viewset_innerContent">
		<h2>Results (343) for "Baskets"</h2>
		<div class="results_pagination">
			<br/><br/>
			<ul>
				<li style="margin-right:10px;">First</li>
				<li style="margin-right:10px;">Previous</li>
				<li style="margin-right:10px;"> &middot; </li>
				<li><strong>1</strong></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li><a href="#">6</a></li>
				<li>...</li>
				<li style="margin-right:10px;"><a href="#">10</a></li>
				<li style="margin-right:10px;"> &middot; </li>
				<li style="margin-right:10px;"><a href="#">Next</a></li>
				<li><a href="#">Last</a></li>
			</ul>
		</div>

		<div id="results_resultsGrid">
			{section name=results loop=$objects}
			<div class="results_result" style="margin-right:40px;font-size:.9em;margin-bottom:20px;">
				<p class="results_resultName" style="margin-bottom:0px;"><b>{$objects[results].name}</b></p>
				<div >
					<a href="details_demo.php?id={$objects[results].id}&img=0003"><img src="{$thumbs}/4.jpg"/></a>
				</div>
								
			
					<a href="details_demo.php?id={$objects[results].id}&img=0003">View Details</a><br/>
					<a href="addToSet.php?height=270&width=400" class="thickbox" title="Add this object to a set">Add to Set</a>
				
			</div>
			{/section}
		</div>
		
		<div class="results_pagination">
			<br/><br/>
			<ul>
				<li style="margin-right:10px;">First</li>
				<li style="margin-right:10px;">Previous</li>
				<li style="margin-right:10px;"> &middot; </li>
				<li><strong>1</strong></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li><a href="#">6</a></li>
				<li>...</li>
				<li style="margin-right:10px;"><a href="#">10</a></li>
				<li style="margin-right:10px;"> &middot; </li>
				<li style="margin-right:10px;"><a href="#">Next</a></li>
				<li><a href="#">Last</a></li>
			</ul>
		</div>
	</div>
	<br style="clear:both;" />
</div>


{include file="footer.tpl"}