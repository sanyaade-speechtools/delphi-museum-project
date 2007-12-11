<?php

/*

Updates the name and notes field for a give object in a set.

Accepts POST data. 
$_POST['obj_id'] -- The ID number of the object
$_POST['set_id'] -- The ID number of the set containing the object
$_POST['objectName'] -- The new object name
$_POST['objectDesc'] -- The new object notes (legacy name = description)

*/

require_once("../../libs/env.php");
require_once("../../libs/utils.php");

$response = array();
$response['error'] = False;
$response['msg'] = array();

// If the right POST params are not present, do nothing
if( isset($_POST['objectName']) && isset($_POST['objectDesc']) && isset($_POST['set_id']) && isset($_POST['obj_id'])) {
	
	$_POST['objectNameobjectName'] = cleanFormData($_POST['objectName']);
	$_POST['objectDesc'] = cleanFormData($_POST['objectDesc']);
	
	// Query DB
	$sql = "UPDATE set_objs SET notes = ".$db->quote($_POST['objectDesc'], 'text').", name = ".$db->quote($_POST['objectName'], 'text')." WHERE set_id = ".$db->quote($_POST['set_id'], 'integer')." AND obj_id = ".$db->quote($_POST['obj_id'], 'integer')." LIMIT 1";

	$res =& $db->exec($sql);
	if (PEAR::isError($res)) {
		$response['error'] = True;
		array_push($response['msg'], $res->getMessage());
	} else {
		$response['objectName'] = $_POST['objectName'];
		$response['objectDesc'] = $_POST['objectDesc'];
	}
} else {
	$response['error'] = True;
	array_push($response['msg'], "Invalid params in the post vars.");
}

// print JSON
echo json_encode($response);
?>
