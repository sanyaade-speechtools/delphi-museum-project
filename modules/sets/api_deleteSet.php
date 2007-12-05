<?php

require_once("../../libs/env.php");

$response = array();
$response['error'] = False;
$response['msg'] = array();

// If there is no sid param in the POST, do nothing
if( isset( $_POST['set_id'] ) ) {
	$setId = $_POST['set_id'];

	//Get the sets's owner
	$sql = "SELECT owner_id FROM sets WHERE id = '$setId'";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
		$response['error'] = True;
		array_push($response['msg'], "Failed to fetch the set from the database.");
	} else {
		if ($res->numRows() < 1) {
			$response['error'] = True;
			array_push($response['msg'], "The set you tried to delete doesn't exist.");
		} else {
			$setOwner = $res->fetchRow();
			$setOwner = $setOwner['owner_id'];
			$res->free();	
		}	
	}
	
	// If the set's owner is requesting the delete, delete set and set objects
	if ($setOwner == $_SESSION['id'] && $response['error'] == False) {
		$sql = "DELETE FROM sets WHERE id = '$setId'";		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
			$response['error'] = True;
			array_push($response['msg'], "Failed to delete the set from the database.");
		}
		
		$sql = "DELETE FROM set_objs WHERE set_id = '$setId'";
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
			$response['error'] = True;
			array_push($response['msg'], "Failed to delete the set objects from the database.");
		}
	} else {
		$response['error'] = True;
		array_push($response['msg'], "The set you tried to delete doesn't belong to you!");
	}
} else {
	$response['error'] = True;
	array_push($response['msg'], "No set found in the params.");
}

// print JSON
echo json_encode($response);

?>
