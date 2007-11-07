<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
/**********************************
GET USER'S SETS
**********************************/

$sql = 	$sql = "SELECT featured_sets.set_id, name, owner_name owner, img_path, aspectR img_ar, tTotal_objects.total_objects
				FROM featured_sets
				
				JOIN 
				(SELECT set_id, count(*) total_objects
				FROM set_objs
				GROUP BY set_id) tTotal_objects
				ON tTotal_objects.set_id = featured_sets.set_id
				
				ORDER BY porder
				";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Error");
	$t->assign('message', "There are no featured sets!");	
	$t->display('error.tpl');
	die;
}

$sets = array();

while ($row = $res->fetchRow()) {
	$imageOptions = array(	'img_path' => $row['img_path'],
							'size' => 118,
							'img_ar' => $row['img_ar'],
							'linkURL' => "/delphi/set/".$row['set_id'],
							'vAlign' => "center",
							'hAlign' => "center"
						);
						
	$set = array(	'set_id' => $row['set_id'], 
					'set_name' => $row['name'], 
					'total_objects' => $row['total_objects'],
					'thumb' => outputSimpleImage($imageOptions)
				);
	
	array_push($sets, $set);
    
}

// Free the result
$res->free();


// Display template
$t->assign('sets', $sets);
$t->display('featuredSets.tpl');

?>




