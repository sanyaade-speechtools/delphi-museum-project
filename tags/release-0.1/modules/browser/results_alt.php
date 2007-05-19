<?

require_once("../../libs/env.php");
/*
// Query DB
$sql = "SELECT * FROM objects o WHERE o.id = $objId LIMIT 1";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('id', $row['id']);
}

// Free the result
$res->free();
*/
// Display template
$t->display('results_alt.tpl');

?>