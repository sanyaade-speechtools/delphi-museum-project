<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");

// Set up the greeting. If user is logged in and has a history, then show news.
$useDefault = true;
if(!empty($_SESSION['id'])) {
	// Check for activity - sets or tags
	$sql = "SELECT count(*) c FROM set_objs so, sets s WHERE so.set_id=s.id AND s.owner_id="
					.$_SESSION['id'];
	$res =& $db->query($sql);
	if(!PEAR::isError($res) && ($res->numRows()==1) && ($row=$res->fetchRow())
			&& ($row['c']>0)) {
		$useDefault = false;
	} else {
		$sql = "SELECT count(*) c FROM tag_user_object WHERE tag_user_id="
						.$_SESSION['id'];
		$res =& $db->query($sql);
		if(!PEAR::isError($res) && ($res->numRows()==1) && ($row=$res->fetchRow())
				&& ($row['c']>0)) {
			$useDefault = false;
		}
	}
	
	if(!$useDefault) {
		// Query DB for first news Item
		$sql = "SELECT header, content FROM newsContent where id=1";
		$res =& $db->query($sql);
		if(!PEAR::isError($res) && ($res->numRows()==1) && ($row=$res->fetchRow())
				&& !empty($row['content'])) {
			$t->assign('greetingHdr', $row['header']);
			$t->assign('greetingTxt', $row['content']);
		} else {
			$useDefault = true;
		}
	}
}

if($useDefault) {
		$t->assign('greetingHdr', "Welcome to Delphi!");
		$t->assign('greetingTxt', "You can explore by location, by how items were used, or even how they were made or decorated. You can save items you like to your own sets and then share these sets with others. To get started, type keywords in the \"Search\" box, or click on \"Browse\" to see the various categories to start from. Or you can explore some of the sets featured to the right or any of the objects below.");
}

// Query DB for featured objects (set #1)
$sql = "SELECT o.id, o.objnum, o.name, o.img_path, o.img_ar FROM objects o, set_objs so
WHERE so.set_id=1 AND so.obj_id=o.id ORDER BY RAND() LIMIT 6";

$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found...  TODO this is not graceful enough
if ( $res->numRows() < 1 ){
	$t->display('objectNotFound.tpl');
	die;
}

$objects = array();

while( $row=$res->fetchRow()) {
	array_push($objects, outputWrappedImage( $row, "front_example", 
							$CFG->wwwroot."/object/", 95 ));
}

// Free the result
$res->free();

// Assign vars to template
$t->assign('objects', $objects);

/**********************************
Get Frontpage sets
**********************************/

$sql = "SELECT featured_sets.set_id, name, owner_name owner, img_path, aspectR img_ar, tTotal_objects.total_objects
				FROM featured_sets
				
				JOIN 
				(SELECT set_id, count(*) total_objects
				FROM set_objs
				GROUP BY set_id) tTotal_objects
				ON tTotal_objects.set_id = featured_sets.set_id
				
				ORDER BY porder
				";
$res =& $db->query($sql);
if (PEAR::isError($res)) {
    die($res->getMessage());
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Error");
	$t->assign('message', "There are no featured sets!");	
	$t->display('error.tpl');
	die;
}

$sets = array();

while ($row = $res->fetchRow()) {
	$imageOptions = array(	'img_path' => $row['img_path'],
							'size' => 118,
							'img_ar' => $row['img_ar'],
							'linkURL' => $CFG->shortbase."/set/".$row['set_id'],
							'vAlign' => "center",
							'hAlign' => "left"
						);
						
	$set = array(	'set_id' => $row['set_id'], 
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
// Display template
$t->display('frontpage/frontpage.tpl');

//Die here or additional PHP lines will be excecuted.
die();
?>
