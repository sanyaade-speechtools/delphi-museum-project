function newSet(){
	$("#createNewSet").css("display","block");
	$("#chooseSet").css("display","none");
}

function backToExistingSets(){
	$("#createNewSet").css("display","none");
	$("#chooseSet").css("display","block");
	
}

function submitObjectToSet(){
	$("#createNewSet").css("display","none");
	$("#chooseSet").css("display","none");
	$("#addingToSet").css("display","block");
	setTimeout("TB_remove()",1250);
}


$(document).ready(
	function () {
		$(".closer").click(TB_remove);
		$(".createNewSet").click(newSet);
		$(".backToExistingSets").click(backToExistingSets);
		$(".setLink").click(submitObjectToSet);		
	}
);