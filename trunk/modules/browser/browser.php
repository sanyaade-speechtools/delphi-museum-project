<?php
require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

$bigint = 999999;
$facetinfo = getCategoriesInFacet("__ALL", true, "HTML_UL", $bigint );

foreach( $facetinfo as $facet ) {
	$t->assign("cats_".$facet['id'], $facet['items']);
}

// Display template
$t->display('browser.tpl');
?>
