<?php

/*

Updates the order values of objects within a set.

Accepts POST data. 
$_POST['jsonRequest'] -- A JSON object of an associative array with two keyed elements. 
						 The two keys are 1) 'set_id' and 2) 'items'. set_id should be an integer
						 and items should be an array of object ids. 

*/

require_once("../../libs/env.php");
$response = array();
$response['error'] = False;
$response['msg'] = array();

if(! isset($_POST['jsonRequest']) ) {
	$response['error'] = True;
	array_push($response['msg'], "No param in the post vars.");
} else {
	$jsonRequest = json_decode($_POST['jsonRequest'], true);
	if(!$jsonRequest){
		$response['error'] = True;
		array_push($response['msg'], "Error decoding JSON: \n\n $jsonRequest");
	} else if( count($jsonRequest['items']) < 1) {
		$response['error'] = True;
		array_push($response['msg'], "No items were passed.");
	} else {
		// Loop through the items and update each object order
		$order = 1;
		foreach ($jsonRequest['items'] as $item){
			$sql = "UPDATE set_objs SET order_num = $order WHERE set_id = ".$jsonRequest['set_id']." AND obj_id = $item";
			$res =& $db->exec($sql);
			if(PEAR::isError($res)){
				$response['error'] = True;
				array_push($response['msg'], "Error updating database.");
			}
			$order++;			
		}
		$response['items'] = $jsonRequest['items'];
	}
}

// print JSON
echo json_encode($response);

?>
