<?

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");

// Query DB for featured objects (set #1)
$sql = "SELECT o.id, o.objnum, o.name, o.img_path, o.img_ar FROM objects o, set_objs so
WHERE so.set_id=1 AND so.obj_id=o.id ORDER BY RAND() LIMIT 6";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...  TODO this is not graceful enough
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}

$objects = array();

while( $row=$res->fetchRow()) {
	array_push($objects, outputWrappedImage( $row, "front_example", 
							$CFG->wwwroot."/modules/browser/details.php?id=", 95 ));
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);

// Query DB for featured sets info
$sql = "SELECT set_id id, name, owner_name owner, img_path, aspectR img_ar FROM featured_sets"
        ." ORDER BY porder LIMIT 6";


$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...  TODO this is not graceful enough
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}


$sets = array();

$maxChars = 25;
$ellipses = " ...";
$eLen = strlen($ellipses);
while( $row=$res->fetchRow()) {
	array_push($sets, outputWrappedImage( $row, "front_fset", 
							$CFG->wwwroot."/modules/sets/viewset.php?sid=", 173 ));
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('sets', $sets);

// Display template
$t->display('frontpage/frontpage.tpl');

//Die here or additional PHP lines will be excecuted.
die();
?>
