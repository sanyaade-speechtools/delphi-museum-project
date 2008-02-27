var currentObjectIndex = 0;
var currentObjectId = 0;
var objectArray = Array();
$(document).ready(function () {	
	if(templateVarsJSON['setHasObjects']){
		
		// populate the objectArray with the id's of all the objects in the set.
		for(i=0;i<templateVarsJSON['objects'].length;i++){
			objectArray.push(templateVarsJSON['objects'][i]['id']);
		}
		
		
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
			// substring trims off "objectThumb_"
			loadObjectDetails(this.id.substring(12));
		});
		if(templateVarsJSON['owner_id'] == templateVarsJSON['currentUser_id']){
			/*
			Editing object details
			*/
			$("#viewset_objectEditLink").click(function(){
				// Copy the displayed title and desc into the form fields
				originalTitle = $("#viewset_objectName").html();
				originalDesc = $("#viewset_objectDescription").html();

				$("#viewset_objectNameInput").val(originalTitle);
				$("#viewset_objectDescTextarea").val(originalDesc);

				$("#viewset_objectDetailsDisplay").hide();
				$("#viewset_objectDetailLinks").hide();
				$("#viewset_objectDetailsEdit").show();
				return false;
			});
			$("#viewset_cancelObjectEditLink").click(function(){
				// Hide the edit form
				$("#viewset_objectDetailsDisplay").show();
				$("#viewset_objectDetailLinks").show();
				$("#viewset_objectDetailsEdit").hide();
				return false;
			});
			
			$("#viewset_objectDetailsEditForm").submit(function(){
				$(this).ajaxSubmit({
					url: templateVarsJSON['wwwroot'] + "/modules/sets/api_updateSetObjectDetails.php",
					dataType: "json", 
					success: function(responseJSON,statusText){
						if(responseJSON['error']){
							alert("Error updating the set details\n"+responseJSON['msg'][0]);
						} else {
							$("#viewset_objectName").html(responseJSON['objectName']);
							$("#viewset_objectDescription").html(responseJSON['objectDesc']);
							
							$("#viewset_objectDetailsDisplay").show();
							$("#viewset_objectDetailLinks").show();
							$("#viewset_objectDetailsEdit").hide();
						}
					}
				}); 
				return false;
			});
			
			/*
			Deleting the object
			*/
			$("#viewset_objectDeleteLink").click(function(){
				confirmation = confirm("Are you sure you want to remove this object from the set?");
				if(confirmation){
					
					var deletedObjectId = currentObjectId;
					
					showNextObject();
					
					// Set up an object to be sent to the server
					var jsonRequest = {};
					jsonRequest.set_id = templateVarsJSON['setId'];
					jsonRequest.obj_id = deletedObjectId;
										
					//Loop through the object array for the id of the removed object
					// When found, splice the removed object out
					for(i=0;i<objectArray.length;i++){
						if(objectArray[i] == deletedObjectId){
							objectArray.splice(i,1);
							break;
						}
					}
					
					jsonRequest.items = objectArray;
					jsonRequest = JSON.stringify(jsonRequest);
					
					$.ajax({type: "POST",
							url: templateVarsJSON['wwwroot'] + "/modules/sets/api_deleteSetObject.php",
							data: "jsonRequest="+jsonRequest,
							dataType: "json",
							success: function(responseJSON){
								if(responseJSON['error']){
									alert(responseJSON['msg'][0]);
								}else {
									$("#objectThumb_"+deletedObjectId).fadeOut(300);	
									if(responseJSON['noItemsRemaining']){
										$("#viewset_rightCol").html('<div id="viewset_noThumbnails">There are no objects in this set</div>');
									}
								}
							}
					});
				}
				return false;
			});
			$("#viewset_thumbnails").sortable({
				items:".viewset_objectThumb",
				update: sortUpdate
			});
		}
	}
	
	if(templateVarsJSON['owner_id'] == templateVarsJSON['currentUser_id']){
		/*
		Editing set details
		*/
		$("#viewset_editSetDetailsLink").click(function(){
			// Copy the displayed title and desc into the form fields
			originalTitle = $("#viewset_setDetailsDisplay h1.viewset_setTitle").html();
			originalDesc = $("#viewset_setDetailsDisplay div.viewset_setDescription").html();

			$("#viewset_setTitleInput").val(originalTitle);
			$("#viewset_setDescTextarea").val(originalDesc);

			$("#viewset_setDetailsDisplay").hide();
			$("#viewset_setDetailLinks").hide();
			$("#viewset_setDetailsEdit").show();
			return false;
		});
		$("#viewset_cancelSetEditLink").click(function(){
			// Hide the edit form
			$("#viewset_setDetailsDisplay").show();
			$("#viewset_setDetailLinks").show();
			$("#viewset_setDetailsEdit").hide();
			return false;
		});
		$("#viewset_setDetailsEditForm").submit(function(){
			$(this).ajaxSubmit({
				url: templateVarsJSON['wwwroot'] + "/modules/sets/api_updateSetDetails.php",
				dataType: "json", 
				success: function(responseJSON,statusText){
					if(responseJSON['error']){
						alert("Error updating the set details");
					} else {
						$("#viewset_setDetailsDisplay h1.viewset_setTitle").html(responseJSON['setTitle']);
						$("#viewset_setDetailsDisplay div.viewset_setDescription").html(responseJSON['setDesc']);
						$("#viewset_policy").html(responseJSON['policy']);
						
						$("#viewset_setDetailsDisplay").show();
						$("#viewset_setDetailLinks").show();
						$("#viewset_setDetailsEdit").hide();
					}
				}
			}); 
			return false;
		});
		
		/*
		Deleting the set
		*/
		$("#viewset_deleteSetLink").click(function(){
			confirmation = confirm("Are you sure you want to delete this set?");
			if(confirmation){
				$.ajax({type: "POST",
						url: templateVarsJSON['wwwroot'] + "/modules/sets/api_deleteSet.php",
						data: "set_id="+templateVarsJSON['setId'],
						dataType: "json",
						success: function(responseJSON){
							if(responseJSON['error']){
								alert(responseJSON['msg'][0]);
							}else{
								window.location = templateVarsJSON['wwwroot'] + "/mysets";
							}
						}
				});
			}
			return false;
		});
	}
	
});

function sortUpdate(e,ui){
	// Set up an object to be sent to the server
	var sortedItems = {};
	sortedItems.set_id = templateVarsJSON['setId'];
	sortedItems.items = Array();

	// loop through each thumb
	$(".viewset_objectThumb").each(function (i) {
		obj_id = this.id.split("_");
		sortedItems.items.push(obj_id[1]);
		// If the thumb is selected, note its index
		if($(this).hasClass('viewset_objectThumbSelected')){
			wouldBeCurrentObjectIndex = i;
		}
	 });
	
	var jsonRequest = JSON.stringify(sortedItems);

	$.ajax({type: "POST",
			url: templateVarsJSON['wwwroot'] + "/modules/sets/api_saveOrder.php",
			data: "jsonRequest="+jsonRequest,
			dataType: "json",
			success: function(responseJSON){
				if(responseJSON['error']){
					alert(responseJSON['msg'][0]);
				}else{
					objectArray = responseJSON['items'];
					// update the index of the selected object thumb
					currentObjectIndex = wouldBeCurrentObjectIndex;
				}
			}
	});
	
}

function loadObjectDetails(obj_id){
	$.ajax({	url: templateVarsJSON['wwwroot'] + "/modules/sets/api_loadDetails.php",
				type: "POST",
				dataType: "json",
				data: "obj_id=" + obj_id+"&set_id="+templateVarsJSON['setId'],
				success: function(responseJSON){
					$("#viewset_objectDescription").html(responseJSON['objectDescription']);
					$("#viewset_objectName").html(responseJSON['objectName']);
					$("#viewset_objectIdFormField").val(responseJSON['obj_id']);
					$("#viewset_objectDetailLink").attr('href', templateVarsJSON['wwwroot']+"/modules/browser/details.php?id="+responseJSON['obj_id']);					
					currentObjectId = responseJSON['obj_id'];
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
	loadObjectDetails(objectArray[currentObjectIndex]);
	
}

function showPrevObject(){
	currentObjectIndex--;
	if(currentObjectIndex < 0){
		currentObjectIndex = (objectArray.length - 1);
	}	
	loadObjectDetails(objectArray[currentObjectIndex]);
}