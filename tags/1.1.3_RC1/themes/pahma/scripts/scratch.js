$(document).ready(function(){
	$(".editSetName").editInPlace({
		url: "api_updateSetName.php",
		params: "ajax=yes"
	});
	$(".editSetDescription").editInPlace({
		url: "api_updateSetDescription.php",
		params: "ajax=yes",
		field_type: "textarea",
	    textarea_rows: "15",
	    textarea_cols: "35"
	});
});
