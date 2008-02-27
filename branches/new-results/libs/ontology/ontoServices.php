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
						$retType, 					//:String 
						$depth ) {					//:int
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
	$countCol = $countsWithImages ? "n_matches_w_img" : "n_matches";
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
							$items = $facet->GeneratePHPItems( 0, $depth, 1, true, false );
							break;
						case "JSON":
							// $items = $facet->GeneratePHPItems( 0, $depth, 1, false, false );
							// Convert to JSON
							// break;
							return false;	// NYI
						case "HTML_UL":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $depth, 1, true, false );
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
	$countCol = $countsWithImages ? "n_matches_w_img" : "n_matches";
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
	$largeNum = 999999;
	foreach( $facets as $facet ) {
		if(( $fid < 0 ) || ( $fid == $facet->id )) {
			$items = array();
			if( !empty($facet->arrChildren) ) {
					switch( $retType ) {
						default:
							return false;
						case "PHP":
							$items = $facet->GeneratePHPItems( 0, $largeNum, 1, true, false );
							break;
						case "JSON":
							// $items = $facet->GeneratePHPItems( 0, $largeNum, 1, false, false );
							// Convert to JSON
							// break;
							return false;	// NYI
						case "HTML_UL":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, true, false );
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

$_DELPHI_PAGE_SIZE = 40;	// TODO move to config.php

function queryObjects(
						$catIDs,						//:int[] array of category IDs
						$kwds,							//:String[] array of keyword names
						$pageNum,						//:int start page
						$pageSize,					//:int page size (specify <=0 for default)
						$countsWithImages ) {	//:boolean
	global $db;
	global $_DELPHI_PAGE_SIZE;

	if( empty($catIDs) && empty($kwds) ) {
		error_log( "queryObjects() called with no categories or keywords" );
		return false;
	}
	$retVal = array();
	if( $pageSize <= 0 )
		$pageSize = $_DELPHI_PAGE_SIZE;
	
	$retVal['pageSize'] = $pageSize;
	
	if( count($catIDs) <= 1 )
		$qCatIDs =& $catIDs;
	else
		$qCatIDs =& orderCatsForQuery( $catIDs, $countsWithImages );
	//error_log( "queryObjects() Kwds: ".(empty($kwds)?"None":$kwds));
	//error_log( "queryObjects() Cats: ".(empty($qCatIDs)?"None":implode(",", $qCatIDs)));
	$tqMain = buildMainQueryForTerm( $qCatIDs, 0, $kwds, $countsWithImages );
	$tqFull = prepareObjsQuery( $tqMain, empty($kwds), $countsWithImages, $pageNum, $pageSize );
	$objsresult =& $db->query($tqFull);
	if (PEAR::isError($objsresult)) {
		error_log( "queryObjects() main Query error: ".$objsresult->getMessage());
		error_log( "queryObjects() Query : ".$tqFull);
		return false;
	}
	//error_log( "queryObjects() Query : ".$tqFull);

	$tqFullCount = "SELECT FOUND_ROWS()";
	$fullCountResult =& $db->query($tqFullCount);
	if($row = $fullCountResult->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		$numResultsTotal = $row[0];
	} else {
		$numResultsTotal = 0;
	}
	$retVal['nObjs'] = $numResultsTotal;
	$retVal['nPages'] = ($numResultsTotal == 0 )?
												1 : ceil($numResultsTotal/$pageSize);

	$objs = array();
	while( $row=$objsresult->fetchRow() )
	{
		$item = array( 'id' => $row['id'], 'objnum' => $row['objnum'],
									'name' => $row['name'], 'description' => $row['description'], 
									'img_path' => $row['img_path'], 'aspectRatio' => $row['img_ar'] ); 
		$objs[] = $item;
	}
	$retVal['objects'] = $objs;

	return $retVal;
}

function queryResultsCategories(
						$catIDs,						//:int[] array of category IDs
						$kwds,							//:String[] array of keyword names
						$countsWithImages,	//:boolean
						$retType) {					//:String "PHP", "JSON" or "HTML_UL"
	global $facets;
	global $db;
	checkAndLoadFacets();
	if( count($facets) == 0 ) 
		return false;		// Can't find any facets in DB - just bail

	if( empty($catIDs) && empty($kwds) ) {
		error_log( "queryResultsCategories() called with no categories or keywords" );
		return false;
	}
	$retVal = array();
	
	if( count($catIDs) <= 1 )
		$qCatIDs =& $catIDs;
	else
		$qCatIDs =& orderCatsForQuery( $catIDs, $countsWithImages );
	//error_log( "queryObjects() Kwds: ".(empty($kwds)?"None":$kwds));
	//error_log( "queryObjects() Cats: ".(empty($qCatIDs)?"None":implode(",", $qCatIDs)));
	$tqMain = buildMainQueryForTerm( $qCatIDs, 0, $kwds, $countsWithImages );
	$tqFull = prepareResultsCatsQuery( $tqMain, empty($kwds), $countsWithImages );
	$catsresult =& $db->query($tqFull);
	if (PEAR::isError($catsresult)) {
		error_log( "queryResultsCategories() main Query error: ".$catsresult->getMessage());
		error_log( "queryResultsCategories() Query : ".$tqFull);
		return false;
	}
	//error_log( "queryResultsCategories() Query : ".$tqFull);

 	PopulateFacetsFromResultSet( $catsresult, true );
  // Facets now exist as array in $facets. Nodes are avail in hashMap.
	$retVal = array();
	//error_log( "getCategoriesForObject() considering ".count($facets)." facets...  ");
	$fid = -1;	// Stub this until we decide to allow facet spec
	$largeNum = 999999;
	foreach( $facets as $facet ) {
		if(( $fid < 0 ) || ( $fid == $facet->id )) {
			$items = array();
			if( !empty($facet->arrChildren) ) {
				$facet->PruneForOutput(0, $catIDs);
				switch( $retType ) {
					default:
						return false;
					case "PHP":
						$items = $facet->GeneratePHPItems( 0, $largeNum, 1, false, false );
						break;
					case "JSON":
						// $items = $facet->GeneratePHPItems( 0, $largeNum, 1, false, false );
						// Convert to JSON
						// break;
						return false;	// NYI
					case "HTML_UL":
						//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
						$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, false, false );
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

function orderCatsForQuery( $catIDs, $countsWithImages ) {
	global $db;
	// We need to order the cats to put the ones with the most objects at the outside
	$tqCatOrder = "SELECT id, ".($countsWithImages?"n_matches_w_img":"n_matches").
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
	$catsQResult =& $db->query($tqCatOrder);
	// $t->assign("tqCatOrder", $tqCatOrder);
	// unset($catIDs);
	$qCatIDs = array();
	while($row = $catsQResult->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		$qCatIDs[] = $row[0];
	}
	return $qCatIDs;
}

function prepareObjsQuery( $tqMain, $nokwds, $countsWithImages, $pageNum, $pageSize ) {
	// If kwds is non-empty, we put the wImgs constraint in that subquery
	if( $countsWithImages && $nokwds) {
		$tqFull =
		"SELECT SQL_CALC_FOUND_ROWS o.id, o.objnum, o.name, o.description, o.img_path, o.img_ar"
		 ." FROM objects o,".$tqMain
		 ." WHERE o.id=tqMain.obj_id AND NOT o.img_path IS NULL LIMIT ".$pageSize;
	} else {
		$tqFull =
		"SELECT SQL_CALC_FOUND_ROWS o.id, o.objnum, o.name, o.description, o.img_path, o.img_ar"
		 ." from objects o,".$tqMain." where o.id=tqMain.obj_id limit ".$pageSize;
	}
	if( $pageNum > 0 )
		$tqFull .= " OFFSET ".($pageSize*$pageNum);
	return $tqFull;
}

function prepareResultsCatsQuery( $tqMain, $nokwds, $countsWithImages ) {
	// If kwds is non-empty, we put the wImgs constraint in that subquery
	if( $countsWithImages && $nokwds) {
		$tqCountsByCat =
			"SELECT c.id, c.parent_id, c.facet_id, c.display_name, count(*) FROM categories c,"
				." (SELECT oc.obj_id, oc.cat_id from obj_cats oc, objects o, "
			.$tqMain
			." WHERE oc.obj_id=tqMain.obj_id AND o.id=tqMain.obj_id AND NOT o.img_path IS NULL) tqTop"
			." WHERE c.id=tqTop.cat_id GROUP BY c.id ORDER BY c.id";
	} else {
		$tqCountsByCat =
			"SELECT c.id, c.parent_id, c.facet_id, c.display_name, count(*) from categories c,"
			." (SELECT oc.obj_id, oc.cat_id from obj_cats oc,"
			.$tqMain
			." where oc.obj_id=tqMain.obj_id) tqTop"
			." where c.id=tqTop.cat_id group by c.id order by c.id";
	}
	return $tqCountsByCat;
}

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
// The cats should have been ordered for efficiency before calling this.
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
		return "(SELECT oc.obj_id from obj_cats oc,".$subQ
						 ." where oc.obj_id=subK.obj_id and oc.cat_id="
							.(string)$catIDList[$iID].") ".$qName;
	}
	// Not the last one, so we build the return string as a join on this and the recursively
	// computed string
	$subQ = buildMainQueryForTerm( $catIDList, $iID+1, $kwds, $wImgs );
	$subName = "sub".($iID+1);
	return "(SELECT oc.obj_id from obj_cats oc,".$subQ
					 ." where oc.obj_id=".$subName.".obj_id and oc.cat_id="
						.(string)$catIDList[$iID].") ".$qName;
}

?>
