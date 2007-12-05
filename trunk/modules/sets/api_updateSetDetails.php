<?php

require_once("../../libs/env.php");
require_once("../../libs/utils.php");

$response = array();
$response['error'] = False;
$response['msg'] = array();

// If the right POST params are not present, do nothing
if( isset($_POST['setTitle']) && isset($_POST['setDesc']) && isset($_POST['set_id']) ) {
	
	$_POST['setTitle'] = cleanFormData($_POST['setTitle']);
	$_POST['setDesc'] = cleanFormData($_POST['setDesc']);
	
	// Query DB
	$sql = "UPDATE sets SET description = ".$db->quote($_POST['setDesc'], 'text').", name = ".$db->quote($_POST['setTitle'], 'text')." WHERE id = ".$db->quote($_POST['set_id'], 'integer')." LIMIT 1";

	$res =& $db->exec($sql);
	if (PEAR::isError($res)) {
		$response['error'] = True;
		array_push($response['msg'], "Error updating database.");
	} else {
		$response['setTitle'] = $_POST['setTitle'];
		$response['setDesc'] = $_POST['setDesc'];
	}
} else {
	$response['error'] = True;
	array_push($response['msg'], "Invalid params in the post vars.");
}

// print JSON
echo json_encode($response);
?>
