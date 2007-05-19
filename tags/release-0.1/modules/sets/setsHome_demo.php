<?

require_once("../../libs/env.php");
/*
// If there is no id param in the url, send to object not found.
if( isset( $_GET['sid'] ) ) {
	$setId = $_GET['sid'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

// Query DB
$sql = "SELECT * FROM sets s WHERE s.id = $setId LIMIT 1";
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
    $t->assign('setName', $row['name']);
    $t->assign('setDescription', $row['description']);
}

// Free the result
$res->free();
*/
// Display template
$t->display('demo/setsHome.tpl');

?>