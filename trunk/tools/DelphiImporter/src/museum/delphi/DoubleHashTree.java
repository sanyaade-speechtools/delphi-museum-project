/**
 *
 */
package museum.delphi;
import java.util.*;
//import java.sql.*;

import org.w3c.dom.*;

/**
 * Maintains a tree/graph of nodes with hash-backed accessors for control
 * names as well as termID values.
 * @author pschmitz
 *
 */
public class DoubleHashTree {
	private int _debugLevel = 1;

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	private class DuplicateTaxoNodeException extends RuntimeException {
		public static final long serialVersionUID = 1;

		public DuplicateTaxoNodeException( String str ) {
			super( str );
		}
	}

	// Note that the tree implied by TaxoNode only allows 1 root.
	// However, multiple trees can be in a single hashMap.
	private class FacetInfo {
		// TODO the nameMap should take a CollationKey, not a String
		private HashMap<String, TaxoNode>  hookMap = null;
		private HashMap<String, TaxoNode>  strIDMap = null;
		private HashMap<Integer, TaxoNode> idMap = null;
		private Facet                      facet = null;

		protected FacetInfo(Facet facet) {
			this.facet = facet;
			hookMap = new HashMap<String, TaxoNode>();
			strIDMap = new HashMap<String, TaxoNode>();
			idMap = new HashMap<Integer, TaxoNode>();
		}

		protected void AddTaxoNodeToMap( TaxoNode node, String hook, Integer id ) {
			if( strIDMap.get( node.name ) == null ) {
				if( idMap.get( id ) != null )
					throw new DuplicateTaxoNodeException( "AddTaxoNodeToMap: node not in name map, but in IDmap:"
												+node.toString());
				idMap.put( id, node );
				// If we have a controlName, we use that as the idMap key.
				// Else (more typical case) we use the id ("name" in token class).
				strIDMap.put( ((node.controlName != null)?node.controlName:node.name), node );
				AddNodeToNameMap( node, hook );
			} else if( idMap.get( id ) == null )
				throw new DuplicateTaxoNodeException(
						"AddTaxoNodeToMap: node in name map, but not in IDmap:"+node.toString());
		}

		protected void AddNodeToNameMap( TaxoNode node, String name ) {
			// TODO This should be getting a CollationKey, not mapping to lower
			if( name != null ) {
				String hookL = name.toLowerCase();
				TaxoNode old = hookMap.get(hookL);
				if( old == null )
					hookMap.put( hookL, node );
				else
					debug( 1, "Duplicate hook:["+hookL+"] New node:["+node.name
							+"] precluded by previous:["+old.name+"]");
			}
		}
	}

	public void AddFacet( Facet facet ) {
	    FacetInfo fInfo = new FacetInfo(facet);
	    facetsByName.put(facet.name, fInfo);
	    facetsByID.put(facet.id, fInfo);
	}

	private HashMap<String,FacetInfo> facetsByName = null;
	private HashMap<Integer,FacetInfo> facetsByID = null;
	private int nCatsPerFacetMax = 10000;

	public DoubleHashTree() {
		facetsByName = new HashMap<String,FacetInfo>();
		facetsByID = new HashMap<Integer,FacetInfo>();
	}

	public int totalConceptCount() {
		int total = 0;
		for( FacetInfo fI : facetsByName.values() )
			total += fI.idMap.size();
		return total;
	}

	public int totalTermCount() {
		int total = 0;
		for( FacetInfo fI : facetsByName.values() )
			total += fI.hookMap.size();
		return total;
	}

	public int facetSize(String name) {
		FacetInfo facet = facetsByName.get(name);
		return ( facet == null )? 0:facet.idMap.size();
	}

	public int facetSize(int id) {
		FacetInfo facet = facetsByID.get(id);
		return ( facet == null )? 0:facet.idMap.size();
	}

	public ArrayList<Facet> GetFacets() {
		ArrayList<Facet> ret = new ArrayList<Facet>();
		for( FacetInfo fI : facetsByName.values() )
			ret.add( fI.facet );
		return ret;
	}

	public Set<String> GetFacetNames() {
		return new HashSet<String>(facetsByName.keySet());
	}

	/*
	public Collection<TaxoNode> GetAllTaxoNodes() {
		return idMap.values();
	}
	*/

	public TaxoNode FindNodeByName( int facetID, String name ) {
		FacetInfo facet = facetsByID.get(facetID);
		return ( facet == null )? null:facet.strIDMap.get(name);
	}

	public TaxoNode FindNodeByName( String facetName, String name ) {
		FacetInfo facet = facetsByName.get(facetName);
		return ( facet == null )? null:facet.strIDMap.get(name);
	}

	public TaxoNode FindNodeByHook( int facetID, String hook ) {
		FacetInfo facet = facetsByID.get(facetID);
		// TODO This should be getting a CollationKey, not mapping to lower
		return ( facet == null )? null:facet.hookMap.get(hook.toLowerCase());
	}

	public TaxoNode FindNodeByHook( String facetName, String hook ) {
		FacetInfo facet = facetsByName.get(facetName);
		// TODO This should be getting a CollationKey, not mapping to lower
		return ( facet == null )? null:facet.hookMap.get(hook.toLowerCase());
	}

	public TaxoNode FindNodeByID( int facetID, int id ) {
		FacetInfo facet = facetsByID.get(facetID);
		return ( facet == null )? null:facet.idMap.get(id);
	}

	public TaxoNode FindNodeByID( String facetName, int id ) {
		FacetInfo facet = facetsByName.get(facetName);
		return ( facet == null )? null:facet.idMap.get(id);
	}

	/*
	public void AddNode( String cn, int termID, int parentID ) {
		// Create a node, add to tables, if parent non-null, find
		// and set link.
		TaxoNode parentNode = null;
		if( parentID >= 0 ) {
			parentNode = idMap.get( parentID );
			if( parentNode == null ) {
				if( verbose )
					System.out.println( "Adding node before parent: "
						+ name + " (idx[" + id + "] parentidx["
						 + parentID + "]" );
				// Now we will synthesize an empty parent node for forward refs
				parentNode = new TaxoNode( "<UNK>", parentID, null );
				idMap.put( parentID, parentNode );
			}
		}
		TaxoNode node = idMap.get( id );
		if( node != null ) {
			// See if we are resolving a forward reference
			if( node.name == "<UNK>" ) {
				node.name = name;
				AddNamedNodeToMap( name, node );
				node.parent = parentNode;
				if( verbose )
					System.out.println( "Resolving fwd reference: "
						+ name + " idx[" + id + "] parentidx["
						 + parentID + "]" );
			}
			else if( node.parent != parentNode ) {
				String str = "Adding node: " + name + " under: ";
				str += ((parentNode == null )?"none":parentNode.name);
				str += " but already under: ";
				str += ((node.parent == null )?"none":node.parent.name );
				throw new RuntimeException( str );
			}
		}
		else {
			node = new TaxoNode( name, id, parentNode );
			idMap.put( id, node );
			AddNamedNodeToMap( name, node );
			if( verbose ) {
				System.out.print("Adding " + ((parentNode==null)? "root":"child") + " node:"
						+ name + " (idx[" + id + "]" );
				if( parentNode != null )
					System.out.print( " under parent: [" + parentNode.name + "]" );
				System.out.println();
			}
		}
	} // AddNode
	*/

	protected void AddTaxoNodeToMap( String facetName, TaxoNode node, String name, Integer id ) {
		FacetInfo facet = facetsByName.get(facetName);
		if( facet == null )
			throw new RuntimeException( "AddTaxoNodeToMap: unknown facet: "+facetName);
		facet.AddTaxoNodeToMap(node, name, id);
	}

	protected void AddTaxoNodeToMap( int facetID, TaxoNode node, String name, Integer id ) {
		FacetInfo facet = facetsByID.get(facetID);
		if( facet == null )
			throw new RuntimeException( "AddTaxoNodeToMap: unknown facet: "+facetID);
		facet.AddTaxoNodeToMap(node, name, id);
	}

	// Add an additional name string to the map for this node
	protected void AddNodeToNameMap( String facetName, TaxoNode node, String name ) {
		FacetInfo facet = facetsByName.get(facetName);
		if( facet == null )
			throw new RuntimeException( "AddNodeToNameMap: unknown facet: "+facetName);
		facet.AddNodeToNameMap(node, name);
	}

	protected void AddNodeToNameMap( int facetID, TaxoNode node, String name ) {
		FacetInfo facet = facetsByID.get(facetID);
		if( facet == null )
			throw new RuntimeException( "AddNodeToNameMap: unknown facet: "+facetID);
		facet.AddNodeToNameMap(node, name);
	}

	/*
	public boolean IsRoot( int id ) {
		TaxoNode node = idMap.get( id );
		if( node == null )
			throw new RuntimeException( "Unknown node index passed to IsRoot: " + id );
		return node.isRoot();
	}
	*/

	/*
	public ArrayList<TaxoNode> GetInferredCategories( int forCategory ) {
		TaxoNode nodeForCat = idMap.get( forCategory );
		if( nodeForCat == null )
			throw new RuntimeException(
					"Unknown category id passed to GetInferredCategories: "
					+ forCategory );
		return GetInferredCategories( nodeForCat );
	}
	*/

	public ArrayList<TaxoNode> GetInferredCategories( TaxoNode nodeForCat ) {
		ArrayList<TaxoNode> retList = new ArrayList<TaxoNode>();
		debug( 2, "Checking inferred Categories for : " + nodeForCat.name );
		boolean fAtBase = true;
		while( nodeForCat != null ) {
			if( !fAtBase ) {
				// Except for facets which may not be inferred by children,
				// when we see a parent not inferred, we stop looking
				if( !nodeForCat.inferredByChildren )
					break;
				retList.add( nodeForCat );
				debug( 2, "Adding inferred (parent) Category: "
											+ nodeForCat.name );
			}
			/*
			 * TODO Need to rework this to deal with implied nodes
			 * New name map assumes unique names. This should extent to handle
			 * namespaced ndoes per facet, but then should require unique name in
			 * facet, or else we cannot use them as ID values.
			 * We also have to ensure that we are working with an ID based nameMap.
			 * Can we set a mode for safety, or just be careful??
			 *
			if( nodeForCat.impliedNodes != null )
				for( String infNm : nodeForCat.impliedNodes ) {
					ArrayList<TaxoNode> infList = nameMap.get( infNm.toLowerCase() );
					if( infList == null ) {
						debug( 2, "Category: " + nodeForCat.name
								+ " refers to unknown infers node: " + infNm );
						continue;
					}
					else if( infList.size() != 1 )
						debug(1, "Category: " + nodeForCat.name
								+ " refers to infers node with more than one mapping (NYI): " + infNm );
					TaxoNode inf = infList.get(0);
					if( inf == null )
						debug( 1, "Category: " + nodeForCat.name
								+ " refers to infers node with internal error: " + infNm );
					else {
						retList.add( inf );
						debug(2, "Adding inferred Category: " + infNm );
					}
				}
			  */
			nodeForCat = nodeForCat.parent;
			fAtBase = false;
		}
		return retList;
	}

	protected final String	supportsName		= "supports";
	protected final String	inferName			= "inferFromChildren";
	protected final String	selectModeName		= "selectmode";
	protected final String	displayNameName		= "title";
	protected final String	sortName			= "sort";

	// We assume that this is taking my modified facetMap schema,
	// and so can have multiple taxonomies, inferredBy flags and synsets.
	public void PopulateFromFacetMap(
		Document	document )		// Xml doc with tree
	{
		final String	taxRootName = "taxonomy";
		final String	idName 		= "id";
		try {
			// First, get the taxonomy children of the root.
			NodeList taxoNodes = document.getElementsByTagName( taxRootName );
			// For each taxonomy element, need to recurse to get all the
			// nodes in the taxonomy tree
			int nTaxos = taxoNodes.getLength();
			for( int iTax = 0; iTax < nTaxos; iTax++) {
			    Element taxoRoot = (Element)taxoNodes.item(iTax);
			    String name = taxoRoot.getAttribute( idName );
			    String displayName = taxoRoot.getAttribute( displayNameName );
			    String rootHTitle = taxoRoot.getAttribute( "root-heading-title" );
			    String supports = taxoRoot.getAttribute( supportsName );
			    if(( supports == null ) || ( supports == "" ))
			    	supports = "None";
			    String sort = taxoRoot.getAttribute( sortName );
			    //String inferredByChildren = taxoRoot.getAttribute( inferName );
			    //boolean infer = (inferredByChildren==null
		    	//				|| inferredByChildren.equalsIgnoreCase("yes"));
			    String selectMode = taxoRoot.getAttribute( selectModeName );
			    boolean single = ((selectMode.length() > 0 )			// not empty string - is there
		    					&& selectMode.equalsIgnoreCase("single"));
			    NodeList kids = taxoRoot.getElementsByTagName("description");
			    String description = null;
			    if( kids.getLength() > 0 ) {
				    Element descEl = (Element)kids.item(0);
			    	description = StringUtils.outputXMLStringForNode( descEl ).replaceAll(
			    							"</?description>", "");
			    }
			    kids = taxoRoot.getElementsByTagName("notes");
			    String notes = null;
			    if( kids.getLength() > 0 ) {
				    Element notesEl = (Element)kids.item(0);
			    	notes = StringUtils.outputXMLStringForNode( notesEl ).replaceAll(
							"</?notes>", "");
			    }
			    // Facet facet = new Facet( name, displayName, -1, infer, single,
			    // Facets should never be inferred from the children, IMO
			    int facetID = (iTax+1)*nCatsPerFacetMax;
			    Facet facet = new Facet( name, displayName, facetID,
			    						description, notes, false, single,
			    						  sort, rootHTitle, supports );
			    FacetInfo fInfo = new FacetInfo(facet);
			    facetsByName.put(name, fInfo);
			    facetsByID.put(facetID, fInfo);
			    taxoRoot.setAttribute( "catID", String.valueOf(facet.id) );
				// This cast is stupid - facet subclasses TaxoNode!!!
			    AddCategoriesFromNode( taxoRoot, (TaxoNode)facet, facetID, facetID, null, null );
			}
			// Now we're done parsing, so we can do the second pass, resolving forward
			// references on implied links, etc.
			// First, do the pending implied nodes
			for( FacetInfo facet : facetsByID.values() ) {
				for( TaxoNode tnode : facet.idMap.values() ) {
					tnode.ResolvePendingImpliedNodes(this);
				}
			}
		} catch(Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
		}
	} // PopulateFromFacetMap

	/**
	 * Parses and fills out the subtree under the passed node. Called initially with a
	 * taxonomy root, and then recurses for all heading elements.
	 * @param currEl	The current local root in the tree
	 * @param parent	The context. A Facet for the root case.
	 * @param facetID	The ID of the current facet
	 * @param nextID	The next available id in this facet
	 * @param prefices	The accumulated prefix strings to apply
	 * @param suffices	The accumulated suffix strings to apply
	 * @return			The number of headings added (and so the # of IDs used).
	 */
	protected int AddCategoriesFromNode( Element currEl, TaxoNode parent, int facetID,
										int nextID, ArrayList<String> prefices, ArrayList<String> suffices ) {
		final String	categoryElName = "heading";
		final String	idName = "id";
		final String	impliesElName = "implies";
		final String	isGuideName = "isGuide";
		final String	noMatchName = "nomatch";
		final String	asTokenName = "astoken";
		final String	combineName = "combine";
		final String	synonymElName = "synonym";
		final String	exclElName = "excl";
		final String	prefixElName = "prefix";
		final String	suffixElName = "suffix";
		final String	reduceElName = "reduce";
		final String	noiseTokenElName = "noiseToken";
		final String	tokenElName = "token";
		// Facets take descriptions and notes, and we can quietly ignore these here.
		final String	descriptionElName = "description";
		final String	notesElName = "notes";
		int nAdded = 0;
		ArrayList<String> local_prefices = null;
		ArrayList<String> local_suffices = null;
		FacetInfo		currFacet;

		try {
			if(( currFacet = facetsByID.get(facetID)) == null )
				throw new RuntimeException( "AddCatsFromNode no such facet: "+facetID);
			// First, get the children of the the current node.
			NodeList childNodes = currEl.getChildNodes();
			// For each taxonomy element, need to recurse to get all the
			// nodes in the taxonomy tree
			int nChildren = childNodes.getLength();
			if( nChildren>0)
    			debug( 2, "AddCatsFromNode:["+parent.name+"] Recursing. "
    					+ "nPrefices: "+((prefices==null)?0:prefices.size())
    					+ " nSuffices: "+((suffices==null)?0:suffices.size()) );
			for( int iChild = 0; iChild < nChildren; iChild++) {
				Node node = childNodes.item(iChild);
				if( node.getNodeType() != Node.ELEMENT_NODE )
					continue;
			    Element childEl = (Element)node;
			    String nodeName = childEl.getNodeName();
			    if( nodeName.equals( categoryElName ) ) {
				    String name = childEl.getAttribute( idName );
				    String displayName = childEl.getAttribute( displayNameName );
				    String inferredByChildren = childEl.getAttribute( inferName );
				    boolean infer = (( inferredByChildren.length() == 0 )	// empty string - not there
	    					|| inferredByChildren.equalsIgnoreCase("yes"));
				    String isGuide = childEl.getAttribute( isGuideName );
				    boolean guide = ((isGuide.length() > 0 )			// not empty string - is there
	    					&& isGuide.equalsIgnoreCase("true"));
				    String sort = childEl.getAttribute( sortName );
				    String selectMode = childEl.getAttribute( selectModeName );
				    boolean single = ((selectMode.length() > 0 )			// not empty string - is there
	    					&& selectMode.equalsIgnoreCase("single"));
				    String noMatchStr = childEl.getAttribute( noMatchName );
				    boolean nomatch = ((noMatchStr.length() > 0 )			// not empty string - is there
	    					&& noMatchStr.equalsIgnoreCase("true"));
				    String asTokenStr = childEl.getAttribute( asTokenName );
				    boolean asTokenPlural = ((asTokenStr.length() > 0 )			// not empty string - is there
	    					&& asTokenStr.equalsIgnoreCase("plural"));
				    boolean asTokenNoPlural = ((asTokenStr.length() > 0 )			// not empty string - is there
	    					&& asTokenStr.equalsIgnoreCase("noplural"));
				    String combineStr = childEl.getAttribute( combineName );
				    boolean combineWithPrefices = ((combineStr.length() <= 0 )			// not empty string - is there
	    					|| !combineStr.equalsIgnoreCase("suffix"));
				    boolean combineWithSuffices = ((combineStr.length() <= 0 )			// not empty string - is there
	    					|| !combineStr.equalsIgnoreCase("prefix"));
				    // TODO see if we want to put this in, and preserve it
				    int termID = -1;

				    TaxoNode newNode = new TaxoNode( name, displayName, null, nextID+nAdded, termID, facetID,
				    								parent, sort, guide, infer, single);
				    try {
				    	currFacet.AddTaxoNodeToMap( newNode, (nomatch?null:displayName), newNode.id );
				    } catch(DuplicateTaxoNodeException dte) {
				    	debug(2, dte.getMessage());
						System.err.println("DuplicateTaxoNodeException (skipping node): " + newNode.toString());
						continue;
					}
				    nAdded++;
				    childEl.setAttribute( "catID", String.valueOf(newNode.id) );
			    	// Note that any local prefices and suffices MUST come before the categories,
			    	// so we can just inherit the ones coming in if we have not add any.
				    String displayNameAsToken = displayName.toLowerCase();
	    			if( local_prefices == null )
		    			local_prefices = prefices;
	    			if( local_suffices == null )
		    			local_suffices = suffices;
			    	if( asTokenNoPlural || asTokenPlural ) {
			    		if( combineWithPrefices ) {
			    			if( local_prefices == null )
			    				throw new RuntimeException("No Prefices to combine with token: "+name);
				    		AddSynonymsForTokenWithPrefices( currFacet, newNode, displayNameAsToken, local_prefices );
			    		}
			    		if( combineWithSuffices ) {
			    			if( local_suffices == null )
			    				throw new RuntimeException("No Suffices to combine with token: "+name);
				    		AddSynonymsForTokenWithSuffices( currFacet, newNode, displayNameAsToken, local_suffices );
			    		}
			    	}
			    	if( asTokenPlural ) {
			    		String plural = displayNameAsToken+"s";
			    		if( combineWithPrefices ) {
			    			if( local_prefices == null )
			    				throw new RuntimeException("No Prefices to combine with token: "+plural);
				    		AddSynonymsForTokenWithPrefices( currFacet, newNode, plural, local_prefices );
			    		}
			    		if( combineWithSuffices ) {
			    			if( local_suffices == null )
			    				throw new RuntimeException("No Suffices to combine with token: "+plural);
				    		AddSynonymsForTokenWithSuffices( currFacet, newNode, plural, local_suffices );
			    		}
			    	}
				    if( parent != null )
				    	parent.AddChild( newNode );
				    // Recurse to handle descendants.
				    // We should hit prefixes and suffices before children, so if we did not,
				    // then just used what was passed in (may be null).
	    			if( local_prefices == null )
		    			local_prefices = prefices;
	    			if( local_suffices == null )
		    			local_suffices = suffices;
				    nAdded += AddCategoriesFromNode( childEl, newNode, facetID, nextID+nAdded,
				    									local_prefices, local_suffices );
			    } else if( nodeName.equals( impliesElName ) ) {
			    	String facetName = childEl.getAttribute( "facet" );
			    	String catName = childEl.getAttribute( "cat" );
			    	if( facetName != null && catName != null ) {
			    		TaxoNode impliedNode = FindNodeByName( facetName, catName );
			    		if( impliedNode != null )
			    			parent.AddImpliedNode( impliedNode );
			    		else
			    			parent.AddImpliedNode(facetName, catName );
			    	} else
			    		debug( 1, "Bad \"implies\" node for category: " + parent.name );
			    } else if( nodeName.equals( synonymElName ) ) {
			    	String synName = childEl.getAttribute( "value" ).toLowerCase();
			    	if( synName != null ) {
			    		// TODO Need to put in termID if we have it???
			    		parent.AddSynonym( synName );
			    		// Add another name to the map for the parent
			    		currFacet.AddNodeToNameMap( parent, synName  );
			    	}
			    } else if( nodeName.equals( exclElName ) ) {
			    	String exclName = childEl.getAttribute( "value" ).toLowerCase();
			    	if( exclName != null ) {
			    		// TODO Need to put in termID if we have it???
			    		parent.AddExclusion( exclName );
			    	}
			    } else if( nodeName.equals( prefixElName ) ) {
			    	String prefixName = childEl.getAttribute( "value" );
			    	if( prefixName != null ) {
			    		if( local_prefices == null ) {
			    			local_prefices = new ArrayList<String>();
			    			if( prefices != null )
			    				local_prefices.addAll(prefices);
			    		}
			    		local_prefices.add(prefixName);
			    	}
			    }
			    else if( nodeName.equals( suffixElName ) ) {
			    	String suffixName = childEl.getAttribute( "value" );
			    	if( suffixName != null ) {
			    		if( local_suffices == null ) {
			    			local_suffices = new ArrayList<String>();
			    			if( suffices != null )
			    				local_suffices.addAll(suffices);
			    		}
			    		local_suffices.add(suffixName);
			    	}
			    }
			    // Handle the reduce tokens
			    else if( nodeName.equals( reduceElName ) ) {
			    	String fromStr = childEl.getAttribute( "from" );
			    	String toStr = childEl.getAttribute( "to" );
			    	if( fromStr != null && toStr != null) {
			    		debug( 2, "Ignoring reduce from:["+fromStr+"] to:["+toStr+"] (NYI)");
			    	}
			    }
			    // Handle the noise tokens
			    else if( nodeName.equals( noiseTokenElName ) ) {
			    	String noiseToken = childEl.getAttribute( "value" );
			    	if( noiseToken != null) {
			    		debug( 2, "Ignoring noiseToken:["+noiseToken+"] (NYI)");
			    	}
			    }
			    else if( nodeName.equals( tokenElName ) ) {
			    	String tokenName = childEl.getAttribute( "value" ).toLowerCase();
				    String combineStr = childEl.getAttribute( combineName );
				    boolean combineWithPrefices = ((combineStr.length() <= 0 )			// not empty string - is there
	    					|| !combineStr.equalsIgnoreCase("suffix"));
				    boolean combineWithSuffices = ((combineStr.length() <= 0 )			// not empty string - is there
	    					|| !combineStr.equalsIgnoreCase("prefix"));
			    	if( tokenName != null ) {
		    			if( local_prefices == null )
			    			local_prefices = prefices;
		    			if( local_suffices == null )
			    			local_suffices = suffices;
			    		// Check for combine rule
			    		if( combineWithPrefices ) {
			    			if( local_prefices == null )
			    				throw new RuntimeException("No Prefices to combine with token: "+tokenName);
				    		AddSynonymsForTokenWithPrefices( currFacet, parent, tokenName, local_prefices );
			    		}
			    		if( combineWithSuffices ) {
			    			if( local_suffices == null )
			    				throw new RuntimeException("No Suffices to combine with token: "+tokenName);
				    		AddSynonymsForTokenWithSuffices( currFacet, parent, tokenName, local_suffices );
			    		}
			    	}
			    }
			    // Ignore description and notes, but complain about all others.
			    else if( !nodeName.equals( descriptionElName ) && !nodeName.equals( notesElName ) )
					System.err.println("Unexpected child element: " + childEl.getNodeName()
							+ " under node: " + parent.name );
			}
		} catch(Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return nAdded;
	}

	public void AddSynonymsForTokenWithPrefices( FacetInfo facet, TaxoNode node, String token, ArrayList<String> prefices ) {
		// For each prefix, combine with token and add to nameMap
		for( String prefix : prefices ) {
			String syn = prefix+token;
			node.AddSynonym( syn );			// We have to do this to get the hooks table right
			facet.AddNodeToNameMap( node, syn );
		}
	}

	public void AddSynonymsForTokenWithSuffices( FacetInfo facet, TaxoNode node, String token, ArrayList<String> suffices ) {
		// For each suffix, combine with token and add to nameMap
		for( String suffix : suffices ) {
			String syn = token+suffix;
			node.AddSynonym( syn );			// We have to do this to get the hooks table right
			facet.AddNodeToNameMap( node, syn );
		}
	}

	/*
	// Takes a result set for a table query of object to keyword associations,
	// and then considers the table is sets of rows based upon the baseID value,
	// checking each of the keywords for the given baseIndex for ascendancy relations.
	// Marks rows of baseID, keywordID values that are inferable.
	// Note: ASSUMES that the result set is from a query ordered by baseID.
	// Also REQUIRES that results set is scrollable and updatable.
	public int MarkInferablesInTable(
			ResultSet	rs,				// results set from query on kw table
			String		baseIDColName, 	// Name of the baseID column
			String		kwIDColName, 	// Name of the keyword ID column
			String		inferableColName, // Name of the inferable column
			ArrayList<Integer> exceptionKWIDs ) // List of keywords never to mark inferred
	{
		// We need to have a treemap already so we can chec ascendancy.
		if( idMap.size() <= 0 )
	    	throw new RuntimeException( "No KW tree to use for MarkInferablesInTable!");
		int markedCount = 0;
		try {
			int currBaseID = -1;
			// Declare a list of rowNum, kwID pairs that we check against next one
			ArrayList<Pair<Integer, Integer>> keywordsForBase =
					new ArrayList<Pair<Integer, Integer>>(20);
			//for( int iRow = 0; rs.next(); iRow++ ) {
			int iRow = 0;
			while( rs.next() ) {
				iRow = rs.getRow();
				if( iRow % 1000 == 0 )
					debug( 1, "MarkInferablesInTable: Processed " + iRow + " rows...");
				int baseID = rs.getInt(baseIDColName);
			    if(( baseID == 0 ) && rs.wasNull() )
			    	throw new RuntimeException( "MarkInferablesInTable got NULL baseID index value!");
			    int kwIdx = rs.getInt(kwIDColName);
			    if(( kwIdx == 0 ) && rs.wasNull() )
			    	throw new RuntimeException( "MarkInferablesInTable got NULL kwID index value!");
			    if( !idMap.containsKey( kwIdx ))
			    	throw new RuntimeException( "MarkInferablesInTable got Unknown kwID index value: " + kwIdx );
			    // Now we see if we are beginning a new baseID
			    if( baseID != currBaseID ) {
			    	currBaseID = baseID;
			    	keywordsForBase.clear();
			    	if( !exceptionKWIDs.contains( kwIdx ))
			    		keywordsForBase.add( new Pair<Integer, Integer>( iRow, kwIdx ) );
			    	else
			    		debug( 1, "Skipping excepted node: " + kwIdx );

			    }
			    else if( exceptionKWIDs.contains( kwIdx )) {
			    	// Skip these - leave them alone, and leave them out of the list
		    		debug( 1, "Skipping excepted node: " + kwIdx );
			    }
			    else { // Not the first kw for this base, so check against the others
		        	// TODO: We need to have a list of exceptions to this, so that where
		        	// we are moving categories out of a path (like the genre) we can leave
		        	// those as not inferable.
			    	boolean fAddKW = true;	// unless we determine otherwise...
			    	for( int i=0; i<keywordsForBase.size(); i++ ) {
			    		Pair<Integer, Integer> pair = keywordsForBase.get(i);
						// Work around a bug with next() after a call to absolute();
			    		if( kwIdx == pair.getSecond() ) {
			    			fAddKW = false;
			    			break;
			    		}
			    		int cmpResult = CmpAscendancy( kwIdx, pair.getSecond() );
			    		if( cmpResult == 0 ) // if unrelated, just continue
			    			continue;
		    			// add a work item for the keyword, and then we're done with it.
			    		if( cmpResult < 0 ) {	 // newKw is an ascendant of existing one.
			    			// just mark it as inferable and then bail.
			    			rs.updateByte( inferableColName, (byte)1 );
			    			//rs.updateInt( inferableColName, 1 );
			    			rs.updateRow();
			    			markedCount++;
			    			// Note we do NOT add new one to the list, since it is inferable
			    			fAddKW = false;
			    		}
			    		else {				 // newKw is a descendant of existing one.
			    			rs.absolute(pair.getFirst());		// Seek to that row
			    			rs.updateByte( inferableColName, (byte)1 );	// Mark it
			    			//rs.updateInt( inferableColName, 1 );
			    			rs.updateRow();
			    			markedCount++;
			    			rs.absolute(iRow);					// Seek back to this row
			    			// Remove the inferable one from the list so we stop considering it
			    			keywordsForBase.remove(i);
			    			// New one is added below.
			    		}
		    			break;	// We can stop looking since we found an issue
			    	}
			    	if( fAddKW )
		    			// Add the new kw to the list
				    	keywordsForBase.add( new Pair<Integer, Integer>( iRow, kwIdx ) );
			    }
			}
			debug( 1, "MarkInferablesInTable: Completed processing of " + iRow + " rows...");
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return markedCount;
	} // MarkInferablesInTable
*/
/*
	// Takes a result set for a table query of object to keyword associations,
	// and then considers the table as sets of rows based upon the baseID value,
	// checking each of the keywords for the given baseIndex for ascendancy relations.
	// Returns  list of pairs of baseID, keywordID values that should be marked inferable.
	// Note: ASSUMES that the result set is from a query ordered by baseID.
	public ArrayList<Pair<Integer, Integer>> CheckTableForInferables(
			ResultSet	rs,				// results set from query on kw table
			String		baseIDColName, 	// Name of the baseID column
			String		kwIDColName ) 		// Name of the keyword ID column
	{
		ArrayList<Pair<Integer, Integer>> retList = new ArrayList<Pair<Integer, Integer>>();
		// We need to have a treemap already so we can chec ascendancy.
		if( idMap.size() <= 0 )
	    	throw new RuntimeException( "No KW tree to use for CheckTableForInferables!");
		try {
			int currBaseID = -1;
			ArrayList<Integer> keywordsForBase = new ArrayList<Integer>(20);
			while (rs.next()) {
			    int baseID = rs.getInt(baseIDColName);
			    if(( baseID == 0 ) && rs.wasNull() )
			    	throw new RuntimeException( "CheckTableForInferables got NULL baseID index value!");
			    int kwIdx = rs.getInt(kwIDColName);
			    if(( kwIdx == 0 ) && rs.wasNull() )
			    	throw new RuntimeException( "CheckTableForInferables got NULL kwID index value!");
			    if( !idMap.containsKey( kwIdx ))
			    	throw new RuntimeException( "CheckTableForInferables got Unknown kwID index value: " + kwIdx );
			    // Now we see if we are beginning a new baseID
			    if( baseID != currBaseID ) {
			    	currBaseID = baseID;
			    	keywordsForBase.clear();
			    	keywordsForBase.add( kwIdx );
			    }
			    else { // Not the first kw for this base, so check against the others
			    	boolean fAddKW = true;	// unless we determine otherwise...
			    	for( int i=0; i<keywordsForBase.size(); i++ ) {
			    		int cmpResult = CmpAscendancy( kwIdx, keywordsForBase.get(i) );
			    		if( cmpResult == 0 ) // if unrelated, just continue
			    			continue;
		    			// add a work item for the keyword, and then we're done with it.
		    			Pair<Integer, Integer> newWorkItem;
			    		if( cmpResult < 0 ) {	 // newKw is an ascendant of existing one.
			    			newWorkItem = new Pair<Integer, Integer>( currBaseID, kwIdx );
			    			// Note we do NOT add new one to the list, since it is inferable
			    			fAddKW = false;
			    		}
			    		else {				 // newKw is a descendant of existing one.
			    			newWorkItem = new Pair<Integer, Integer>( currBaseID, keywordsForBase.get(i) );
			    			// Remove the inferable one from the list so we stop considering it
			    			keywordsForBase.remove(i);
			    			// New one is added below.
			    		}
		    			retList.add( newWorkItem );
		    			break;	// We can stop looking since we found an issue
			    	}
			    	if( fAddKW )
		    			// Add the new kw to the list
				    	keywordsForBase.add( kwIdx );
			    }
			}
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return retList;
	} // CheckTableForInferables
*/

}
