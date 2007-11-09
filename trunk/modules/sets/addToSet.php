<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
/*

Handles AJAX calls to remove OR add an object to a set. 

Accepts POST data. 
$_POST['set_id'] -- The ID number of the set involved in the action
$_POST['oid'] -- The ID number of the object involved in the action
$_POST['action'] -- The action to perform. Either "add" or "remove"

*/

if( isset($_POST['set_id']) && isset($_POST['oid']) && isset($_POST['action'])){
	$set_id = $_POST['set_id'];
	$obj_id = $_POST['oid'];
	$response = array();
	// Make sure this set belong to the user

	if ( $_POST['action'] == "add" ) {		
			// Get order value so new item can be added in the last place
			$sql = 	"	SELECT * FROM set_objs
						WHERE set_id = $set_id
						ORDER BY order_num DESC
						LIMIT 1
					";
			$res =& $db->query($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
			$order = $res->fetchRow();
			$order = $order['order_num'] + 1;
			
			// add object to the set
			$sql = 	"	INSERT INTO set_objs 
						(`set_id`, `obj_id`, `order_num`) 
						VALUES 
						($set_id, $obj_id, $order)
					";

			$res =& $db->exec($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
			
			// If order = 1, then this is the only object in the set. 
			// Get the thumb and send it with thr response.
			if($order == 1){
				$sql = 	"	SELECT img_path, img_ar FROM objects
							WHERE objects.id = $obj_id
							LIMIT 1
						";
				$res =& $db->query($sql);
				if (PEAR::isError($res)) {die($res->getMessage());}
				while($row = $res->fetchRow()){
					$imageOptions = array(	'img_path' => $row['img_path'],
											'size' => 50,
											'img_ar' => $row['img_ar'],
											'linkURL' => $CFG->shortbase."/set/".$set_id,
											'vAlign' => "center",
											'hAlign' => "center"
										);
					$response['updateThumb'] = true;
					$response['thumbDiv'] = outputSimpleImage($imageOptions);
					$response['set_id'] = $set_id;
				}
			} else {
				$response['updateThumb'] = false;
			}			
	} else if ( $_POST['action'] == "remove" ){
		// Get the object's order within the set
		$sql = 	"	SELECT order_num FROM set_objs
					WHERE set_id = $set_id AND obj_id = $obj_id
					LIMIT 1
				";
		$res =& $db->query($sql);
		if (PEAR::isError($res)) {die($res->getMessage());}
		$order = $res->fetchRow();
		$order = $order['order_num'];
		
		// Delete the offending object
		$sql = "DELETE FROM set_objs WHERE set_id = $set_id AND obj_id = $obj_id";		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {die($res->getMessage());}

		// Get the total number of objects in the set after the delete
		$sql = "SELECT count(*) as count FROM set_objs WHERE set_id = $set_id";
		$res =& $db->query($sql);
		if (PEAR::isError($res)) {die($res->getMessage());}
		$count = $res->fetchRow();
		$count = $count['count'];
		
		
		if($count == 0){ // If there are no more objects in the set, show the "no objects" image
			$imageOptions = array(	'img_path' => "noSetObjects",
									'size' => 50,
									'img_ar' => "1.065",
									'linkURL' => $CFG->shortbase."/set/".$set_id,
									'vAlign' => "center",
									'hAlign' => "center"
								);
			$response['updateThumb'] = true;
			$response['thumbDiv'] = outputSimpleImage($imageOptions);
			$response['set_id'] = $set_id;
		} elseif($order == 1) { // If the removed object was first in the set, get the new set thumb
			$sql = 	"	SELECT order_num, set_objs.set_id, objects.id, objects.img_path, objects.img_ar FROM set_objs
						JOIN objects
						ON objects.id = set_objs.obj_id
						WHERE set_objs.order_num= 2 AND set_objs.set_id = $set_id
						LIMIT 1
					";
			$res =& $db->query($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
			while($row = $res->fetchRow()){
				$imageOptions = array(	'img_path' => $row['img_path'],
										'size' => 50,
										'img_ar' => $row['img_ar'],
										'linkURL' => $CFG->shortbase."/set/".$row['set_id'],
										'vAlign' => "center",
										'hAlign' => "center"
									);
				$response['updateThumb'] = true;
				$response['thumbDiv'] = outputSimpleImage($imageOptions);
				$response['set_id'] = $set_id;
			}
		} else {
			$response['updateThumb'] = false;
		}
		
		
		// If the order of the deleted object is less than the current number of objects in the set (plus 1)
		// then we need to update the order column for all the objects in the set between the object
		// ordered just after the deleted object, through to the last object.		
		if($order < $count+1){
			// Get all the objects that need new orders
			$sql = 	"	SELECT * FROM set_objs
						WHERE set_id = $set_id AND order_num > $order
						ORDER BY order_num ASC
					";
			$res =& $db->query($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
		
			// Generate all the update statements necessary and execute them
			while ($row = $res->fetchRow()) {
				$sql = "UPDATE set_objs SET order_num = $order WHERE set_id = ".$row['set_id']." AND obj_id = ".$row['obj_id'];
				$res2 =& $db->exec($sql);
				if (PEAR::isError($res2)) {die($res2->getMessage());}
				$order++;
			}
		}
		
	}
	
	echo json_encode($response); 
}
?>
