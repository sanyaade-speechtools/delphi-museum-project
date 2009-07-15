{include file="header.tpl"}
<script type="text/javascript" charset="utf-8">
//<![CDATA[
	var templateVarsJSON = eval({$templateVarsJSON});
//]]>
</script>

<div id="contentNarrow">
	<h1>{if $user_mode=='curr'}My{else}Community{/if} Tags</h1>
	<p>Tags allow {if $user_mode=='curr'}you{else}users {/if} to associate 
	   {if $user_mode=='curr'}your{else}their{/if} own vocabulary to objects,
		 or to document {if $user_mode=='curr'}your{else}their{/if} reactions to objects.</p>
	<p>Click on any tag below to see the objects that 
	   {if $user_mode=='curr'}you{else}the community{/if} have tagged with that term,
		 or <a href="{$wwwroot}/modules/tags/showTags.php?user={if $user_mode=='curr'}any{else}curr{/if}">
		 click here to view {if $user_mode!='curr'}My{else}the Community's{/if} tags</a>.</p>
		{* Tag font-sizes are set in steps on a scale of 0-10, so we define min font in pix and
			 a pix-per-step assuming 10 steps. This precludes fancy math, expensive in templates. *}
		{assign var='font_min' value='12'}
		{assign var='pix_per_step' value='2'}
		<div class="tag_cloud">
			{section name=tag loop=$tags}
				<span><a href="{$wwwroot}/results/?tag={$tags[tag].id}&user={$user_mode}"
				  style="font-size:{$font_min+$tags[tag].steps*$pix_per_step}px">
					{$tags[tag].name}{* ({$tags[tag].count}) *}</a> &nbsp; </span>
			{/section}
		</div>
</div>


{include file="footer.tpl"}
