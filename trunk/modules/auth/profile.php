<?php
require_once("../../libs/env.php");
require_once("../common/imgthumb.php");

if(isset($_GET['uid']) && is_numeric($_GET['uid'])){
	$profileId = $_GET['uid'];
} else {
	$t->assign('heading', "No such user");
	$t->assign('message', "We could not find any users in the system that matched your query.");
	$t->display('error.tpl');
	die();
}

/**********************************
Get current user info
**********************************/

$sql = "SELECT * FROM user WHERE id = $profileId LIMIT 1";

$res =& $db->query($sql);
if (PEAR::isError($res)) {die($res->getMessage());}
if ( $res->numRows() < 1 ){
	$t->assign('heading', "No such user");
	$t->assign('message', "We could not find any users in the system that matched your query.");
	$t->display('error.tpl');
	die();
} else {
	$row = $res->fetchRow();

	$t->assign('email', $row['email']);
	$t->assign('real_name', $row['real_name']);
	$t->assign('website_url', $row['website_url']);
	$t->assign('about', $row['about']);
	$t->assign('creation_time', $row['creation_time']);
	$t->assign('username', $row['username']);

	// Check if this user is the current user
	if( $profileId == $_SESSION['id']){
		$t->assign('ownProfile', true);
	}


	/**********************************
	GET USER'S SETS
	**********************************/

	$sql = 	"	SELECT sets.id, sets.name, sets.creation_time, tTotal_objects.total_objects, tImage_paths.thumb_path, tImage_paths.img_ar

				FROM sets

				LEFT OUTER JOIN 
				(SELECT set_id, count(*) total_objects
				FROM set_objs
				GROUP BY set_id) tTotal_objects
				ON tTotal_objects.set_id = sets.id

				LEFT OUTER JOIN
				(SELECT set_objs.set_id, objects.img_path as thumb_path, objects.img_ar
				FROM set_objs
				LEFT JOIN objects
				ON set_objs.obj_id = objects.id
				GROUP BY set_id
				ORDER BY set_objs.order ASC) tImage_paths
				ON tImage_paths.set_id = sets.id

				WHERE sets.owner_id = $profileId AND sets.policy = 'public'
				ORDER BY sets.creation_time DESC";
	$res =& $db->query($sql);
	if (PEAR::isError($res)) {
	    die($res->getMessage());
	}

	// If nothing is found, send to object not found.
	if ( $res->numRows() < 1 ){
		$t->assign('sets', false);
	} else {
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

		$t->assign('sets', $sets);
	}
	// Free the result
	$res->free();
	$t->display('profile.tpl');
}
?>
