function loadObjDetails(id){
	$(".viewset_thumbnail").removeClass("viewset_thumbnailSelected")
	$("#thumb_" + id).addClass("viewset_thumbnailSelected");
	$("#viewset_objectDetails").load("api_loadDetails.php?id=" + id);
}

$(document).ready(
	function () {
		$(".editSetName").editInPlace({
			url: "api_updateSetName.php",
			params: "ajax=yes"
		});
		$(".editSetDescription").editInPlace({
			url: "api_updateSetDescription.php",
			params: "ajax=yes",
			field_type: "textarea",
		    textarea_rows: "5",
		    textarea_cols: "40"
		});
		$(".viewset_thumbnail").hover(function(){
			$(this).addClass("viewset_thumbnailHover");
		},function(){
			$(this).removeClass("viewset_thumbnailHover");
		});
		$(".addButton a").click(function(){
			$(this).load("api_addButtonPress.php");
		});		
	}
);
