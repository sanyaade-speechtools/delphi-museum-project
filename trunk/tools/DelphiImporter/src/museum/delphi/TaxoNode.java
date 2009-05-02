package museum.delphi;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Node;

public class TaxoNode {
	static int nextID = 1;		// used to synthesize ID values

	public String name;						// Called "id" in facetmap schema
	public String displayName;				// "title" in facetmap, unused in keyword tree
	public String controlName;				// From TMS - used to map to parents
	public int id;							// Becomes categoryid in DB
	public int termID;						// As per TMS
	public int facetid;						// With which facet is this associated?
	public TaxoNode parent;					// There is only one parent for now
	public ArrayList<TaxoNode> children;	// No children until specified
	public String sort;						// null if no sort specified
	public ArrayList<String> synset;	// Synonyms/alt terms for this node
	public ArrayList<String> exclset;	// Synonyms/alt terms for this node
	// This is a list of TaxoNodes, which may be in other facets.
	// Since each Taxonode is tied to a facet, this is okay, and just
	// affords arcs across facets, making a linked graph.
	public ArrayList<TaxoNode> impliedNodes;	// Other than ascendants
	// We may get forward references to nodes, and keep them to resolve later
	// The references are by facet and category names
	public ArrayList<Pair<String, String>> impliedNodesPending;
	public boolean inferredByChildren;		// Is this node implied by children
	public boolean isGuideTerm;				// Is this node implied by children
	public boolean selectSingle;			// For facet map UI
	public int iMaskBase;					// First mask for this
	public int nMasks;						// How many masks does this subtree need
	public int iBitInMask;					// bit number for this node
	public Node elementNode;				// Used when working with XML and this

	protected int _debugLevel = 1;

	// Simple constructor uses defaults for synset and inferredByChildren
	// This is for the trivial keyword tree case
	public TaxoNode( String name, int termID, TaxoNode parent, String controlName, boolean isGuide ) {
		// No displayname, facetid or sort, and default values for others
		this( name, null, controlName, -1, termID, -1, parent, null, isGuide, true, false );
	}

	// Complex constructor for categories
	public TaxoNode(
			String		name,
			String		displayName,
			String		controlName,
			int				id,
			int				termID,
			int				facetid,
			TaxoNode	parent,
			String		sort,
			boolean		isGuide,
			boolean		inferredByChildren,
			boolean		selectSingle ) {
		this.name = name;
		this.displayName = displayName;
		this.controlName = controlName;
		if( id < 0 )
			id = nextID++;
		this.id= id;
		this.termID= termID;
		this.facetid= facetid;		// Only applies to categories
		this.parent = parent;
		this.sort = sort;
		this.isGuideTerm = isGuide;
		this.inferredByChildren = inferredByChildren;
		this.selectSingle = selectSingle;
		children = null;
		synset = null; 		// No syns unless specified
		exclset = null; 	// No exclusions unless specified
		impliedNodes = null; 	// No implied (other than parent) unless specified
		iMaskBase = -1;
		nMasks = 0;
		iBitInMask = -1;
	}

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof TaxoNode)) return false;

	    final TaxoNode tnode = (TaxoNode) o;

	    return id == tnode.id;
  	}

	public boolean isRoot() {
		return parent==null;
	}

	public void AddBitMaskAssignment( int iMaskToSet, int iBitInMaskToSet,
										boolean verbose ) {
		if( iMaskBase >= 0 ) {
			if(( iMaskToSet < (iMaskBase+nMasks) )
					|| ( iMaskToSet > (iMaskBase+nMasks) ))
				throw new RuntimeException(
						"Trying to set conflicting mask info for: " + name
						+ " MaskBase is: " + iMaskBase
						+ " nMasks : " + nMasks
						+ " trying to set: " + iMaskToSet );
			if( iBitInMask != iBitInMaskToSet )
				throw new RuntimeException(
						"Trying to set conflicting bit in mask info for: " + name
						+ " Prev is: " + iBitInMask
						+ " new is : " + iBitInMaskToSet );
			// So we just need to add one to the mask
			nMasks++;
			if( verbose )
				System.out.printf( "Added mask info for ["+name+"] nmasks:["
					+ nMasks+"]\n" );
		}
		else {
			iMaskBase = iMaskToSet;
			nMasks = 1;
			iBitInMask = iBitInMaskToSet;
			if( verbose )
				System.out.printf( "Set mask info for ["+name+"] mask:["
					+iMaskBase+"] bit: " + iBitInMask+"\n" );
		}
	}

	public String GetPathToNode() {
		if( parent == null )
			return "/" + name;
		else
			return parent.GetPathToNode() + "/" + name;
	}

	boolean AddSynonym( String synname ) {
		if( synset == null ) {
			synset = new ArrayList<String>(4);
		} else if( synset.contains(synname)) {
			return false;
		}
		synset.add(synname);
		return true;
	}

	boolean AddExclusion( String exclname ) {
		if( exclset == null ) {
			exclset = new ArrayList<String>(2);
		} else if( exclset.contains(exclname)) {
			return false;
		}
		exclset.add( exclname );
		return true;
	}

	void AddChild( TaxoNode child ) {
		if( children == null )
			children = new ArrayList<TaxoNode>();
		children.add( child );
		if( child.parent != this )
			System.out.println( "Added child that is not pointing to parent!!!");
	}

	public int GetNumDescendents() {
		int nDesc = 0;
		if(( children != null ) && ( children.size() > 0 ))
			for( int i=0; i<children.size(); i++ ) {
				TaxoNode child = children.get(i);
				nDesc++;	// One for the node, then add descendents
				nDesc += child.GetNumDescendents();
			}
		return nDesc;
	}

	// Add another reference to a category that this category infers.
	// This is an id in the global ontology namespace.
	boolean AddImpliedNode( String facet, String cat ) {
		Pair<String, String> newPair = new Pair<String, String>( facet, cat );
		if( impliedNodesPending == null )
			impliedNodesPending = new ArrayList<Pair<String, String>>();
		// Check to see if there already, and if so, return false
		else if( impliedNodesPending.contains( newPair ))
			return false;
		impliedNodesPending.add( newPair );
		return true;
	}

	boolean ResolvePendingImpliedNodes( DoubleHashTree map ) {
		if(( impliedNodesPending == null ) || ( impliedNodesPending.size() <= 0 ))
			return false;
		while(!impliedNodesPending.isEmpty()) {
			Pair<String, String> pair = impliedNodesPending.remove(0);
			TaxoNode implied = map.FindNodeByName(pair.first, pair.second);
			if( implied != null ) {
				AddImpliedNode( implied );
				debug( 2, "Resolved implied link from [" + displayName + "("+id+")] to ["
					+pair.first + " : " + pair.second + "("+implied.id+")]" );
			} else {
				debug( 1, "Could not resolve implied link from [" + displayName + "("+id+")] to ["
						+pair.first + " : " + pair.second + "]" );
			}
		}
		return true;
	}

	// Add another reference to a category that this category infers.
	// This is an id in the global ontology namespace.
	boolean AddImpliedNode( TaxoNode implied ) {
		if( impliedNodes == null )
			impliedNodes = new ArrayList<TaxoNode>();
		// Check to see if there already, and if so, return false
		else if( impliedNodes.contains( implied ))
			return false;
		impliedNodes.add( implied );
		return true;
	}

	@Override
	public String toString(){
		// String str = "TaxoNode{ "+name+", ID:"+id+", tID:"+termID+", "+controlName;
		String str = "TaxoNode{ "+name+", ID:"+id;
		if(parent!=null)
			str += ", parent:"+parent.name;
		if(facetid>=0)
			str += ", facetid:"+facetid;
		str += "}";
		return str;
	}

	// This checks whether either node is an ascendant of the other.
	// Returns 0 if the nodes are not related in the tree
	// Returns -1 if tag1 is an ascendant of tag2
	// Returns 1 if tag2 is an ascendant of tag1
	public static int CmpAscendancy( TaxoNode node1, TaxoNode node2 ) {
		if( node1 == null || node2 == null )
			throw new RuntimeException( "Bad node passed to CmpAscendancy" );
		TaxoNode par = node1.parent;
		while( par != null ) {
			if( par == node2 )
				return 1;  // Tag 2 is ascendant of tag1
			par = par.parent;
		}
		// Okay, so tag2 is not above tag1. Look the other way
		par = node2.parent;
		while( par != null ) {
			if( par == node1 )
				return -1;  // Tag1 is ascendant of tag2
			par = par.parent;
		}
		// If we got here, the nodes are not related
		return 0;
	} // CmpAscendancy

	/**** OBSOLETE - rewrite to follow pattern of other form, below
	public void AddInferredNodes(ArrayList<TaxoNode> list) {
		// while parents not null and inferredByChidren, add to list.
		// If find a node already in list, can assume we're done, since its
		// inferred nodes must also already be there
		// TODO include the implies nodes for each node, and not just ascendants
		TaxoNode ascendant = parent;
		while( ascendant != null && ascendant.inferredByChildren
				&& !list.contains(ascendant)) {
			list.add(ascendant);
			ascendant = ascendant.parent;
		}
	}
	*/

	public void AddInferredNodes(HashMap<TaxoNode, Float> map, float minValue ) {
		// We add the nodes in our inferred list, and recurse for them as well.
		// We stop when we find a node already in list and with same or higher
		//  confidence. This is efficient, and ensures we break inference cycles.
		if( impliedNodes != null && !impliedNodes.isEmpty()) {
			for(TaxoNode impliedNode:impliedNodes ) {
				Float ret = map.get(impliedNode); // returns null if not in hashMap
				float priorVal = (ret==null)?0:ret.floatValue();
				if( minValue > priorVal ) {	// If not found, or lower confidence
					map.put(impliedNode, minValue);					// update in map
					if( priorVal == 0 )
						debug( 2, "Adding implied concept [" + impliedNode.name + "]");
					impliedNode.AddInferredNodes(map, minValue);	// Recurse to handle its implieds
				}
			}
		}
		// Now we handle our parent as well, if it is inferred by this
		if( parent != null && parent.inferredByChildren ) {
			Float ret = map.get(parent); // returns null if not in hashMap
			float priorVal = (ret==null)?0:ret.floatValue();
			if( minValue > priorVal ) {	// If not found, or lower confidence
				map.put(parent, minValue);				// update in map
				if( priorVal == 0 )
					debug( 2, "Adding implied concept [" + parent.name + "]");
				parent.AddInferredNodes(map, minValue);	// Recurse to handle its implieds
			}
		}
	}

} // TreeNode class

