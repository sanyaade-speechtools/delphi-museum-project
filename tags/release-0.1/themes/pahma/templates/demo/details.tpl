{include file="header.tpl"}

<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery-1.1.2.pack.js"></script>
<script type="text/javascript" src="{$wwwroot}/libs/jquery/jquery.thickbox.js"></script>

<div id="content">
	<div id="viewset_nameDiv"><span class="viewset_setName" id="setName___1">{$name}</span></div>
	<a href="javascript:history.go(-1)" class="viewset_shareLink" title="Share this set" style="top:30px">Return to results</a>
	<div id="inner_content">
		
		<br/>

			<div ><img	src="{$mids}/{$img}.jpg" width="398px" height="298px"/></div>
			<div style="position:relative;width=350px;">
				<a href="addToSet.php?height=270&width=400" class="thickbox" style="position:absolute;left:330px;" title="Add this object to a set">Add to Set</a>
			</div>

		<div id="detail_description">
			<br/>
			<h3>This details page will have more information about the object as well as an interactive image that users can zoom in with. </h3>
			<p>{$description}</p>
		</div>
	</div>
</div>
{include file="footer.tpl"}