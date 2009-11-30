$(document).ready(function(){
  //alert("Hi there");
  addReqRow();
  addReqRow();
  addExclRow();
  addExclRow();
	$("#InfConcept")[0].focus();
});

var nReqRows = 0;
var nExclRows = 0;

function newReqRowHTML( iRow ) {
	var id0 = "ReqRow" + iRow;
	var id2 = "ReqConcept" + iRow;
	var id3 = "ReqConceptID" + iRow;
	var htmlStr = '<tr class="ReqRow" id="' + id0 + '"><td width="10px"></td>' +
		'<td><p class="label" align="right">Concept:</p></td>' +
		'<td><input id="'+id2+'" type="text" maxlength="150" size="40" value="" />' +
		'<input id="'+id3+'" type="hidden" value="'+(10001*(iRow+1))+'" /></td>' +
		'<td><input type="image" src="'+_themeroot+'/images/choose.gif" onclick="ShowChooser(\''+id2+'\',\''+id3+'\');"/></td>' +
		'<td><input type="image" src="'+_themeroot+'/images/delete.gif" onclick="DeleteRow(\''+id0+'\',\'Req\');"/></td>' +
		'<td width="10px"></td></tr>';
	return htmlStr;
}

function addReqRow() {
	if( $("tr.ReqRow").length <= 0 ) {
		$("#ReqModeRow").after( newReqRowHTML(0) );
	} else {
		$("tr.ReqRow:last").after( newReqRowHTML(nReqRows) );
	}
	nReqRows += 1;
}

function DeleteRow(rowID, rowtype) {
	var sel = "#"+rowID;
	$(sel).remove();
	if( rowtype == 'Req') {
		if( $("tr.ReqRow").length == 0 ) {
			nReqRows = 0;
			addReqRow();
		}
	} else if( $("tr.ExclRow").length == 0 ) {
			nExclRows = 0;
			addExclRow();
	}
}

function newExclRowHTML( iRow ) {
	var id0 = "ExclRow" + iRow;
	var id2 = "ExclConcept" + iRow;
	var id3 = "ExclConceptID" + iRow;
	var htmlStr = '<tr class="ExclRow" id="' + id0 + '"><td width="10px"></td>' +
		'<td><p class="label" align="right">Concept:</p></td>' +
		'<td><input id="'+id2+'" type="text" maxlength="150" size="40" value="" />' +
		//'<input id="'+id3+'" type="hidden" value="'+(10100*(iRow+1))+'" /></td>' +
		'<input id="'+id3+'" type="hidden" value="" /></td>' +
		'<td><input type="image" src="'+_themeroot+'/images/choose.gif" onclick="ShowChooser(\''+id2+'\',\''+id3+'\');"/></td>' +
		'<td><input type="image" src="'+_themeroot+'/images/delete.gif" onclick="DeleteRow(\''+id0+'\',\'Excl\');"/></td>' +
		'<td width="10px"></td></tr>';
	return htmlStr;
}

function addExclRow() {
	if( $("tr.ExclRow").length <= 0 ) {
		$("#ExclModeRow").after( newExclRowHTML(0) );
	} else {
		$("tr.ExclRow:last").after( newExclRowHTML(nExclRows) );
	}
	nExclRows += 1;
}

function ShowChooser( conceptText, conceptID ) {
	var str = "Browse for concepts...";
	var concept = "" + $("#"+conceptText)[0].value;
	var id = "" + $("#"+conceptID)[0].value;
	if( concept != "" )
		str += "\n  Specified concept: \"" + concept + "\""; 
	if( id != "" )
		str += "\n  Specified ID: \"" + id + "\""; 
	alert( str );
}

function prepareAddInfXML() {
		var xmlStr = '<infer name="' + $("#NameText")[0].value + '"' +
		' confidence="'+$("#ConfSel")[0].options[$("#ConfSel")[0].selectedIndex].value +'"';
		var refidstr = $("#InfConceptID")[0].value;
		if(( "" + refidstr ) == "" ) {
			alert( "Invalid Rule: No inferred concept specified!" );
			return null;
		}
			xmlStr += ' idref="'+ refidstr + '">';
			xmlStr += "\n  <notes>" + $("#InfNotes")[0].value + "</notes>";
			xmlStr += '\n  <require mode="'+$("#InfReqSel")[0].options[$("#InfReqSel")[0].selectedIndex].text +'">';
			
			var nReqRows = $("tr.ReqRow").length;
			var nReqFound = 0;
			for( var i=0; i < nReqRows; i++ ) {
				var idval = "#ReqConceptID" + i;
				refidstr = $(idval)[0].value;
				if(( "" + refidstr ) != "" )  {
					xmlStr += '\n    <concept idref="'+ refidstr + '" />';
					nReqFound++;
				}
			}
			if( nReqFound == 0 ) {
				alert( "Invalid Rule: No required concepts specified!" );
				return null;
			}
			xmlStr += '\n  </require>';
	
			var nExclRows = $("tr.ExclRow").length;
			var nExclFound = 0;
			for( i=0; i < nExclRows; i++ ) {
				var idval = "#ExclConceptID" + i;
				refidstr = $(idval)[0].value;
				if(( "" + refidstr ) != "" ) {
					if( nExclFound == 0 )
						xmlStr += '\n  <exclude mode="'+$("#InfExclSel")[0].options[$("#InfExclSel")[0].selectedIndex].text +'">';
					xmlStr += '\n    <concept idref="'+ refidstr + '" />';
					nExclFound++;
				}
			}
			if( nExclFound > 0 )
				xmlStr += '\n  </exclude>';
			xmlStr += '\n</infer>';
		alert( "XML for new Rule:\n\n" + xmlStr );
		return xmlStr;
}

