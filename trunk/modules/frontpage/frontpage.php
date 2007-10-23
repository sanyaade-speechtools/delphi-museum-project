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
	array_push($objects, outputThumbnail( $row, "front_example" ));
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);

/*
// Query DB for featured sets info
$sql = "SELECT f.set_id, f.name, f.owner_name, f.img_path FROM featured_sets f
ORDER BY f.order_num LIMIT 6";


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
	$imageOutput = "<div class=\"front_fset\">";
	$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
	$pathToSet = $CFG->wwwroot . "/modules/sets/viewset.php?sid=" . $row['set_id'];

	$imageOutput .= "<a href=\"".$pathToSet."\"><img src=\"".$pathToImg."\" alt=\"Set: ".$row['name']."\"";
	$imageOutput .= " class=\"front_fset_Thumbnail\" /></a>";

	$text = $row['name'];

	if (strlen($text) > $maxChars) {
		$text = substr($text,0,$maxChars-$eLen);
		$text = substr($text,0,strrpos($text,' '));
		$text .= $ellipses;
	}

	$text = "<abbr title=\"".$row['name']."\">".$text."</abbr>";
	$imageOutput .= "<div class=\"front_featuredSetName\"><a href=\"".$pathToSet."\">".$text."</a></div>";
	$imageOutput .= "<div class=\"front_featuredSetCreator\">Created by <span>".$row['owner_name']."</span></div>";
	$imageOutput .= "</div>";

	array_push($sets, $imageOutput);
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('sets', $sets);
 */

// Display template
$t->display('frontpage/frontpage.tpl');

//Die here or additional PHP lines will be excecuted.
die();
?>
