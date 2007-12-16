<?php
require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

$facetinfo = getCategoriesInFacet("__ALL", true, "HTML_UL" );

foreach( $facetinfo as $facet ) {
	$t->assign("cats_".$facet['id'], $facet['items']);
}

// Display template
$t->display('browser.tpl');
?>
