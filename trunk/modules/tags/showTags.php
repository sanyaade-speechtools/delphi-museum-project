<?php

require_once("../../libs/env.php");
require_once("../common/imgthumb.php");
/**********************************
GET USER'S or ANYONE's TAGS, WEIGHTED BY USAGE
**********************************/
// If there is no id param in the url, send to object not found.
if( isset( $_GET['user'] ) && ("any"==$_GET['user'] )) {
	$anyUser = true;
	$limitClause = "LIMIT 150";
} else if(empty($_SESSION['id'])) {  // User must be logged in
	header( 'Location: ' . $CFG->wwwroot . '/modules/auth/login.php?redir=' . XXX CURR LOC );
	die();
} else {
	$anyUser = false;
	$limitClause = "";
}

$t->assign('page_title', ($anyUser?'Community Tags - ':'My Tags - ').$CFG->page_title_default);
$t->assign('user_mode', ($anyUser?'any':'curr'));

if($anyUser) {
	$sql = 	"SELECT tag_id id, tag_name name, tag_count tcount FROM tags WHERE tag_count>0"
	 				." ORDER BY name LIMIT 150";
} else {
	$sql = 	"SELECT tuo.tag_id id, t.tag_name name, count(*) tcount FROM tags t, tag_user_object tuo"
		." WHERE tuo.tag_id=t.tag_id AND tuo.tag_user_id=".$_SESSION['id']
		." GROUP BY tuo.tag_id ORDER BY name";
}

$res =& $db->query($sql);
if (PEAR::isError($res)) {
	$t->assign('heading', "Whoops!");
	$t->assign('message', "Internal error getting tag information!\nPlease report to Delphi feedback"
												.$res->getMessage());
	$t->display('error.tpl');
  die();
}

// If nothing is found, send to object not found.
if ( $res->numRows() < 1 ){
	$t->assign('heading', "Nothing to show");
	$t->assign('message', "You don't have any tags!");	
	$t->display('error.tpl');
	die;
}

$tags = array();
$countMin = 9999999;
$countMax = 0;

while ($row = $res->fetchRow()) {
	$count = $row['tcount'];
	$tagInfo = array(	'id' => $row['id'],
										'name' => $row['name'],
										'count' => $count,
										'steps' => 0 );
	if($count>$countMax)
		$countMax = $count;
	if($count<$countMin)
		$countMin = $count;

	array_push($tags, $tagInfo);
}
$range = $countMax-$countMin;

foreach( $tags as &$tagInfo ) {
	$tagInfo['steps'] = ($range==0)?0:(10*($tagInfo['count']-$countMin)/$range);
}

// Free the result
$res->free();

// Display template
$t->assign('tags', $tags);
$t->assign("templateVarsJSON", json_encode($t->_tpl_vars));
$t->display('showtags.tpl');

?>

