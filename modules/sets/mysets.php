<?

require_once("../../libs/env.php");

/**********************************
GET USER'S SETS
**********************************/


/*
	TODO Add Count of objects within the set
*/
$sql = "SELECT * FROM sets WHERE owner_id = '". $_SESSION['id'] . "'" ;
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Error");
	$t->assign('message', "You don't have any sets!");	
	$t->display('error.tpl');
	die;
}

$sets = array();

while ($row = $res->fetchRow()) {
	
	$set = array(	'sid' => $row['id'], 
					'setName' => $row['name'], 
					'setDescription' => $row['description']
				);
	
	array_push($sets, $set);
    
}

// Free the result
$res->free();


// Display template
$t->assign('sets', $sets);
$t->display('mysets.tpl');

?>






















