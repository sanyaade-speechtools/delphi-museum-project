<?php

require_once("../../libs/env.php");

// Print debug option:
$DEBUG = 0;

//Default user id used for tagging until we integrate with logining:
$uid = 1;  
print "userId = $uid<br/>"; 

$problem_msg = "We are sorry, the system cannot perform the requested action at this time.";

if( isset( $_GET['tag_id'] ) ) {
  $tag_id = $_GET['tag_id'];
 }else{
  if($DEBUG){print $problem_msg;}
 }
if( isset( $_GET['tag_name'] ) ) {
  $tag_name = $_GET['tag_name'];
  $t->assign('tag_name', $tag_name);
 }else{
  if($DEBUG){print $problem_msg;}
 }

print "<br/>* User's objects tagged with <b>\"$tag_name\"</b>: <br/>";

$uid_objects = findObjects($tag_id, $uid, 0, $db);
foreach($uid_objects as $obj){
  print $obj['name'] . ", " . $obj['img_path'] . "<br/>";
}

$t->assign('uid_objects', $uid_objects);

print "<br/>* Other user's objects tagged with <b>\"$tag_name\"</b>: <br/>";

$other_objects = findObjects($tag_id, $uid, 1, $db);
foreach($other_objects as $obj){
  print $obj['name'] . ", " . $obj['img_path'] . "<br/>";
}

$t->assign('other_objects', $other_objects);

// Find  objects tagged by this tag by the *current* user when $others == 0
// and by others when $others == 1 :

function findObjects($tag_id, $uid, $others, $db){

  if ($others != 1){
    $obj_sql = "SELECT DISTINCT tag_object_id FROM tag_user_object "
      . "WHERE tag_id = \"$tag_id\" AND tag_user_id = \"$uid\" ";
  }else{
    $obj_sql = "SELECT DISTINCT tag_object_id "
      . "FROM tag_user_object WHERE tag_object_id NOT IN "
      . "(SELECT tag_object_id FROM tag_user_object "
      . "WHERE tag_user_id = \"$uid\" AND tag_id = \"$tag_id\")";
  }


  $obj_res =& $db->query($obj_sql);

  if (PEAR::isError($obj_res)) {
    die($obj_res->getMessage());
  }

  // Array that contains objects tagged with this tag:
  $objects = array();

  // If nothing is found...
  // This should never happen because the tag links 
  // are provided on the details.php for the existing active tags:
  if ( $obj_res->numRows() < 1  ){
    $no_tags_msg = "The \"search\" functionality is experiencing issues.";
    $t->assign('no_tags_msg', $no_tags_msg);
    die;
  }
  
  while ($obj_row = $obj_res->fetchRow()) {
    // Find thumbnail for this object:
    $oid = $obj_row['tag_object_id'];
    
    // Find the thumbnail image path for an object:
    $img_sql = "SELECT * FROM objects o WHERE o.id = $oid LIMIT 1";
    $img_res =& $db->query($img_sql);
    
    // If nothing is found, set to some sort of empty icon
    if ( $img_res->numRows() < 1 ){
      $img_path = "empty.html";
    }

    while ($row = $img_res->fetchRow()) {
      $object = array();
      $object['name'] = $row['name'];
      $object['img_path'] = $row['img_path'];
      $objects[$oid] = $object;
    }

    // Free the result
    $img_res->free();
  }

  return $objects;
}



// Display template
$t->display('tagResults.tpl');


?>
