<?php
class TaxoNode {
		var $name;					// Called "id" in facetmap schema
		var $id;						// categoryid equivalent to that in DB
		var $facet_id;			// ID of owning facet
		var $pParent;				// TaxoNode pointer
		var $arrChildren;		// array of TaxoNodes
		var $fInferredByChildren;
		var $fSelectSingle;		// For UI
		// var $iMaskBase;
		// var $nMasks;
		// var $iBitInMask;
		var $nResults;		// nResults associated to this category
		var $nResults2;		// secondary Results associated to this category
		var $fHidden;			// Is this category hidden in the output tree
		var $fMustShow;		// Set when a child is among query categories. fHidden overrides.
		var $fNoClick;		// Is this category shown, but not clickable
		var $fPrune;			// Should we prune this subtree?
	
	function __construct( $name, $id, $fID, $pParent, $fInferredByChildren, $fSelectSingle ) {
		$this->name = $name;
		$this->id= $id;
		$this->facet_id = $fID;
		$this->pParent = $pParent;
		$this->fInferredByChildren = $fInferredByChildren;
		$this->fSelectSingle = $fSelectSingle;
		$this->arrChildren = null; 
//		$this-iMaskBase = -1;
//		$this-nMasks = 0;
//		$this-iBitInMask = -1;
		$this->nResults = 0;
		$this->nResults2 = 0;
		$this->fHidden = false;
		$this->fNoClick = false;
		$this->fMustShow = false;
		$this->fPrune = false;
	}

	function AddChild( $child ) {
		if( $this->arrChildren == null )
			$this->arrChildren = array();
		$this->arrChildren[] = $child;		// adds at end
		//echo1 "<p>AddChild: ".$child->name." under: ".$this->name;
		//echo1 " now have: ".count($this->arrChildren)." children.</p>";
	}

	function PruneForOutput($nResultsTotal, $catIDs) {
		// if leaf, check hidden and return count
		if( in_array($this->id, $catIDs) ) {
			$this->fNoClick = true;
			$this->fMustShow = true;
		}
		if( empty($this->arrChildren )) {
			// If this covers all results, disable link. 
			// Need to get nResults working first.
			//if( $nResultsTotal>0 && $this->nResults == $nResultsTotal )
			//	$this->fNoClick = true;
		} else {
			// See if we have children to deal with
			$noHide = false;
			foreach( $this->arrChildren as $node ) {
				$nSubTree = $node->PruneForOutput($nResultsTotal, $catIDs);
				// If we find a child with fewer results, or a leaf child, show this
				if(( $nSubTree != $this->nResults ) || empty($node->arrChildren ))
						$noHide = true;
			}
			// If we do not have to show it, mark it hidden
			if( !$noHide ) {
				$this->fHidden = true;
				//echo1 "Marking node fHidden: ".$this->name;
			}
		}
		// If we are set to MustShow (because we are among the query categories,
	  // or one of our descendents is), then propagate up.
		if( $this->fMustShow && $this->pParent != null )
			$this->pParent->fMustShow = true;
		return $this->nResults;
	}

	// TODO: need to count not just siblings, but how many entries we have opened
	// up under this facet. We also need to figure out how to collapse some nodes
	// into the children as prefix strings, rather than hiding altogether.
	// We may need to mark categories in the taxonomies as hidable or required,
	// and even specify abbreviations for collapsing. E.g., "US:" before "Kansas"
	// 
	function GenerateHTMLItems( $depth, $limit, $nSiblings, $fFullTree, $countsFields, 
												$fClickMode, $HTparam ) {
		
		$nChildren = count($this->arrChildren);
		//var to hold concated output
		$responseOutput = ( $depth == 0 )?"<ul>":"";
		
		if( $this->fPrune ) {
			//echo1 "<p>Pruned: ".$this->name."</p>";
			return;
		}

		// If no clicks, then just put out the name.
		// Note: do not close the LI here.
		$nested = true;
		if(( $depth == 0 ) || $this->fHidden )	{
			// Do not generate li for facet - just for it's kids
			// Skip this step for hidden items - we just skip to children
			$nested = false;
		} else if( $this->fNoClick ) {
			$responseOutput .= '<li class="treeCat noClick">'.$this->name;
			if( $countsFields >= 1 )
				$responseOutput .= " (".$this->nResults.")";
			if( $countsFields == 2 )
				$responseOutput .= " (".$this->nResults2.")";
		} else {
			if( $nChildren > 0 )
				$responseOutput .= '<li class="treeCat parent">';
			else
				$responseOutput .= '<li class="treeCat">';
			$open = "";
			$open2 = "";
			$open3 = "";
			$close = "";
			switch( $fClickMode ) {
				default:
					break;
				case "A_Tag":
					$open = '<a href="'.$HTparam.$this->id.'">';
					$open2 = '<a class="c1" href="'.$HTparam.$this->id.'">';
					$open3 = '<a class="c2" href="'.$HTparam.$this->id.'">';
					$close = "</a>
					";
					break;
				case "onclick":
					$open = '<span onclick="'.$HTparam.'(event, '.$this->id.', 1)">';
					$open2 = '<span class="c1" onclick="'.$HTparam.'(event, '.$this->id.', 1)">';
					$open3 = '<span class="c2" onclick="'.$HTparam.'(event, '.$this->id.', 2)">';
					$close = "</span>
					";
					break;
				case "attr":
					$open = '<span '.$HTparam.'="'.$this->id.'">';
					$open2 = '<span class="c1" '.$HTparam.'="'.$this->id.'">';
					$open3 = '<span class="c2" '.$HTparam.'="'.$this->id.'">';
					$close = "</span>
					";
					break;
			}
			$responseOutput .= $open.$this->name.$close;
			if( $countsFields == 1 ) {
				$responseOutput .= " (".$this->nResults.")";
			} else if( $countsFields == 2 ) {
				// When showing both, need to add additional links on the counts
				if( $this->nResults > 0 )
					$responseOutput .= " ".$open2."(".$this->nResults.")".$close;
				else
					$responseOutput .= " (".$this->nResults.")";
				$responseOutput .= " ".$open3."(".$this->nResults2.")".$close;
			}
		}

		if($nChildren <= 0)
			$doChildren = false;
		else {
			// In some cases, we always recurse
			if(($depth <= 0)
					|| ($nSiblings == 1)
					|| (($depth <= 1) && ($nSiblings < 3))
					|| $this->fMustShow) {
				$doChildren = true;
			} else {
				// If a full tree, or if this is hidden and we have children, recurse
				$doChildren = ($this->fHidden || ($fFullTree && ($depth<$limit)));
			}
		}

		if($doChildren) {
			if( $nested )
				$responseOutput .= "<ul>
				";
			foreach( $this->arrChildren as $child ) {
				if( !$child->fPrune )
					$responseOutput .= $child->GenerateHTMLItems( $depth+1, $limit,
																					 $nChildren, $fFullTree, $countsFields,
																					 $fClickMode, $HTparam	);
			}
			if( $nested )
				$responseOutput .= "</ul>
				";
		}
		if( $nested )
			$responseOutput .= "</li>
			";
		
		if( $depth == 0 )
			$responseOutput .= "</ul>
		";
		return $responseOutput;
	}
	
	// Returns an item for this node, including children if there are any.
	// item is an assoc. array with fields 'id', 'name', 'count', 'children'.
	// If this node is marked to prune, returns null.
	// If this node is marked hidden, only "children" is set.
	// If there are no children, then "children" is not set.
	// If $countsFields is 0, omit counts. If 1, show single count. If 2, show a pair.
	function GeneratePHPItems( $depth, $limit, $nSiblings, $fFullTree, $countsFields ) {
		
		if( $this->fPrune ) {
			return null;
		}
		$nChildren = count($this->arrChildren);
		//if( $depth < 2 )
		//	error_log( "GeneratePHPItems for ".$this->name.". Children: ".$nChildren );

		if($nChildren <= 0)
			$doChildren = false;
		else {
			// In some cases, we always recurse
			if(($depth <= 0)
					|| ($nSiblings == 1)
					|| (($depth <= 1) && ($nSiblings < 3))
					|| $this->fNoClick) {
				$doChildren = true;
			} else {
				// If a full tree, or if this is hidden and we have children, recurse
				$doChildren = ($this->fHidden || ($fFullTree && ($depth<$limit)));
			}
		}

		if($doChildren) {
			$children = array();
			foreach( $this->arrChildren as $child ) {
				if( !$child->fPrune ) {
					$children[] = $child->GeneratePHPItems( $depth+1, $limit,
																								$nChildren, $fFullTree, $countsFields );
			  }
			}
		}
		// Only show id, name, etc. for non-hidden nodes below facet level
		if( !$this->fHidden && $depth > 0 ) {
			$item = array( "id" => $this->id, "name" => $this->name );
			if( $countsFields >= 1 )
				$item["count"] = $this->nResults;
			if( $countsFields == 2 )
				$item["count2"] = $this->nResults2;
		} else 
			$item = array();

		if( !empty($children) )
			$item["children"] = $children;
		
		return $item;
	}
	
} // TaxoNode class
?>
