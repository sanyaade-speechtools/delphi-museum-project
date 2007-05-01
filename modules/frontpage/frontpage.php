<?

require_once("../../libs/env.php");

// Query DB
$sql = "SELECT o.id, o.objnum, o.name, o.img_path FROM objects o, set_objs so
WHERE so.set_id=1 AND so.obj_id=o.id ORDER BY RAND() LIMIT 12";


$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}



$objects = array();

//while ($row = $res->fetchRow()) {
//
//	$object = array(	'id' => $row['id'],
//						'img_path' => $row['img_path'],
//						'name' => $row['name'],
//						'objnum' => $row['objnum']
//					);
//
//	array_push($objects, $object);
//
//}

$maxChars = 18;
$ellipses = " ...";
$eLen = strlen($ellipses);
while( $row=$res->fetchRow()) {
	$imageOutput = "<div class=\"front_example\">";
	$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
	$pathToDetails = $CFG->wwwroot . "/modules/browser/details.php?id=" . $row['id'];

	$imageOutput .= "<a href=\"".$pathToDetails."\"><img src=\"".$pathToImg."\" alt=\"".$row['name']."\"";
	$imageOutput .= " class=\"front_exampleThumbnail\" /></a>";

	$text = $row['name'];

	if (strlen($text) > $maxChars) {
		$text = substr($text,0,$maxChars-$eLen);
		$text = substr($text,0,strrpos($text,' '));
		$text .= $ellipses;
	}

	$text = "<abbr title=\"".$row['name']."\">".$text."</abbr>";
	$imageOutput .= "<div class=\"front_exampleLabel\"><a href=\"".$pathToDetails."\">".$text."</a></div>";
	$imageOutput .= "</div>";

	array_push($objects, $imageOutput);
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);

// Display template
$t->display('frontpage.tpl');

//Die here or additional PHP lines will be excecuted.
die();


?>
