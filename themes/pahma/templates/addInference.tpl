{include file="header.tpl"}

{if isset($addinf_error) }
	<h2>{$addinf_error}</h2>
{else}
	<h2>Add a new Inference Rule:</h2>
	<div id="formCont">
	<p class="label">Name: <input id="NameText" type="text" maxlength="150" size="35" value="NewRule" />&nbsp;
	Confidence: 		  
	<select id="ConfSel" SelectedIndex="0">
		<option value="0.1" >10%</option>
		<option value="0.2" >20%</option>
		<option value="0.3" >30%</option>
		<option value="0.4" >40%</option>
		<option value="0.5" >50%</option>
		<option value="0.6" >60%</option>
		<option value="0.7" >70%</option>
		<option value="0.8" >80%</option>
		<option value="0.9" selected="true" >90%</option>
		<option value="1.0" >100%</option>
	</select>

	&nbsp;<br />&nbsp;
	</p>

	<table border="0" id="table1" cellspacing="4px">
		<tr><td colspan="6" height="10px"></td></tr>
		<tr>
			<td width="10px"></td>
			<td colspan="4"><p class="intro" align="left">
			Make Delphi infer that an object has</p>
			</td>
			<td width="10px"></td>
		</tr>
		<tr>
			<td width="10px"></td>
			<td><p class="label" align="right">Concept:</p></td>
			<td>
				<input id="InfConcept" type="text" maxlength="150" size="40" value="" />
				<input id="InfConceptID" type="hidden" value="-1" />
			</td>
			<!-- <td><input type="image" src="{$themeroot}/images/choose.gif" onclick="ShowChooser('InfConcept','InfConceptID');"/></td> -->
			<td colspan="2"></td>
			<td width="10px"></td>
		</tr>
		<tr><td colspan="6" height="15px"></td>
		<tr id="ReqModeRow">
			<td width="10px"></td>
			<td><p class="intro" align="right">When it has:</p></td>
			<td colspan="3"><p class="intro up">
				<select id="InfReqSel" SelectedIndex="0">
					<option value="0" selected="true" >All</option>
					<option value="1" >Any</option>
				</select>&nbsp; of the following concepts:</p>
			</td>
			<td width="10px"></td>
		</tr>
		<tr>
			<td width="10px"></td>
			<td colspan="4"><p align="right">
				<input id="MoreReqBtn" type="button" value="(Add more)" style="font-size: 8pt" onclick="addReqRow();"/>
			</td>
			<td width="10px"></td>
		</tr>
		<!-- <tr><td colspan="6" height="5px"></td> -->
		<tr id="ExclModeRow">
			<td width="10px"></td>
			<td><p class="intro" align="right">Unless it has:</p></td>
			<td colspan="3"><p class="intro up">
				<select id="InfExclSel" SelectedIndex="1">
					<option value="0" >All</option>
					<option value="1" selected="true">Any</option>
				</select>&nbsp; of the following concepts:</p>
			</td>
			<td width="10px"></td>
		</tr>
		<tr>
			<td width="10px"></td>
			<td colspan="4"><p align="right">
				<input id="MoreExclBtn" type="button" value="(Add more)" style="font-size: 8pt" onclick="addExclRow();"/>
			</td>
			<td width="10px"></td>
		</tr>
		<tr><td colspan="6"><hr></td>
		<tr>
			<td width="10px"></td>
			<td><p class="label" align="right">Notes:</p></td>
			<td colspan="3">
				<textarea rows="2" id="InfNotes" cols="40"></textarea>
			</td>
			<td width="10px"></td>
		</tr>
		<tr><td colspan="6"><hr></td>
		</tr>
		<tr><td colspan="6">
		<p align="center">	<input id="CommitBtn" type="button" value="     Add this rule     " 
			style="font-size:10pt;font-weight:bold" onclick="prepareAddInfXML();" /></p>
		</td></tr>
		<tr><td colspan="6" height="10px"></td></tr>
	</table>

	</div>
	{if isset($opmsg) }
		<p>{$opmsg}</p>
	{/if}
{/if}
{include file="footer.tpl"}
