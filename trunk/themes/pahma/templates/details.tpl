{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<div id="content">
	<h2 id="detail_name">{$name}</h2>

	<div id="inner_content">
		<div id="detail_innerContentTop">
			<a href="#" onClick="history.go(-1)">&lt;&lt; Back to Results</a> 
		</div>
		<div id="detail_imageColumn">
			<div id="detail_image">
				<img src="{$mids}/{$img_path}" />
				<div style="position:relative;width=350px;">
					<a href="{$wwwroot}/modules/sets/addToSet.php?height=270&width=400&oid={$id}" class="thickbox" style="position:absolute;left:330px;" title="Add this object to a set">Add to Set</a>
				</div>
			</div>
			<div id="detail_thumbnails">
				<!--
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
				-->
			</div>
		</div>
		<div id="detail_information">
			<h3>Description</h3>
			{if $description != ""}
				<p>{$description}</p>
			{else}
				<p>None available</p>
			{/if}
			<h3>Categories</h3>
			{$facetTree}
		</div>
		<br class="clearbreak" />
	</div>
</div>

{include file="footer.tpl"}
