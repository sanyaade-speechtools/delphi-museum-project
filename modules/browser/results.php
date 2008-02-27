<?php

require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

// Parse out Cats from URL
isset($_GET['cats']) ? $catIDs = explode(",",$_GET['cats']) : $catIDs = array();

// Parse out Keywords from URL
isset($_GET['kwds']) ? $kwds = explode(",",$_GET['kwds']) : $kwds = array();

// Parse out page
isset($_GET['page']) ? $page = $_GET['page'] : $page = 1;

// Generate the pager


//$resInfo = queryObjects($catIDs, $kwds, $pageNum, $pageSz, $onlyWithImgs );
// $resInfo = queryObjects($catIDs, $kwds, 1, 20, true );
// print_r($resInfo);

$t->assign('facets', queryResultsCategories( $catIDs, $kwds, true, "HTML_UL"));

$objResults = queryObjects($catIDs, $kwds, 1, 20, true);
$t->assign('objects', $objResults['objects']);
$t->assign('results_total', $objResults['nObjs']);
$t->assign('results_start', ($page * $objResults['pageSize']) - $objResults['pageSize'] + 1);
$t->assign('results_end', $page * $objResults['pageSize']);

print_r(queryObjects($catIDs, $kwds, 1, 20, true));

// Display template
$t->display('results.tpl');

?>
