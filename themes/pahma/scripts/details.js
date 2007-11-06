$(document).ready(function(){
	$('#detail_tabBox ul').tabs();
	
	$('.addToSetLink').click(function(){
		// Show progress indicator
		var link = $('#' + this.id).hide();
		var indicator = $('#' + this.id + "Indicator").show();
		var url_and_params = this.href.split("?");
		
		// Check if we're adding or removing the object
		if(link.html() == "[add to set]"){
			// Ajax add to set
			$.ajax({url: url_and_params[0], type: "POST", data: url_and_params[1] + "&action=add",
					success: function(response){
						// Update to the content with a remove link
						link.html("[remove]");

						// hide the progress indicator
						indicator.hide();
						link.show();
					}
			});
		} else if (link.html() == "[remove]"){
			$.ajax({url: url_and_params[0], type: "POST", data: url_and_params[1] + "&action=remove",
					success: function(response){
						// Update to the content with a remove link
						link.html("[add to set]");

						// hide the progress indicator
						indicator.hide();
						link.show();

					}
			});
		}
		
		// Return false
		return false;
		
	});
});