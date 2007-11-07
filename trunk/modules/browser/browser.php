<?php
require_once("../../libs/env.php");
// Temp hack until we move from mysqli to all PEAR
include("../facetBrowser/dbconnect.php");
// This should go somewhere else
require "../facetBrowser/Facet.inc";

	$onlyWithImgs = true;		// default to only images
	if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
		$onlyWithImgs = false;

	$allCountsResults=$mysqli->query("SELECT n_objs_total, n_objs_w_imgs FROM DBInfo LIMIT 1");
	if($row = $allCountsResults->fetch_array()) {
		$t->assign("objsTotal", $row[0]);
		$t->assign("objsTotalWImgs", $row[1]);
	}
	$tqCountsByCat = 
		"select c.id, c.parent_id, c.facet_id, c.display_name, n_matches 
		from categories c where n_matches>0 order by id";
	$facetsResults=$mysqli->query("select id, display_name from facets");
	GetFacetListFromResultSet($facetsResults);
	$countsresult=$mysqli->query($tqCountsByCat);
	PopulateFacetsFromResultSet( $countsresult, true );
  // Facets now exist as array in $facets. Nodes are avail in hashMap.
	$baseQ = "facetBrowse.php?";
	$catsParam = "cats=";
	if( !$onlyWithImgs )
		$baseQ.= "wImgs=false&".$catsParam;
  else 
		$baseQ.= $catsParam;	
	$t->assign("catsByCountQ", $tqCountsByCat);
	foreach( $facets as $facet ) {
		if( empty($facet->arrChildren) ) {
			$facetTreeOutput .= "<code class=\"hidden\">Facet: "
														.$facet->name." has no no matches</code>";
		} else {
			//$facet->PruneForOutput($numResultsTotal, $catIDs);
			$facetTreeOutput .= $facet->GenerateHTMLOutput( "facet", 0, 1, $baseQ, true );
		}
	}
	$t->assign("facetTree", $facetTreeOutput);
// Display template
$t->display('browser.tpl');
?>
