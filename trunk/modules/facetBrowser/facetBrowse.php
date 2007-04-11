<?
require_once("../../libs/env.php");

include("dbconnect.php");
require "Facet.inc";
$_DELPHI_PAGE_SIZE = 40;

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
		$onlyWithImgs = true;		// default to only images
		if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
			$onlyWithImgs = false;
		$pageNum = 0;
		if( !empty( $_GET['page'] ))
			$pageNum = 1*$_GET['page'];

		// We have to set all the nResults values. Get the counts first

		if( $onlyWithImgs ) {
			$qual = " (with images)";
			$tqCountsByCat = 
				"select c.id, c.parent_id, c.facet_id, c.display_name, count(*) from categories c,
					objects o, (select distinct oc.obj_id, oc.cat_id from obj_cats oc,
				".$tqMain."
				where oc.obj_id=tqMain.obj_id) tqTop
				where c.id=tqTop.cat_id and o.id=tqTop.obj_id and NOT o.img_path IS NULL
			 	group by c.id order by c.id";
			$tqFull = 
			"select o.id, o.objnum, o.name, o.description, o.img_path
			 from objects o,".$tqMain." where o.id=tqMain.obj_id and NOT o.img_path IS NULL limit ".$_DELPHI_PAGE_SIZE;
			$tqFullCount = 
				"select count(*) from objects o,".$tqMain." 
				where o.id=tqMain.obj_id and NOT o.img_path IS NULL";
		} else {
			$qual = "";
			$tqCountsByCat = 
				"select c.id, c.parent_id, c.facet_id, c.display_name, count(*) from categories c,
				(select distinct oc.obj_id, oc.cat_id from obj_cats oc,
				".$tqMain."
				where oc.obj_id=tqMain.obj_id) tqTop
				 where c.id=tqTop.cat_id group by c.id order by c.id";
			$tqFull = 
			"select o.id, o.objnum, o.name, o.description, o.img_path
			 from objects o,".$tqMain." where o.id=tqMain.obj_id ".$_DELPHI_PAGE_SIZE;
			$tqFullCount = 
				"select count(*) from objects o,".$tqMain." where o.id=tqMain.obj_id";
		}
		if( $pageNum > 0 )
			$tqFull .= " OFFSET ".($_DELPHI_PAGE_SIZE*$pageNum);
		echo "<code style=\"display:none;\">CountsByCat Query:
			".$tqCountsByCat."
			</code>";
		echo "<code style=\"display:none;\">Full Query:
			".$tqFull."
			</code>";
		echo "<code style=\"display:none;\">Full Count Query:
			".$tqFullCount."
			</code>";

		$objsresult=$mysqli->query($tqFull);
		$fullCountResult=$mysqli->query($tqFullCount);
		// This is unreliable because of the LIMIT clause in the query.
		// $numResultsTotal = $mysqli->num_rows($objsresult);
		if($row = $fullCountResult->fetch_array())
			$numResultsTotal = $row[0];
		else
			$numResultsTotal = 0;
	}
	$facetsResults=$mysqli->query("select id, display_name from facets");
	GetFacetListFromResultSet($facetsResults);
	$countsresult=$mysqli->query($tqCountsByCat);
	PopulateFacetsFromResultSet( $countsresult, true );
	// Facets now exist as array in $facets. Nodes are avail in hashMap.
	
	/*
		TODO fix urls so that cats doesn't have to be the last parameter
	*/
	$baseQ = $_SERVER['PHP_SELF']."?".$_SERVER['QUERY_STRING'];

	$t->assign("numResultsTotal", $numResultsTotal); // e.g. "48"
	$t->assign("qual", $qual); // e.g. "with images"
	$t->assign("query", buildStringForQueryTerms($catIDs)); // e.g. "Color:White + Site or Provenience:Western Africa"
	
	// var for holding concated output
	$facetTreeOutput = "";
	
	foreach( $facets as $facet ) {
		if( empty($facet->arrChildren) )
			echo( "<code style=\"display:none;\">Facet: ".$facet->name." has no no matches</code>" );
		else {
			$facet->PruneForOutput($numResultsTotal, $catIDs);
			$facetTreeOutput .= $facet->GenerateHTMLOutput( "facet", 0, 1, $baseQ, false );
		}
	}
	
	$t->assign("facetTree", $facetTreeOutput);


	$nPix=0;
	$objsresult=$mysqli->query($tqFull);
	
	// var for holding concated output
	$imageOutput = "";
	
	/*
		TODO rewrite thumbnail fetching to pass an array, rather than html, to the template
	*/
	while( $row=$objsresult->fetch_assoc() )
	{
		$imageOutput .= "<div class=\"imageblock\">";
		$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
		$imageOutput .= "<a href=\"".$pathToImg."\"><img src=\"".$pathToImg."\"";
		$imageOutput .= " class=\"h160thumb\" width=\"160px\" height=\"120px\"	/></a>";

		$imageOutput .= "<span class=\"label\">".$row['name']."</span>";
		$imageOutput .= "</div>";
		$nPix++;
		if( $nPix >= 40 ) {
			$imageOutput .= "<p>Another ".($numResultsTotal-40)." images not shown...</p>";
			break;
		}
	}
	
	$t->assign("imageOutput", $imageOutput);
	$t->display("results.tpl");
	die;
 
?>
