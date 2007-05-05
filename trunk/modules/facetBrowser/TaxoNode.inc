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
		var $fHidden;			// Is this category hidden in the output tree
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
		$this->fHidden = false;
		$this->fNoClick = false;
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
		if( in_array($this->id, $catIDs) )
			$this->fNoClick = true;
		if( empty($this->arrChildren )) {
			// If this covers all results, disable link. 
			// Need to get nResults working first.
			//if( $this->nResults == $nResultsTotal )
			//	$this->fNoClick = true;
		} else {
			// See if we have children to deal with
			$mustShow = false;
			foreach( $this->arrChildren as $node ) {
				$nSubTree = $node->PruneForOutput($nResultsTotal, $catIDs);
				// If we find a child with fewer results, or a leaf child, show this
				if(( $nSubTree != $this->nResults ) || empty($node->arrChildren ))
						$mustShow = true;
			}
			// If we do not have to show it, mark it hidden
			if( !$mustShow ) {
				$this->fHidden = true;
				//echo1 "Marking node fHidden: ".$this->name;
			}
		}
		return $this->nResults;
	}

	// TODO: need to count not just siblings, but how many entries we have opened
	// up under this facet. We also need to figure out how to collapse some nodes
	// into the children as prefix strings, rather than hiding altogether.
	// We may need to mark categories in the taxonomies as hidable or required,
	// and even specify abbreviations for collapsing. E.g., "US:" before "Kansas"
	function GenerateHTMLOutput( $class, $depth, $nSiblings, $pageURL, $fFullTree , $fOmitDetails ) {
		
		//var to hold concated output
		$responseOutput = "";
		
		if( $this->fPrune ) {
			//echo1 "<p>Pruned: ".$this->name."</p>";
			return;
		}

		/*
		if( $this->fHidden && $class == "facet" ) {
			echo1 "<div id=\"N_".$this->id."\" class=\"".$class." open\"><span>$this->name</span>";
		}
		*/
		if( $class == "facet" ) 
			$span = "<span class=\"title\">";
		else
			$span = "<span>";

		$nChildren = count($this->arrChildren);
		// If we are in full tree mode, then close up everything to the roots.
		// if(!$fFullTree && (($depth == 0)
		// 
		if(($depth <= 0)
			|| ($nSiblings == 1)
			|| (($depth <= 1) && ($nSiblings < 3))
			|| $this->fNoClick) {
			$oc = " open";
			$doChildren = ($nChildren > 0);
			} else {
			$oc = " closed";
			// If a full tree, of if this is hidden, and we have children, recurse
			$doChildren = ($this->fHidden || $fFullTree) && ($nChildren > 0);
			}

		// Distinguish between facet and node, since there might be ID overlap
		$nid = (($class == "facet")?"F_":"N_").$this->id;

		if( !$this->fHidden || $class == "facet" ) {
			$responseOutput .= "<div id=\"".$nid."\" class=\"".$class.$oc."\">".$span;
			if( $fOmitDetails ) {
				// Put in a spacer that approximates the images if we are omitting them
				if( $class != "facet" )
					$responseOutput .= "<span>-&nbsp;</span>";
			} else {
				if( !$doChildren )
					$responseOutput .= "
			<img class=\"treeitem\" src=\"images/leafmrkr2.gif\" width=\"9\" height=\"9\" />";
				else {
					$responseOutput .= "
			<img class=\"open ".$class."\" src=\"images/collapsebtn.gif\" width=\"9\" height=\"9\" 
				onclick=\"toggleVis( ".$nid.");\" />
			<img class=\"closed ".$class."\" src=\"images/expandbtn.gif\" width=\"9\" height=\"9\" 
				onclick=\"toggleVis( ".$nid.");\" />";
				}
			}
			if( $class == "facet" )
				$responseOutput .= " $this->name</span>";
			else if( $this->fNoClick )
				$responseOutput .= " <strong>".$this->name."</strong> (".$this->nResults.")
						</span>";
			else {
				$responseOutput .= "<a href=\"".$pageURL;
				$responseOutput .= $this->id."\"> ".$this->name;
				if( !$fOmitDetails )
					$responseOutput .= " (".$this->nResults.")";
				$responseOutput .= "</a>
				</span>";
			}
		}
		if($doChildren) {
			//$responseOutput .= "Node: ".$this->name." has ".count($this->arrChildren)." children.";
			foreach( $this->arrChildren as $child ) {
				if( !$child->fPrune )
					$responseOutput .= $child->GenerateHTMLOutput( "treeitem", $depth+1,
																	$nChildren, $pageURL, $fFullTree, $fOmitDetails );
			}
		}
		if( !$this->fHidden || $class == "facet") {
			$responseOutput .= "</div>";
		}
		
		return $responseOutput;
	}
	
} // TaxoNode class
?>
