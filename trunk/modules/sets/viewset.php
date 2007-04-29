<?

require_once("../../libs/env.php");

// If there is no id param in the url, send to object not found.
if( isset( $_GET['sid'] ) ) {
	$setId = $_GET['sid'];
} else {
	$t->display('objectNotFound.tpl');
	die;
}

/**********************************
FETCH SET DETAILS
***********************************/

// Query DB
$sql = 	"	SELECT sets.id, sets.name, sets.description, user.username
			FROM sets
			LEFT JOIN user
			ON sets.owner_id = user.id
			WHERE sets.id = $setId
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

// Assign vars to template
while ($row = $res->fetchRow()) {
    $t->assign('setId', $row['id']);
    $t->assign('setName', $row['name']);
    $t->assign('setDescription', $row['description']);
    $t->assign('username', $row['username']);
}

// Free the result
$res->free();

/**********************************
FETCH SET OBJECTS
***********************************/

// Query DB

$sql =	"	SELECT objects.id, objects.name, objects.description, objects.img_path
			FROM set_objs 
			LEFT JOIN objects
			ON objects.id = set_objs.obj_id
			WHERE set_objs.set_id = $setId
		";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

$objects = array();

while ($row = $res->fetchRow()) {
	
	$object = array(	'id' => $row['id'], 
						'name' => $row['name'],
						'description' => $row['description'],
						'img_path' => $row['img_path']
					);
	
	array_push($objects, $object);
    
}

$res->free();

/**********************************
DISPLAY TEMPLATE
***********************************/

//print_r($objects);
$t->assign('detail_img_path', $objects[0]['img_path']); //used for displaying the first object
$t->assign('detail_name', $objects[0]['name']); //used for displaying the first object
$t->assign('detail_description', $objects[0]['description']); //used for displaying the first object
$t->assign('detail_id', $objects[0]['id']); //used for displaying the first object
$t->assign('objects', $objects); 
$t->assign('objectCount', $res->numRows());
$t->display('viewset.tpl');

?>







