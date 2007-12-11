<?php
session_cache_limiter("public");		// enable caching of this page

require_once("Facet.inc");

function checkAndLoadFacets() {
	global $facets;
	global $db;
	if( count($facets) <= 0 ) {
		$facetsResults =& $db->query("SELECT id, display_name from facets order by id");
		if (PEAR::isError($facetsResults)) {
				echo "ERROR: ".$facetsResults->getMessage();
				return false;
		}
		GetFacetListFromResultSet($facetsResults);
	}
}

function getFacets() {
	global $facets;
	checkAndLoadFacets();
	if( count($facets) == 0 ) {
		return false;		// Can't find any facets in DB - just bail
	}
	$retVal = array();
	foreach( $facets as $facet ) {
		$retVal[] = array( "id" => $facet->id, "name" => $facet->name );
	}
	return $retVal;
}

function findFacetID( $fname /* :String */ ) {
	global $facets;
	checkAndLoadFacets();
	if( count($facets) > 0 ) {
		foreach( $facets as $facet ) {
			if( $facet->name == $fname )
				return $facet->id;
		}
	}
	return -1;
}

function getNumObjs() {
	global $db;
 	$allCountsResults =& $db->query("SELECT n_objs_total, n_objs_w_imgs FROM DBInfo LIMIT 1");
	if($row = $allCountsResults->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		return array( "allObjs" => $row[0], "withImgs" => $row[1] );
	} else
		return false;
}

function getCategoriesInFacet(
						$facetname,					//:String
						$countsWithImages,	//:boolean
						$retType) {					//:String 
	global $facets;
	global $db;
	checkAndLoadFacets();
	if( count($facets) == 0 ) 
		return false;		// Can't find any facets in DB - just bail
	// If facet is qualified, then just get info for that facet. Otherwise get for all.
	if( !isset($facetname) || $facetname == "__ALL" ) {
		$fid = -1;
	} else {
		$fid = findFacetID( $facetname );
		if( $fid < 0 ) {
			echo "Bad facet name: ".$facetname;
			return false;
		}
	}
	$countCol = $onlyWithImgs ? "n_matches_w_img" : "n_matches";
 	$tqCountsByCat = "select id, parent_id, facet_id, display_name, ".$countCol
 										  ." from categories where ".$countCol.">0";
	if( $fid >= 0 ) {
 		$tqCountsByCat .= " AND facet_id=".$fid;
 	}
	$tqCountsByCat .= " order by id";
 	$countsresult =& $db->query($tqCountsByCat);
 	PopulateFacetsFromResultSet( $countsresult, true );
  // Facets now exist as array in $facets. Nodes are avail in hashMap.
	// Note that we do not prune since this is for the ontology, not a query.
	$retVal = array();
	//error_log( "getCategoriesInFacet() considering ".count($facets)." facets...  ");
	foreach( $facets as $facet ) {
		if(( $fid < 0 ) || ( $fid == $facet->id )) {
			$items = array();
			if( !empty($facet->arrChildren) ) {
					switch( $retType ) {
						default:
							return false;
						case "PHP":
							$items = $facet->GeneratePHPItems( 0, 1, true, false );
							break;
						case "JSON":
							// $items = $facet->GeneratePHPItems( 0, 1, false, false );
							// Convert to JSON
							// break;
							return false;	// NYI
						case "HTML_UL":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, 1, true, false );
							break;
					}
				// Only add a new item if there are children for that facet
				$facetVal = array( 'facet' => $facet->name,
													 'id'    => $facet->id,
													 'items' => $items );
				$retVal[] = $facetVal;
			}
		}
	}
	return $retVal;
}


function getCategoriesForObject(
						$forObjID,					//:int ID of object in DB
						$facetname,					//:String 1 facet name or "__ALL"
						$countsWithImages,	//:boolean
						$retType) {					//:String "PHP", "JSON" or "HTML_UL"
	global $facets;
	global $db;
	checkAndLoadFacets();
	if( count($facets) == 0 ) 
		return false;		// Can't find any facets in DB - just bail
	// If facet is qualified, then just get info for that facet. Otherwise get for all.
	if( !isset($facetname) || $facetname == "__ALL" ) {
		$fid = -1;
	} else {
		$fid = findFacetID( $facetname );
		if( $fid < 0 ) {
			echo "Bad facet name: ".$facetname;
			return false;
		}
	}
	$countCol = $onlyWithImgs ? "n_matches_w_img" : "n_matches";
 	$tqCountsByCat = "select c.id, c.parent_id, c.facet_id, c.display_name, c.".$countCol
 										  ." from categories c, obj_cats oc where c.id=oc.cat_id"
											." AND oc.obj_id=".$forObjID;
	if( $fid >= 0 ) {
 		$tqCountsByCat .= " AND c.facet_id=".$fid;
 	}
	$tqCountsByCat .= " order by id";
 	$countsresult =& $db->query($tqCountsByCat);
 	PopulateFacetsFromResultSet( $countsresult, true );
  // Facets now exist as array in $facets. Nodes are avail in hashMap.
	// Note that we do not prune since this is for the details, not a full query.
	$retVal = array();
	//error_log( "getCategoriesForObject() considering ".count($facets)." facets...  ");
	foreach( $facets as $facet ) {
		if(( $fid < 0 ) || ( $fid == $facet->id )) {
			$items = array();
			if( !empty($facet->arrChildren) ) {
					switch( $retType ) {
						default:
							return false;
						case "PHP":
							$items = $facet->GeneratePHPItems( 0, 1, true, false );
							break;
						case "JSON":
							// $items = $facet->GeneratePHPItems( 0, 1, false, false );
							// Convert to JSON
							// break;
							return false;	// NYI
						case "HTML_UL":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, 1, true, false );
							break;
					}
				// Only add a new item if there are children for that facet
				$facetVal = array( 'facet' => $facet->name,
													 'id'    => $facet->id,
													 'items' => $items );
				$retVal[] = $facetVal;
			}
		}
	}
	return $retVal;
}


?>
