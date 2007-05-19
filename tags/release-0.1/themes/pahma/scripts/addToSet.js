$(document).ready(
	function () {
		//$(".closer").click(TB_remove);
		var options = { success: showResponse };

		$("#addToSetForm").validate({
		  submitHandler: function(form) {
		  	$(form).ajaxSubmit(options);
		  }
		});
	}
);


function showResponse(responseText, statusText)  {
	$("#createNewSet").css("display","none");
	$("#chooseSet").css("display","none");
	$("#addingToSet").css("display","block");
	setTimeout("TB_remove()",1250);
}
