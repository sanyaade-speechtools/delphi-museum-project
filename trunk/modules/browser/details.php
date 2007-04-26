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

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('id', $row['id']);
    $t->assign('objnum', $row['objnum']);
    $t->assign('name', $row['name']);
    $t->assign('description', $row['description']);
    $t->assign('img_path', $row['img_path']);
}

// Free the result
$res->free();

// Query DB for categories
$tqCatsForObj =
	"SELECT c.id, c.parent_id, c.facet_id, c.display_name
		FROM categories c, obj_cats oc, objects o
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

$baseQ = "facetBrowse.php?";
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
