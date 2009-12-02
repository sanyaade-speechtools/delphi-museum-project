var nReqRows = 0;
var nExclRows = 0;

var REQ = true;
var EXCL = false;

function getRow_ID(isReq, iRow) {
	return (isReq?"Req":"Excl")+"Row"+iRow;
}

function getConcept_ID(isReq, iRow) {
	return (isReq?"Req":"Excl")+"Concept" + iRow;
}

function getConceptID_ID(isReq, iRow) {
	return (isReq?"Req":"Excl")+"ConceptID" + iRow;
}

function newReqRowHTML(rowID, concID, idID) {
	var htmlStr = '<tr class="ReqRow" id="' + rowID + '"><td width="10px"></td>' +
		'<td><p class="label" align="right">Concept:</p></td>' +
		'<td><input id="'+concID+'" class="ac_concept" type="text" maxlength="150" size="40" value="" />' +
		'<input id="'+idID+'" type="hidden" value="-1" /></td>' +
//		'<td><input type="image" src="'+_themeroot+'/images/choose.gif" onclick="ShowChooser(\''+concID+'\',\''+idID+'\');"/></td>' +
		'<td colspan="2"><input type="image" src="'+_themeroot+'/images/delete.gif" onclick="DeleteRow(\''+rowID+'\',\'Req\');"/></td>' +
		'<td width="10px"></td></tr>';
	return htmlStr;
}

function addReqRow() {
	var addAfter;
	if( $("tr.ReqRow").length <= 0 ) {
		nReqRows = 0;
		addAfter = $("#ReqModeRow");
	} else {
		addAfter = $("tr.ReqRow:last");
	}
	var rowID = getRow_ID(REQ,nReqRows);
	var concID = getConcept_ID(REQ,nReqRows);
	var idID = getConceptID_ID(REQ,nReqRows);
	addAfter.after( newReqRowHTML(rowID, concID, idID) );
	//alert("Setting autocomplete on: " + concID + " with URL:\n"+wwwroot+"/api/ac_concepts.php");
	$("#"+concID).autocomplete(wwwroot+"/api/ac_concepts.php", {
		minChars: 2,
		scroll: true,
		scrollHeight: 180,
		max: 40,
		matchSubset: false
	});
	$("#"+concID).result(function(event, data, formatted) {
		if (data)
			$("#"+idID).val(data[1]);
	});

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

function newExclRowHTML(rowID, concID, idID) {
	var htmlStr = '<tr class="ExclRow" id="' + rowID + '"><td width="10px"></td>' +
		'<td><p class="label" align="right">Concept:</p></td>' +
		'<td><input id="'+concID+'" class="ac_concept" type="text" maxlength="150" size="40" value="" />' +
		'<input id="'+idID+'" type="hidden" value="-1" /></td>' +
//		'<td><input type="image" src="'+_themeroot+'/images/choose.gif" onclick="ShowChooser(\''+concID+'\',\''+idID+'\');"/></td>' +
		'<td colspan="2"><input type="image" src="'+_themeroot+'/images/delete.gif" onclick="DeleteRow(\''+rowID+'\',\'Excl\');"/></td>' +
		'<td width="10px"></td></tr>';
	return htmlStr;
}

function addExclRow() {
	if( $("tr.ExclRow").length <= 0 ) {
		nExclRows = 0;
		addAfter = $("#ExclModeRow");
	} else {
		addAfter = $("tr.ExclRow:last");
	}
	var rowID = getRow_ID(EXCL,nExclRows);
	var concID = getConcept_ID(EXCL,nExclRows);
	var idID = getConceptID_ID(EXCL,nExclRows);
	addAfter.after( newExclRowHTML(rowID, concID, idID) );
	$("#"+concID).autocomplete(wwwroot+"/api/ac_concepts.php", {
		minChars: 2,
		scroll: true,
		scrollHeight: 180,
		max: 40,
		matchSubset: false
	});
	$("#"+concID).result(function(event, data, formatted) {
		if (data)
			$("#"+idID).val(data[1]);
	});

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
				if((( "" + refidstr ) != "" ) && ((0+refidstr)>=1))  {
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
				if((( "" + refidstr ) != "" ) && ((0+refidstr)>=1))  {
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
