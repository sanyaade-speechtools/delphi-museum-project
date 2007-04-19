function sendSet(){
	 $("#viewset_confirmSendForm").css("display","block");
	$("#viewset_sendSetForm").css("display","none");
}


$(document).ready(
	function () {
		$(".closer").click(TB_remove);
		$(".shareSetSendButton").click(sendSet);
		
	}
);