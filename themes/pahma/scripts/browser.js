$(document).ready(function(){
	$(".browser_topLevelFacet").click(function(){
		// Show the right menu div
		$(".browser_topLevelFacet").removeClass("browser_topLevelFacetSelected")
		$(this).addClass("browser_topLevelFacetSelected");
		
		// Show the right pane
		$(".browser_browsePane").removeClass("browser_browsePaneSelected")
		$("#" + this.id + "_pane").addClass("browser_browsePaneSelected");
	});
});