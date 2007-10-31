<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox2.js"></script>

<div id="viewset_objectDetails" class="viewset_box">
	<div id="viewset_objectDetailsContent" class="viewset_boxContent">
			{if isset($zoom_path) }
	  	  <OBJECT CLASSID="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
				   CODEBASE="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0"
					 WIDTH="350" HEIGHT="350" ID="theMovie">
					<PARAM NAME="FlashVars" VALUE="zoomifyImagePath={$zoom_path}/&zoomifyMaxZoom=125&zoomifyNavWindow=0&zoomifySlider=0">
					<PARAM NAME="MENU" VALUE="FALSE">
					<PARAM NAME="SRC" VALUE="{$zoomer}">
					<EMBED FlashVars="zoomifyImagePath={$zoom_path}/&zoomifyMaxZoom=125&zoomifyNavWindow=0&zoomifySlider=0"
						SRC="{$zoomer}" MENU="false"
						PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"
						WIDTH="350" HEIGHT="350" NAME="theMovie"></EMBED>
				</OBJECT>
			{else}
				<code class="hidden">{$bad_zoom_path}</code>
				<code class="hidden">{$bad_img_path}</code>
				<img src="{$img_path}" />
			{/if}

		<!-- <div style="position:relative;width=350px;">
			<a href="{$wwwroot}/modules/browser/details.php?id={$detail_id}" style="position:absolute;left:0px;"title="View more details on this object">View Details</a>
			<a href="addToSet.php?height=270&width=400&oid={$detail_id}" class="thickbox2" style="position:absolute;right:18px;" title="Add this object to a set">Add to Set</a>
		</div>
		<br/>
		<p><b style="color:#000;">Object Name:</b><br/> {$detail_name}</p>
		<p><b style="color:#000;">Description:</b><br/> {$detail_description}</p> -->
	</div>
</div>