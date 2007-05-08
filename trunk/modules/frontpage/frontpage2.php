<?

require_once("../../libs/env.php");
/*
// Query DB
$sql = "SELECT o.id, o.objnum, o.name, o.img_path FROM objects o, set_objs so 
WHERE so.set_id=1 AND so.obj_id=o.id ORDER BY RAND() LIMIT 12";


$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}



$objects = array();

while ($row = $res->fetchRow()) {
	
	$object = array(	'id' => $row['id'], 
						'img_path' => $row['img_path'], 
						'name' => $row['name'], 
						'objnum' => $row['objnum']
					);
	
	array_push($objects, $object);
    
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);
*/
// Display template
$t->display('frontpage/frontpage2.tpl');

//Die here or additional PHP lines will be excecuted.
die();


?>
