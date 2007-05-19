$(document).ready(
	function () {
		$(".closer").click(TB_remove);
		var options = { target: '#toEmailConfirm', success: showResponse };

		$("#shareSetForm").validate({
		  submitHandler: function(form) {
		  	$(form).ajaxSubmit(options);
		  }
		});
	}
);


function showResponse(responseText, statusText)  {
	$("#viewset_confirmSendForm").css("display","block");
	$("#viewset_sendSetForm").css("display","none");
}