<?php

require_once("../../libs/env.php");

// If there is no id param in the url, send to object not found.
if( isset($_POST['obj_id']) && isset($_POST['set_id'])) {
	$obj_id = $_POST['obj_id'];
	$set_id = $_POST['set_id'];
} else {
	$t->assign('heading', "Error");
	$t->assign('message', "Did get the right vars to complete the request.");
	$t->display('error.tpl');
	die;
}

// Query DB
$sql = "SELECT objects.id, objects.objnum, objects.img_path, set_objs.order_num, set_objs.name, set_objs.notes FROM objects
		LEFT JOIN set_objs
		ON objects.id = set_objs.obj_id
		WHERE objects.id = $obj_id AND set_objs.set_id = $set_id
		LIMIT 1";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Error");
	$t->assign('message', "Did not find the object you were looking for.");
	$t->display('error.tpl');
	die;
}


// Assign vars to resonse
$response = array();
while ($row = $res->fetchRow()) {
    $response['obj_id'] = $row['id'];
    $response['obj_num'] = $row['objnum'];
    $response['objectName'] = $row['name'];
	$response['objectDescription'] = $row['notes'];
	$response['obj_order'] = $row['order_num'];
	$response['obj_img'] = $row['img_path'];
	// HACK to accommodate the current zoomer tool
	// dirs for filenames have spaces replaced with underscores for ZOOMs
	$lastSlash = strrpos($row['img_path'], "/")+1;
	if( $lastSlash === false ) {
		$path = substr($row['img_path'], 0, -4);
	} else {
		// Use up to slash, plus filename without extension where space converted to _.
		$path = substr($row['img_path'], 0, $lastSlash) 
							. str_replace(" ", "_", substr($row['img_path'], $lastSlash, -4));
	}
	$response['obj_zoomDir'] = $path; // trims off .jpg
}

// Free the result
$res->free();

// print JSON
echo json_encode($response);

?>
