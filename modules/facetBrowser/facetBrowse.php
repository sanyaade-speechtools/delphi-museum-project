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
		return "(SELECT obj_id from obj_cats where cat_id=".(string)$catIDList[$iID].") ".$qName;
	}
	// Not the last one, so we build the return string as a join on this and the recursively
	// computed string
	$subQ = buildMainQueryForTerm( $catIDList, $iID+1 );
	$subName = "sub".($iID+1);
	return "(SELECT oc.obj_id from obj_cats oc,".$subQ."
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
		$onlyWithImgs = true;		// default to only images
		if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
			$onlyWithImgs = false;
		$cats = $_GET['cats'];
		$catIDs = explode( ",", $cats );
		if( count($catIDs) == 1 ) {
			$t->assign("catsByCountQ", "Unneeded");
			$qCatIDs = $catIDs;
		} else {
			// We need to order the cats to put the ones with the most objects at the outside
			$tqCatOrder = "SELECT id, ".($onlyWithImgs?"n_matches_w_img":"n_matches").
										" n from categories where ";
			$fFirst = true;
			foreach( $catIDs as $catID ) {
				if( $fFirst )
					$fFirst = false;
				else
					$tqCatOrder .= " or ";
				$tqCatOrder .= "id=".$catID;
			}
			$tqCatOrder .= " order by n desc";
			$catsQResult=$mysqli->query($tqCatOrder);
			$t->assign("tqCatOrder", $tqCatOrder);
			// unset($catIDs);
			while($row = $catsQResult->fetch_array()) {
				$qCatIDs[] = $row[0];
			}
		}

		$tqMain = buildMainQueryForTerm( $qCatIDs, 0 );
		$pageNum = 0;
		if( !empty( $_GET['page'] ))
			$pageNum = 1*$_GET['page'];

		// We have to set all the nResults values. Get the counts first

		if( $onlyWithImgs ) {
			$qual = " (with images)";
			$tqCountsByCat =
				"SELECT c.id, c.parent_id, c.facet_id, c.display_name, count(*) FROM categories c,
					(SELECT oc.obj_id, oc.cat_id from obj_cats oc, objects o, 
				".$tqMain."
				WHERE oc.obj_id=tqMain.obj_id AND o.id=tqMain.obj_id AND NOT o.img_path IS NULL) tqTop
				WHERE c.id=tqTop.cat_id GROUP BY c.id ORDER BY c.id";
			$tqFull =
			"SELECT SQL_CALC_FOUND_ROWS o.id, o.objnum, o.name, o.description, o.img_path
			 FROM objects o,".$tqMain." WHERE o.id=tqMain.obj_id AND NOT o.img_path IS NULL LIMIT ".$_DELPHI_PAGE_SIZE;
		} else {
			$qual = "";
			$tqCountsByCat =
				"SELECT c.id, c.parent_id, c.facet_id, c.display_name, count(*) from categories c,
				(SELECT oc.obj_id, oc.cat_id from obj_cats oc,
				".$tqMain."
				where oc.obj_id=tqMain.obj_id) tqTop
				 where c.id=tqTop.cat_id group by c.id order by c.id";
			$tqFull =
			"SELECT SQL_CALC_FOUND_ROWS o.id, o.objnum, o.name, o.description, o.img_path
			 from objects o,".$tqMain." where o.id=tqMain.obj_id limit ".$_DELPHI_PAGE_SIZE;
		}
		$tqFullCount = "SELECT FOUND_ROWS()";
		if( $pageNum > 0 )
			$tqFull .= " OFFSET ".($_DELPHI_PAGE_SIZE*$pageNum);
		$t->assign("catsByCountQ", $tqCountsByCat);
		$t->assign("fullQ", $tqFull);
		$t->assign("fullCountQ", $tqFullCount);
		$pageNum2 = $pageNum+1;		// Page uses 1-based pages.

		$objsresult=$mysqli->query($tqFull);
		$fullCountResult=$mysqli->query($tqFullCount);
		if($row = $fullCountResult->fetch_array()) {
			$numResultsTotal = $row[0];
			$numPagesTotal = ceil($numResultsTotal/$_DELPHI_PAGE_SIZE);
			//$numPagesLeft = $numPagesTotal - $pageNum2; // 1-based page indices.
			$pageRangeStart = max(($pageNum2-3), 1);    // start at 1 or curr-3
			for( $i=0; $i<7 && ($pageRangeStart+$i)<=$numPagesTotal; $i++ )
				$pageNums[] = $pageRangeStart+$i;
		} else {
			$numResultsTotal = 0;
			$numPagesTotal = 1;
			//$numPagesLeft = 0;
			$pageNums[] = 1;
		}
	}
	$facetsResults=$mysqli->query("SELECT id, display_name from facets order by id");
	GetFacetListFromResultSet($facetsResults);
	$countsresult=$mysqli->query($tqCountsByCat);
	PopulateFacetsFromResultSet( $countsresult, true );
	// Facets now exist as array in $facets. Nodes are avail in hashMap.
	// Need to build the base query out of the existing parameters.
	// As written, baseQ is sufficient to use for page queries - just append the page=#
	$baseQ = $_SERVER['PHP_SELF']."?";
	$catsParam = "cats=".$cats;
	if( !$onlyWithImgs )
		$baseQ.= "wImgs=false&".$catsParam;
  else
		$baseQ.= $catsParam;

	$t->assign("baseQ", $baseQ); 			// for pagination queries in page
	$t->assign("pageNum", $pageNum2); 	// for pagination queries in page
	$t->assign("pageNums", $pageNums); 	// for pagination queries in page
	$t->assign("numPagesTotal", $numPagesTotal);
	$t->assign("numResultsTotal", $numResultsTotal); // e.g. "48"
	$t->assign("iFirstResult", 1+($pageNum*$_DELPHI_PAGE_SIZE)); // e.g. "48"
	$t->assign("iLastResult", min((($pageNum+1)*$_DELPHI_PAGE_SIZE),$numResultsTotal));
	//$t->assign("numPagesLeft", $numPagesLeft);
	$t->assign("qual", $qual); // e.g. "with images"
	$t->assign("query", buildStringForQueryTerms($catIDs)); // e.g. "Color:White + Site or Provenience:Western Africa"

	// var for holding concated output
	$facetTreeOutput = "";

	foreach( $facets as $facet ) {
		if( empty($facet->arrChildren) ) {
			$facetTreeOutput .= "<code class=\"hidden\">Facet: "
														.$facet->name." has no no matches</code>";
		} else {
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
		$imageOutput .= "<div class=\"results_result\">";
		$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
		$pathToDetails = $CFG->wwwroot . "/modules/browser/details.php?id=" . $row['id'];
		$imageOutput .= "<a href=\"".$pathToDetails."\"><img src=\"".$pathToImg."\"";
		$imageOutput .= " class=\"results_resultThumbnail\" /></a>";

		$imageOutput .= "<!--<span class=\"label\">".$row['name']."</span>-->";
		$imageOutput .= "</div>";
		$nPix++;
		if( $nPix >= 40 ) {
			$imageOutput .= "<br class=\"clearbreak\"";
			$imageOutput .= "<p>Another ".($numResultsTotal-40)." images not shown...</p>";
			break;
		}
	}

	$t->assign("imageOutput", $imageOutput);
	$t->display("results.tpl");
	die;

?>
