function loadObjDetails(id,img){
	$(".viewset_thumbnail").removeClass("viewset_thumbnailSelected")
	$("#thumb_" + id).addClass("viewset_thumbnailSelected");
	$("#viewset_objectDetails").load("api_loadDetails.php?id=" + id + "&img=" + img);
}

$(document).ready(
	function () {
		/*$('#viewset_sortableThumbs').Sortable(
			{
				accept : 		'viewset_sortableitem',
				helperclass : 	'viewset_sorthelper',
				activeclass : 	'viewset_sortableactive',
				hoverclass : 	'viewset_sortablehover',
				opacity: 		0.8,
				fx:				200,
				revert:			true,
				floats:			true,
				tolerance:		'pointer'
			}
		)*/
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
		loadObjDetails(4,"0002");
	}
);
