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
		    textarea_cols: "35"
		});
		$("img").hover(function(){
		   $(this).addClass("viewset_thumbnailHover");
		 },function(){
		   $(this).removeClass("viewset_thumbnailHover");
		 });
		
	}	
);



function loadObjDetails(id){
	$("#viewset_objectDetails").load("api_loadDetails.php?id=" + id);
}