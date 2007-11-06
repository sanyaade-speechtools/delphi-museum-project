<?

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
/**********************************
GET USER'S SETS
**********************************/

$sql = 	$sql = "SELECT set_id id, name, owner_name owner, img_path, aspectR img_ar 
				FROM featured_sets 
				ORDER BY porder
				";
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
	$imageOptions = array(	'img_path' => $row['thumb_path'],
							'size' => 118,
							'img_ar' => $row['img_ar'],
							'linkURL' => "/delphi/set/".$row['id'],
							'vAlign' => "center",
							'hAlign' => "center"
						);
						
	$set = array(	'set_id' => $row['id'], 
					'set_name' => $row['name'], 
					'total_objects' => $row['total_objects'],
					'thumb' => outputSimpleImage($imageOptions)
				);
	
	array_push($sets, $set);
    
}

// Free the result
$res->free();


// Display template
$t->assign('sets', $sets);
$t->display('featuredSets.tpl');

?>




