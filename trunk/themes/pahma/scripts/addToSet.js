function submitObjectToSet(){
	$("#createNewSet").css("display","none");
	$("#chooseSet").css("display","none");
	$("#addingToSet").css("display","block");
	setTimeout("TB_remove()",1250);
}


$(document).ready(
	function () {
		$(".closer").click(TB_remove);
		$(".setLink").click(submitObjectToSet);		
	}
);