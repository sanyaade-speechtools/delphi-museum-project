<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
require_once("../../libs/ontology/ontoServices.php");

$t->assign("templateVarsJSON", json_encode($t->_tpl_vars));

// Parse out Cats from URL
isset($_GET['cats']) ? $catIDs = explode(",",$_GET['cats']) : $catIDs = array();
// Parse out Keywords from URL
isset($_GET['kwds']) ? $kwds = $_GET['kwds'] : $kwds = false;
// Parse out page
isset($_GET['page']) ? $page = $_GET['page'] : $page = 1;
// Parse page size
isset($_GET['pageSize']) ? $pageSize = $_GET['pageSize'] : $pageSize = 36;
// Parse out with images
(isset($_GET['images']) && $_GET['images'] == 0)? $images = false : $images = true;

// Query objects
$objResults = queryObjects($catIDs, $kwds, $page-1, $pageSize, $images);

// Get object thumbs
$objects = array();
foreach($objResults['objects'] as $obj){
	$imageOptions = array(	'img_path' => $obj['img_path'],
							'size' => 80,
							'img_ar' => $obj['aspectRatio'],
							'linkURL' => $CFG->shortbase."/object/".$obj['id'],
							'vAlign' => "center",
							'hAlign' => "center"
						);
	
	$obj['thumb'] = outputSimpleImage($imageOptions);
	$objects[] = $obj;
}

// print_r($objects);

$t->assign('facets', queryResultsCategories( $catIDs, $kwds, true, "HTML_UL_ATAG", "id_"));
$t->assign('filters',getFilters($kwds,$catIDs));
$t->assign('pager',themePager($page, $objResults['nPages'],"cats=10004"));
$t->assign('objects', $objects);
$t->assign('results_total', $objResults['nObjs']);
$t->assign('results_start', ($page * $objResults['pageSize']) - $objResults['pageSize'] + 1);
$t->assign('results_end', ($page * $objResults['pageSize'] <= $objResults['nObjs']) ? $page * $objResults['pageSize'] : $objResults['nObjs']);


// Display template
$t->display('results.tpl');

function getFilters($kwds, $catIDs){
	$filters = array();
	if (count($catIDs)){
		foreach($catIDs as $catID){
			$cat = getCategoryByID($catID);
			$facet = getFacetByID($cat['facet_id']);
			$filters[$facet['display_name']][] = $cat;
		}
	}
	if ($kwds){
		$filters['Keywords'] = explode(",",$kwds);
	}
	return themeFilters($filters);
}

function themeFilters($filters){
	$output = "";
	foreach($filters as $filterType => $filter ){
		$output .= "<div class='results_filterFacet'>".$filterType
								.(($filterType == "Keywords")?" include:</div>":" is:</div>");
		foreach($filter as $item){
			if($filterType == "Keywords"){
				$output .= "<div class='results_filterName'>".$item." <a href='".$item."' class='results_kwdRemoveLink'>[remove]</a></div>";	
			} else {
				$output .= "<div class='results_filterName'>".$item['display_name']." <a href='id_".$item['id']."' class='results_catRemoveLink'>[remove]</a></div>";
			}
		}
	}
	return $output;
}

function getCategoryByID($catID){
	if(!is_numeric($catID)){
		// avoid sql injection
		return "Error: Non-numeric ID";
	} else {
		global $db;
		$res =& $db->query("SELECT * FROM categories WHERE id = $catID LIMIT 1");
		if (PEAR::isError($res)) {
		    return $res->getMessage();
		} else {
			return $res->fetchRow();
		}		
	}
}

function getFacetByID($facetID){
	if(!is_numeric($facetID)){
		// avoid sql injection
		return "Error: Non-numeric ID";
	} else {
		global $db;
		$res =& $db->query("SELECT * FROM facets WHERE id = $facetID LIMIT 1");
		if (PEAR::isError($res)) {
		    return $res->getMessage();
		} else {
			return $res->fetchRow();
		}		
	}
}

function themePager($page, $nPages){
	if ($nPages == 1) {
		return;
	}
	$pager = "";
	// Make an array of all the pages
	$pageArray = range(1,$nPages);
	
	// Splice out some pages if there are lots	
	if($nPages > 10){
		switch($page){
			case $page <= 10:
				array_splice($pageArray, 10, count($pageArray)-12, array("..."));
				break;
			case $page > 10 && $page < $nPages-7:
				array_splice($pageArray, 2, $page-6, array("..."));
				array_splice($pageArray, 10, count($pageArray)-12, array("..."));
				break;
			case $page >= $nPages-7:
				array_splice($pageArray, 2, $nPages-10, array("..."));
				break;
		}
	}
	
	// Put out previous link if not on the first page
	if ($page > 1) {
		$pager .= "<a href='".($page - 1)."' class='results_pagerLink'>Previous</a>";
	}
	
	// Loop through page array and spit out the links
	foreach($pageArray as $p){
		if($page == $p || $p == "..."){
			$pager .= $p;
		} else {
			$pager .= "<a href='".($p)."' class='results_pagerLink'>".($p)."</a>";	
		}
	}
	// Put out next link if not on the last page
	if ($page < $nPages) {
		$pager .= "<a href='".($page + 1)."' class='results_pagerLink'>Next</a>";
	}
	
	return $pager;
}
?>
