<?php
	require_once "TaxoNode.inc";

class Facet extends TaxoNode {
	var $supports;			// "supports" in facetmap schema
	var $description;		// <description> child in schema
	var $notes;					// <notes> child in facetmap schema

	function __construct( $name, $id, $description, $notes, $supports ) {
		parent::__construct($name, $id, null, null, false, false);
		$this->supports = $supports;
		$this->description = $description;
		$this->notes = $notes;
	}
}

	$facets = array();
	$taxoNodesHashMap = array();

	// Expects results in rows of 2 columns: id and name
	function GetFacetListFromResultSet( $result )	{
		global $facets;
		while ($row = $result->fetchRow(MDB2_FETCHMODE_ORDERED)) {
			$id = $row[0];
			$name = $row[1];
			$description = $row[2];
			$notes = $row[3];
			$facet = new Facet( $name, $id, $description, $notes, null );
			$facets[$id] = $facet;
		}
	}

	// Expects results in rows of 3 or 4 columns: id, parent_id, display name, count.
	// The count column is only expected if fWithCounts is true
	function PopulateFacetsFromResultSet( $result, $fBothCounts )	{
		global $facets;
		global $taxoNodesHashMap;
		while ($row = $result->fetchRow(MDB2_FETCHMODE_ORDERED)) {
			$id = $row[0];
			$p_id = $row[1];
			$f_id = $row[2];
			$name = $row[3];
			if( $p_id == null ) {
				$parent = null;
				if( $f_id != null )
					$parent = $facets[$f_id];
				if( $parent == null )
					die("<p>Error building facet tree: Missing facet for taxo root:".$name." [".$id."]</p>");
			} else {
				$parent = $taxoNodesHashMap[$p_id];
				if( $parent == null )
					die("<p>Error building facet tree: Missing parent for node:".$name." [".$p_id."]</p>");
			}
			$node = new TaxoNode( $name, $id, $f_id, $parent, true, false );
			$taxoNodesHashMap[$id] = $node;
			$parent->AddChild($node);
			// If single count, the right count is in col 4
			$node->nResults = $row[4];
			$node->nResults2 = ( $fBothCounts ) ? $row[5] : 0;
		}
	}
?>
