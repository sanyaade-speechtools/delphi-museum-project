<?php

require_once("../../libs/env.php");

// If there is no id param in the url, send to object not found.
if( isset( $_GET['sid'] ) ) {
	$setId = $_GET['sid'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

/**********************************
FETCH SET DETAILS
***********************************/

// Query DB
$sql = 	"	SELECT sets.id, sets.name, sets.description, user.username
			FROM sets
			LEFT JOIN user
			ON sets.owner_id = user.id
			WHERE sets.id = $setId
		";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('setId', $row['id']);
    $t->assign('setName', $row['name']);
    $t->assign('setDescription', $row['description']);
    $t->assign('username', $row['username']);
}

// Free the result
$res->free();

/**********************************
FETCH SET OBJECTS
***********************************/

// Query DB

$sql =	"	SELECT objects.id, objects.name, objects.description, objects.img_path
			FROM set_objs 
			LEFT JOIN objects
			ON objects.id = set_objs.obj_id
			WHERE set_objs.set_id = $setId
		";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

$objects = array();

while ($row = $res->fetchRow()) {
	
	$object = array(	'id' => $row['id'], 
						'name' => $row['name'],
						'description' => $row['description'],
						'img_path' => $row['img_path']
					);
	
	array_push($objects, $object);
    
}

$res->free();

/**********************************
FETCH FIRST SET OBJECT
***********************************/

// Query DB
$sql = "SELECT * FROM objects o WHERE o.id = " . $objects[0]['id'] . " LIMIT 1";
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
    $t->assign('detail_id', $row['id']);
    $t->assign('detail_objnum', $row['objnum']);
    $t->assign('detail_name', $row['name']);
	$t->assign('detail_description', $row['description']);
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



/**********************************
DISPLAY TEMPLATE
***********************************/

//print_r($objects);
$t->assign('objects', $objects); 
$t->assign('objectCount', $res->numRows());
$t->display('viewset.tpl');

?>







