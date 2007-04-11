 function toggleVis2( element )
 {
 	var state = element.className;
 	if( state == "treeitem open" )
 		element.className = "treeitem closed";
 	else if( state == "treeitem closed" )
 		element.className = "treeitem open";
 	else
 		alert( "toggleVis called on element with unknown class" );
 }
 function toggleVis( element )
 {
 	var state = element.className;
 	if( state.indexOf( "open" ) >= 0 )
 	{
 		rExp = /open/;
 		element.className = state.replace( rExp, "closed" );
 	}
 	else if( state.indexOf( "closed" ) >= 0 )
 	{
 		rExp = /closed/;
 		element.className = state.replace( rExp, "open" );
 	}
 	else
 		alert( "toggleVis called on element with unknown class" );
 }
 function filter( id ) {
	alert( "filter to category: " + id );
 	event.returnValue = false;
 	return false;
 }

