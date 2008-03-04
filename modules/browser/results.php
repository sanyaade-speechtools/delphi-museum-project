<?php

require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

// Parse out Cats from URL
isset($_GET['cats']) ? $catIDs = explode(",",$_GET['cats']) : $catIDs = array();
// Parse out Keywords from URL
isset($_GET['kwds']) ? $kwds = $_GET['kwds'] : $kwds = false;
// Parse out page
isset($_GET['page']) ? $page = $_GET['page'] : $page = 1;
// Parse page size
isset($_GET['pageSize']) ? $pageSize = $_GET['pageSize'] : $pageSize = 20;
// Parse out with images
(isset($_GET['images']) && $_GET['images'] == 0)? $images = false : $images = true;

$objResults = queryObjects($catIDs, $kwds, $page, $pageSize, $images);
// print_r($objResults);

$t->assign('facets', queryResultsCategories( $catIDs, $kwds, true, "HTML_UL"));
$t->assign('filters',getFilters($kwds,$catIDs));
$t->assign('pager',themePager($page, $objResults['nPages'],"kwds=mask"));
$t->assign('objects', $objResults['objects']);
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
			$filters[$facet['display_name']][] = $cat['display_name'];
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
		$output .= "<div class='results_filterName'>".$filterType."</div>";
		foreach($filter as $item){
			$output .= "<div>".$item." <a href=''>[remove]</a></div>";
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

function themePager($page, $nPages, $query){
	$pager = "";
	// Put up first and previous links if not on the first page
	if ($page > 1) {
		$pager .= "<a href='?$query&page=1'>First</a>";
		$pager .= "<a href='?$query&page=".($page - 1)."'>Previous</a>";
	}
	for ($i=0; $i < $nPages; $i++) { 
		if($page - 1 == $i){
			$pager .= $page;
		} else {
			$pager .= "<a href='?$query&page=".($i+1)."'>".($i+1)."</a>";	
		}
	}
	if ($page < $nPages) {
		$pager .= "<a href='?$query&page=".($page + 1)."'>Next</a>";
		$pager .= "<a href='?$query&page=".$nPages."'>Last</a>";
	}
	
	return $pager;
}
?>
