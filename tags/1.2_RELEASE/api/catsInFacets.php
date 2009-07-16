<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

if(empty($_GET['f'])) 
	$facetname = '__ALL';
else
	$facetname = $_GET['f'];

if(empty($_GET['rT'])) {
	$retType = 'HTML_UL';
	$HTparam = '';
} else {
	$retType = $_GET['rT'];
	if(empty($_GET['p'])) 
		$HTparam = '';
	else
		$HTparam = $_GET['p'];
}

if(empty($_GET['d']))
	$depth = 9999;
else
	$depth = $_GET['d'];

?>
<HTML>
<script>
function query(e, id) {
	//alert( "Query for category: " + id );
	location.href = "../modules/facetBrowser/facetBrowse.php?cats="+id;
	if (!e) var e = window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return false;
}
</script>
<BODY>
<?php
$facetinfo = getCategoriesInFacet($facetname, true, $retType, $HTparam, $depth );
if( !$facetinfo || count($facetinfo) == 0 ) {
	echo "<h2>Cannot find categories";
	if( $facetname != "__ALL" )
		echo " for facet: ".$facetname;
	echo "</h2>";
} else {
	echo "<h2>Facet info:</h2>
	";
	foreach( $facetinfo as $facet ) {
		echo "<h3>".$facet['facet']." (".$facet['id'].")</h3>";
		echo "<p>".$facet['desc']."</p>";
		if(( $retType == 'HTML_UL' ) || 
			( $retType == 'HTML_UL_ATAG' ) || ( $retType == 'HTML_UL_ATTR' ) || ( $retType == 'HTML_UL_OC' )) {
			echo $facet['items'];
		} else {	// Get as PHP and output completely 
			echo "<ul>
			";
			outputPHP( $facet['items'] );
			echo "
			</ul>
			";
		}
	}
}

function outputPHP( $item ) {
		if( isset($item['id']) ) {
			echo "<li>".$item['name'];
			if( isset( $item['count'] ))
				echo " (".$item['count'].")";
			}
		if( isset( $item['children'] ) && count($item['children']) > 0 ) {
			echo "<ul>
			";
			foreach( $item['children'] as $child )
				outputPHP( $child );
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
