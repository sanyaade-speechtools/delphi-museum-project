function sendSet(){
	 $("#confirmSendForm").css("display","block");
	$("#sendSetForm").css("display","none");
}


$(document).ready(
	function () {
		$(".closer").click(TB_remove);
		$(".shareSetSendButton").click(sendSet);
		
	}
);