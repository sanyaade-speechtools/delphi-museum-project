<?php
session_cache_limiter("public");		// enable caching of this page

require_once("Facet.inc");
require_once("onto_utils.php");

// Utility function to ensure that we have initialized the array of facets
// in use in this ontology.
function checkAndLoadFacets() {
	global $facets;
	global $db;
	if( count($facets) <= 0 ) {
		$facetsResults =& $db->query(
			"SELECT id, display_name, description, notes from facets order by id");
		if (PEAR::isError($facetsResults)) {
				echo "ERROR: ".$facetsResults->getMessage();
				return false;
		}
		GetFacetListFromResultSet($facetsResults);
	}
}

// Returns an array of structured info about the facets in the ontology
// Each item in the indexed (not associative) array has the following fields:
// "id", "name", "description" and "notes".
function getFacets() {
	global $facets;
	checkAndLoadFacets();
	if( count($facets) == 0 ) {
		return false;		// Can't find any facets in DB - just bail
	}
	$retVal = array();
	foreach( $facets as $facet ) {
		$retVal[] = array( "id" => $facet->id, "name" => $facet->name,
											 "description" => $facet->description, "notes" => $facet->notes );
	}
	return $retVal;
}

// Searches for a facet with a given name.
// Returns facet id if found, or -1.
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

// Returns an associative array with two values:
// "allObjs" => the count of all objects in the system
// "withImgs" => the count of all objects that have at least one image
function getNumObjs() {
	global $db;
 	$allCountsResults =& $db->query("SELECT n_objs_total, n_objs_w_imgs FROM DBInfo LIMIT 1");
	if($row = $allCountsResults->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		return array( "allObjs" => $row[0], "withImgs" => $row[1] );
	} else
		return false;
}

// Gets all the categories in the named facet, down to the specified depth
// To get info on all facets, pass "__ALL" as the facet name.
// $retType controls what form the returned information takes:
//  "PHP":
//     Returns an array (of length 1 for a single facet) of facet info structures: 
//      "id", "name", "description" and "notes" contain basic info and
//      "items" has a nested array of category information structures:
//      "id", "name", "count" (the number of objects associated to the category)
//       and "children" has the nested array of children categories.
//  "JSON":
//     Not Yet Implemented - will produce the PHP structure and then
//      convert this to a JSON form and return it.
//  "HTML_UL":
//     Returns a string that contains HTML for the information, formatted as
//      a nest unordered list (UL tags).
//  "HTML_UL_ATAG":
//     Returns a string that contains HTML for the information, formatted as
//      a nest unordered list (UL tags), but with anchor tags on each category name.
//      $HTparam specifies the base of the hyperlink, to which the id is appended.
//  "HTML_UL_ATTR":
//     Returns a string that contains HTML for the information, formatted as
//      a nest unordered list (UL tags), and with id info in an attribute.
//      $HTparam specifies the name of the attribute
//  "HTML_UL_OC":
//     Returns a string that contains HTML for the information, formatted as
//      a nest unordered list (UL tags), and with onclick handler
//      $HTparam specifies the name of the event handler
//
// If $countsWithImages is true, then all counts are for objects with images,
// Else, the counts are for total objects.
// TODO need to allow getting both counts at once.
// $depth specifies the maximum depth to traverse the ontology.
function getCategoriesInFacet(
						$facetname,					//:String
						$countsMode,				//:String: "withImg", "all", "both"
						$retType, 					//:String 
						$HTparam,						//:String
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
	$fBothCounts = false;
	if( $countsMode == "withImg" ) {
		$countField = "n_matches_w_img";
		$countCheck = "n_matches_w_img>0";
	}	else if( $countsMode == "all" ) {
		$countField = "n_matches";
		$countCheck = "n_matches>0";
	} else {// assume "both" 
		$countField = "n_matches_w_img, n_matches";
		$countCheck = "(n_matches_w_img>0 OR n_matches>0)";
		$fBothCounts = true;
	}

 	$tqCountsByCat = "select id, parent_id, facet_id, display_name, ".$countField
 										  ." from categories where ".$countCheck;
	if( $fid >= 0 ) {
 		$tqCountsByCat .= " AND facet_id=".$fid;
 	}
	$tqCountsByCat .= " order by id";
 	$countsresult =& $db->query($tqCountsByCat);
 	PopulateFacetsFromResultSet( $countsresult, $fBothCounts );
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
							$items = $facet->GenerateHTMLItems( 0, $depth, 1, true, 2, "none", "" );
							break;
						case "HTML_UL_ATAG":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $depth, 1, true, 2, 'A_Tag', $HTparam );
							break;
						case "HTML_UL_ATTR":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $depth, 1, true, 2, 'attr', $HTparam );
							break;
						case "HTML_UL_OC":
							//error_log( "getCategoriesInFacet() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $depth, 1, true, 2, 'onclick', $HTparam );
							break;
					}
				// Only add a new item if there are children for that facet
				$facetVal = array( 'facet' => $facet->name,
													 'desc'  => $facet->description,
													 'notes' => $facet->notes,
													 'id'    => $facet->id,
													 'items' => $items );
				$retVal[] = $facetVal;
			}
		}
	}
	return $retVal;
}


// Gets all the categories associated with the passed object id.
// the rest of the parameters function as for getCategoriesInFacet()
function getCategoriesForObject(
						$forObjID,					//:int ID of object in DB
						$facetname,					//:String 1 facet name or "__ALL"
						$countsWithImages,	//:boolean
						$retType,						//:String "PHP", "JSON" or "HTML_UL*"
						$HTparam ) {				//:String
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
 	PopulateFacetsFromResultSet( $countsresult, false );
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
							//error_log( "getCategoriesForObject() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, true, 1, "none", "" );
							break;
						case "HTML_UL_ATAG":
							//error_log( "getCategoriesForObject() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, true, 1, 'A_Tag', $HTparam );
							break;
						case "HTML_UL_ATTR":
							//error_log( "getCategoriesForObject() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, true, 1, 'attr', $HTparam );
							break;
						case "HTML_UL_OC":
							//error_log( "getCategoriesForObject() dumping HTML for: ".$facet->name);
							$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, true, 1, 'onclick', $HTparam );
							break;
					}
				// Only add a new item if there are children for that facet
				$facetVal = array( 'facet' => $facet->name,
													 'desc'  => $facet->description,
													 'notes' => $facet->notes,
													 'id'    => $facet->id,
													 'items' => $items );
				$retVal[] = $facetVal;
			}
		}
	}
	return $retVal;
}

// Searches for categories that are tied to the passed keyword
// by name or synonym.
// Returns an object (array) with two array properties: 
//   'cats' and 'kwds'. These are the found category ids, and
//   all remaining keywords, respectively.
//   
// Will accept partial name matches, but prefers a full name.
// For categories in a given facet, if one is an ascendant of another,
// this will ignore the descendant.
// Ideally, we would favor the pure match categories over the partials,
// requiring that we pass back an array of pairs that indicate the weight.
// I would set the weight as (# words in kwd)/(# words in category hook).
// The passed Keyword may be multiple words, including quotation delimiters.
// We need to consider things like "wood mask" as two keywords, but recognize
// "San Francisco" as one. We'll have to be careful with this, since "San" will
// match a lot of other locations. If the full string (or some n-gram substring)
// matches in a given facet, that should preclude looking for matches on the words in
// the n-gram (but not the other words, as in "west african wood masks" which should
// match a location, a material and a use.
// Select as OR and then group by obj id summing the number
// of categories that were matched and ordering on that. Give text-only matches a "1".
// BUT: If get exact match, then add as a category with an AND, not an OR. 
// E.g., west african wood masks should be an AND of three categories.
// Once we do this, we should promote the kwdCats to params so we do not repeat.
function getCategoryIDsForKwds(
						$kwdStr,
						$countsWithImages ) {	//:boolean
	//global $facets;
	global $db;
	$countCol = $countsWithImages ? "n_matches_w_img" : "n_matches";
	$retval = array( 'cats' => array(), 'kwds' => array(), 'msg' => null, 'query' => null );

	// Split on the quotes, then spaces to get token/words
	$tokens = splitKwdString($kwdStr);

	// Try longest n-grams first, and if get categories, drop all those words.
	// This means the n-grams must have the string, the start index and length.
	// If just order n-grams by length, then can proceed through list.
	// Must also filter later n-grams contained by a matched n-gram, but see these
	// easily by finding any n-gram with start-index from matched_start_index down
	// to (matched_start_index - matched_length).
	// For looping to work, must either be while(more) based or have markers to ignore.
	// If put words into list as unigrams, then have unified loop.
	// When done with loop, should have some set of categories.
	// Now have to find ascendant/descendant relations and prune lower ones.
	// Last, should we consider facets in this? Not sure; for now, no.

	// Double check error condition - no params in.	
	$nTokens = count($tokens);
	if( $nTokens <= 0 ) {
		$retval['msg'] = 'No tokens, so just returning nothing!';
		return $retval;
	}
	
	$ngrams = array();
	// if only one word, build an ngram item from the one token
	if( $nTokens == 1 ) {
		$ngrams[] = array( 'len'   => 1,
											 'start' => 0,
											 'ngram' => strtolower($tokens[0]) );
		$nNgrams = 1;
	} else {
		$ngrams = buildNGramsFromTokens($tokens);
		$nNgrams = count( $ngrams );
		if( $nNgrams <= 0 ) {
			// Should complain somehow
			$retval['kwds'] = requoteMultiTermKwdTokens($tokens);
			$retval['msg'] = 'Could not create nGrams from tokens???';
			return $retval;
		}
	}

	// TODO set up as a prepared and parameterized query.
	$tqCatsForKwds = "select c.id cid, c.parent_id pid, c.facet_id fid,"
										." LOWER(hk.token) token, CHAR_LENGTH(hk.token) as tlen,"
										." c.".$countCol." count from categories c, hooks hk"
										." where c.id=hk.cat_id AND (";
	// Should we put in the like matches as well? We can match against the tokens
	// and figure out which are proper matches and which not.
	// Not now - TODO?
	for( $i = 0; $i < $nNgrams; $i++ ) {
		if( $i > 0 )
			$tqCatsForKwds .= " OR ";
		$tqCatsForKwds .= "hk.token='".$ngrams[$i]['ngram']."'";
	}
	// We sort by token length to pull out longest n-grams.
	// We use a secondary sort on count, so if we get multiple matches, we
	// choose the category with the most associated objects.
	$tqCatsForKwds .= ") ORDER BY tlen desc, count desc";
	// Only for debug!!!
	$retval['query'] = $tqCatsForKwds;
 	$catsresult =& $db->query($tqCatsForKwds);
	if (PEAR::isError($catsresult)) {
		error_log( "getCategoryIDsForKwds() Query error: ".$catsresult->getMessage());
		error_log( "getCategoryIDsForKwds() Query : ".$tqCatsForKwds);
		// Fall back to just returning the keywords as input.
		$retval['kwds'] = requoteMultiTermKwdTokens($tokens);
		$retval['msg'] = "getCategoryIDsForKwds() Query error: ".$catsresult->getMessage();
		return $retval;
	}

	$catsFound = array();
	$retval['msg'] .= "Query returned ".$catsresult->numRows()." rows...";
	while( $row=$catsresult->fetchRow() ) {
		// We have to make sure that the token is still in the list we're considering.
		// If we have "West African" and match the full token, we do not want to also
		// match the token "African".
		// This will also filter out multiple matches of a given token (preferring first).
		$iMatch = -1;
		for( $i = 0; $i < $nNgrams; $i++ ) {
			if( !strcmp( $ngrams[$i]['ngram'], $row['token']) ) {
				$iMatch = $i;
				break;
			}
		}
		if( $iMatch >= 0 ) {
			$retval['msg'] .= "<br />nGram match for row with token:".$row['token']." cat:".$row['cid'];
			// We found a match. Add the category to the cats list
			$catsFound[]= $row['cid']; 
			// Now remove this and all overlapping ngrams from the list
			$newNGrams = array();
			$matchStart = $ngrams[$iMatch]['start'];
			$matchEnd = $matchStart + $ngrams[$iMatch]['len']-1;
			// Let's trim the tokens for this nGram from the tokens list
			for( $i = $matchStart; $i <= $matchEnd; $i++ )
				unset($tokens[$i]);
			if( count($tokens) == 0 ) {
				$retval['msg'] .= "<br />Matched all tokens to cats";
				break;	// We're done
			}

			for( $i = 0; $i < $nNgrams; $i++ ) {
				// If this is the one we matched, skip it
				if( $i == $iMatch )
					continue;
				// OR if curr start is within the range of the matched one, skip it
				$currStart = $ngrams[$i]['start'];
				if(($currStart >= $matchStart) && ($currStart <= $matchEnd))
					continue;
				// OR if curr end is within the range of the matched one
				$currEnd = $currStart + $ngrams[$i]['len']-1;
				if(($currEnd >= $matchStart) && ($currEnd <= $matchEnd))
					continue;
				// If we reach here, the current nGram does not overlap with the matched one
				$newNGrams[] = $ngrams[$i];
			}
			$ngrams = $newNGrams;
			$nNgrams = count($newNGrams);
			// If we've covered all the n-grams, then there's no point considering
			// more of the query results.
			if( $nNgrams <= 0 )
				break;
		} else {
			$retval['msg'] .= "<br />No nGram match for row with token:"
											.$row['token']." cat:".$row['cid'];
		}
	}
	$retval['cats'] = $catsFound;
	// collapse all the unset values and re-index
	$retval['kwds'] = requoteMultiTermKwdTokens(array_values($tokens));

	return $retval;
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
	//$tqMain = buildMainQueryForTerm( $qCatIDs, 0, $kwds, $countsWithImages );
	$tqFull = prepareObjsQuery( $qCatIDs, $kwds, $countsWithImages, $pageNum, $pageSize );
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
						$catIDs,					//:int[] array of category IDs
						$kwds,						//:String[] array of keyword names
						$countsWithImages,			// :boolean
						$retType,					//:String "PHP", "JSON" or "HTML_UL"
						$HTparam 					//:String
						) {					
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
	//$tqMain = buildMainQueryForTerm( $qCatIDs, 0, $kwds, $countsWithImages );
	$tqFull = prepareResultsCatsQuery( $qCatIDs, $kwds, $countsWithImages );
	$catsresult =& $db->query($tqFull);
	if (PEAR::isError($catsresult)) {
		error_log( "queryResultsCategories() main Query error: ".$catsresult->getMessage());
		error_log( "queryResultsCategories() Query : ".$tqFull);
		return false;
	}
	//error_log( "queryResultsCategories() Query : ".$tqFull);

 	PopulateFacetsFromResultSet( $catsresult, false );
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
						//error_log( "queryResultsCategories() dumping HTML for: ".$facet->name);
						$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, false, 1, "none", "" );
						break;
					case "HTML_UL_ATAG":
						//error_log( "queryResultsCategories() dumping HTML for: ".$facet->name);
						$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, false, 1, 'A_Tag', $HTparam );
						break;
					case "HTML_UL_ATTR":
						//error_log( "queryResultsCategories() dumping HTML for: ".$facet->name);
						$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, false, 1, 'attr', $HTparam );
						break;
					case "HTML_UL_OC":
						//error_log( "queryResultsCategories() dumping HTML for: ".$facet->name);
						$items = $facet->GenerateHTMLItems( 0, $largeNum, 1, false, 1, 'onclick', $HTparam );
						break;
				}
				// Only add a new item if there are children for that facet
				$facetVal = array( 'facet' => $facet->name,
													 'id'    => $facet->id,
													 'desc'  => $facet->description,
													 'notes' => $facet->notes,
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
	$tqCatOrder .= " order by n asc";
	$catsQResult =& $db->query($tqCatOrder);
	// $t->assign("tqCatOrder", $tqCatOrder);
	// unset($catIDs);
	$qCatIDs = array();
	while($row = $catsQResult->fetchRow(MDB2_FETCHMODE_ORDERED)) {
		$qCatIDs[] = $row[0];
	}
	return $qCatIDs;
}

// Assumes that any categories have already been sorted for query-opt.
function prepareObjsQuery( $qCatIDs, $kwds, $withImages, $pageNum, $pageSize ) {
	$tq = "SELECT SQL_CALC_FOUND_ROWS o.id, o.objnum, o.name, o.description, o.img_path, o.img_ar";
	if( empty($qCatIDs) ) {
		if( empty($kwds))
			die("prepareObjsQuery: no categories and no keywords!");
		// No relevance ranking for keywords-only search
		$tq .= ", MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) score 
		     from objects o where MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) ";
		if( $withImages )
			$tq .= " AND NOT o.img_path IS NULL";
	} else { // Have some concepts to search on
		$nCats = count($qCatIDs);
		// First, tack on the relevance computation to the select fields
		$tq .= ",";
		for($i=0; $i<$nCats; $i++) {
			if($i>0)
				$tq .= "*";
			$tq .= "oc".($i+1).".reliability";
		}
		if( !empty($kwds) )
			$tq .= " * MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) ";
		$tq .= " score 
		FROM objects o";
		// Now, tack on the self-join tables for the category matching
		for($i=0; $i<$nCats; $i++) {
			$tq .= ", ".($withImages?"obj_cats_wimgs":"obj_cats")." oc".($i+1);
		}
		// Now, tack on the self-join statements
		for($i=0; $i<$nCats; $i++) {
			$tq .= ($i==0)?" WHERE ":" AND ";
			$tq .= "o.id=oc".($i+1).".obj_id AND oc".($i+1).".cat_id=".$qCatIDs[$i];
		}
		if( !empty($kwds) )
			$tq .= " AND MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) ";
		// Note that if we only want objs with images, this is covered by using
		// the custom obj_cats table that only associates to such objects. We
		// can omit the explicit qualifier in the query.
	}
	$tq .= " order by score desc limit ".$pageSize;
	if( $pageNum > 0 )
		$tq .= " OFFSET ".($pageSize*$pageNum);
	return $tq;
}

function prepareResultsCatsQuery( $qCatIDs, $kwds, $withImages ) {
	$tq = "SELECT c.id, c.parent_id, c.facet_id, c.display_name, count(*) "
				."FROM categories c, ".($withImages?"obj_cats_wimgs":"obj_cats")." oc0";
	if( empty($qCatIDs) ) {
		if( empty($kwds))
			die("prepareResultsCatsQuery: no categories and no keywords!");
		$tq .= ", objects o where MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) "
					."AND oc0.obj_id=o.id AND c.id=oc0.cat_id ";
		if( $withImages )
			$tq .= "AND NOT o.img_path IS NULL";
	} else { // Have some concepts to search on
		if( !empty($kwds) )
			$tq .= ", objects o";
		$nCats = count($qCatIDs);
		// Now, tack on the self-join tables for the category matching
		for($i=0; $i<$nCats; $i++) {
			$tq .= ", ".($withImages?"obj_cats_wimgs":"obj_cats")." oc".($i+1);
		}
		$tq .= " WHERE c.id=oc0.cat_id ";
		// Now, tack on the self-join statements
		for($i=0; $i<$nCats; $i++) {
			$tq .= " AND oc0.obj_id=oc".($i+1).".obj_id AND oc".($i+1).".cat_id=".$qCatIDs[$i];
		}
		if( !empty($kwds) )
			$tq .= " AND oc0.obj_id=o.id AND MATCH(o.name, o.description, o.hiddenNotes) AGAINST('".$kwds."' IN BOOLEAN MODE) ";
		// Note that if we only want objs with images, this is covered by using
		// the custom obj_cats table that only associates to such objects. We
		// can omit the explicit qualifier in the query.
	}
	$tq .= " GROUP BY c.id ORDER BY c.id ";
	return $tq;
}

?>
