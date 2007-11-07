<?php

require_once("../../libs/env.php");


// If there is no id param in the POST, do nothing
if( isset( $_POST['element_id'] ) ) {
	$original_html = $_POST['original_html'];
	$update_value = addslashes($_POST['update_value']);
	
	// element id should be in the form of "setName___0"
	// The trailing digit is the set id.
	$element_id = split("___", $_POST['element_id']);
	$set_id = $element_id[1];
} else {
	die;
}


// Query DB
$sql = "UPDATE sets SET description = '" . $update_value . "' WHERE id = '" . $set_id . "' LIMIT 1";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// Free the result
$res->free();

// Display template
echo stripslashes($update_value . " <span class='viewset_editLink'>(<a href='#'>edit</a>)</span>");
?>
