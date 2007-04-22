<?
require_once("../../libs/env.php");
// Temp hack until we move from mysqli to all PEAR
include("dbconnect.php");
// This should go somewhere else
require "Facet.inc";

	$onlyWithImgs = true;		// default to only images
	if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
		$onlyWithImgs = false;

	$objsTotal = 0;
	$objsTotalWImgs = 0;
	$allCountsResults=$mysqli->query("SELECT n_objs_total, n_objs_w_imgs FROM DBInfo LIMIT 1");
	if($row = $allCountsResults->fetch_array()) {
		$objsTotal = $row[0];
		$objsTotalWImgs = $row[1];
	}
	$tqCountsByCat = "select c.id, c.parent_id, c.facet_id, c.display_name, ";
	if(	$onlyWithImgs ) {
		$tqCountsByCat .= "n_matches_w_img"
										  ." from categories c where n_matches_w_img>0 order by id";
		$t->assign("objsTotal", $objsTotalWImgs);
	} else {
		$tqCountsByCat .= "n_matches"
										  ." from categories c where n_matches>0 order by id";
		$t->assign("objsTotal", $objsTotal);
	}
	$facetsResults=$mysqli->query("select id, display_name from facets order by id");
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
	$qual = $onlyWithImgs ? " with images. <a href=\"".$_SERVER['PHP_SELF']."?wImgs=false\">Show All</a>":". (<a href=\"".$_SERVER['PHP_SELF']."\">Only show objects with images</a>)";
	$t->assign("qual", $qual);
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
