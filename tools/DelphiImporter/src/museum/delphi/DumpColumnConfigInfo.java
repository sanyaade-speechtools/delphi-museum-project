package museum.delphi;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DumpColumnConfigInfo {
	private static int columnSeparator = -1;
	private static HashMap<String, DumpColumnConfigInfo> columnInfoMap = null;
	protected static PriorityQueue<Integer> descColsPQ = null;
	protected static ArrayList<Integer> hiddenNotesCols = null;

	protected String name;
	protected boolean addToHiddenNotes;
	protected int descriptionOrder;		// Part of description? -1 of not, else order
	protected String comment;
	protected ArrayList<String> tokenSeparators;
	protected ArrayList<String> noiseTokens;
	protected ArrayList<Pair<String,String>> reduceRules;
	protected ArrayList<Pair<String,Float>> facetsToMine;
	// TODO Add array of integers that indicate how to build description.
	// TODO Value <0 means do not use column, and value >=0 indicates order for descr.

	// TODO Add variables that store the ID column index, the ObjNum index,
	// and the Name/Title index. Provide setter/getters.


	protected DumpColumnConfigInfo( String newName, String newComment ) {
		name = newName;
		comment = newComment;
		addToHiddenNotes = false;
		descriptionOrder = -1;
	}

	public static int getColumnSeparator() {
		return columnSeparator;
	}

	private static DumpColumnConfigInfo GetColInfo( String forColumn, String detail ) {
		if( columnInfoMap == null )
			throw new RuntimeException("DumpColumnConfigInfo."+detail+": Module not initialized.");
		DumpColumnConfigInfo returnVal =
			columnInfoMap.get(forColumn);
		if( returnVal == null)
			throw new RuntimeException("DumpColumnConfigInfo."+detail+": Bad column name:"+forColumn);
		return returnVal;
	}

	protected static DumpColumnConfigInfo[] getAllColumnInfo( String[] columnNames) {
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

	public static ArrayList<String> getTokenSeparators( String forColumn ) {
		return GetColInfo(forColumn, "getTokenSeparators").tokenSeparators;
	}

	public static ArrayList<String> getNoiseTokens( String forColumn ) {
		return GetColInfo(forColumn, "getNoiseTokens").noiseTokens;
	}

	public static ArrayList<Pair<String,String>> getReduceRules( String forColumn ) {
		return GetColInfo(forColumn, "getReduceRules").reduceRules;
	}

	public float columnReliabilityForFacet( String facetName ) {
		for( Pair<String,Float> info : facetsToMine ) {
			if( info.first.equalsIgnoreCase(facetName) )
				return info.second;
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
		boolean isValid = !objNumStr.isEmpty()
						&& !objNumStr.contains("Acc")
						&& !objNumStr.contains("Nature of contents:")
						&& !objNumStr.contains(".");
		return isValid;
	}

	public static float columnReliabilityForFacet( String forColumn, String facetName ) {
		ArrayList<Pair<String,Float>> facetsToMine =
			GetColInfo(forColumn, "columnReliabilityForFacet").facetsToMine;
		for( Pair<String,Float> info : facetsToMine ) {
			if( info.first.equalsIgnoreCase(facetName) )
				return info.second;
		}
		return 0;
	}

	public static void PopulateFromConfigFile(
			Document	document )		// Xml doc with tree
	{
		int nAdded = 0;
		try {
			if( columnInfoMap != null ) {
				System.out.println("Rebuilding columnInfoMap...");
				columnInfoMap = null;
				descColsPQ = null;
				hiddenNotesCols = null;
			}
			columnInfoMap = new HashMap<String, DumpColumnConfigInfo>();
			descColsPQ = new PriorityQueue<Integer>();
			hiddenNotesCols = new ArrayList<Integer>();
			// First, get the column separator.
			NodeList colSepNodes = document.getElementsByTagName( "colSep" );
			// Only use the first one.
			if( colSepNodes.getLength() <= 0 )
				throw new RuntimeException("DumpColumnConfigInfo.PopulateFromConfigFile: missing colSep node.");
		    Element colSepNode = (Element)colSepNodes.item(0);
			String colSepValue = colSepNode.getAttribute("value");
			if( colSepValue.length() != 1 )
				throw new RuntimeException("DumpColumnConfigInfo.PopulateFromConfigFile: bad colSep node (must be 1 char).");
			columnSeparator = colSepValue.charAt(0);
			// Next, get the info children of the root.
			NodeList colNodes = document.getElementsByTagName( "colInfo" );
			// For each info element, need to get all the fields.
			int nCols = colNodes.getLength();
			for( int iCol = 0; iCol < nCols; iCol++) {
			    Element colInfoEl = (Element)colNodes.item(iCol);
			    String name = colInfoEl.getAttribute( "name" );
			    if( name.length() <= 0 )
					throw new RuntimeException("DumpColumnConfigInfo.PopulateFromConfigFile: No name on colInfo.");
			    String comment = null;
				ArrayList<String> tokenSeparators = new ArrayList<String>();
				ArrayList<String> noiseTokens = new ArrayList<String>();
				ArrayList<Pair<String,String>> reduceRules = new ArrayList<Pair<String,String>>();
				ArrayList<Pair<String,Float>> facetsToMine = new ArrayList<Pair<String,Float>>();
		    	String function = colInfoEl.getAttribute( "function" );
		    	if( function.equals("hiddenNotes"))
		    		hiddenNotesCols.add(iCol);
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
				    	if( value != null )
				    		tokenSeparators.add(value);
				    }
				    else if( childEl.getNodeName().equals( "noiseToken" ) ) {
				    	String value = childEl.getAttribute( "value" );
				    	if( value != null )
				    		noiseTokens.add(value);
				    }
				    else if( childEl.getNodeName().equals( "reduce" ) ) {
				    	String fromStr = childEl.getAttribute( "from" );
				    	String toStr = childEl.getAttribute( "to" );
				    	if( fromStr != null && toStr != null) {
				    		reduceRules.add(new Pair<String, String>(fromStr, toStr));
				    	}
				    }
				    else if( childEl.getNodeName().equals( "facetInfo" ) ) {
				    	String fName = childEl.getAttribute( "name" );
				    	String relevStr = childEl.getAttribute( "relevancy" );
				    	if( fName != null && relevStr != null) {
					    	float rel = Float.parseFloat(relevStr);
					    	facetsToMine.add(new Pair<String, Float>(fName, rel));
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
			    columnInfoMap.put( name, info );
			    nAdded++;
				System.out.println("Parsed col info for: " + name );
			}
			System.out.println("Parsed info for "+nAdded+" columns.");
		} catch(Exception ex) {
			System.err.println("PopulateFromConfigFile: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	} // PopulateFromConfigFile
}