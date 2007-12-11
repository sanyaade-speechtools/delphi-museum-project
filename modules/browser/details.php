<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
// This should go somewhere else
//require "../facetBrowser/Facet.inc";


// If there is no id param in the url, send to object not found.
if( isset( $_GET['id'] ) ) {
	$objId = $_GET['id'];
} else {
	$t->assign('heading', "Whoops!");
	$t->assign('message', "We could not find the object you were looking for!");
	$t->display('error.tpl');
	die;
}
$onlyWithImgs = true;		// default to only images
if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
	$onlyWithImgs = false;


//---------------------------------
// Query DB for obj info
//---------------------------------

$sql = "SELECT * FROM objects o WHERE o.id = $objId LIMIT 1";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Whoops!");
	$t->assign('message', "We could not find the object you were looking for!");
	$t->display('error.tpl');
	die;
}

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('id', $row['id']);
    $t->assign('objnum', $row['objnum']);
    $t->assign('name', $row['name']);
	$t->assign('description', $row['description']);
	$t->assign('zoomDir', $CFG->image_zoom."/".substr($row['img_path'], 0, -4));
}

// Free the result
$res->free();


//---------------------------------
// Query DB for Object Images
//---------------------------------
$sql = "SELECT * FROM media WHERE media.obj_id = $objId";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() <= 1 ){
	$t->assign('hasAdditionalMedia', false);
}else{
	$t->assign('hasAdditionalMedia', true);
	
	$additionalMediaItems = array();
	while ($row = $res->fetchRow()) {
		$imageOptions = array(	'img_path' => $row['path'],
								'size' => 40,
								'img_ar' => $row['aspectr'],
								'linkURL' => $CFG->image_zoom."/".substr($row['path'], 0, -4),
								'vAlign' => "center",
								'hAlign' => "center"
							);

		$additionalMediaItem = array('name' => $row['name'], 
									'description' => $row['description'],
									'type' => $row['type'],
									'width' => $row['width'],
									'height' => $row['height'],
									'thumb' => outputSimpleImage($imageOptions)
						);
		array_push($additionalMediaItems, $additionalMediaItem);

	}
	$t->assign("additionalMediaItems", $additionalMediaItems);
}
// Free the result
$res->free();

//---------------------------------
// Query DB for categories
//---------------------------------
// $tqCatsForObj =
// 	"SELECT c.id, c.parent_id, c.facet_id, c.display_name
// FROM categories c, obj_cats oc
// WHERE oc.obj_id=".$objId." AND c.id=oc.cat_id
// GROUP BY c.id ORDER BY c.id";
// $t->assign("tqCatsForObj", $tqCatsForObj);
// 
// $facetsResults = $mysqli->query("select id, display_name from facets order by id");
// // Fills global $facets variable
// GetFacetListFromResultSet($facetsResults);
// $catsresult=$mysqli->query($tqCatsForObj);
// if( !empty($catsresult))
// 	// Fills out the paths under facets
// 	PopulateFacetsFromResultSet( $catsresult, false );
// 
// $baseQ = "../facetBrowser/facetBrowse.php?";
// $catsParam = "cats=";
// if( !$onlyWithImgs )
// 	$baseQ.= "wImgs=false&".$catsParam;
// else 
// 	$baseQ.= $catsParam;	
// 
// // var for holding concated output
// $facetTreeOutput = "";
// foreach( $facets as $facet ) {
// 	if( empty($facet->arrChildren) ) {
// 		$facetTreeOutput .= "<code class=\"hidden\">Facet: "
// 													.$facet->name." has no no matches</code>";
// 	} else {
// 		$catIDs = array();
// 		$facet->PruneForOutput(1, $catIDs);
// 		$facetTreeOutput .= $facet->GenerateHTMLOutput( "facet", -9, 1, $baseQ, false, true );
// 	}
// }
// 
// $t->assign("facetTree", $facetTreeOutput);

//If there is a logged in user, show their sets and tags
if (isset($_SESSION['id'])){
	//---------------------------------
	// Query DB for sets containing this object
	//---------------------------------
	$sql = 	"	SELECT 
					set_objs.set_id, 
					sets.name as set_name, 
					sets.owner_id, 
					sets.creation_time, 
					user.username as owner_name, 
					tFirstSetObject.img_path, 
					tFirstSetObject.img_ar,
					sets.id
				FROM set_objs
				LEFT JOIN sets
				ON set_objs.set_id = sets.id
				LEFT JOIN user
				ON sets.owner_id = user.id
				LEFT JOIN (SELECT set_objs.set_id, objects.img_path, objects.img_ar
							FROM set_objs
							LEFT JOIN objects
							ON set_objs.obj_id = objects.id
							WHERE set_objs.order_num = 1) tFirstSetObject
				ON tFirstSetObject.set_id = set_objs.set_id
				WHERE set_objs.obj_id = ".$objId." AND sets.policy = 'public'
				ORDER BY creation_time DESC
			";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}

	// If nothing is found, set containingSets variable to false
	if ( $res->numRows() < 1 ){
		$t->assign("containingSets", false);
	} else {
		$t->assign("containingSets", true);
		if ( $res->numRows() > 1 ) $t->assign("moreSetsLink", true);

		$otherSets = array();
		$otherSetsLimit = 1;
		$otherSetsCounter = 0;
		while ($row = $res->fetchRow()) {
			if($otherSetsCounter < $otherSetsLimit){
				$imageOptions = array(	'img_path' => $row['img_path'],
										'size' => 40,
										'img_ar' => $row['img_ar'],
										'linkURL' => $CFG->shortbase."/set/".$row['set_id'],
										'vAlign' => "top",
										'hAlign' => "center"
									);

				$otherSet = array(	'set_id' => $row['set_id'], 
									'set_name' => $row['set_name'],
									'owner_id' => $row['owner_id'],
									'owner_name' => $row['owner_name'],
									'thumb' => outputSimpleImage($imageOptions)
								);

				array_push($otherSets, $otherSet);
			}
			$otherSetsCounter++;
		}
		$t->assign("otherSets", $otherSets);
	}
	// Free the result
	$res->free();

	//---------------------------------
	// Query DB for a list of the user's sets
	//---------------------------------
	$sql = 	"	SELECT 
					sets.id as set_id, 
					sets.name as set_name, 
					sets.creation_time, 
					tFirstSetObject.img_path, 
					tFirstSetObject.img_ar,
					tMySetsWithObject.contains_obj,
					tTotal_objects.total_objects
				FROM sets
				LEFT OUTER JOIN 
				(SELECT set_id, count(*) total_objects
				FROM set_objs
				GROUP BY set_id) tTotal_objects
				ON tTotal_objects.set_id = sets.id
				LEFT JOIN (SELECT set_objs.set_id, objects.img_path, objects.img_ar
							FROM set_objs
							LEFT JOIN objects
							ON set_objs.obj_id = objects.id
							WHERE set_objs.order_num = 1) tFirstSetObject
				ON tFirstSetObject.set_id = sets.id
				LEFT JOIN (SELECT set_objs.set_id, objects.id as contains_obj
							FROM set_objs
							LEFT JOIN objects
							ON set_objs.obj_id = objects.id
							WHERE set_objs.obj_id = ".$objId.") tMySetsWithObject
				ON tMySetsWithObject.set_id = sets.id
				WHERE sets.owner_id = ".$_SESSION['id']."
				ORDER BY sets.id ASC
			";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}

	// If nothing is found, set personalSets variable to false
	if ( $res->numRows() < 1 ){
		$t->assign("personalSets", false);
	} else {
		$t->assign("personalSets", true);

		$personalSets = array();
		while ($row = $res->fetchRow()) {
			if(!$row['total_objects'] > 0){
				$setHasObjects = false;
				$img_ar = "1.065";
				$img_path = "noSetObjects";
				$total_objects = 0;
			} else {
				$setHasObjects = true;
				$img_ar = $row['img_ar'];
				$img_path = $row['img_path'];
				$total_objects = $row['total_objects'];
			}

			$imageOptions = array(	'img_path' => $img_path,
									'size' => 50,
									'img_ar' => $img_ar,
									'linkURL' => $CFG->shortbase."/set/".$row['set_id'],
									'vAlign' => "center",
									'hAlign' => "center"
								);

			$personalSet = array('set_id' => $row['set_id'], 
								'set_name' => $row['set_name'],
								'contains_obj' => $row['contains_obj'],
								'thumb' => outputSimpleImage($imageOptions),
								'setHasObjects' => $setHasObjects,
								'$total_object' => $total_objects
							);
			array_push($personalSets, $personalSet);

		}
		$t->assign("personalSets", $personalSets);

	}
	// Free the result
	$res->free();


	//------------------------------------------------------------------------------
	// Query DB for a list of tags the user has applied to this object
	//------------------------------------------------------------------------------

	$sql = 	"	SELECT tag_user_object.tag_id, tags.tag_name
				FROM tag_user_object
				JOIN tags		
				ON tags.tag_id = tag_user_object.tag_id
				WHERE tag_user_id = ".$_SESSION['id']." AND tag_object_id = $objId
				ORDER BY tag_user_object.tag_id
			";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}

	// If nothing is found, set personalSets variable to false
	if ( $res->numRows() < 1 ){
		$t->assign("objectTags", false);
	} else {
		$t->assign("objectTags", true);

		$tags = array();
		while ($row = $res->fetchRow()) {
			$tag = array(	'tag_id' => $row['tag_id'], 
							'tag_name' => $row['tag_name']
							);
			array_push($tags, $tag);

		}
		$t->assign("tags", $tags);

	}
	// Free the result
	$res->free();
}


$t->assign("templateVarsJSON", json_encode($t->_tpl_vars));
// Display template
$t->display('details.tpl');

?>
