package edu.berkeley.delphi.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.berkeley.delphi.utils.Pair;
import edu.berkeley.delphi.utils.PriorityQueue;

// TODO This class does not present a clean model: the static class members are ugly.
// It should be rewritten to support a static class factory that takes the config
// file as input and produces a class instance with most of the information.
// Callers will have to be adjusted to use an instance rather than the Class.
public class DumpColumnConfigInfo {

	public static class MineInfoForColumn {
		protected String facetName = null;
		protected float reliability = 0;
		protected boolean filterTokenOnMatch = true;
		protected MineInfoForColumn(String fname, float rel, boolean filter ) {
			facetName = fname;
			reliability = rel;
			filterTokenOnMatch = filter;
		}
		public String getFacetName() {
			return facetName;
		}
		public float getReliability() {
			return reliability;
		}
		public boolean isFilterTokenOnMatch() {
			return filterTokenOnMatch;
		}
	}

	private static int columnSeparator = -1;
	private static int objectIDColumnIndex = -1;
	private static int museumIDColumnIndex = -1;
	private static int nameColumnIndex = -1;
	private static HashMap<String, DumpColumnConfigInfo> singletonColumnInfoMap = null;
	private static ArrayList<String> columnNames = null;
	protected static PriorityQueue<Integer> descColsPQ = null;
	protected static ArrayList<Integer> hiddenNotesCols = null;

	protected String name;
	protected boolean addToHiddenNotes;
	protected int descriptionOrder;		// Part of description? -1 of not, else order
	protected String comment;
	protected ArrayList<String> tokenSeparators;
	protected ArrayList<String> noiseTokens;
	protected ArrayList<Pair<String,String>> reduceRules;
	// Consider making mineInfo a static inner class, and add rule to filter matched
	// tokens from further matching.
	protected ArrayList<MineInfoForColumn> facetsToMine;
	// TODO Add variables that store the ID column index, the ObjNum index,
	// and the Name/Title index. Provide setter/getters.

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			ConfigStringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			ConfigStringUtils.outputExceptionTrace(e);
	}


	protected DumpColumnConfigInfo( String newName, String newComment ) {
		name = newName;
		comment = newComment;
		addToHiddenNotes = false;
		descriptionOrder = -1;
	}

	public static int getColumnSeparator() {
		return columnSeparator;
	}

	public static boolean isInitialized() {
		return(singletonColumnInfoMap!=null);
	}

	public static int getNumColumns() {
		if(singletonColumnInfoMap==null)
			throw new RuntimeException("DumpColumnConfigInfo.getNumColumns: Module not initialized.");
		return singletonColumnInfoMap.size();
	}

	public static int getIdColumnIndex() {
		if( objectIDColumnIndex < 0 )
			throw new RuntimeException("DumpColumnConfigInfo.getIdColumnIndex: Module not initialized.");
		return objectIDColumnIndex;
	}

	public static int getMuseumIdColumnIndex() {
		if( museumIDColumnIndex < 0 )
			throw new RuntimeException("DumpColumnConfigInfo.getMuseumIdColumnIndex: Module not initialized.");
		return museumIDColumnIndex;
	}

	public static int getNameColumnIndex() {
		if( nameColumnIndex < 0 )
			throw new RuntimeException("DumpColumnConfigInfo.getNameColumnIndex: Module not initialized.");
		return nameColumnIndex;
	}

	public static DumpColumnConfigInfo GetColInfo( String forColumn, String detail ) {
		if( singletonColumnInfoMap == null )
			throw new RuntimeException("DumpColumnConfigInfo."+detail+": Module not initialized.");
		DumpColumnConfigInfo returnVal =
			singletonColumnInfoMap.get(forColumn);
		if( returnVal == null)
			throw new IllegalArgumentException("DumpColumnConfigInfo."+detail+": Bad column name:"+forColumn);
		return returnVal;
	}

	public static String[] getColumnNames() {
		if(columnNames == null)
			throw new RuntimeException("DumpColumnConfigInfo.getColumnNames: Module not initialized.");
		String[] retColNames = new String[columnNames.size()];
		return columnNames.toArray(retColNames);
	}

	public static DumpColumnConfigInfo[] getAllColumnInfo( String[] columnNames) {
		int nCols = columnNames.length;
		DumpColumnConfigInfo[] allCols = new DumpColumnConfigInfo[nCols];
		for(int i=0; i<nCols; i++) {
			DumpColumnConfigInfo info = GetColInfo(columnNames[i], "getAllColumnInfo");
			allCols[i] = info;
		}
		return allCols;
	}

	public static ArrayList<Integer> getDescriptionColumns() {
		if( descColsPQ == null )
			throw new RuntimeException("DumpColumnConfigInfo.getDescriptionColumns: Module not initialized.");
		return descColsPQ.asArrayList();
	}

	public static String getColComment( String forColumn ) {
		return GetColInfo(forColumn, "getColComment").comment;
	}

	public String getName() {
		return name;
	}

	public static ArrayList<Integer> getHiddenNotesCols() {
		return hiddenNotesCols;
	}

	public ArrayList<MineInfoForColumn> getFacetsToMine() {
		return facetsToMine;
	}

	public static ArrayList<String> getTokenSeparators( String forColumn ) {
		return GetColInfo(forColumn, "getTokenSeparators").tokenSeparators;
	}

	public static ArrayList<String> getNoiseTokens( String forColumn ) {
		return GetColInfo(forColumn, "getNoiseTokens").noiseTokens;
	}

	public static ArrayList<Pair<String,String>> getReduceRules( String forColumn ) {
		return GetColInfo(forColumn, "getReduceRules").reduceRules;
	}

	/**
	 * @return true if column mined for any facet, else false.
	 */
	public boolean columnMinedForAnyFacet() {
		for( MineInfoForColumn info : facetsToMine ) {
			if( info.reliability > 0 )
				return true;
		}
		return false;
	}

	/**
	 * @param facetName The facet for which to return reliability.
	 * @return true if column mined for this facet, else false.
	 */
	public boolean columnMinedForFacet( String facetName ) {
		return (columnReliabilityForFacet(facetName) != 0);
	}

	/**
	 * @param facetName The facet for which to return reliability.
	 * @return reliability if set, else 0 if facet not mined from this column
	 */
	public float columnReliabilityForFacet( String facetName ) {
		for( MineInfoForColumn info : facetsToMine ) {
			if( info.facetName.equalsIgnoreCase(facetName) )
				return info.reliability;
		}
		return 0;
	}

	// This supports filtering of objects in a metadata dump that should not be promoted
	// to the web collections browser. In general, these should be filtered out of the
	// dump up front, but in reality, garbage creeps in.
	// The PAHMA-SPECIFIC rules are: must not be empty, must not include "Acc."
	// or "Nature of contents:", (both indicate an accession record),
	// and must not include "." (indicates a burial record).
	public static boolean objNumIsValid( String objNumStr ) {
		// TODO This should be configured as rules somehow.
		// PAHMA-SPECIFIC - this must be generalized.
		// If rules not initialized, should throw a not initialized exception
		String asLower = objNumStr.toLowerCase();
		boolean isValid = !objNumStr.isEmpty()
						&& !objNumStr.contains("Acc")
						&& !objNumStr.contains("Nature of contents:")
						&& !asLower.contains("temp")
						&& !asLower.contains("test")
						&& !objNumStr.contains(".");
		return isValid;
	}

	public static float columnReliabilityForFacet( String forColumn, String facetName ) {
		ArrayList<MineInfoForColumn> facetsToMine =
			GetColInfo(forColumn, "columnReliabilityForFacet").facetsToMine;
		for( MineInfoForColumn info : facetsToMine ) {
			if( info.facetName.equalsIgnoreCase(facetName) )
				return info.reliability;
		}
		return 0;
	}

	public static void PopulateFromConfigFile(
			Element	processingInfoNode )		// Xml doc with tree
	{
		String myName = "DumpColumnConfigInfo.PopulateFromConfigFile";
		int nAdded = 0;
		try {
			if( singletonColumnInfoMap != null ) {
				System.out.println("Rebuilding columnInfoMap...");
				singletonColumnInfoMap = null;
				columnNames = null;
				descColsPQ = null;
				hiddenNotesCols = null;
				objectIDColumnIndex = -1;
				museumIDColumnIndex = -1;
				nameColumnIndex = -1;
			}
			singletonColumnInfoMap = new HashMap<String, DumpColumnConfigInfo>();
			columnNames = new ArrayList<String>();
			descColsPQ = new PriorityQueue<Integer>();
			hiddenNotesCols = new ArrayList<Integer>();
			// Get the column separator.
			NodeList colSepNodes = processingInfoNode.getElementsByTagName( "colSep" );
			// Only use the first one.
			if( colSepNodes.getLength() <= 0 )
				throw new RuntimeException(myName+": missing colSep node.");
		    Element colSepNode = (Element)colSepNodes.item(0);
			String colSepValue = colSepNode.getAttribute("value");
			if( colSepValue.length() != 1 )
				throw new RuntimeException(myName+": bad colSep node (must be 1 char).");
			columnSeparator = colSepValue.charAt(0);
			// Next, get the info children of the root.
			NodeList colNodes = processingInfoNode.getElementsByTagName( "colInfo" );
			// For each info element, need to get all the fields.
			int nCols = colNodes.getLength();
			for( int iCol = 0; iCol < nCols; iCol++) {
			    Element colInfoEl = (Element)colNodes.item(iCol);
			    String name = colInfoEl.getAttribute( "name" );
			    if( name.length() <= 0 )
					throw new RuntimeException(myName+": No name on colInfo.");
			    String comment = null;
				ArrayList<String> tokenSeparators = new ArrayList<String>();
				ArrayList<String> noiseTokens = new ArrayList<String>();
				ArrayList<Pair<String,String>> reduceRules = new ArrayList<Pair<String,String>>();
				ArrayList<MineInfoForColumn> facetsToMine = new ArrayList<MineInfoForColumn>();
		    	String function = colInfoEl.getAttribute( "function" );
		    	if( function.equals("hiddenNotes")) {
		    		hiddenNotesCols.add(iCol);
		    	} else if( function.equals("id")) {
		    		objectIDColumnIndex = iCol;
					if( iCol != 0 )
						throw new RuntimeException(myName+": ObjectID column must be first column (0).");
		    	} else if( function.equals("museumID")) {
		    		museumIDColumnIndex = iCol;
		    	} else if( function.equals("name")) {
		    		nameColumnIndex = iCol;
		    	}
		    	String descOrderStr = colInfoEl.getAttribute( "descriptionOrder" );
		    	double descOrder = -1;
		    	if( !descOrderStr.isEmpty()) {
			    	descOrder = Double.parseDouble(descOrderStr);
				    if( descOrder >= 0 )
				    	// Note that PriorityQueue ranks high to low
				    	descColsPQ.add(iCol, 1.0/descOrder);
		    	}
				NodeList childNodes = colInfoEl.getChildNodes();
				int nChildren = childNodes.getLength();
				for( int iChild = 0; iChild < nChildren; iChild++) {
					Node node = childNodes.item(iChild);
					if( node.getNodeType() != Node.ELEMENT_NODE )
						continue;
				    Element childEl = (Element)node;
				    if( childEl.getNodeName().equals( "comment" ) ) {
				    	comment = childEl.getTextContent();
				    }
				    else if( childEl.getNodeName().equals( "tokenSep" ) ) {
				    	String value = childEl.getAttribute( "value" );
				    	if( value != null && !value.isEmpty())
				    		tokenSeparators.add(value);
				    }
				    else if( childEl.getNodeName().equals( "noiseToken" ) ) {
				    	String value = childEl.getAttribute( "value" );
				    	if( value != null && !value.isEmpty() )
				    		noiseTokens.add(value);
				    }
				    else if( childEl.getNodeName().equals( "reduce" ) ) {
				    	String fromStr = childEl.getAttribute( "from" );
				    	String toStr = childEl.getAttribute( "to" );
				    	if( fromStr != null && !fromStr.isEmpty() && toStr != null && !toStr.isEmpty()) {
				    		reduceRules.add(new Pair<String, String>(fromStr, toStr));
				    	}
				    }
				    else if( childEl.getNodeName().equals( "facetInfo" ) ) {
				    	String fName = childEl.getAttribute( "name" );
				    	String relevStr = childEl.getAttribute( "relevancy" );
				    	String filterStr = childEl.getAttribute( "filterTokenOnMatch" );
				    	if( fName != null && !fName.isEmpty()
				    			&& relevStr != null && !relevStr.isEmpty()) {
					    	float rel = Float.parseFloat(relevStr);
					    	// Note filter defaults to true if not set
					    	boolean filter = ( filterStr == null ) || filterStr.isEmpty()
					    						|| Boolean.parseBoolean(filterStr);
					    	facetsToMine.add(new MineInfoForColumn(fName, rel, filter));
				    	}
				    }
				    else
						System.err.println("Unexpected child element: " + childEl.getNodeName()
								+ " under colInfo for: " + name );
				}
			    DumpColumnConfigInfo info = new DumpColumnConfigInfo( name, comment );
			    if( function != null && function.equals("hiddenNotes"))
			    	info.addToHiddenNotes = true;
		    	info.facetsToMine = facetsToMine;
		    	info.tokenSeparators = tokenSeparators;
		    	info.reduceRules = reduceRules;
		    	info.noiseTokens = noiseTokens;
			    singletonColumnInfoMap.put( name, info );
			    columnNames.add(name);
			    nAdded++;
				System.out.println("Parsed col info for: " + name );
			}
			if(objectIDColumnIndex != 0)
				throw new RuntimeException(myName+
						": ObjectID column must be marked (with function=\"id\"), and must be first column (0).");
			System.out.println("Parsed info for "+nAdded+" columns.");
		} catch(Exception ex) {
			String tmp = myName+": Exception :"
				+"\n"+ ex.getMessage();
			debug(1, tmp);
            debugTrace(1, ex);
			throw new RuntimeException( tmp );
		}
	} // PopulateFromConfigFile

}
