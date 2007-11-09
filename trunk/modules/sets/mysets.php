<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
/**********************************
GET USER'S SETS
**********************************/

$sql = 	"	SELECT sets.id, sets.name, sets.creation_time, tTotal_objects.total_objects, tImage_paths.thumb_path, tImage_paths.img_ar

			FROM sets

			LEFT OUTER JOIN 
			(SELECT set_id, count(*) total_objects
			FROM set_objs
			GROUP BY set_id) tTotal_objects
			ON tTotal_objects.set_id = sets.id

			LEFT OUTER JOIN
			(SELECT set_objs.set_id, objects.img_path as thumb_path, objects.img_ar
			FROM set_objs
			LEFT JOIN objects
			ON set_objs.obj_id = objects.id
			GROUP BY set_id
			ORDER BY set_objs.order_num ASC) tImage_paths
			ON tImage_paths.set_id = sets.id

			WHERE sets.owner_id = ".$_SESSION['id']."
			ORDER BY sets.creation_time DESC";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Error");
	$t->assign('message', "You don't have any sets!");	
	$t->display('error.tpl');
	die;
}

$sets = array();

while ($row = $res->fetchRow()) {
	
	if(!$row['total_objects'] > 0){
		$setHasObjects = false;
		$img_ar = "1.065";
		$img_path = "noSetObjects";
		$total_object = 0;
	} else {
		$setHasObjects = true;
		$img_ar = $row['img_ar'];
		$img_path = $row['thumb_path'];
		$total_object = $row['total_objects'];
	}
	
	
	$imageOptions = array(	'img_path' => $img_path,
							'size' => 118,
							'img_ar' => $img_ar,
							'linkURL' => "/delphi/set/".$row['id'],
							'vAlign' => "center",
							'hAlign' => "center"
						);
	
	$set = array(	'set_id' => $row['id'], 
					'set_name' => $row['name'], 
					'total_objects' => $total_object,
					'thumb' => outputSimpleImage($imageOptions),
					'setHasObjects' => $setHasObjects
				);
	array_push($sets, $set);
    
}

// Free the result
$res->free();


// Display template
$t->assign('sets', $sets);
$t->display('mysets.tpl');

?>






















