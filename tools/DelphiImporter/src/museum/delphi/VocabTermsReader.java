/**
 *
 */
package museum.delphi;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.w3c.dom.*;
//import javax.xml.parsers.*;

/**
 * @author pschmitz
 */
public class VocabTermsReader {

	protected BufferedReader reader = null;
	protected HashSet<String> skipTerms;

	protected Node outputDocRoot = null;
	protected int _debugLevel = 2;
	// TODO we should have the user select the encoding.
	private static String encoding = "ISO-8859-1";
	// private static String encoding = "UTF-8";
	//private static String encoding = "UTF-16";

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public VocabTermsReader( String inFileName ) {
		try {
			reader = new BufferedReader(new FileReader(inFileName));
			reader = new BufferedReader(
			          new InputStreamReader(new FileInputStream(inFileName),
			        		  encoding));
			skipTerms = new HashSet<String>();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open input file: " + inFileName);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException("Internal error: Unsupported encoding: " + encoding);
		}
	}

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	public void addSkipTerm( String termToSkip ) {
		skipTerms.add( termToSkip );
	}


	// Each line is assumed to have these tokens:
	// Term|TermID|Term MasterID|CN of Term|Guide Term|TermType
	public DoubleHashTree readTerms( int nTermsMax, Document outputDoc, String mapTitle ) {
		DoubleHashTree hashTree = new DoubleHashTree();
		int reportSize = 1000;
		int nPrimaryAdded = 0;
		int nFacetsAdded = 0;
		int nSynsAdded = 0;
		int dummyFacetID = 0;
		try {
			// Create dummy facet to add everything to
			Facet facet = new Facet( "default", "none", dummyFacetID, null, null,
															false, false, null, null, "None" );
			hashTree.AddFacet(facet);
			boolean fFirstLine = true;
			int lastTermMasterID = -1;
			TaxoNode lastPrimaryNode = null;
			//Node outputRoot = outputDoc.get
			if( mapTitle == null )
				mapTitle = "Delphi Facet Map";
			Element newEl = outputDoc.createElement("facetmap");
			newEl.setAttribute("title", mapTitle);
			Node outputDocRoot = outputDoc.appendChild(newEl);
			while ( reader.ready() && (nPrimaryAdded<nTermsMax) ) {
				String line = reader.readLine();
				if( fFirstLine ) {
					debug(2,"Skipping first line:\n"+line);
					fFirstLine = false;
					continue;
				}
				String[] tokens = line.split( "[|]" );
				if( tokens.length != 6 )
					throw new RuntimeException( "VTR.readTerms: Bad syntax - wrong # of terms.\n" + line );
				String trimmedName = StringUtils.trimQuotes(tokens[0]);
				if( skipTerms.contains(trimmedName)){
					debug(2,"Skipping term: "+trimmedName);
					continue;
				}
				String name = makeXMLSafeString(trimmedName);
				int termID = Integer.parseInt( StringUtils.trimQuotes(tokens[1]) );
				int termMasterID = Integer.parseInt( StringUtils.trimQuotes(tokens[2]) );
				String termType = StringUtils.trimQuotes(tokens[5]);
				if( termMasterID == lastTermMasterID ) {
					boolean fAddSym = false;
					// We have a duplicate term for a previous term. Check type.
					if( termType.equalsIgnoreCase( "Alternate British" )
						|| termType.equalsIgnoreCase( "British Equivalent" )
						|| termType.equalsIgnoreCase( "Alternate Term" )
						|| termType.equalsIgnoreCase( "Miscellaneous Source Equivalent" )
						|| termType.equalsIgnoreCase( "Search Term Equivalent" )
						|| termType.equalsIgnoreCase( "Misspelling" )
						|| termType.equalsIgnoreCase( "Native name" )
						|| termType.equalsIgnoreCase( "Non-native name" )
						|| termType.equalsIgnoreCase( "Other term" )
						|| termType.equalsIgnoreCase( "Village Name" )
						|| termType.equalsIgnoreCase( "Place Name" )
						|| termType.equalsIgnoreCase( "Geographic Location" )
						|| termType.equalsIgnoreCase( "Scientific Name" )
						|| termType.equalsIgnoreCase( "Spelling Variant" )
						|| termType.equalsIgnoreCase( "Synonym" )
						|| termType.equalsIgnoreCase( "Derogatory" )
						|| termType.equalsIgnoreCase( "Common name" )
						|| termType.equalsIgnoreCase( "Historical name" )
						|| termType.equalsIgnoreCase( "LCSH Link" )
						|| termType.equalsIgnoreCase( "Use for Term" )) {
						fAddSym = true;
						debug(3, "VTR.readTerms: Found synonym ["+name+"] for: "+lastPrimaryNode.name );
					}
					else if( termType.equalsIgnoreCase( "Preferred Term" )) {
						fAddSym = true;
						debug(1, "VTR.readTerms: Found illegally declared synonym ["+name+"] for: "+lastPrimaryNode.name );
					}
					else if( termType.equalsIgnoreCase( "Related Term" )) {
						debug(1, "VTR.readTerms: Found related term for: "
								+lastPrimaryNode.name+" related: "+name);
					}
					else if( termType.equalsIgnoreCase( "Descriptor" )) {
						fAddSym = true;
						debug(2, "VTR.readTerms: Unexpected 'Descriptor' termType for: ["
								+name+"] related to: ["+lastPrimaryNode.name+"]");
					}
					else {
						debug(1, "VTR.readTerms: Found unknown termType ["+termType+"] name: ["
								+name+"] related to: ["+lastPrimaryNode.name+"]");
					}
					if( fAddSym ) {
						lastPrimaryNode.AddSynonym(name);
						newEl = outputDoc.createElement("synonym");
						newEl.setAttribute("value", name);
						lastPrimaryNode.elementNode.appendChild(newEl);
						nSynsAdded++;
					}
				}
				else if( termType.equalsIgnoreCase( "To Delete" )) {
					debug(2, "VTR.readTerms: Found 'To Delete' term Type for: "+name);
				}
				else {
					if( !termType.equalsIgnoreCase( "Descriptor" )
							&& !termType.equalsIgnoreCase( "Preferred Term" )) {
						debug(2, "VTR.readTerms: Found unexpected term Type ["+termType+"] for: "+name);
					}
					boolean isGuide = Boolean.parseBoolean( tokens[4] );
					String termCN = tokens[3];
					String parentCN = termCN;
					TaxoNode parent = null;
					int lastDot = -1;
					// Keep trying up the tree to find a parent until
					while(( parent==null ) && ((lastDot = parentCN.lastIndexOf('.')) > 0 )) {
						parentCN = parentCN.substring(0, lastDot);
						parent = hashTree.FindNodeByName(dummyFacetID,parentCN);
					}
					TaxoNode newNode = new TaxoNode( name, termID, parent, tokens[3], isGuide );
					hashTree.AddTaxoNodeToMap(dummyFacetID, newNode, tokens[3], termID );
					lastPrimaryNode = newNode;
					if( parent == null) {	// this is a facet, by definition or bug
						newEl = outputDoc.createElement("taxonomy");
						newEl.setAttribute("title", newNode.name);
						Node newParentEl = outputDocRoot.appendChild(newEl);
						newNode.elementNode = newParentEl;
						debug(3, "VTR.readTerms: Added new facet: "+name );
						nFacetsAdded++;
					} else {
						// Add the new node to the parent
						newEl = outputDoc.createElement("heading");
						newEl.setAttribute("title", newNode.name);
						newEl.setAttribute("id", makeIDSafeString(newNode.name));
						if( newNode.isGuideTerm )
							newEl.setAttribute("isGuide", "true");
						newNode.elementNode = parent.elementNode.appendChild(newEl);
						debug(3, "VTR.readTerms: Added new primary term: "+name
						          +" under "+((parent==null)?"unknown parent":("parent: "+parent.name)));
						nPrimaryAdded++;
					}
				}
				lastTermMasterID = termMasterID;
				if(( nPrimaryAdded % reportSize ) == 0 )
					debug(1, "VTR.readTerms Added "+nPrimaryAdded+" primary terms in "+nFacetsAdded+
							" facets with a total of "+nSynsAdded+" synonyms." );
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		debug(1, "VTR.readTerms Returning: "+nPrimaryAdded+" primary terms in "+nFacetsAdded+
							" facets with a total of "+nSynsAdded+" synonyms." );
		return hashTree;
	}

	protected String makeIDSafeString( String input ) {
		// TODO Change to chars with diacriticals become simple chars. Better match.
		String retVal = input.replaceAll("[\\W]", "");
		return retVal;
	}

	protected String makeXMLSafeString( String input ) {
		//String retVal = input.replaceAll("[^\\p{Graph} ]", "");
		//String retVal = Pattern.compile("[\\P{N}&&\\P{L}&&\\P{M}]", Pattern.CANON_EQ).matcher(input).replaceAll("");
		String retVal = input.replaceAll("[\\p{Zl}\\p{Zp}\\p{C}]", "");
		return retVal;
	}

}
