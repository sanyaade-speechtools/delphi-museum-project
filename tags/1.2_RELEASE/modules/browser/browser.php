<?php
require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

// Do this before we assign the facets, so we do not duplicate this huge structure
// in the JSON vars.
$t->assign("templateVarsJSON", json_encode($t->_tpl_vars));

$bigint = 999999;
// $facetinfo = getCategoriesInFacet("__ALL", "both", "HTML_UL_ATAG", "id_", $bigint );
$facetinfo = getCategoriesInFacet("__ALL", "both", "HTML_UL_OC", "handleLink", $bigint );

$t->assign("facets", $facetinfo);
$t->assign('page_title', 'Delphi Category Browser');

// Display template
$t->display('browser.tpl');
?>
