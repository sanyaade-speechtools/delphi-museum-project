<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

$badarg = false;
if(empty($_GET['id'])) {
	$badarg = true;
	$objID = '[empty]';
} else {
	$objID = $_GET['id'];
	if(empty($_GET['f'])) 
		$facetname = '__ALL';
	else
		$facetname = $_GET['f'];

	if(empty($_GET['rT']))
		$retType = 'HTML_UL';
	else
		$retType = $_GET['rT'];

	if(empty($_GET['d']))
		$depth = 9999;
	else
		$depth = $_GET['d'];
}
if( $badarg ) {
	header("HTTP/1.0 400 Bad Request");
	echo "Bad Arg: id[".$objID."]";
	echo "GET: ";
	print_r( $_GET );
	exit();
}

?>
<HTML>
<BODY>
<?php
$facetinfo = getCategoriesForObject($objID, $facetname, true, $retType );
if( !$facetinfo || count($facetinfo) == 0 ) {
	echo "<h2>Cannot find info for: ".$objID;
	if( $facetname != "__ALL" )
		echo " on facet: ".$facetname;
	echo "</h2>";
} else  {
	echo "<h2>Facet info:</h2>
	";
	foreach( $facetinfo as $facet ) {
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
		if( isset($item['id']) ) {
			echo "<li>".$item['name'];
			if( isset( $item['count'] ))
				echo " (".$item['count'].")";
			}
		if( $currDepth < $limit
			&& isset( $item['children'] ) && count($item['children']) > 0 ) {
			echo "<ul>
			";
			foreach( $item['children'] as $child )
				outputPHPToDepth( $child, $currDepth+1, $limit );
			echo "
			</ul>
			";
		}
		if( isset($item['id']) ) {
			echo "
			</li>
			";
		}
}
?>
</BODY>
</HTML>
