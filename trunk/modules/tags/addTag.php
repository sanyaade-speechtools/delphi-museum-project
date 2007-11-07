<?

require_once("../../libs/env.php");
/*

Handles AJAX calls to Add a tag to an object

Accepts POST data. 
$_POST['tagInput'] -- text entered in the tab input box
$_POST['obj_id'] -- The ID number of the object involved in the action

outputs JSON serialization of $resonse

*/
$response = array(	"success" => false, 
					"msg" => "Did not receive the right vars", 
					"tag_id" => 0, 
					"tag_name" => ""
				);

if( isset($_POST['tagInput']) && isset($_POST['obj_id']) ){
	$tagInput = $_POST['tagInput'];
	$obj_id = $_POST['obj_id'];

	// Check to see if the tag is already in the tags table
	$sql = "SELECT * FROM tags WHERE tag_name = ".$db->quote($tagInput, 'text')." LIMIT 1";
	$tagExistCheck =& $db->query($sql); if(PEAR::isError($tagExistCheck)) {die($tagExistCheck->getMessage());}
	if($tagExistCheck->numRows() > 0){
		// If so, check to see if the user has already applied this tag to the object
		$tagExistCheckRow = $tagExistCheck->fetchRow();
		$sql = "SELECT * FROM tag_user_object WHERE 
				tag_id = ".$tagExistCheckRow['tag_id']." AND 
				tag_user_id = ".$_SESSION['id']." AND
				tag_object_id = $obj_id
				LIMIT 1";
		$dupeCheck =& $db->query($sql); 
		if(PEAR::isError($dupeCheck)) {die($dupeCheck->getMessage());}
		if($dupeCheck->numRows() > 0){
			// The user has already applied this tag to this object
			$response['success'] = false;
			$response['msg'] = "You already added this tag to this object.";
		} else {
			// The tag exists, but the user had not associated it with this object
			$sql = "INSERT INTO tag_user_object
					(`tag_id`, `tag_user_id`, `tag_object_id`)
					VALUES
					(".$tagExistCheckRow['tag_id'].",".$_SESSION['id'].",$obj_id)";
			$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
			// Increment tag count
			$sql = "UPDATE tags SET tag_count = tag_count+1 WHERE tag_id = ".$tagExistCheckRow['tag_id'];
			$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
			$response['success'] = true;
			$response['msg'] = "Tag successfully added.";
			$response['tag_id'] = $tagExistCheckRow['tag_id'];
			$response['tag_name'] = stripslashes($tagInput);
		}
	} else {
		// The tag was not in the tags table, so add it to the tags table and associate it with the user
		$sql = "INSERT INTO tags
				(`tag_name`, `tag_count`)
				VALUES
				(".$db->quote($tagInput, 'text').", 1)";
		$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
		$tag_id = $db->lastInsertId();
		$response['success'] = true;
		$response['msg'] = "Tag successfully added.";
		$response['tag_id'] = $tag_id;
		$response['tag_name'] = stripslashes($tagInput);
		$sql = "INSERT INTO tag_user_object
				(`tag_id`, `tag_user_id`, `tag_object_id`)
				VALUES
				($tag_id,".$_SESSION['id'].",$obj_id)";
		$res =& $db->exec($sql); if(PEAR::isError($res)) {die($res->getMessage());}
	}
}

echo json_encode($response);

?>
