var newSetId = -1;
$(document).ready(function(){
	$("#mysets_createNewSet").click(function(){
		$.ajax({type: "POST",
				url: templateVarsJSON['wwwroot'] + "/modules/sets/api_createNewSet.php",
				data: "owner_id="+templateVarsJSON['currentUser_id'],
				dataType: "json",
				success: function(responseJSON){
					if(responseJSON['error']){
						alert(responseJSON['msg'][0]);
					}else {
						window.location = templateVarsJSON['shortbase'] + "/set/" + responseJSON['set_id'];
					}
				}
		});
	});
});