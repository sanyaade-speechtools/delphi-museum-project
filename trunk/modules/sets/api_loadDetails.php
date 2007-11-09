<?php

require_once("../../libs/env.php");

// If there is no id param in the url, send to object not found.
if( isset($_POST['obj_id']) && isset($_POST['set_id'])) {
	$obj_id = $_POST['obj_id'];
	$set_id = $_POST['set_id'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

// Query DB
$sql = "SELECT objects.*, set_objs.order_num FROM objects
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
	$t->display('objectNotFound.tpl');
	die();
}


// Assign vars to resonse
$response = array();
while ($row = $res->fetchRow()) {
    $response['obj_id'] = $row['id'];
    $response['obj_num'] = $row['objnum'];
    $response['obj_name'] = $row['name'];
	$response['obj_description'] = $row['description'];
	$response['obj_order'] = $row['order'];
	$response['obj_img'] = $row['img_path'];
	$response['obj_zoomDir'] = substr($row['img_path'], 0, -4); // trims off .jpg
}

// Free the result
$res->free();

// print JSON
echo json_encode($response);

?>
