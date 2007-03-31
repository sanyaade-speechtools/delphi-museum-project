<?

require_once("../../libs/env.php");

/*

pull object id from the URL.
	If doesn't exist: "object not found"

Look up object in DB

Assign vars to template

display template

*/

if( isset( $_GET['id'] ) ) {
	$objId = $_GET['id'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

$sql = "SELECT * FROM objects o WHERE o.id = $objId LIMIT 1";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

while ($row = $res->fetchRow()) {
    $t->assign('id', $row['id']);
    $t->assign('objnum', $row['objnum']);
    $t->assign('name', $row['name']);
    $t->assign('description', $row['description']);
    $t->assign('thumb_path', $row['thumb_path']);
    $t->assign('med_img_path', $row['med_img_path']);
    $t->assign('lg_img_path', $row['lg_img_path']);
}

$res->free();

$t->display('details.tpl');

?>