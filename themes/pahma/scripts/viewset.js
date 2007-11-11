var currentObjectIndex = 0;

$(document).ready(function () {

	
	objectArray = templateVarsJSON['objects'];
	
	if(templateVarsJSON['setHasObjects']){
		// Get the details of the first object in the set
		loadObjectDetails(templateVarsJSON['firstObjectID']);

		$(".viewset_nextIcon").mousedown(function(){
			$(".viewset_nextIcon").hide();
			$("#viewset_next_click").show();
			showNextObject();
						//alert(currentObjectIndex);
		});
		$(".viewset_nextIcon").mouseup(function(){
			$(".viewset_nextIcon").hide();
			$("#viewset_next").show();
		});
		$(".viewset_prevIcon").mousedown(function(){
			$(".viewset_prevIcon").hide();
			$("#viewset_prev_click").show();
			showPrevObject();
		});
		$(".viewset_prevIcon").mouseup(function(){
			$(".viewset_prevIcon").hide();
			$("#viewset_prev").show();
		});

		$(".viewset_objectThumb").click(function(){
			// substring trims off "objectThumb_"
			loadObjectDetails(this.id.substring(12));
		});
	}
	
});



function loadObjectDetails(obj_id){
	$.ajax({	url: templateVarsJSON['wwwroot'] + "/modules/sets/api_loadDetails.php",
				type: "POST",
				dataType: "json",
				data: "obj_id=" + obj_id+"&set_id="+templateVarsJSON['setId'],
				success: function(responseJSON){
					$("#viewset_objectDescription").html(responseJSON['obj_description']);
					$("#viewset_objectName").html(responseJSON['obj_name']);
					$("#viewset_objectDetaiLink").attr('href', templateVarsJSON['wwwroot']+"/modules/browser/details.php?id="+responseJSON['obj_id']);
					currentObjectIndex = parseInt(responseJSON['obj_order'])-1;
					highlightObject(responseJSON['obj_id']);
					loadZoomer(responseJSON['obj_zoomDir']);
				}
			});
}

function highlightObject(obj_id){
	$(".viewset_objectThumb").removeClass("viewset_objectThumbSelected");
	$("#objectThumb_"+obj_id).addClass("viewset_objectThumbSelected");
}

function loadZoomer(path){
	 	myflashvars = {
		zoomifyMaxZoom: "125",
		zoomifySlider: "0",
		zoomifyNavWindow: "0",
		zoomifyImagePath: templateVarsJSON['image_zoom']+"/"+path
	};
	$('#viewset_objectImage').html("");
	$('#viewset_objectImage').flash(
	        { src: templateVarsJSON['zoomer'],
	          width: 367,
	          height: 350,
			flashvars: myflashvars,
			expressInstall: true }, 
	        { version: 6 }
	    );
}

function showNextObject(){

	currentObjectIndex++;
	if(currentObjectIndex >= objectArray.length){
		currentObjectIndex = 0;
	}	
	loadObjectDetails(objectArray[currentObjectIndex]['id']);
	
}

function showPrevObject(){
	currentObjectIndex--;
	if(currentObjectIndex < 0){
		currentObjectIndex = (objectArray.length - 1);
	}	
	loadObjectDetails(objectArray[currentObjectIndex]['id']);
}