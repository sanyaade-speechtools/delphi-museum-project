/**
 *
 */
package museum.delphi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pschmitz
 *
 */
public class Categorizer {

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	/**
	 * Processes the object metadata to produce concept associations
	 * @param metaDataReader the object metadata source
	 * @param columnNames the configured column names in the metadata source
	 * @param facetMapHashTree the concept ontology, by facet
	 * @param facetName the name of the facet, or null to do all at once
	 * @param fDumpAsSQLInsert set TRUE for an insert statement, FALSE for a loadfile
	 * @param dbName the name of the DB to be used.
	 */
	public static void categorizeForFacet(MetaDataReader metaDataReader,
			String columnNames[],
			DoubleHashTree facetMapHashTree, String facetName,
			String basefilename, boolean fDumpAsSQLInsert, String dbName ) {
    	String filename = null;
    	String extension = ".sql";
    	String currFilename = null;
    	int iOutputFile;
    	int nObjCatsOutMax = Integer.MAX_VALUE;
    	int nObjCatsReport = 100000;
    	int nObjCatsTillReport;
    	// If need to build files under a size limit (e.g., 5 MB):
    	// Each line in the output file is about 20 chars max.
    	// We want a max of 5 MB files, so we can take up to
    	// 5000000 / 20 = 250000.
    	// To be safer, we'll set it at 200K
    	int nObjsCatsPerDumpFile = Integer.MAX_VALUE;
    	int nObjCatsDumpedToFile = 0;
    	int nObjCatsDumpedTotal = 0;
    	int nObjsSkippedTotal = 0;
    	// TODO get this from col config
    	int objIDCol = 0;
		int objNumCol = 4;
    	// Hold the information for the Facet(s)
    	DumpColumnConfigInfo[] colDumpInfo = null;
		try {
			// Open the dump file
			if( columnNames == null || columnNames.length < 2 )
				throw new RuntimeException("categorizeForFacet: DumpColConfig info not yet built!");
			colDumpInfo = DumpColumnConfigInfo.getAllColumnInfo(columnNames);
			int nCols = columnNames.length;
			// Since we run through lots of lines, cache the skip columns info for speed
			boolean[] skipCol = new boolean[nCols];
			for( int iCol=0; iCol<nCols; iCol++ ) {
				skipCol[iCol] = !((facetName==null)?
								 colDumpInfo[iCol].columnMinedForAnyFacet()
								:colDumpInfo[iCol].columnMinedForFacet(facetName));
			}
			BufferedWriter objCatsWriter = null;
	    	iOutputFile = 0;
			ArrayList<String> nextLine;
			boolean fFirst = true;
			boolean fWithNewlines = true;
			int lastIDVal = -1;
			ArrayList<String> lastStrings = new ArrayList<String>();
			HashMap<TaxoNode, Float> matchedCats = new HashMap<TaxoNode, Float>();
			nObjCatsTillReport = nObjCatsReport;
			boolean fMoreLines = true;
			while( fMoreLines ) {
				int id = -1;
				if((nextLine = metaDataReader.getNextLineAsColumns()) == null) {
					fMoreLines = false;
					// Handle case of empty file.
					if( lastStrings.size() == 0 )
						break;
				} else {
					// Look at new line and consider merging with previous
					if(nextLine.get(objIDCol).length() == 0) {
						debug(2,"Skipping line with empty id" );
						continue;
					}
					id = Integer.parseInt(nextLine.get(objIDCol));
					if( lastIDVal < 0 ) {
						lastStrings.addAll(nextLine);
						lastIDVal = id;
						continue;
					}
					if( id == lastIDVal ) {
						// Consider merging the two lines, but only for the columns we care about.
						// Separate them by '|' chars to ensure we tokenize in next step
						// Note that we always skip col 0, the ID column
						for(int i=1; i<nCols; i++ ) {
							if(skipCol[i])				// Do not bother with misc cols
								continue;
							String nextToken = nextLine.get(i);
							if(nextToken.length()==0)	// If new token is empty ("") skip it
								continue;
							String last = lastStrings.get(i);
							if(last.length()==0)		// If old token is empty (""), just set
								lastStrings.set(i, nextLine.get(i));
							else if(nextLine.get(i).equalsIgnoreCase(last)
									|| last.contains(nextLine.get(i)))
								continue;
							else
								lastStrings.set(i, last+"|"+nextLine.get(i));
							debug(4,"Combining "+columnNames[i]+ " for id: "+id+
												" ["+lastStrings.get(i)+"]");
						}
						continue;
					}
				}
				// Have a complete line. Check for validity
				String objNumStr = lastStrings.get(objNumCol);
				if(!DumpColumnConfigInfo.objNumIsValid(objNumStr)) {
					// Skip lines with no object number.
					debug(1,"CategorizeForFacet: Discarding line for id (bad objNum): "+lastIDVal );
					nObjsSkippedTotal++;
				} else { //  Process a valid line
					if( objCatsWriter == null ) {
						nObjCatsDumpedToFile = 0;
			    		iOutputFile++;
						fFirst = true;
			    		currFilename = basefilename + iOutputFile + extension;
			    		objCatsWriter = new BufferedWriter(new FileWriter( currFilename ));
			    		if( fDumpAsSQLInsert ) {
				    		objCatsWriter.append("USE "+dbName+";");
				    		objCatsWriter.newLine();
				    		objCatsWriter.append("INSERT IGNORE INTO obj_cats(obj_id, cat_id, inferred, reliability) VALUES" );
				    		objCatsWriter.newLine();
			    		}
					}
					// Otherwise, now we output the info for the last Line, and then transfer
					// the next line to Last line and loop
					// We need to track the matches we have made, and the reliability assigned per column.
					// If two columns generate the same category, then we use the higher reliability.
					matchedCats.clear();
					for(int i=1; i<nCols; i++ ) {
						if(skipCol[i])				// Do not bother with misc cols
							continue;
						String source = lastStrings.get(i);
						if( source.length() <= 1 )
							continue;
						if( facetName != null )
							categorizeObjectForFacet( lastIDVal, source, lastStrings,
								facetName, colDumpInfo[i], facetMapHashTree, matchedCats );
						else
							categorizeObjectForAllFacets( lastIDVal, source, lastStrings,
									colDumpInfo[i], facetMapHashTree, matchedCats );
					}
					HashMap<TaxoNode, Float> inferredCats = addSimpleInferences(matchedCats);
					filterMatchedInferences( matchedCats, inferredCats);
					int nCatsDumped = SQLUtils.writeObjCatsSQL( lastIDVal,
											matchedCats, inferredCats, objCatsWriter,
											fFirst, fWithNewlines, fDumpAsSQLInsert );
					matchedCats.clear();
					if( nCatsDumped > 0) {
						nObjCatsDumpedTotal += nCatsDumped;
						nObjCatsDumpedToFile += nCatsDumped;
						nObjCatsTillReport -= nCatsDumped;
						fFirst = false;
					}
					if( nObjCatsDumpedToFile >= nObjsCatsPerDumpFile ) {
						if( fDumpAsSQLInsert )
							objCatsWriter.append(";");
						objCatsWriter.flush();
						objCatsWriter.close();
						objCatsWriter = null;	// signal to open next one
						debug(1,"Wrote "+nObjCatsDumpedToFile+" object categories to file: "+currFilename);
					} else if( nObjCatsTillReport <= 0 ) {
						debug(1,"Wrote "+nObjCatsDumpedToFile+" object categories to file: "+currFilename);
						nObjCatsTillReport = nObjCatsReport;
					}
					if( nObjCatsDumpedTotal >= nObjCatsOutMax )
						break;
				}
				lastIDVal = id;
				lastStrings = nextLine;
			}
			if( objCatsWriter != null ) {
				if( fDumpAsSQLInsert ) {
					objCatsWriter.append(";");
					objCatsWriter.newLine();
					objCatsWriter.append("SHOW WARNINGS;");
				}
				objCatsWriter.flush();
				objCatsWriter.close();
			}
			debug(1,"Wrote "+nObjCatsDumpedTotal+" total object categories"
					+ ((facetName==null)?"":(" for facet: "+facetName)));
		} catch( IOException e ) {
			String tmp = "Categorizer.categorizeForFacet: Could not create or other problem writing:\n  "
				+((filename==null)?"null":filename)
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		} catch( RuntimeException e ) {
			String tmp = "Categorizer.categorizeForFacet: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	private static void categorizeObjectForFacet( int id, String source, ArrayList<String> allStrings,
			String facetName, DumpColumnConfigInfo colInfo,
			DoubleHashTree facetMapHashTree, HashMap<TaxoNode, Float> matchedCats ) {
		// Tokenize the string for this column
		Pair<ArrayList<String>,ArrayList<String>> tokenPair
				= prepareSourceTokens( source, colInfo );
		float reliability = colInfo.columnReliabilityForFacet(facetName);
		// Now, we try to find a category in the hashMap for each token
		for( String token:tokenPair.first ) {
			// Test where id is 317239 - how do we match FUR?
			TaxoNode node = facetMapHashTree.FindNodeByHook(facetName,token);
			if( node == null )
				continue;
			debug(3, "Obj:"+id+" matched concept: ["+
					facetName+":"+node.displayName+"] on token: "+token);
			// We have a candidate here. Check for the exclusions
			if( lineContainsExclusions( node.exclset, allStrings ))
				continue;
			// We have a TaxoNode match! Update matches with this node
			updateMatches( matchedCats, reliability, node);
			// Now, consider any pending implied nodes as well.
			if(( node.impliedNodesPending != null ) && ( node.impliedNodesPending.size() > 0 ))
				for( int i=0; i<node.impliedNodesPending.size(); i++ ) {
					updateMatches( matchedCats, reliability, node.impliedNodes.get(i));
				}
		}
	}

	private static void categorizeObjectForAllFacets( int id, String source, ArrayList<String> allStrings,
			DumpColumnConfigInfo colInfo,
			DoubleHashTree facetMapHashTree, HashMap<TaxoNode, Float> matchedCats ) {
		// Tokenize the string for this column. First list is tokens, second is words.
		Pair<ArrayList<String>,ArrayList<String>> tokenPair
				= prepareSourceTokens( source, colInfo );
		// Now, we try to find a category in each facet hashMap for each token
		for( String token:tokenPair.first ) {
			// Loop over the facets to mine list. Note that list specifies order.
			// As we find a token, we remove it from further consideration so we
			// get some auto-exclusion (should we configure this?)
			for( DumpColumnConfigInfo.MineInfoForColumn mineInfo:colInfo.facetsToMine ) {
				// Test where id is 317239 - how do we match FUR?
				TaxoNode node = facetMapHashTree.FindNodeByHook(mineInfo.facetName,token);
				if( node == null )
					continue;
				debug(3, "Obj:"+id+" matched concept: ["+
						mineInfo.facetName+":"+node.displayName+"] on token: "+token);
				// We have a candidate here. Check for the exclusions
				// TODO - shouldn't the exclusion be checked in the entire string?
				// If we only check the current token, then we'll only catch exclusions
				// that match hooks!
				if( lineContainsExclusions( node.exclset, allStrings )) {
					debug(3, "Obj:"+id+" excluding matched concept: ["+
							mineInfo.facetName+":"+node.displayName+"]");
					continue;
				}
				// We have a TaxoNode match! Update matches with this node
				updateMatches( matchedCats, mineInfo.reliability, node);
				// Now, consider any pending implied nodes as well.
				// TODO Verify that this should never happen, and remove this code.
				if(( node.impliedNodesPending != null ) && ( node.impliedNodesPending.size() > 0 ))
					for( int i=0; i<node.impliedNodesPending.size(); i++ ) {
						updateMatches( matchedCats, mineInfo.reliability, node.impliedNodes.get(i));
					}
				// Since we got a match, we may stop considering the other facets for this token.
				if( mineInfo.filterTokenOnMatch )
					break;
			}
		}
	}

	private static boolean lineContainsExclusions(ArrayList<String> exclSet, ArrayList<String> line) {
		if( exclSet != null && line != null ) {
			for( String str:line) {
				String strLower = str.toLowerCase();
				for( String excl:exclSet ) {
					if( strLower.contains(excl) )
						return true;
				}
			}
		}
		return false;
	}

	private static void updateMatches(
			HashMap<TaxoNode, Float> matchedCats,
			float baseReliability,
			TaxoNode node ) {
		// If not yet matched, or if this reliability
		// is greater than any previous match, set the value in the matchedCats.
		Float ret = matchedCats.get(node); // returns null if not in hashMap
		float priorRel = (ret==null)?0:ret.floatValue();
		if( baseReliability > priorRel )
			matchedCats.put(node, baseReliability);
	}

	/**
	 * Adds all the simply inferred concepts for the set of matched concepts.
	 * Also removes all matched concepts that were otherwise inferred, so we
	 * do not duplicate the entries in the index.
	 * @param matchedCats the set of matched concepts
	 * @return the set of inferred concepts
	 */
	protected static HashMap<TaxoNode, Float> addSimpleInferences(
						HashMap<TaxoNode, Float> matchedCats) {
		HashMap<TaxoNode, Float> inferredCats = new HashMap<TaxoNode, Float>();
		// We just consider each match in turn, and get all the inferred nodes.
		for( TaxoNode node:matchedCats.keySet() ) {
			Float ret = matchedCats.get(node); // returns null if not in hashMap
			float currRel = (ret==null)?0:ret.floatValue();
			node.AddInferredNodes(inferredCats, currRel);
		}
		return inferredCats;
	}

	/**
	 * Removes all matched concepts that were otherwise inferred, so we
	 * do not duplicate the entries in the index.
	 * @param matchedCats the set of matched concepts
	 * @param inferredCats the set of inferred concepts
	 * @return the set of inferred concepts
	 */
	protected static void filterMatchedInferences( HashMap<TaxoNode, Float> matchedCats,
						HashMap<TaxoNode, Float> inferredCats) {
		// Remove all the matchedCats that were otherwise inferred.
		// Before we do that, we adjust the reliability of the inferred node.
		ArrayList<TaxoNode> toRemove = null;
		for( TaxoNode node:matchedCats.keySet() ) {
			if( inferredCats.containsKey(node)) {
				Float ret = matchedCats.get(node);	// Should not return null, but be safe
				float matchRel = (ret==null)?0:ret.floatValue();
				ret = inferredCats.get(node);		// Should not return null, but be safe
				float infRel = (ret==null)?0:ret.floatValue();
				// We will boost reliability given match and inference
				float boostedRel = (float)(1.0 - ((1.0-matchRel)*(1.0-infRel)));
				inferredCats.put(node, boostedRel);
				// And add to list of nodes to remove from Matched Cats
				if( toRemove == null)
					toRemove = new ArrayList<TaxoNode>();
				toRemove.add(node);
			}
		}
		// Now remove the nodes from the matchedCats set.
		if( toRemove != null )
			for( TaxoNode node:toRemove )
				matchedCats.remove(node);
	}

	//
	/**
	 * Tokenizes an input string according to column info rules.
	 * Also maps all tokens and words to lower case.
	 * @param source input String
	 * @param colInfo columnInfo for this string
	 * @return Pair of Lists: first is tokens, second is words
	 */
	private static Pair<ArrayList<String>,ArrayList<String>> prepareSourceTokens(
			String source, DumpColumnConfigInfo colInfo ) {  //  @jve:decl-index=0:
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		// First we apply reduction. This cleans up certain oddities in the source
		String reducedSource = source.toLowerCase();
		for(Pair<String, String> reduce : colInfo.reduceRules) {
			reducedSource = reducedSource.replaceAll(reduce.first, reduce.second);
		}
		// Next, we tokenize with the token separators
		String[] tokens_1;
		if( colInfo.tokenSeparators.size() == 0 ) {
			// throw new RuntimeException( "No Token separators for column: " + colInfo.name);
			tokens_1 = new String[1];
			tokens_1[0] = reducedSource;
		} else {
			String regex = "\\||"+colInfo.tokenSeparators.get(0);
			for( int i=1; i<colInfo.tokenSeparators.size(); i++)
				regex += "|"+colInfo.tokenSeparators.get(i);
			tokens_1 = reducedSource.split(regex);
		}
		// Next, we further split up each token on space and certain punctuation and remove the noise items
		// We also build the words list for Colors
		for( int i=0; i< tokens_1.length; i++ ){
			String[] words_1 = tokens_1[i].trim().split("[\\W&&[^\\-]]");
			if( colInfo.noiseTokens.size() == 0 ){
				tokens.add(tokens_1[i].trim());
				for( int iW=0; iW<words_1.length; iW++)
					words.add(words_1[iW].trim());
			}
			else {
				StringBuilder sb = new StringBuilder();
				for( int iW=0; iW<words_1.length; iW++)
					if( !colInfo.noiseTokens.contains(words_1[iW]) ) {
						if( iW > 0)
							sb.append(' ');
						String word = words_1[iW].trim();
						sb.append(word);
						words.add(word);
					}
				tokens.add(sb.toString());
			}
		}
		return new Pair<ArrayList<String>,ArrayList<String>>(tokens, words);
	}



}
