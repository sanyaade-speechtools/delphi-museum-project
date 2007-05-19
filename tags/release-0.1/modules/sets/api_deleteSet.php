<?

require_once("../../libs/env.php");


// If there is no sid param in the GET, do nothing
if( isset( $_GET['sid'] ) ) {
	$setId = $_GET['sid'];

	//Get the sets's owner
	$sql = "SELECT owner_id FROM sets WHERE id = '$setId'";

	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}
	
	if ($res->numRows() < 1) {
		$t->assign('heading', "Error");
		$t->assign('message', "The set you tried to delete doesn't exist");	
		$t->display('error.tpl');
		die;
	}
	
	$setOwner = $res->fetchRow();
	$setOwner = $setOwner['owner_id'];

	$res->free();
	
	// If the set's owner is requesting the delete, delete set and set objects
	if ($setOwner == $_SESSION['id']) {
		$sql = "DELETE FROM sets WHERE id = '$setId'";		
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}
		
		$sql = "DELETE FROM set_objs WHERE set_id = '$setId'";
		$res =& $db->exec($sql);
		if (PEAR::isError($res)) {
		    die($res->getMessage());
		}

		//redirect to mysets.php
		header( 'Location: ' . $CFG->wwwroot . '/modules/sets/mysets.php' );
		die();
	} else {
		$t->assign('heading', "Error");
		$t->assign('message', "The set you tried to delete doesn't belong to you!");	
		$t->display('error.tpl');
		die;
	}
	

} else {
	$t->assign('heading', "Error");
	$t->assign('message', "No set specified.");	
	$t->display('error.tpl');
	die;
}





?>