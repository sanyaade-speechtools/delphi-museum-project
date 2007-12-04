<html>
<head>
<title>Tree-Based Search Start Page</title>
<link href="style/results.css" rel="stylesheet" type="text/css" />
<script src="script/treeview.js" type="text/javascript" ></script>
</head>

<body>
<?php
require "Facet.inc";

	//$cats = array();	// Create an empty array to pass in to Prune

	$allCountsResults =& $db->query("SELECT n_objs_total, n_objs_w_imgs FROM DBInfo LIMIT 1");
	if($row = $allCountsResults->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		$all = $row[0];
		$wImgs = $row[1];
		echo "<h2>PAHMA online has ".$all." total objects (".$wImgs." with images).</h2>";
	}

	echo "<h4>Click on any category to search for objects that match.
		<br>Counts after terms indicate total objects for each category; there are fewer objects <em>with images</em>.</h4>";

	$tqCountsByCat = 
		"select c.id, c.parent_id, c.facet_id, c.display_name, n_matches 
		from categories c where n_matches>0 order by id";
	$facetsResults =& $db->query("select id, display_name from facets");
	GetFacetListFromResultSet($facetsResults);
	$countsresult =& $db->query($tqCountsByCat);
	PopulateFacetsFromResultSet( $countsresult, true );
  // Facets now exist as array in $facets. Nodes are avail in hashMap.
	$baseQ = "facetBrowse.php?cats=";
?>
<div id="container">
<div id="leftSide">
<div class="tree">
<?php
	foreach( $facets as $facet ) {
		if( empty($facet->arrChildren) )
			echo( "<code class=\"hidden\">Facet: ".$facet->name." has no no matches</code>" );
		else {
			//$facet->PruneForOutput($numResultsTotal, $catIDs);
			$facet->GenerateHTMLOutput( "facet", 0, 1, $baseQ, true );
		}
	}
?>
</div>
</div> <!-- close left side div -->
<div id="rightSide">
</div> <!-- close rightSide div -->
</div> <!-- close container div -->
</body>
</html>
