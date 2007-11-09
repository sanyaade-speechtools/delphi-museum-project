$(document).ready(function () {
	
	objectArray = templateVarsJSON['objects'];
	currentObjectIndex = 1;
	
	if(templateVarsJSON['setHasObjects']){
		// Get the details of the first object in the set
		loadObjectDetails(templateVarsJSON['firstObjectID']);

		$(".viewset_nextIcon").mousedown(function(){
			$(".viewset_nextIcon").hide();
			$("#viewset_next_click").show();
			showNextObject();
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
			loadObjectDetails(this.id);
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
					currentObjectIndex = responseJSON['obj_order'];
					highlightObject(responseJSON['obj_id']);
					loadZoomer(responseJSON['obj_zoomDir']);

				}
			});
}

function highlightObject(obj_id){
	$(".viewset_objectThumb").removeClass("viewset_objectThumbSelected");
	$("#"+obj_id).addClass("viewset_objectThumbSelected");
}

function loadZoomer(path){
	var so = new SWFObject(templateVarsJSON['zoomer'], "zoomer", "367", "350", "6", "#FFFFFF");
	so.useExpressInstall(templateVarsJSON['wwwroot'] + '/libs/swfobject1-5/expressinstall.swf');
	so.addVariable("zoomifyMaxZoom", 150);
	so.addVariable("zoomifySlider", 0);
	so.addVariable("zoomifyNavWindow", 0);
	so.addVariable("zoomifyImagePath", templateVarsJSON['image_zoom']+"/"+path);
	so.write("viewset_objectImage");
}

function showNextObject(){
	currentObjectIndex++;
	if(currentObjectIndex > objectArray.length){
		currentObjectIndex = 1;
	}	
	loadObjectDetails(objectArray[currentObjectIndex-1]['id']);
}

function showPrevObject(){
	currentObjectIndex--;
	if(currentObjectIndex < 1){
		currentObjectIndex = objectArray.length;
	}	
	loadObjectDetails(objectArray[currentObjectIndex-1]['id']);
}