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
if(empty($_GET['rT']))
	$retType = 'HTML_UL';
else
	$retType = $_GET['rT'];

if(empty($_GET['d']))
	$depth = 9999;
else
	$depth = $_GET['d'];
?>
<HTML>
<BODY>
<?php
$resInfo = queryResultsCategories($catIDs, $kwds, $onlyWithImgs, $retType );
if( !$resInfo || count($resInfo) == 0 ) {
	echo "<h2>Query Failed</h2>";
} else {
	echo "<h3>Categories for query on Categories:".$cats." and Keywords:".$kwds."</h3>
	";
	foreach( $resInfo as $facet ) {
		echo "<h3>".$facet['facet']." (".$facet['id'].")</h3>";
		if( $retType == 'HTML_UL') {
			echo $facet['items'];
		} else {	// Get as PHP and output completely 
			echo "<ul>
			";
			outputPHPToDepth( $facet['items'], 0, $depth );
			echo "
			</ul>
			";
		}
	}
}

function outputPHPToDepth( $item, $currDepth, $limit ) {
		$nested = isset($item['id']);
		if( $nested ) {
			echo "<li>".$item['name'];
			if( isset( $item['count'] ))
				echo " (".$item['count'].")";
		}
		if( $currDepth < $limit
			&& isset( $item['children'] ) && count($item['children']) > 0 ) {
			if( $nested )
				echo "<ul>
				";
			foreach( $item['children'] as $child )
				outputPHPToDepth( $child, $currDepth+1, $limit );
			if( $nested )
				echo "
				</ul>
				";
		}
		if( $nested ) {
			echo "
			</li>
			";
		}
}
?>
</BODY>
</HTML>
