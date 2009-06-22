var page;
var kwds = new Array();
var cats = new Array();
var pageSize;
var images;
var baseURL;

$(document).ready(function(){
	initVars();
	
	// Bind Pager Links
	$(".results_pagerLink").click(function(){
		page = $(this).attr("href");
		window.location = generateURL();
		return false;
	});
	
	// Bind keyword [remove] link
	$(".results_kwdRemoveLink").click(function(){
		removeArrayElement($(this).attr("href"), kwds);
		page = 1;
		window.location = generateURL();
		return false;
	});
	// Bind adding keyword form submit
	$("#results_keywordForm").submit(function(){
		addArrayElement($("#results_keywordInput").val(),kwds);
		page = 1;
		window.location = generateURL();
		return false;
	});
	
	// Bind category [remove] link
	$(".results_catRemoveLink").click(function(){
		removeArrayElement($(this).attr("href").split("_")[1], cats);
		page = 1;
		window.location = generateURL();
		return false;
	});
	// Bind category links for adding
	$("#results_categoriesContainer a").click(function(){
		addArrayElement($(this).attr("href").split("_")[1],cats);
		page = 1;
		window.location = generateURL();
		return false;
	});
	// Bind link to toggle images
	$("#toggleImagesLink").click(function(){
		if(images > 0){images = 0;}
		else{images = 1;}
		window.location = generateURL();
	});
	
});

// Parse out variables from the URL
function initVars(){
	// Parse out Cats from URL
	// NO - get from passed var. catsGET = $.getURLParam("cats");
	if( requested_cats.length > 0 )
		{cats = requested_cats.split(",");}
	
	// Parse out Keywords from URL
	// NO - get from passed var. kwdsGET = $.getURLParam("kwds");
	if( requested_kwds.length > 0 )
		{kwds = requested_kwds.split(",");}
	
	// Parse out page
	page = $.getURLParam("page");
	if(page == null){page = 1;}
	
	// Parse page size
	pageSize = $.getURLParam("pageSize");
	
	// Parse out with images
	images = $.getURLParam("images");
	if(images == null){images = 1;}
	
	// Determine base url
	baseURL = templateVarsJSON['wwwroot'];
}

function generateURL(){
	output = new Array();
	if(kwds.length == 0 && cats.length > 0 == 0){
		return baseURL + "/browser/";
	} else {
		if(kwds.length > 0){output.push("kwds=" + kwds.join(","));}
		if(cats.length > 0){output.push("cats=" + cats.join(","));}
		if(page > 0){output.push("page=" + page);}
		if(pageSize > 0){output.push("pageSize=" + pageSize);}
		if(images != null){output.push("images=" + images);}
		return baseURL + "/results/?" + output.join("&");		
	}
}

function removeArrayElement(item,array){
	removed = false;
	for(i=0;i<array.length;i++){
		if(array[i]==item){
			array.splice(i,1);
			removed = true;
		}
	}
	return removed;
}

function addArrayElement(item,array){
	added = false;
	duplicate = false;
	if(item != ""){
		for(i=0;i<array.length;i++){
			if(array[i]==item){duplicate = true;}
		}
		if(!duplicate){
			array.push(item);
			added = true;
		}
	}
	return added;
}
