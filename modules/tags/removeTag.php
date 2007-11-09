<?

require_once("../../libs/env.php");
/*

Handles AJAX calls to remove a tag to an object

Accepts POST data. 
$_POST['tag_id'] -- text entered in the tab input box
$_POST['obj_id'] -- The ID number of the object involved in the action

outputs JSON serialization of $resonse

*/
$response = array(	"success" => false, 
					"msg" => "Did not receive the right vars"
				);

if( isset($_SESSION['id']) && isset($_POST['tag_id']) && isset($_POST['obj_id']) ){
	$tag_id = $_POST['tag_id'];
	$obj_id = $_POST['obj_id'];
	$uid = $_SESSION['id'];
	
	// Dissassociate the tag from the user
	$sql = "DELETE FROM tag_user_object WHERE tag_id = $tag_id AND tag_user_id = $uid AND tag_object_id = $obj_id";
	$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
		
	// Decrement the tag count in tags
	$sql = "UPDATE tags SET tag_count = tag_count-1 WHERE tag_id = $tag_id";
	$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
	
	$response['success'] = true;
	$response['msg'] = "Tag successfully removed.";
}

echo json_encode($response);

?>
