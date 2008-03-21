$(document).ready(function(){
	$(".browser_topLevelFacet").click(function(){
		// Show the right menu div
		$(".browser_topLevelFacet").removeClass("browser_topLevelFacetSelected")
		$(this).addClass("browser_topLevelFacetSelected");
		
		// Show the right pane
		$(".browser_browsePane").removeClass("browser_browsePaneSelected")
		$("#" + this.id + "_pane").addClass("browser_browsePaneSelected");
		
	});
	$(function() {
		$('div.tree li:has(ul)').addClass('parent'); // Requires jQuery 1.2!
		$('div.tree li').click(function() {
			var o = $(this);
			o.children('ul').toggle();
			o.filter('.parent').toggleClass('expanded');
			return false;
		});
		$('div.tree li a').click(function(e) {
			var o = $(this);
			var id = o.attr("href").split("_")[1];
			//alert( "go to: " + id );
			window.location.href = templateVarsJSON['wwwroot'] + '/results/?cats=' + id;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return false;
		});
	});
});