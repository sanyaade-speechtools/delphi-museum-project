<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

if(empty($_GET['depth'])) {
	$asHTML = true;
} else {
	$asHTML = false;
	$depth = $_GET['depth'];
}

?>
<HTML>
<BODY>
<?php
if( $asHTML ) {
	$facetinfo = getCategoriesInFacet("__ALL", true, "HTML_UL");
	if( !$facetinfo || count($facetinfo) == 0 ) {
		echo "<h2>Cannot get facets...</h2>";
	} else {
		echo "<h2>Facet info:</h2>
		";
		foreach( $facetinfo as $facet ) {
			echo "<h3>".$facet['facet']." (".$facet['id'].")</h3>";
			echo $facet['items'];
		}
	}
} else {	// Get as PHP and output only to depth
	$facetinfo = getCategoriesInFacet("__ALL", true, "PHP");
	if( !$facetinfo || count($facetinfo) == 0 ) {
		echo "<h2>Cannot get facets...</h2>";
	} else {
		echo "<h2>Facet info:</h2>
		";
		foreach( $facetinfo as $facet ) {
			echo "<h3>".$facet['facet']." (".$facet['id'].")</h3>";
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
