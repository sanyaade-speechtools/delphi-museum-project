
$(document).ready(function(){
	$('#detail_tabBox ul').tabs();
	
	$('.ajaxLink').click(function(){
		ajaxLinkHandler(this.id, this.href, this.title);
		return false;
	});

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
});

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
	}
	
	// Return false so that the link is not followed
	return false;
}