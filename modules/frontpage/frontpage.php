<?

require_once("../../libs/env.php");

// Query DB
$sql = "	SELECT o.id, o.objnum, o.name, o.thumb_path 
			FROM objects o, categories c, obj_cats oc 
			WHERE o.id=oc.obj_id and c.id=oc.cat_id and c.name='gray' 
			LIMIT 12;
		";

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
						'thumb_path' => $row['thumb_path'], 
						'name' => $row['name'], 
						'objnum' => $row['objnum']
					);
	
	array_push($objects, $object);
    
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);

// Display template
$t->display('frontpage.tpl');

?>