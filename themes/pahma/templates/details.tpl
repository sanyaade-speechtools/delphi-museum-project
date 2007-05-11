{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>
<br/><br/>
		<!--
		<div id="detail_innerContentTop">
			<a href="#" onClick="history.go(-1)">&lt;&lt; Back to Results</a> 
		</div>
		-->
		<div id="detail_imageColumn">
			<div id="detail_image">
				{if isset($zoom_path) }
		  	  <OBJECT CLASSID="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
					   CODEBASE="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0"
						 WIDTH="400" HEIGHT="400" ID="theMovie">
						<PARAM NAME="FlashVars" VALUE="zoomifyImagePath={$zoom_path}/&zoomifyMaxZoom=125">
						<PARAM NAME="MENU" VALUE="FALSE">
						<PARAM NAME="SRC" VALUE="{$wwwroot}/libs/zoom/zoomifyDynamicViewer.swf">
						<EMBED FlashVars="zoomifyImagePath={$zoom_path}/&zoomifyMaxZoom=125"
							SRC="{$wwwroot}/libs/zoom/zoomifyDynamicViewer.swf" MENU="false"
							PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"
							WIDTH="300" HEIGHT="300" NAME="theMovie"></EMBED>
					</OBJECT>
				{else}
					<code class="hidden">{$bad_zoom_path}</code>
					<code class="hidden">{$bad_img_path}</code>
					<img src="{$img_path}" />
				{/if}
				<div style="position:relative;width=350px;">
					<a href="{$wwwroot}/modules/sets/addToSet.php?height=270&width=400&oid={$id}" class="thickbox" style="position:absolute;left:300px;" title="Add this object to a set">Add to Set</a>
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
			<h1>{$name}</h1>
			{if $description != ""}
				<p>{$description}</p>
			{else}
				<p>No description available for this item.</p>
			{/if}
			<h1>Categories</h1>
			{$facetTree}
		</div>
		<br class="clearbreak" />


{include file="footer.tpl"}