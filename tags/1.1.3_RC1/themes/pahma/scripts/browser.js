function handleLink( e, id, count ) {
	// alert( "Handle Link for id: " + id + " with count: " + count);
	var newloc = templateVarsJSON['wwwroot'] + '/results/?cats=' + id;
	if( count > 1 )
		newloc += "&images=0";
	window.location.href = newloc;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return false;
}

$(document).ready(function(){
	$("div.browser_topLevelFacet").click(function(){
		// Show the right menu div
		$("div.browser_topLevelFacet").removeClass("browser_topLevelFacetSelected")
		$(this).addClass("browser_topLevelFacetSelected");
		
		// Show the right pane
		$("div.browser_browsePane").removeClass("browser_browsePaneSelected")
		$("#" + this.id + "_pane").addClass("browser_browsePaneSelected");
		$("#" + this.id + '_pane div.tree li').click(function() {
			var o = $(this);
			o.children('ul').toggle();
			o.filter('.parent').toggleClass('expanded');
			return false;
		});
		
	});
	$(function() {
		// $('div.tree li:has(ul)').addClass('parent'); // Requires jQuery 1.2!
		$('div.browser_browsePaneSelected div.tree li').click(function() {
			var o = $(this);
			o.children('ul').toggle();
			o.filter('.parent').toggleClass('expanded');
			return false;
		});
	});
});
