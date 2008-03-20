<?php
require_once("../../libs/env.php");
require_once("../../libs/ontology/ontoServices.php");

$bigint = 999999;
$facetinfo = getCategoriesInFacet("__ALL", true, "HTML_UL_ATAG", "id_", $bigint );

$t->assign("facets", $facetinfo);
$t->assign("templateVarsJSON", json_encode($t->_tpl_vars));

// Display template
$t->display('browser.tpl');
?>
