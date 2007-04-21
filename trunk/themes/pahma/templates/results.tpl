{include file="header.tpl"}

<script type="text/javascript" src="{$themeroot}/scripts/treeview.js"></script>

<code style="display:none;">CountsByCat Query: {$catsByCountQ}</code>
<code style="display:none;">Full Query: {$fullQ}</code>
<code style="display:none;">Full Count Query: {$tqFullCount}</code>

<div id="content">
	<h2 id="results_heading">{$iFirstResult} - {$iLastResult} of {$numResultsTotal} Results {$qual}</h2>

	<div id="inner_content">
		{$query}
		<div id="results_facetTree">
			{$facetTree}
		</div>

		<div id="results_results">
			<div class="results_pagination">
				<ul>
					<li><a href="{$baseQ}&page=0">First</a></li>
					{if $pageNum gt 1}<li><a href="{$baseQ}&page={$pageNum-2}">Previous</a></li>{/if}
					<li> &middot; </li>
					{foreach from=$pageNums item=pn}
						<li>{if $pageNum eq $pn}{$pn}{else}<a href="{$baseQ}&page={$pn-1}">{$pn}</a>{/if}</li>
					{/foreach}
					<li> &middot; </li>
					<li>{if $pageNum lt $numPagesTotal}<a href="{$baseQ}&page={$pageNum}">Next</a>{/if}</li>
					<li><a href="{$baseQ}&page={$numPagesTotal-1}">Last</a></li>
				</ul>
			</div>

			<div id="results_resultsGrid">
				{$imageOutput}
				
				<!--<div class="results_result">
					<h3 class="results_resultName">Name</h3>
					<div class="results_resultThumbnail">
						image
					</div>
					<ul>
						<li>Favorite</li>
						<li>Add to Set</li>
					</ul>
				</div>-->
				
			</div>

			<div class="results_pagination">
				<ul>
					<li><a href="{$baseQ}&page=0">First</a></li>
					{if $pageNum gt 1}<li><a href="{$baseQ}&page={$pageNum-2}">Previous</a></li>{/if}
					<li> &middot; </li>
					{foreach from=$pageNums item=pn}
						<li>{if $pageNum eq $pn}{$pn}{else}<a href="{$baseQ}&page={$pn-1}">{$pn}</a>{/if}</li>
					{/foreach}
					<li> &middot; </li>
					<li>{if $pageNum lt $numPagesTotal}<a href="{$baseQ}&page={$pageNum}">Next</a>{/if}</li>
					<li><a href="{$baseQ}&page={$numPagesTotal-1}">Last</a></li>
				</ul>
			</div>
		</div>
		<br class="clearbreak" />
	</div>
</div>


{include file="footer.tpl"}
