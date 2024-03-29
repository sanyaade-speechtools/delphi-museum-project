//load zoomer

$(document).ready(function(){
	
	//init tabs
	$('#detail_tabBox ul').tabs();
	
	// Load the first image into the zoomer and hightlight the first thumb
	loadObjectZoomer(templateVarsJSON['zoomDir']);
	$(".detail_thumbnail:first").addClass("detail_thumbnailSelected");
	
	//bind various links that require an ajax action on click
	$('.ajaxLink').click(function(){
		ajaxLinkHandler(this.id, this.href, this.title);
		return false;
	});
	
	// handle the add tag form
	$("#tagAddForm").submit(function() {
		var tagInput = $('#tagAddForm_input').val();
		var obj_id = $('#tagAddForm_obj_id').val();
		var indicator = $("#addTagFormIndicator").show();
		var url = $("#tagAddForm").attr("action");
		$.ajax({url: url, type: "POST", data: "tagInput="+tagInput+"&obj_id="+obj_id, dataType: "json",
				success: function(response){
					if(response['success']){
						// Clear the input in the add tag field
						$('#tagAddForm_input').val("");
						// Hide the no tags message if it's showing
						$("#detail_noTagsMessage").hide();
						// Add a div with the new tag
						newDiv = '<div class="detail_tag">'+response['tag_name']+' <a href="'+templateVarsJSON['wwwroot']+'/modules/tags/removeTag.php?tag_id='+response['tag_id']+'&obj_id='+obj_id+'" class="ajaxLink" id="tag'+response['tag_id']+'" title="Remove this tag">[x]</a></div>';
						$("#detail_tagList").append(newDiv);
						$("#tag"+response['tag_id']).click(function(){
							ajaxLinkHandler(this.id, this.href, this.title);
							return false;
						});
					}
					// hide the progress indicator
					indicator.hide();
				}
		});
		return false;
	});
	
	// show the right image when addional media thumbs are clicked
	$(".detail_thumbnail").click(function(){
		// Show the right object in the zoomer
		loadObjectZoomer($(this).children().children().attr("href"));
		
		// highlight the correct thumb
		$(".detail_thumbnailSelected").removeClass("detail_thumbnailSelected");
		$(this).addClass("detail_thumbnailSelected");
		
		return false;
	});
	$('#detail_infoCol li.treeCat a').click(function(e) {
		var o = $(this);
		var id = o.attr("href").split("_")[1];
		//alert( "go to: " + id );
		window.location.href = templateVarsJSON['wwwroot'] + '/results/?cats=' + id;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return false;
	});
});

function loadObjectZoomer(path){
	$('#detail_image').html("");
	myflashvars = {
		zoomifyMaxZoom: 100,
		zoomifySlider: 0,
		zoomifyNavWindow: 1,
		zoomifyNavWidth: 85,
		zoomifyNavHeight: 70,
		zoomifyNavY: 280,
		zoomifyImagePath: path
	};
	$('#detail_image').flash(
	        { src: templateVarsJSON['zoomer'],
	          width: 425,
	          height: 350,
			flashvars: myflashvars,
			expressInstall: true }, 
	        { version: 6 }
	);
}

function ajaxLinkHandler(arg_id, arg_href, arg_title){
	var link = $('#' + arg_id).hide();
	var url_and_params = arg_href.split("?");
	var title = arg_title;
	var indicator = $('#' + arg_id + "Indicator").show();
	
	// Check if we're adding or removing the object
	if(title == "Add object to this set"){
		// Ajax add to set
		$.ajax({url: url_and_params[0], type: "POST", data: url_and_params[1] + "&action=add", dataType: "json",
				success: function(responseJSON){
					// Update the content with a remove link
					link.html("[remove]");
					link.attr("title", "Remove object from this set");
					
					if(responseJSON['updateThumb']){
						$("#set_thumb_"+responseJSON['set_id']).hide();
						$("#set_thumb_"+responseJSON['set_id']).html(responseJSON['thumbDiv']);
						$("#set_thumb_"+responseJSON['set_id']).fadeIn(400);
					}
					
					// hide the progress indicator
					indicator.hide();
					link.fadeIn(400);
				}
		});
	} else if (title == "Remove object from this set"){
		$.ajax({url: url_and_params[0], type: "POST", data: url_and_params[1] + "&action=remove", dataType: "json",
				success: function(responseJSON){
					// Update the content with a remove link
					link.html("[add to set]");
					link.attr("title", "Add object to this set");

					if(responseJSON['updateThumb']){
						$("#set_thumb_"+responseJSON['set_id']).hide();
						$("#set_thumb_"+responseJSON['set_id']).html(responseJSON['thumbDiv']);
						$("#set_thumb_"+responseJSON['set_id']).fadeIn(400);
					}
					
					// hide the progress indicator
					indicator.hide();
					link.fadeIn(400);

				}
		});
	}else if (title == "Remove this tag"){
		$.ajax({url: url_and_params[0], type: "POST", data: url_and_params[1], dataType: "json",
				success: function(response){
					if(response['success']){
						// Remove the tag div
						indicator.hide();
						link.parent('.detail_tag').fadeOut(200).remove();
						if($('.detail_tag').length == 0){
							$("#detail_noTagsMessage").show();
						}
					} else {
						indicator.hide();
					}
				}
		});
	}else if (title == "Create a new set with this object"){
		// Make two ajax calls. The first creates the set, the second add the object to the set
		$.ajax({url: url_and_params[0], 
				type: "POST", 
				data: url_and_params[1], 
				dataType: "json",
				success: function(newSetResponse){
					if(!newSetResponse['error']){
						$.ajax({url: templateVarsJSON['wwwroot'] + "/modules/sets/api_addToSet.php", 
								type: "POST", 
								data: url_and_params[1]+"&action=add&set_id="+newSetResponse['set_id'], 
								dataType: "json",
								success: function(addObjectrResponse){
									if(!addObjectrResponse['error']){
										window.location = templateVarsJSON['shortbase'] + "/set/" + newSetResponse['set_id'];
									} else {
										alert("An error occured trying to add this object to your new set.");
									}
								}
						});
					} else {
						alert("An error occured trying to create a new set.\n"+newSetResponse['msg'][0]);
					}
				}
		});
	}
	
	// Return false so that the link is not followed
	return false;
}

function doCatCardZoom(e) {
	if($('#cat_card_clipper').hasClass('zoomed')){
		$('#cat_card_clipper').addClass('unzoomed');
		$('#cat_card_clipper').removeClass('zoomed');
	} else {
		$('#cat_card_clipper').addClass('zoomed');
		$('#cat_card_clipper').removeClass('unzoomed');
	}
	e.cancelBubble = true;
	if (e.stopPropagation) 
		e.stopPropagation();
	return false;
}

function doHideCard(e) {
  $('#detail_image').css("visibility","visible");
  $('#pop_screen').css("display","none");
  e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return false;
}

function setCatCardImage(srcURL) {
	$('#cat_card_image').attr("src", srcURL);
	if($('#cat_card_clipper').hasClass('zoomed')){
		$('#cat_card_clipper').addClass('zoomed');
		
	} else {
		$('#cat_card_clipper').addClass('unzoomed');
	}
}

function doViewCard(e,nimages) {
	$('#vcc_inner').css('height', vcc_inner_height);
  	$('#detail_image').css("visibility","hidden");
	$('#pop_screen').css("display","block");
	if(nimages>5){
		var rows = nimages/5;
		var thumb_height = 75;
		var padding = 2 * rows;
		if(nimages<=40){
			var regular_height = 625;
		}else{
			var regular_height = 730;
		}
		var vcc_inner_height = (rows * thumb_height) + padding + regular_height;
		$('#vcc_inner').css('height', vcc_inner_height);
	}else{
		//do nothing
	}
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return false;
}