<?

require_once("../../libs/env.php");
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
	
	// Make sure this set belong to the user

	if ( $_POST['action'] == "add" ) {		
			// Get order value so new item can be added in the last place
			$sql = 	"	SELECT * FROM set_objs
						WHERE set_id = $set_id
						ORDER BY `order` DESC
						LIMIT 1
					";
			$res =& $db->query($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
			$order = $res->fetchRow();
			$order = $order['order'] + 1;

			// add object to the set
			$sql = 	"	INSERT INTO set_objs 
						(`set_id`, `obj_id`, `order`) 
						VALUES 
						($set_id, $obj_id, $order)
					";

			$res =& $db->exec($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}

	} else if ( $_POST['action'] == "remove" ){
		// Get the object's order within the set
		$sql = 	"	SELECT `order` FROM set_objs
					WHERE set_id = $set_id AND obj_id = $obj_id
					LIMIT 1
				";
		$res =& $db->query($sql);
		if (PEAR::isError($res)) {die($res->getMessage());}
		$order = $res->fetchRow();
		$order = $order['order'];
		
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
		
		// If the order of the deleted object is less than the current number of objects in the set
		// then we need to update the order column for all the objects in the set between the object
		// ordered just after the deleted object, through to the last object.
		if($order < $count){
			// Get all the objects that need new orders
			$sql = 	"	SELECT * FROM set_objs
						WHERE set_id = $set_id AND `order` > $order
						ORDER BY `order` ASC
					";
			$res =& $db->query($sql);
			if (PEAR::isError($res)) {die($res->getMessage());}
		
			// Generate all the update statements necessary and execute them
			while ($row = $res->fetchRow()) {
				$sql = "UPDATE set_objs SET `order` = $order WHERE set_id = ".$row['set_id']." AND obj_id = ".$row['obj_id'];
				$res2 =& $db->exec($sql);
				if (PEAR::isError($res2)) {die($res2->getMessage());}
				$order++;
			}
		}
	}
}
?>
