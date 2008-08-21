$(document).ready(function() {
	$("label.overlabel").overlabel();
	
	// Add Search box override to check for objnums
	$("#navBarSearchBoxForm").submit(function(){
		// See if it looks like an obj num
		kwdVal = $('#navSearchBoxInput').val();
		objNumRegex = new RegExp("^\\d{1,2}-\\d*");
		if (objNumRegex.test(kwdVal)) {
			// call object details page with alternate param to get details by objNum
			newURL = templateVarsJSON['wwwroot'] + "/modules/browser/details.php?onum=" + kwdVal;
			window.location = newURL;
			// Return false so normal page handling does not kick in
			return false;
		} else {
			// Let the normal page handling kick in
			return true;
		}
	});
	
});
