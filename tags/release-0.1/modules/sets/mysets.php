<?

require_once("../../libs/env.php");

/**********************************
GET USER'S SETS
**********************************/

$sql = 	"	SELECT sets.id, sets.name, sets.description, tTotal_objects.total_objects, tImage_paths.thumb_path

			FROM sets

			LEFT OUTER JOIN 
			(SELECT set_id, count(*) total_objects
			FROM set_objs
			GROUP BY set_id) tTotal_objects
			ON tTotal_objects.set_id = sets.id

			LEFT OUTER JOIN
			(SELECT set_objs.set_id, objects.img_path as thumb_path
			FROM set_objs
			LEFT JOIN objects
			ON set_objs.obj_id = objects.id
			GROUP BY set_id
			ORDER BY set_objs.order ASC) tImage_paths
			ON tImage_paths.set_id = sets.id

			WHERE sets.owner_id = '" . $_SESSION['id'] . "'";

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
					'setDescription' => $row['description'],
					'total_objects' => $row['total_objects'],
					'thumb_path' => $row['thumb_path']
				);
	
	array_push($sets, $set);
    
}

// Free the result
$res->free();


// Display template
$t->assign('sets', $sets);
$t->display('mysets.tpl');

?>






















