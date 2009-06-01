<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

if( empty( $_GET['cats'] ) && empty( $_GET['kwds'] )) {
	header("HTTP/1.0 400 Bad Request");
	echo "Bad Arg: No cats or kwds ";
	echo "GET: ";
	print_r( $_GET );
	exit();
}
$onlyWithImgs = true;		// default to only images
if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
	$onlyWithImgs = false;
if( empty($_GET['kwds']) )
	$kwds = null;
else
	$kwds = $_GET['kwds'];
if( empty($_GET['cats']) ) {
	$cats = null;
	$catIDs = array();
} else {
	$cats = $_GET['cats'];
	$catIDs = explode( ",", $cats );
}
$pageNum = 0;
if( !empty( $_GET['page'] ))
	$pageNum = 1*$_GET['page'];
$pageSz = 0;
if( !empty( $_GET['pageSz'] ))
	$pageSz = 1*$_GET['pageSz'];

?>
<HTML>
<BODY>
<?php
$resInfo = queryObjects($catIDs, $kwds, $pageNum, $pageSz, $onlyWithImgs );
if( !$resInfo ) {
	echo "<h2>Query Failed</h2>";
} else if( $resInfo['nObjs'] == 0 ) {
	echo "<h2>Query returned no results!</h2>";
} else {
	echo "<h2>Query:";
	if( !empty($cats) )
		echo " Cats: <em>".$cats."</em>";
	if( !empty($kwds) )
		echo " Kwds: <em>".$kwds."</em>";
	echo "</h2>";
	echo "<h3>Returned ".$resInfo['nObjs']." results.</h2>";
	echo "<p>Page ".($pageNum+1)." of ".$resInfo['nPages']."</p>";
	echo '<table border="1px" cellspacing="0" cellpadding="4px" >
	';
	echo "<tr><td>ID</td><td>Objnum</td><td>Name</td><td>Description</td></tr>
	";
	foreach( $resInfo['objects'] as $obj ) {
		echo "<tr><td>".$obj['id']."</td><td>".$obj['objnum']
		       ."</td><td>".$obj['name']."</td><td>".$obj['description']."</td></tr>
					 ";
	}
	echo '
	</table>';
}

?>
</BODY>
</HTML>
