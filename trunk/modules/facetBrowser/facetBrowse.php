<?
require_once("../../libs/env.php");

include("dbconnect.php");
require "Facet.inc";
$_DELPHI_PAGE_SIZE = 40;

	// Build up a keyword(s) query. Does NOT append subQ name!
function buildKwdQuery( $kwds, $wImgs ) {
	$subQ = "(SELECT id as obj_id FROM objects o WHERE MATCH(name, description) AGAINST('"
		.$kwds."') ";
	if( $wImgs )
		$subQ .= "AND NOT o.img_path IS NULL) ";
	else
		$subQ .= ") ";
	return $subQ;
}

	// This builds up the query by recursing for the rest of the list
function buildMainQueryForTerm( $catIDList, $iID, $kwds, $wImgs ) {
	if( $iID == 0 )
		$qName = "tqMain";
	else {
		$qName = "sub";
		$qName .= (string)$iID;
	}
	if( empty($catIDList) ) {
		if( empty($kwds))
			die("buildMainQuery: no categories and no keywords!");
		$subQ = buildKwdQuery( $kwds, $wImgs );
		return $subQ.$qName;
	}
	
	if( $iID == count($catIDList)-1 ) {		// simple form
		if( empty($kwds) )
			return "(SELECT obj_id from obj_cats where cat_id=".(string)$catIDList[$iID].") ".$qName;
		$subQ = buildKwdQuery( $kwds, $wImgs )."subK";
		return "(SELECT oc.obj_id from obj_cats oc,".$subQ."
						 where oc.obj_id=subK.obj_id and oc.cat_id="
							.(string)$catIDList[$iID].") ".$qName;
	}
	// Not the last one, so we build the return string as a join on this and the recursively
	// computed string
	$subQ = buildMainQueryForTerm( $catIDList, $iID+1, $kwds, $wImgs );
	$subName = "sub".($iID+1);
	return "(SELECT oc.obj_id from obj_cats oc,".$subQ."
					 where oc.obj_id=".$subName.".obj_id and oc.cat_id="
						.(string)$catIDList[$iID].") ".$qName;
}

function buildStringForQueryTerms( $kwds, $catIDs ) {
	global $facets;
	global $taxoNodesHashMap;
	$fFirstName = true;
	$retStr = "";
	if( !empty($kwds) ) {
		$retStr .= "\"".$kwds."\"";
		$fFirstName = false;
	}
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


	if( empty( $_GET['cats'] ) && empty( $_GET['kwds'] )) {
		echo "<h2>No query params specified</h2>";
		return;
	} else {
		$onlyWithImgs = true;		// default to only images
		if( !empty( $_GET['wImgs'] ) && ($_GET['wImgs'] == 'false'))
			$onlyWithImgs = false;
		$kwds = $_GET['kwds'];
		if( empty($_GET['cats']) ) {
			$cats = null;
			$catIDs = array();
		} else {
			$cats = $_GET['cats'];
			$catIDs = explode( ",", $cats );
		}
		if( count($catIDs) <= 1 ) {
			$t->assign("tqCatOrder", "Unneeded");
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

		$tqMain = buildMainQueryForTerm( $qCatIDs, 0, $kwds, $onlyWithImgs );
		$pageNum = 0;
		if( !empty( $_GET['page'] ))
			$pageNum = 1*$_GET['page'];

		// We have to set all the nResults values. Get the counts first
		$qual = $onlyWithImgs?" (with images)":"";

		// If kwds is non-empty, we put the wImgs constraint in that subquery
		if( $onlyWithImgs && empty($kwds)) {
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
			if($numResultsTotal == 0 ) {
				$numPagesTotal = 1;
				$pageNums[] = 1;
			} else {
				$numPagesTotal = ceil($numResultsTotal/$_DELPHI_PAGE_SIZE);
				//$numPagesLeft = $numPagesTotal - $pageNum2; // 1-based page indices.
				$pageRangeStart = max(($pageNum2-3), 1);    // start at 1 or curr-3
				for( $i=0; $i<7 && ($pageRangeStart+$i)<=$numPagesTotal; $i++ )
					$pageNums[] = $pageRangeStart+$i;
			}
		} else {
			$numResultsTotal = 0;
			$numPagesTotal = 1;
			$pageNums[] = 1;
		}
	}
	// var for holding concated output
	$facetTreeOutput = "";
	// var for holding concated output
	$imageOutput = "";

	if( $numResultsTotal == 0 ) {
		$baseQ = "";
		$t->assign("iFirstResult", 0); // e.g. "48"
		$t->assign("iLastResult", 0);
	} else {
		$facetsResults=$mysqli->query("SELECT id, display_name from facets order by id");
		GetFacetListFromResultSet($facetsResults);
		try {
			$countsresult=$mysqli->query($tqCountsByCat);
			if( empty($countsresult))
				throw new Exception($mysqli->error);
			PopulateFacetsFromResultSet( $countsresult, true );
		} catch( Exception $e ) {
			echo "<h1>Problem with cat counts query/tree building</h1>\n";
			echo "<h2>".$e->getMessage()."</h2>\n";
			echo "<h2>Query:</h2>\n";
			echo "<code>".$tqCountsByCat."</code>\n";
			die();
		}
		// Facets now exist as array in $facets. Nodes are avail in hashMap.
		// Need to build the base query out of the existing parameters.
		// As written, baseQ is sufficient to use for page queries - just append the page=#
		$baseQ = $_SERVER['PHP_SELF']."?";
		$firstP = true;
		if( !$onlyWithImgs ) {
			$baseQ.= "wImgs=false";
			$firstP = false;
		}
		if( !empty($kwds)) {
			if( $firstP ) {
				$firstP = false;
				$baseQ.= "kwds=".$kwds;
			}
			else
				$baseQ.= "&kwds=".$kwds;
		}
		// If have cats, include for baseQ (for pagination)
		$catsParam = $firstP?"cats=":"&cats=";
		if( !empty($cats)) {
			$catsParam .= $cats;
			$baseQ .= $catsParam;
			$refineQ = $baseQ.",";
		} else {
			$refineQ = $baseQ.$catsParam;
		}

		$t->assign("iFirstResult", 1+($pageNum*$_DELPHI_PAGE_SIZE)); // e.g. "48"
		$t->assign("iLastResult", min((($pageNum+1)*$_DELPHI_PAGE_SIZE),$numResultsTotal));
		//$t->assign("numPagesLeft", $numPagesLeft);
		foreach( $facets as $facet ) {
			if( empty($facet->arrChildren) ) {
				$facetTreeOutput .= "<code class=\"hidden\">Facet: "
															.$facet->name." has no no matches</code>";
			} else {
				$facet->PruneForOutput($numResultsTotal, $catIDs);
				$facetTreeOutput .= $facet->GenerateHTMLOutput( "facet", 0, 1, $refineQ, false );
			}
		}
		/*
			TODO rewrite thumbnail fetching to pass an array, rather than html, to the template
		*/
		$nPix=0;
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
	}
	$t->assign("baseQ", $baseQ); 			// for pagination queries in page
	$t->assign("numResultsTotal", $numResultsTotal); // e.g. "48"
	$t->assign("pageNum", $pageNum2); 	// for pagination queries in page
	$t->assign("pageNums", $pageNums); 	// for pagination queries in page
	$t->assign("numPagesTotal", $numPagesTotal);
	$t->assign("qual", $qual); // e.g. "with images"
	$t->assign("query", buildStringForQueryTerms($kwds, $catIDs));
	$t->assign("facetTree", $facetTreeOutput);
	$t->assign("imageOutput", $imageOutput);
	$t->display("results.tpl");
	die;

?>
