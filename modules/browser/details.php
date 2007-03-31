<?

require_once("../../libs/env.php");

// If there is no id param in the url, send to object not found.
if( isset( $_GET['id'] ) ) {
	$objId = $_GET['id'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

// Query DB
$sql = "SELECT * FROM objects o WHERE o.id = $objId LIMIT 1";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('id', $row['id']);
    $t->assign('objnum', $row['objnum']);
    $t->assign('name', $row['name']);
    $t->assign('description', $row['description']);
    $t->assign('thumb_path', $row['thumb_path']);
    $t->assign('med_img_path', $row['med_img_path']);
    $t->assign('lg_img_path', $row['lg_img_path']);
}

// Free the result
$res->free();

// Display template
$t->display('details.tpl');

?>