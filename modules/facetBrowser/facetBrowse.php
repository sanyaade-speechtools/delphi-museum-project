<html>
<head>
<title>Facet UI Test</title>
<link href="style/results.css" rel="stylesheet" type="text/css" />
<script src="script/treeview.js" type="text/javascript" ></script>
</head>

<body>
<?
include("dbconnect.php");
require "Facet.inc";

	// This builds up the query by recursing for the rest of the list
function buildMainQueryForTerm( $catIDList, $iID ) {
	if( $iID == 0 )
		$qName = "tqMain";
	else {
		$qName = "sub";
		$qName .= (string)$iID;
	}
	if( $iID == count($catIDList)-1 ) {		// simple form
		return "(select distinct obj_id from obj_cats where cat_id=".(string)$catIDList[$iID].") ".$qName;
	}
	// Not the last one, so we build the return string as a join on this and the recursively
	// computed string
	$subQ = buildMainQueryForTerm( $catIDList, $iID+1 );
	$subName = "sub".($iID+1);
	return "(select oc.obj_id from obj_cats oc,".$subQ."
					 where oc.obj_id=".$subName.".obj_id and oc.cat_id="
						.(string)$catIDList[$iID].") ".$qName;
}

function buildStringForQueryTerms( $catIDs ) {
	global $facets;
	global $taxoNodesHashMap;
	$fFirstName = true;
	$retStr = "";
	foreach( $catIDs as $catID ) {
		$cnode = $taxoNodesHashMap[$catID];
		if( $fFirstName )
			$fFirstName = false;
		else
			$retStr .= " + ";
		if( $cnode == null )
			$retStr .= "Unknown";
		else {
			if( $cnode->facet_id != null ) {
				$fnode = $facets[$cnode->facet_id];
				$retStr .= "<em>".$fnode->name."</em>:";
			}
			$retStr .= $cnode->name;
		}
	}
	return $retStr;
}


	if( empty( $_GET['cats'] )) {
		echo "<h2>No query params specified</h2>";
		return;
	} else {
		$cats = $_GET['cats'];
		$catIDs = explode( ",", $cats );

		$tqMain = buildMainQueryForTerm( $catIDs, 0 );

		// We have to set all the nResults values. Get the counts first

		$tqCountsByCat = 
			"select c.id, c.parent_id, c.facet_id, c.display_name, count(*) from categories c,
			(select distinct oc.obj_id, oc.cat_id from obj_cats oc,
			".$tqMain."
			where oc.obj_id=tqMain.obj_id) tqTop
			 where c.id=tqTop.cat_id group by c.id order by c.id";
		echo "<code class=\"hidden\">".$tqCountsByCat."</code>
			";

		$tqFull = 
		"select o.id, o.objnum, o.name, o.description, o.img_path
		 from objects o,".$tqMain." where o.id=tqMain.obj_id limit 40";
		
		echo "<code class=\"hidden\">".$tqFull."</code>
			";

		$objsresult=$mysqli->query($tqFull);
		// This is unreliable because of the LIMIT clause in the query.
		// $numResultsTotal = $mysqli->num_rows($objsresult);
		$numResultsTotal = 0;
		//echo "<h2>Found ".$numResultsTotal." images for query: ".$UIQString."</h2>";
	}
	$facetsResults=$mysqli->query("select id, display_name from facets");
	GetFacetListFromResultSet($facetsResults);
	$countsresult=$mysqli->query($tqCountsByCat);
	PopulateFacetsFromResultSet( $countsresult, true );
// Facets now exist as array in $facets. Nodes are avail in hashMap.
	$baseQ = $_SERVER['PHP_SELF']."?".$_SERVER['QUERY_STRING'];
	echo( "<h3>Query: ".buildStringForQueryTerms($catIDs)."</h3>");
?>
<div id="container">
<div id="leftSide">
<div class="tree">
<?php
	foreach( $facets as $facet ) {
		if( empty($facet->arrChildren) )
			echo( "<code class=\"hidden\">Facet: ".$facet->name." has no no matches</code>" );
		else {
			$facet->PruneForOutput($numResultsTotal, $catIDs);
			$facet->GenerateHTMLOutput( "facet", 0, 1, $baseQ );
		}
	}
?>
</div>
</div> <!-- close left side div -->
<div id="rightSide">

<?php
/*
	$nPix=0;
	while( $row=mysql_fetch_array($objsresult) )
	{
		echo "<div class=\"imageblock\">";
		$pathToImg = $row['BasePath'].$row['Path']."/".$row['Filename'];
		echo "<a href=\"".$pathToImg."\"><img src=\"".$pathToImg."\"";
		$w = (int)$row['Width'];
		$h = (int)$row['Height'];
		if( $w >= $h )
			echo " class=\"h160thumb\" width=\"160px\" height=\"120px\"	/></a>";
		else
			echo " class=\"v160thumb\" width=\"120px\" height=\"160px\"	/></a>";
		echo "<span class=\"label\">".$row['Filename']." (".$w." x ".$h.")</span>";
		echo "</div>";
		$nPix++;
		if( $nPix >= 40 ) {
			echo "<p>Another ".($numResultsTotal-40)." images not shown...</p>";
			break;
		}
	}
 */
?>
</div> <!-- close rightSide div -->
</div> <!-- close container div -->
</body>
</html>
