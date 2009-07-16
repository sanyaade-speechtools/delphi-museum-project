<?php

/*

Creates a new set and associates it with an owner

Accepts POST data. 
$_POST['owner_id'] -- The ID number user who will own the set

*/

require_once("../../libs/env.php");

$response = array();
$response['error'] = False;
$response['msg'] = array();

if( isset($_POST['owner_id']) ){
	$sql = "INSERT INTO sets
			(`name`, `policy`, `owner_id`)
			VALUES
			(".$db->quote("Untitled set", 'text').", ".$db->quote("private", 'text').", ".$db->quote($_POST['owner_id'], 'integer').")";
	$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
	$set_id = $db->lastInsertId();
	$response['set_id'] = $set_id;
} else {
	$response['error'] = True;
	array_push($response['msg'], "No owner id found in the params.");
}

// print JSON
echo json_encode($response);
?>
