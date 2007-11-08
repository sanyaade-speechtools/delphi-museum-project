{include file="header.tpl"}

<h1>{$username}</h1>
<div class="profile_memberSince">Member since {$creation_time|date_format}</div>
<p>{if $ownProfile}<a href="{$wwwroot}/modules/auth/profileEdit.php">Edit my profile</a>{/if}</p>

{if $real_name}
<div class="profile_metadata">
	<span class="profile_metadataLabel">Name:</span> {$real_name}
</div>
{/if}
{if $website_url}
<div class="profile_metadata">
	<span class="profile_metadataLabel">Website:</span> <a href="{$website_url}">{$website_url}</a>
</div>
{/if}
{if $about}
<div class="profile_metadata">
	<span class="profile_metadataLabel">About:</span> {$about} 
</div>
{/if}
<br/>

<h2>Public Sets by {$username}</h2>
{if $sets}
	{section name=set loop=$sets}
		<div class="setlet_Large smaller">
			<div class="setletThumb_Large">
				{$sets[set].thumb}
			</div>
			<div class="setletDetails_Large">
				<a href="/delphi/set/{$sets[set].set_id}">{$sets[set].set_name}</a>
				<p><strong>{$sets[set].total_objects}</strong> objects</p>
			</div>
		</div>
	{/section}
{else}
	{$username} does not have any public sets.
{/if}

{include file="footer.tpl"}