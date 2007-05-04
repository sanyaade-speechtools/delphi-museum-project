<?

require_once("../../libs/env.php");
// Temp hack until we move from mysqli to all PEAR
include("../facetBrowser/dbconnect.php");
// This should go somewhere else
require "../facetBrowser/Facet.inc";

// If there is no id param in the url, send to object not found.
if( isset( $_GET['id'] ) ) {
	$objId = $_GET['id'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}
$onlyWithImgs = true;		// default to only images
if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
	$onlyWithImgs = false;

// Query DB for obj info
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
    $t->assign('id', $row['id']);
    $t->assign('objnum', $row['objnum']);
    $t->assign('name', $row['name']);
		$t->assign('description', $row['description']);
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

// Query DB for categories
$tqCatsForObj =
	"SELECT c.id, c.parent_id, c.facet_id, c.display_name
FROM categories c, obj_cats oc
WHERE oc.obj_id=".$objId." AND c.id=oc.cat_id
GROUP BY c.id ORDER BY c.id";
$t->assign("tqCatsForObj", $tqCatsForObj);

$facetsResults = $mysqli->query("select id, display_name from facets order by id");
// Fills global $facets variable
GetFacetListFromResultSet($facetsResults);
$catsresult=$mysqli->query($tqCatsForObj);
if( !empty($catsresult))
	// Fills out the paths under facets
	PopulateFacetsFromResultSet( $catsresult, false );

$baseQ = "../facetBrowser/facetBrowse.php?";
$catsParam = "cats=";
if( !$onlyWithImgs )
	$baseQ.= "wImgs=false&".$catsParam;
else 
	$baseQ.= $catsParam;	

// var for holding concated output
$facetTreeOutput = "";
foreach( $facets as $facet ) {
	if( empty($facet->arrChildren) ) {
		$facetTreeOutput .= "<code class=\"hidden\">Facet: "
													.$facet->name." has no no matches</code>";
	} else {
		$catIDs = array();
		$facet->PruneForOutput(1, $catIDs);
		$facetTreeOutput .= $facet->GenerateHTMLOutput( "facet", -9, 1, $baseQ, false, true );
	}
}

$t->assign("facetTree", $facetTreeOutput);

// Display template
$t->display('details.tpl');

?>
