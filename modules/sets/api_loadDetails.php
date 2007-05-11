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


$mid_dir = $CFG->dir_image_medium;
$zoom_dir = $CFG->dir_image_zoom;
if( empty($mid_dir) || empty($zoom_dir) )
	die("Paths to images not configured!");

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('detail_id', $row['id']);
    $t->assign('detail_objnum', $row['objnum']);
    $t->assign('detail_name', $row['name']);
	$t->assign('detail_description', $row['description']);
	$relPath = $row['img_path'];
	$mid_path = $mid_dir.'/'.$relPath;
	$rel_zoom_dir = substr($relPath, 0, strlen($relPath)-4);
	$zoom_img_dir = $zoom_dir.'/'.$rel_zoom_dir;
		if( is_dir($zoom_img_dir) )
			$t->assign('zoom_path', $CFG->image_zoom.'/'.$rel_zoom_dir);
		else
			$t->assign('bad_zoom_path', $CFG->image_zoom.'/'.$rel_zoom_dir);
		// We always set the image path so we can fall back from the flash app
		if( is_file($mid_path) )
			$t->assign('img_path', $CFG->image_medium.'/'.$relPath);
		else {
			$t->assign('img_path', $CFG->no_image_medium);
			$t->assign('bad_img_path', $CFG->image_medium.'/'.$relPath);
		}
}

// Free the result
$res->free();

// Display template
$t->display('viewsetDetails.tpl');

?>