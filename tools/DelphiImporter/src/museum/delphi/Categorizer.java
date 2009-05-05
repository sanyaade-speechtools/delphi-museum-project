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
 * Also maintains a pair of class static hashMaps of inference info,
 * indexed respectively by required and excluded concepts.
 *
 */
// TODO make this an instance with instances of DoubleHashTree for facet info.
// Main should create the DoubleHashTree, and pass it in here when creating an instance.
public class Categorizer {
	public static final int COMPLEX_INFER_NONE = 0;
	public static final int COMPLEX_INFER_1_STEP = 1;
	public static final int COMPLEX_INFER_3_STEPS = 3;
	public static final int COMPLEX_INFER_5_STEPS = 5;
	private HashMap<Integer,ArrayList<ComplexInference>> inferencesByReqID = null;
	private HashMap<Integer,ArrayList<ComplexInference>> inferencesByExclID = null;
	private DoubleHashTree facetMapHashTree = null;
	// TODO We need to either compute this from the ontologies, OR configure it.
	private int maxNGramLength = 5;

	private static int _debugLevel = 2;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	public Categorizer( DoubleHashTree facetMapHashTree ) {
		if( facetMapHashTree == null )
			throw new RuntimeException("Categorizer must have non-null facetMap!");
		inferencesByReqID = new HashMap<Integer,ArrayList<ComplexInference>>();
		inferencesByExclID = new HashMap<Integer,ArrayList<ComplexInference>>();
		this.facetMapHashTree = facetMapHashTree;
	}

	/**
	 * mapComplexInference sets up the hashMaps references for the passed ComplexInferece.
	 * For each CI in the system, need to map it for doing the inferencing work.
	 * @param ci
	 */
	public void mapComplexInference( ComplexInference ci ) {
		// First add all the required ids to the associated map
		for( TaxoNode node : ci.required ) {
			ArrayList<ComplexInference> currlist = inferencesByReqID.get(node.id);
			if( currlist == null ) {
				currlist = new ArrayList<ComplexInference>();
				currlist.add(ci);
				inferencesByReqID.put(node.id, currlist);
			} else if( !currlist.contains(ci)) {
				currlist.add(ci);
			} else {
				throw new RuntimeException("Adding duplicate ComplexInference: "+ ci.toString()
											+" for required id: " + node.id);
			}
		}
		// Now add all the excluded ids to the associated map
		for( TaxoNode node : ci.excluded ) {
			ArrayList<ComplexInference> currlist = inferencesByExclID.get(node.id);
			if( currlist == null ) {
				currlist = new ArrayList<ComplexInference>();
				currlist.add(ci);
				inferencesByExclID.put(node.id, currlist);
			} else if( !currlist.contains(ci)) {
				currlist.add(ci);
			} else {
				throw new RuntimeException("Adding duplicate ComplexInference: "+ ci.toString()
						+" for excluded id: " + node.id);
			}
		}
	}

	/**
	 * Processes the object metadata to produce concept associations
	 * @param metaDataReader the object metadata source
	 * @param facetName the name of the facet, or null to do all at once
	 * @param complexInferenceSteps use one of COMPLEX_INFER_* constants, or #steps max.
	 *          If after n steps no new inferences are found, it will also stop.
	 * @param fDumpAsSQLInsert set TRUE for an insert statement, FALSE for a loadfile
	 * @param dbName the name of the DB to be used.
	 * @param compileUnmatchedUsage if TRUE, builds up a report of unmatched tokens, by column
	 */
	public void categorizeForFacet(MetaDataReader metaDataReader, String facetName,
			int complexInferenceSteps,
			String basefilename, boolean fDumpAsSQLInsert, String dbName,
			boolean compileUnmatchedUsage ) {
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
		int objNumCol = DumpColumnConfigInfo.getMuseumIdColumnIndex();
		int currID = -1;
    	// Hold the information for the Facet(s)
    	DumpColumnConfigInfo[] colDumpInfo = null;
		try {
			// Open the dump file
			String columnNames[] = metaDataReader.getColumnNames();
			try {
			colDumpInfo = DumpColumnConfigInfo.getAllColumnInfo(columnNames);
			} catch( IllegalArgumentException iae ) {
				String msg = "Mismatched columns between metadata configuration and metadata file.\n"
					+ iae.getLocalizedMessage();
				debug(0, msg);
				throw new RuntimeException(msg);
			}
			int nCols = columnNames.length;
			// Since we run through lots of lines, cache the skip columns info for speed
			boolean[] skipCol = new boolean[nCols];
			ArrayList<Counter<String>> vocabCounts = new ArrayList<Counter<String>>(nCols);
			for( int iCol=0; iCol<nCols; iCol++ ) {
				skipCol[iCol] = !((facetName==null)?
								 colDumpInfo[iCol].columnMinedForAnyFacet()
								:colDumpInfo[iCol].columnMinedForFacet(facetName));
				if(compileUnmatchedUsage && !skipCol[iCol])
					vocabCounts.add(new Counter<String>());
				else
					vocabCounts.add(null);
			}
			BufferedWriter objCatsWriter = null;
	    	iOutputFile = 0;
			boolean fFirst = true;
			boolean fWithNewlines = true;
			HashMap<TaxoNode, Float> matchedCats = new HashMap<TaxoNode, Float>();
			nObjCatsTillReport = nObjCatsReport;
			Pair<Integer,ArrayList<String>> objInfo = null;
			while((objInfo = metaDataReader.getNextObjectAsColumns()) != null ) {
				currID = objInfo.first;
				ArrayList<String> objStrings = objInfo.second;
				assert (objStrings.size()==nCols) : "Bad parse for obj:"+currID;
				// Have a complete line. Check for validity
				String objNumStr = objStrings.get(objNumCol);
				if(!DumpColumnConfigInfo.objNumIsValid(objNumStr)) {
					// Skip lines with no object number.
					debug(1,"CategorizeForFacet: Discarding line for id (bad objNum): "+currID );
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
						String source = objStrings.get(i);
						if( source.length() <= 1 )
							continue;
						if( facetName != null )
							categorizeObjectForFacet( currID, source, objStrings,
								facetName, colDumpInfo[i], matchedCats, vocabCounts.get(i) );
						else
							categorizeObjectForAllFacets( currID, source, objStrings,
									colDumpInfo[i], matchedCats, vocabCounts.get(i) );
					}
					// Now we handle inferences. First get all the simple inferences,
					// including inherited (ascendant) concepts, and simple declarations.
					HashMap<TaxoNode, Float> inferredCats = deriveSimpleInferences(matchedCats);
					// Now filter the matched cats that are inferred
					filterMatchedInferences( matchedCats, inferredCats);
					// Now we consider complex inferences.
					if(complexInferenceSteps >= Categorizer.COMPLEX_INFER_1_STEP) {
						// Merge the matched and inferred cats for complex inferencing
						HashMap<TaxoNode, Float> allCats = new HashMap<TaxoNode, Float>();
						allCats.putAll(matchedCats);
						allCats.putAll(inferredCats);
						int nStepsLeft = complexInferenceSteps;
						while(nStepsLeft>0) {
							HashMap<TaxoNode, Float> newlyInferredCats =
											deriveComplexInferences(allCats, false);
							if(newlyInferredCats.size()>0) {
								nStepsLeft--;
								// Add all the new cats to the set of inferred ones.
								inferredCats.putAll(newlyInferredCats);
								// And to the set we infer from
								allCats.putAll(newlyInferredCats);
							} else {
								nStepsLeft = 0;		// Stop looping - nothing new found
							}
						}
					}
					int nCatsDumped = SQLUtils.writeObjCatsSQL( currID,
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
			// Now, consider the unmatched usage
			if(compileUnmatchedUsage) {
				// We're going to dump the usage per column
				for(int i=1; i<nCols; i++ ) {
					Counter<String> vocabCountsForCol = vocabCounts.get(i);
					if(vocabCountsForCol == null)
						continue;
					currFilename = basefilename
	    						+"_"+(colDumpInfo[i].name.replaceAll("\\W", "_"))+"_Usage.txt";
					BufferedWriter usageWriter = new BufferedWriter(new FileWriter( currFilename ));
	    			debug(1,"Saving column usage info to file: " + currFilename);
	    			vocabCountsForCol.write(usageWriter, true, 0, true);
					usageWriter.flush();
					usageWriter.close();
				}
			}
		} catch( IOException e ) {
			String tmp = "Categorizer.categorizeForFacet: Could not create or other problem writing:\n  "
				+((currFilename==null)?"null":currFilename)
				+"\n Processing ID: " + currID
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		} catch( RuntimeException e ) {
			String tmp = "Categorizer.categorizeForFacet: Error encountered processing ID: " + currID
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	private void categorizeObjectForFacet( int id, String source, ArrayList<String> allStrings,
			String facetName, DumpColumnConfigInfo colInfo,
			HashMap<TaxoNode, Float> matchedCats, Counter<String> vocabCounts ) {
		// Tokenize the string for this column
		ArrayList<Pair<String,ArrayList<String>>> tokenList
				= StringUtils.prepareSourceTokens( source, colInfo );
		float reliability = colInfo.columnReliabilityForFacet(facetName);
		// Now, we try to find a category in the hashMap for each token
		for( Pair<String,ArrayList<String>> pair:tokenList ) {
			String token = pair.first;
			TaxoNode node = facetMapHashTree.FindNodeByHook(facetName,token);
			if( node != null ) {
				debug(3, "Obj:"+id+" matched concept: ["+
						facetName+":"+node.displayName+"] on token: "+token);
				// We have a candidate here. Check for the exclusions
				if( !lineContainsExclusions( node.exclset, allStrings )) {
					// We have a TaxoNode match! Update matches with this node
					updateMatches( matchedCats, reliability, node);
				}
			} else {
				debug(3, "No matched concept for full token: "+token);
				if(vocabCounts != null)
					vocabCounts.incrementCount(token, 1);
				// Now we consider the ngrams within, if there are 2 or more words
				ArrayList<String> words = pair.second;
				if(words.size()>1) {
					NGramStack ngrams = new NGramStack(words, maxNGramLength);
					NGram next;
					while((next=ngrams.pop())!=null) {
						String ngramStr = next.getString();
						node = facetMapHashTree.FindNodeByHook(facetName,ngramStr);
						if(node != null ) {
							debug(3, "Obj:"+id+" matched concept: ["+
									facetName+":"+node.displayName+"] on NGram: "+ngramStr);
							// We have a candidate here. Check for the exclusions
							if( !lineContainsExclusions( node.exclset, allStrings )) {
								// We have a TaxoNode match! Update matches with this node
								updateMatches( matchedCats, reliability, node);
								// And filter this nGram out from the list
								ngrams.filterMatch(next);
							}
						} else if(vocabCounts != null) {
							vocabCounts.incrementCount(ngramStr, 1);
						}
					}
				}
			}
		}
	}

	private void categorizeObjectForAllFacets( int id, String source, ArrayList<String> allStrings,
			DumpColumnConfigInfo colInfo, HashMap<TaxoNode, Float> matchedCats, Counter<String> vocabCounts ) {
		try {
			// Tokenize the string for this column. First list is tokens, second is words.
			ArrayList<Pair<String,ArrayList<String>>> tokenList
								= StringUtils.prepareSourceTokens( source, colInfo );
			// Now, we try to find a category in each facet hashMap for each token
			for( Pair<String,ArrayList<String>> pair:tokenList ) {
				String token = pair.first;
				boolean foundMatch =
					checkAllFacetsForToken(id, token, allStrings, false, colInfo, matchedCats );
				if(!foundMatch) {
					debug(3, "No matched concept for full token: "+token);
					ArrayList<String> words = pair.second;
					NGramStack missedngrams = null;
					if(vocabCounts != null) {
						//vocabCounts.incrementCount(token, 1);
						missedngrams = new NGramStack();
						// Treat as an ngram, to filter if partial ngrams match.
						if(words.size()>0)	// Some tokens are all punct, etc. - skip them
							missedngrams.add(new NGram(token, 0, words.size()-1));
					}
					// Now we consider the ngrams within, if there are 2 or more words
					if(words.size()>1) {
						NGramStack ngrams = new NGramStack(words, maxNGramLength);
						NGram next;
						while((next=ngrams.pop())!=null) {
							String ngramStr = next.getString();
							if(checkAllFacetsForToken(id, ngramStr,
									allStrings, true, colInfo, matchedCats )) {
								ngrams.filterMatch(next);
								if(missedngrams != null)
									missedngrams.filterMatch(next);
							} else if(missedngrams != null) {
								missedngrams.add(next);
							}
						}
					}
					// Now for all the ngrams left on the missed stack, add them
					if(missedngrams != null) {
						NGram next;
						while((next=missedngrams.pop())!=null) {
							vocabCounts.incrementCount(next.getString(), 1);
						}
					}
				}
			}
		} catch (Exception e ) {
		String tmp = "Categorizer.categorizeObjectForAllFacets: Error encountered processing ID: " + id
			+"\nSrc:"+source
			+"\n"+e.toString();
		debug(1, tmp);
        debugTrace(2, e);
		throw new RuntimeException( tmp );
		}
	}

	private boolean checkAllFacetsForToken(int id, String token,
			ArrayList<String> allStrings, boolean isNGram,
			DumpColumnConfigInfo colInfo, HashMap<TaxoNode, Float> matchedCats ) {
		// Loop over the facets to mine list. Note that list specifies order.
		// As we find a token, we may remove it from further consideration so we
		// get some auto-exclusion (configured)
		boolean foundMatch = false;
		for( DumpColumnConfigInfo.MineInfoForColumn mineInfo:colInfo.facetsToMine ) {
			TaxoNode node = facetMapHashTree.FindNodeByHook(mineInfo.facetName,token);
			if( node == null )
				continue;
			debug(3, "Obj:"+id+" matched concept: ["+
					mineInfo.facetName+":"+node.displayName+"] on "
						+(isNGram?"NGram: ":"token: ")+token);
			// We have a candidate here. Check for the exclusions
			if( lineContainsExclusions( node.exclset, allStrings )) {
				debug(3, "Obj:"+id+" excluding matched concept: ["+
						mineInfo.facetName+":"+node.displayName+"]");
				continue;
			}
			foundMatch = true;
			// We have a TaxoNode match! Update matches with this node
			updateMatches( matchedCats, mineInfo.reliability, node);
			// Since we got a match, we may stop considering the other facets for this token.
			if( mineInfo.filterTokenOnMatch )
				break;
		}
		return foundMatch;
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
	 * @param matchedCats the set of matched concepts
	 * @return the set of inferred concepts
	 */
	protected static HashMap<TaxoNode, Float> deriveSimpleInferences(
						HashMap<TaxoNode, Float> matchedCats) {
		try {
			HashMap<TaxoNode, Float> inferredCats = new HashMap<TaxoNode, Float>();
			// We just consider each match in turn, and get all the inferred nodes.
			for( TaxoNode node:matchedCats.keySet() ) {
				Float ret = matchedCats.get(node); // returns null if not in hashMap
				float currRel = (ret==null)?0:ret.floatValue();
				node.AddInferredNodes(inferredCats, currRel);
			}
			return inferredCats;
		} catch ( Exception e ) {
			String tmp = "Categorizer.deriveSimpleInferences: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	/**
	 * Adds all the complex inferred concepts for the passed set of matched concepts.
	 * @param matchedCats the set of matched concepts
	 * @param fCheckMatchedConcepts if true, will also consider rules that infer
	 *                     concepts already in matchedCats. If false, skips these.
	 *                     Can be used to check for stronger relevance.
	 * @return the set of inferred concepts
	 */
	protected HashMap<TaxoNode, Float> deriveComplexInferences(
						HashMap<TaxoNode, Float> matchedCats,
						boolean fCheckMatchedConcepts) {
		class MatchInfo {
			public ComplexInference ci = null;
			public int nReq = 0;
			public float reqMatches[] = null;
			public int nExcl = 0;
			public boolean exclMatches[] = null;
			public MatchInfo(ComplexInference newci) {
				ci = newci;
				nReq = ci.required.size();
				reqMatches = new float[nReq];
				for( int i=0; i<nReq; i++ ) {
					reqMatches[i] = 0;
				}
				if( ci.excluded != null && ci.excluded.size() > 0) {
					nExcl = ci.excluded.size();
					exclMatches = new boolean[nExcl];
					for( int i=0; i<nExcl; i++ ) {
						exclMatches[i] = false;
					}
				}
			}

			public void setReq( int id, float relevance ) {
				for( int i = 0; i<nReq; i++ ) {
					TaxoNode node = ci.required.get(i);
					if( node.id == id ) {
						reqMatches[i] = relevance;
					}
				}
			}

			public void setExcl( int id ) {
				for( int i = 0; i<nExcl; i++ ) {
					TaxoNode node = ci.excluded.get(i);
					if( node.id == id ) {
						exclMatches[i] = true;
					}
				}
			}
		}

		// This is used to track the matched required and excluded concepts
		ArrayList<MatchInfo> matches = new ArrayList<MatchInfo>();

		HashMap<TaxoNode, Float> inferredCats = new HashMap<TaxoNode, Float>();
		// We look at each matched cat, gathering up all the possible complex
		// inferences. We may need to match all the required concepts, and none
		// of the excluded ones, so we have to keep track of all we see.
		// We could make this a lot smarter about minimal requirements, but it
		// does not seem merited at this point. We could keep a boolean at the top
		// for "foundAtLeastOneForAnyRule" on both required and excluded, and then
		// skip further processing, but it is hardly worth it at this point.
		for( TaxoNode node:matchedCats.keySet() ) {
			Float ret = matchedCats.get(node); // Should never return null!
			float currRel = (ret==null)?0:ret.floatValue();
			if( currRel > 0 ) {
				// See if it is required in any complex inference.
				ArrayList<ComplexInference> aci = inferencesByReqID.get(node.id);
				if( aci != null ) {
					// For each complex inference the current node may infer...
					for( ComplexInference ci: aci ) {
						// Consider those where ci.infer is NOT in matchedCats?
						Float rel = fCheckMatchedConcepts?null:matchedCats.get(ci.infer);
						if((rel == null) || (rel.floatValue() <= 0)) {
							boolean fFound = false;
							for( MatchInfo mi:matches ) {
								if( mi.ci == ci ) {		// If already tracking this CI
									fFound = true;		// Note we found it
									mi.setReq(node.id, currRel);	// Set relevance
								}
							}
							if( !fFound ) {				// Add a new Match tracker
								MatchInfo mi = new MatchInfo(ci);
								mi.setReq(node.id, currRel);
								matches.add(mi);
							}
						}
					}
				} // Close if node is a requirement for any CIs
				// Now, run through the same process for exclusions
				// Note: concept can be both a requirement and an exclusion, in different CI's
				aci = inferencesByExclID.get(node.id);
				if( aci != null ) {
					// For each complex inference the current node may infer...
					for( ComplexInference ci: aci ) {
						boolean fFound = false;
						for( MatchInfo mi:matches ) {
							if( mi.ci == ci ) {		// If already tracking this CI
								fFound = true;		// Note we found it
								mi.setExcl(node.id);// Mark exclusion
							}
						}
						if( !fFound ) {				// Add a new Match tracker
							MatchInfo mi = new MatchInfo(ci);
							mi.setExcl(node.id);// Mark exclusion
							matches.add(mi);
						}
					}
				} // Close if node is an exclusion for any CIs
			}
		} // Close for-each matchedCats
		// Now, we consider all the MatchInfos to see if any CI's are inferred
		for( MatchInfo mi:matches ) {
			boolean fRequireAll = mi.ci.fRequireAll;
			boolean fReqFailure = false;
			float relevanceAccum = fRequireAll?1:0;
			for( int i = 0; i<mi.nReq; i++ ) {
				if( mi.reqMatches[i] == 0 ) {
					if( fRequireAll ) {
						fReqFailure = true;
						break;
					} // else cannot contribute to the accumulated relevance
				} else if(fRequireAll) {
					// Accumulate running total for relevance.
					// Assume rule is 1- (product of (1-r)'s)
					relevanceAccum *= 1-mi.reqMatches[i];
				} else {
					// Accumulate running total for relevance.
					// Assume rule is max relevance.
					if( mi.reqMatches[i] > relevanceAccum) {
						relevanceAccum = mi.reqMatches[i];
					}
				}
			}
			if( fReqFailure ) {
				debug(3, "Requirements failed for inference: " + mi.ci.name);
				continue;
			} else {
				if( fRequireAll ) {
					relevanceAccum = 1-relevanceAccum;
				}
			}
			// Now, consider the exclusions.
			boolean fExcludeAll = mi.ci.fExcludeAll;
			boolean fExclFound = false;
			for( int i = 0; i<mi.nReq; i++ ) {
				if( mi.exclMatches[i] ) {	// If this exclusion found
					fExclFound = true;		// note
					if( !fExcludeAll ) {	// If exclude any, we're done
						break;
					}
				} else if(fExcludeAll) {	// If exclusion not found, and need all
					fExclFound = false;		// Indicate failure to exclude, and done.
					break;
				}
			}
			if( fExclFound ) {
				debug(3, "Inference failed on exclusions: " + mi.ci.name);
				continue;
			}
			// Have all the requirements, and did not exclude, so add this to inferred concepts
			debug(2, "Adding inference: " + mi.ci.name + "("+mi.ci.infer.name+")"
								+" with relevance: " + relevanceAccum);
			inferredCats.put(mi.ci.infer, relevanceAccum);
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
		try {
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
		} catch ( Exception e ) {
			String tmp = "Categorizer.filterMatchedInferences: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}


}
