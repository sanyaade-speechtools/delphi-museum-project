<?

require_once("../../libs/env.php");

// If the form is being submitted, handle it
if ( isset( $_POST['submitted'] ) ) {

	// if newSetName is not "", create a new set and add object to the new set	
	if ($_POST['newSetName'] != "") {
		//create a new set
		$name = addslashes($_POST['newSetName']);
		$obj_id = $_POST['obj_id'];
		$owner_id = $_SESSION['id'];
		
		$sql = "	INSERT INTO sets 
					(name, policy, owner_id, creation_time) 
					VALUES 
					('$name', 'private', '$owner_id', now() )";
		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
		
		// add object to the new set
		$set_id =& $db->lastInsertID('sets');
		$sql = "	INSERT INTO set_objs 
					(`set_id`, `obj_id`, `order`) 
					VALUES 
					($set_id, $obj_id, '1')";
		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
	
	// else add object to selected set in SetList
	} else {
		$set_id = $_POST['setList'];
		$obj_id = $_POST['obj_id'];
		
		// Get order value so new item can be added in the last place
		$sql = 	"	SELECT * FROM set_objs
					WHERE set_id = '$set_id'
					ORDER BY `order` DESC
					LIMIT 1
				";
		$res =& $db->query($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
		$order = $res->fetchRow();
		$order = $order['order'] + 1;
		
		// add object to the set
		$sql = 	"	INSERT INTO set_objs 
					(`set_id`, `obj_id`, `order`) 
					VALUES 
					($set_id, $obj_id, $order)
				";
		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
	}
	
	
// else display the add to set form
} elseif ( isset( $_GET['oid'] ) ) {
	$objId = $_GET['oid'];
	
	
	// Get the object name and thumbnail
	$sql = "SELECT name, img_path FROM objects WHERE id = $objId LIMIT 1";
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
	$objDetails = $res->fetchRow();
	$res->free();
	
	$t->assign('obj_name', $objDetails['name']);
	$t->assign('obj_img_path', $objDetails['img_path']);
	$t->assign('obj_id', $objId);
	
	
	// Get a list of the user's sets
	$sql = "SELECT name, id FROM sets WHERE owner_id = '" . $_SESSION['id'] . "'";
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
	$sets = array();
	while ($row = $res->fetchRow()) {
		$set = array(		'id' => $row['id'], 
							'name' => $row['name']
					);
		array_push($sets, $set);
	}
	
	$res->free();
	$t->assign('sets', $sets);
	
	// Display template
	$t->display('addToSet.tpl');
	
} else {
	$t->assign('heading', "Error");
	$t->assign('message', "There was no object id in the GET");	
	$t->display('error.tpl');
	die;
}


?>
