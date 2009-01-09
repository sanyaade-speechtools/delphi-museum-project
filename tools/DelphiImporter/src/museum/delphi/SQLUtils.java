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
/**
 * @author pschmitz
 *
 */
public class SQLUtils {

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
	 * Produce the SQL dump for all hooks or exclusions in the passed facets
	 * @param facetList The list of facets to write
	 * @param dbName Name of the database to use
	 * @param fSaveHooks set to TRUE to write hooks; FALSE to write exclusions
	 * @param writer  SQL output file
	 * @param fWithNewlines set to TRUE to add newlines for easier reading
	 */
	public static void writeHooksOrExclusionsSQL( java.util.ArrayList<Facet> facetList,
			String dbName, boolean fSaveHooks, BufferedWriter writer, boolean fWithNewlines ) {
		String tablename = fSaveHooks ? "hooks":"exclusions";
		try {
			// Set up the correct DB
			writer.append("USE "+dbName+";");
			writer.newLine();
			writer.append("truncate "+tablename+";");
			writer.newLine();
			// First, let's populate the hooks table
			writer.append("INSERT INTO "+tablename+" ( cat_id, token ) VALUES" );
			writer.newLine();
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				for( TaxoNode child : facet.children ) {
					// for each child of the facet, dump.
					// Once something has been saved, fFirst will be marked false.
					fFirst = SQLUtils.writeHookOrExclusionSQL( child, fSaveHooks, writer,
														true, fFirst, fWithNewlines );
				}
			}
			writer.append(';');
			writer.newLine();
		} catch( IOException e ) {
			String tmp = "SQLUtils.setupHooksOrExclusionsDump: Exception writing to output file."
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	/**
	 * Generate all the hooks (or exclusions), with category id associations,
	 * for loading into the runtime Delphi database.
	 * May seem inefficient with all the entailment phrases, but
	 * it is just sql rows and makes some search (kwd match to category) easier.
	 * Will recurse for the entire subtree under the passed node.
	 *
	 * @param node The node to write hooks or exclusions for
	 * @param fSaveHooks set to TRUE to write hooks; FALSE to write exclusions
	 * @param writer  SQL output file
	 * @param fRootNode set to TRUE if this node is the facet root (unused)
	 * @param fFirst set to TRUE if this is the first line of output
	 * @param fWithNewlines set to TRUE to add newlines for easier reading
	 * @return TRUE if at least one output entry generated.
	 */
	public static boolean writeHookOrExclusionSQL(
			TaxoNode node, boolean fSaveHooks, BufferedWriter writer,
			boolean fRootNode, boolean fFirst, boolean fWithNewlines ) {
		try {
			if( fSaveHooks ) {
				// Save the display name, even if marked as nomatch, since things like
				// design motifs should show up if they enter "bird" in kwd.
				if( !fFirst ) {
					writer.append(',');
					if( fWithNewlines )
						writer.newLine();
				}
				// We use display names (in most cases) as hooks, but do not
				// fold to lower in the TaxoNode structure. Hooks assumes all lower.
				// TODO this should properly deal with Unicode collation mechanisms.
				writer.append("("+node.id+",\""+node.displayName.toLowerCase()+"\")");
				fFirst = false;
				// Save any synonyms as hooks
				if( node.synset != null )
					for( String hook : node.synset ) {
						if( !fFirst ) {
							writer.append(',');
							if( fWithNewlines )
								writer.newLine();
						}
						writer.append("("+node.id+",\""+hook+"\")");
						fFirst = false;
					}
			} else {
				// Save any exclusions
				if( node.exclset != null )
					for( String excl : node.exclset ) {
						if( !fFirst ) {
							writer.append(',');
							if( fWithNewlines )
								writer.newLine();
						}
						writer.append("("+node.id+",\""+excl+"\")");
						fFirst = false;
					}
			}
			// Now recurse to catch children
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					fFirst = writeHookOrExclusionSQL( child, fSaveHooks, writer,
																	false, fFirst, fWithNewlines );
				}
		} catch( IOException e ) {
			String tmp = "SQLUtils.dumpHooksOrExclusionsSQLForVocabNode: Exception writing to output file."
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
		return fFirst;
	}

	/**
	 * Generate the boilerplate before writing facet information to SQL file
	 * @param facetList The list of facets to write
	 * @param dbName Name of the database to use
	 * @param writer SQL output file
	 * @param fWithNewlines set to TRUE to add newlines for easier reading
	 */
	public static void writeFacetsSQL( java.util.ArrayList<Facet> facetList,
					String dbName, BufferedWriter writer, boolean fWithNewlines ) {
		try {
			// Set up the correct DB
			writer.append("USE "+dbName+";");
			writer.newLine();
			writer.append("truncate facets;");
			writer.newLine();
			// First, let's populate the facets table
			writer.append("INSERT INTO facets( id, name, display_name, description, notes ) VALUES" );
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				if( fFirst )
					fFirst = false;
				else
					writer.append(',');
				if(fWithNewlines)
					writer.newLine();
				writer.append("("+facet.id+",\""+facet.name+"\",\""+facet.displayName+"\", \'"
									+facet.description+"\', \'" + facet.notes + "\')");
			}
			writer.append(';');
			writer.newLine();
		} catch( IOException e ) {
			String tmp = "SQLUtils.writeFacetsAsSQL: Exception writing to output file."
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	/**
	 * Generate the boilerplate before writing facet information to SQL file
	 * Assumes that appending to facet info, so does not generate a "USE db" statement.
	 * @param facetList The list of facets to write
	 * @param dbName Name of the database to use
	 * @param writer SQL output file
	 * @param fWithNewlines set to TRUE to add newlines for easier reading
	 */
	public static void writeCategoriesSQL( java.util.ArrayList<Facet> facetList,
			String dbName, BufferedWriter writer, boolean fWithNewlines ) {
		try {
			writer.append("truncate categories;");
			writer.newLine();
			// Now, let's populate the categories table
			writer.append("INSERT INTO categories(id, parent_id, name, display_name, facet_id, select_mode, always_inferred) VALUES" );
			writer.newLine();
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				for( TaxoNode child : facet.children ) {
					// for each child of the facet, dump. Mark as a root in facet.
					writeCategorySQL( child, writer, true, fFirst, fWithNewlines );
					fFirst = false;
				}
			}
			writer.append(';');
			writer.newLine();
		} catch( IOException e ) {
			String tmp = "SQLUtils.writeCategoriesAsSQL: Exception writing to output file."
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	// We insert all the fields in declaration order:
	// id, parent_id, name, display_name, facet_id, select_mode, always_inferred
	// Since the facet roots are TaxoNodes, the effective roots in all facets
	// (i.e., the first level nodes, all will have non-null parents).
	// We pass in a flag to simplify this (rather than checking if parent->parent is null).
	// What about token-based pattern model?
	public static void writeCategorySQL( TaxoNode node, BufferedWriter writer,
			boolean fRootNode, boolean fFirst, boolean fWithNewlines ) {
		try {
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			String parentID = (fRootNode || node.parent == null)?"null":Integer.toString(node.parent.id);
			String select = (node.selectSingle)? "'single'":"'multiple'";
			int infer = (node.inferredByChildren)? 1:0;
			writer.append("("+node.id+","+parentID+",\""+node.name+"\",\""+node.displayName+"\","
							+node.facetid+","+select+","+infer+")");
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					writeCategorySQL( child, writer, false, false, fWithNewlines );
				}
		} catch( IOException e ) {
			String tmp = "SQLUtils.dumpSQLForVocabNode: Exception writing to output file."
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	/**
	 * Run through the metadata file, and gather up the ID, ObjectNumber,
	 * Name/Title and Description information for insertion into the DB.
	 * Will generate output file based upon the input filename.
	 * @param metaDataReader initialized reader of metadata source.
	 * @param imagePathsReader pass in non-NULL to add image path info
	 */
	public static void writeObjectsSQL( MetaDataReader metaDataReader,
										String columnNames[],
										ImagePathsReader imagePathsReader ) {
    	String filename = null;
    	String basefilename = null;
    	String extension = ".sql";
    	String currFilename = null;
    	int iOutputFile;
    	int nObjsOutMax = Integer.MAX_VALUE;
    	int nObjsProcessedFile = 0;
    	int nObjsProcessedTotal = 0;
    	int nObjsSkippedTotal = 0;
    	int nObjsPerDumpFile = 50000;
    	String descMergeSeparator = "|";
		ArrayList<Integer> descrCols = DumpColumnConfigInfo.getDescriptionColumns();
		try {
			debug(1,"Build Objects SQL...");
			// We need ObjectID, ObjectNumber, ObjectName, Description
			// TODO These should be mapped in the ColConfig file, and then
			//      we should check that the named columns are present.
			int objIDCol = 0;
			int objNumCol = 4;
			int objNameCol = 5;
			if( !columnNames[objIDCol].equalsIgnoreCase("ObjectID")
				|| !columnNames[objNumCol].equalsIgnoreCase("ObjectNumber")
				|| !columnNames[objNameCol].equalsIgnoreCase("ObjectName"))
				throw new RuntimeException("BuildObjectSQL columns not as expected!");
			/*
		    int returnVal = chooser.showSaveDialog(getJFrame());
		    if(returnVal != JFileChooser.APPROVE_OPTION)
		    	return;
	    	filename = chooser.getSelectedFile().getPath();
	    	*/
			filename = metaDataReader.getFileName();
	    	int iDot = filename.lastIndexOf('.');
	    	if( iDot<=0 )
	    		throw new RuntimeException("BuildObjectSQL: bad output filename!");
	    	/*
	    	extension = filename.substring(iDot);
	    	if(( filename.charAt(iDot-1) == '1' )
	    		&& ( filename.charAt(iDot-2) == '_' ))
	    		iDot -= 2;
	    	 */
	    	basefilename = filename.substring(0, iDot)+'_';
			BufferedWriter objWriter = null;
	    	iOutputFile = 0;
			ArrayList<String> nextLine;
			boolean fFirst = true;
			boolean fWithNewlines = true;
			int lastIDVal = -1;
			ArrayList<String> lastStrings = new ArrayList<String>();
			while((nextLine = metaDataReader.getNextLineAsColumns()) != null ) {
				if(nextLine.get(objIDCol).length() == 0) {
					debug(2,"Skipping line with empty id" );
					continue;
				}

				int id = Integer.parseInt(nextLine.get(objIDCol));
				if( lastIDVal < 0 ) {
					lastStrings.addAll(nextLine);
					lastIDVal = id;
					continue;
				}
				if( id == lastIDVal ) {
					// We cannot merge objNum or Name, but we will allow out of order
					// primary lines, and set these fields
					if( lastStrings.get(objNumCol).length() == 0 )
						lastStrings.set(objNumCol, nextLine.get(objNumCol));
					if( lastStrings.get(objNameCol).length() == 0 )
						lastStrings.set(objNameCol, nextLine.get(objNameCol));
					// Merge description columns
					for( int i=0; i<descrCols.size(); i++ ) {
						// Take each description column and fold all white space
						// (especially the newlines) into a single space.
						int iCol = descrCols.get(i);
						String desc = nextLine.get(iCol);
						if(desc.length()!=0) {	// If new token is empty ("") skip it
							String lastDesc = lastStrings.get(iCol);
							if(lastDesc.length()==0)		// If old token is empty (""), just set
								lastStrings.set(iCol, desc);
							else if(!desc.equalsIgnoreCase(lastDesc)
									&& !lastDesc.contains(desc)) {
								lastStrings.set(iCol, lastDesc+descMergeSeparator+desc);
								debug(2,"Combining descriptions for id: "+id+
											" ["+lastStrings.get(iCol)+"]");
							}
						}
					}
					// Merge hiddenNotes columns
					for( int iCol=0; iCol<DumpColumnConfigInfo.hiddenNotesCols.size(); iCol++ ) {
						// Take each description column and fold all white space
						// (especially the newlines) into a single space.
						String hNote = nextLine.get(iCol);
						if(hNote.length()!=0) {	// If new token is empty ("") skip it
							String lastHNote = lastStrings.get(iCol);
							if(lastHNote.length()==0)		// If old token is empty (""), just set
								lastStrings.set(iCol, hNote);
							else if(!hNote.equalsIgnoreCase(lastHNote)
									&& !lastHNote.contains(hNote)) {
								lastStrings.set(iCol, lastHNote+descMergeSeparator+hNote);
								debug(2,"Combining descriptions for id: "+id+
											" ["+lastStrings.get(iCol)+"]");
							}
						}
					}
					continue;
				}
				// Have a complete line - check for validity
				String objNumStr = lastStrings.get(objNumCol);
				if(!DumpColumnConfigInfo.objNumIsValid(objNumStr)) {
					// Skip lines with no object number.
					debug(2,"writeObjectsSQL: Discarding line for id (bad objNum): "+id );
					nObjsSkippedTotal++;
				} else {
					if( objWriter == null ) {
			        	nObjsProcessedFile = 0;
			    		iOutputFile++;
						fFirst = true;
			    		currFilename = basefilename + iOutputFile + extension;
						objWriter = new BufferedWriter(new FileWriter( currFilename ));
						//objWriter.append("USE "+dbName+";");
						//objWriter.newLine();
						// INSERT ALL in order:
						// id, objnum, name, description, thumb_path, med_img_path, lg_img_path, creation_time
						objWriter.append("INSERT IGNORE INTO objects(id, objnum, name, description, hiddenNotes, img_path, creation_time) VALUES" );
						objWriter.newLine();
					}
					// Otherwise, now we output the info for the last Line, and then transfer
					// the next line to Last line and loop
					writeObjectSQL( lastIDVal, lastStrings, objWriter,
										fFirst, fWithNewlines, imagePathsReader );
					fFirst = false;
					nObjsProcessedFile++;
					nObjsProcessedTotal++;
					if( nObjsProcessedTotal >= nObjsOutMax ) {
						if(lastStrings.size() > 0)
							writeObjectSQL( lastIDVal, lastStrings, objWriter,
												false, fWithNewlines, imagePathsReader  );
						break;
					}
					if( nObjsProcessedFile >= nObjsPerDumpFile ) {
						objWriter.append(";");
						objWriter.flush();
						objWriter.close();
						objWriter = null;	// signal to open next one
						debug(1,"Wrote "+nObjsProcessedFile+" objects to file: "+currFilename);
					}
				}
				lastIDVal = id;
				lastStrings = nextLine;
			}
			if( objWriter != null ) {
				objWriter.append(";");
				objWriter.newLine();
				objWriter.append("SHOW WARNINGS;");
				objWriter.flush();
				objWriter.close();
			}
			debug(1,"Wrote "+nObjsProcessedTotal+" total objects. Skipped: "+nObjsSkippedTotal);
		} catch( IOException e ) {
            e.printStackTrace();
			throw new RuntimeException("Could not create or other problem writing: "+
										((filename==null)?"null":filename));
		} catch( RuntimeException e ) {
			String tmp = "SQLUtils.writeObjectsSQL: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	public static void writeObjectSQL( int id, ArrayList<String> line, BufferedWriter writer,
			boolean fFirst, boolean fWithNewlines, ImagePathsReader imagePathsReader ) {
		try {
			// TODO use function attribute to find this
			int objNumCol = 4;
			// TODO use function attribute to find this
			int objNameCol = 5;
			String name = (line.get(objNameCol).length() == 0)?"(no name)"
					:(line.get(objNameCol).replace("\"", "\\\"").replace("'", "\\'"));
			ArrayList<ImageInfo> imgInfo = null;
			if( imagePathsReader != null )
				imgInfo = imagePathsReader.GetInfoForID(id);
			// Gather all the description columns together and append
			String description = "";
			ArrayList<Integer> descrCols = DumpColumnConfigInfo.getDescriptionColumns();
			boolean fFirstDescLine = true;
			for( int i=0; i<descrCols.size(); i++ ) {
				// Take each description column and fold all white space
				// (especially the newlines) into a single space.
				String newDescLine = line.get(descrCols.get(i)).trim();
				if( newDescLine.isEmpty() )
					continue;
				newDescLine = newDescLine.replaceAll("[\\s]+", " " );
				// There are some pipes as field separators when Description is merged
				// from several lines or DB fields. Turn these into line breaks.
				newDescLine = newDescLine.replaceAll("[|]", "<br />" );
				// newDescLine = newDescLine.replaceAll("\\(['\"]\\)", "\\\1");
				newDescLine = newDescLine.replace("\"", "\\\"");
				newDescLine = newDescLine.replace("'", "\\'");
				if( fFirstDescLine )
					fFirstDescLine = false;
				else
					description += "<br />";	// Join with line breaks between
				description += newDescLine;
			}
			// Now do the hiddenNotes column
			String hiddenNotes = "";
			boolean fFirstHNLine = true;
			for( int i=0; i<DumpColumnConfigInfo.hiddenNotesCols.size(); i++ ) {
				// Take each hiddenNotes column and fold all white space
				// (especially the newlines) into a single space.
				String newHNLine = line.get(DumpColumnConfigInfo.hiddenNotesCols.get(i)).trim();
				if( newHNLine.isEmpty() )
					continue;
				newHNLine = newHNLine.replaceAll("[\\s]+", " " );
				// newDescLine = newDescLine.replaceAll("\\(['\"]\\)", "\\\1");
				newHNLine = newHNLine.replace("\"", "\\\"");
				newHNLine = newHNLine.replace("'", "\\'");
				if( fFirstHNLine )
					fFirstHNLine = false;
				else
					hiddenNotes += " ";	// Join with spaces between
				hiddenNotes += newHNLine;
			}
			// Deal with line separation
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			// Dump the data
			writer.append("("+id+",\""+line.get(objNumCol).trim()+"\",\""+name+"\",\""
					+description+"\",\""+hiddenNotes+"\",");
			if(imgInfo == null)
				writer.append("null,");
			else {
				ImageInfo info = imgInfo.get(0);
				String imgPath = "\""+info.path + "/" + info.filename+"\",";
				writer.append(imgPath);
			}
			writer.append("now())");
		} catch( IOException e ) {
			e.printStackTrace();
			throw new RuntimeException("Could not create or other problem writing MD SQL file." );
		} catch( RuntimeException e ) {
			String tmp = "SQLUtils.writeObjectSQL: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	public static int writeObjCatsSQL( int id,
			HashMap<TaxoNode, Float> matchedCats, HashMap<TaxoNode, Float> inferredCats,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines, boolean fDumpAsSQLInsert ) {

		int nCatsMatched = 0;
		// Now we have sets of the Nodes we matched and inferred, to dump
		// We dump the matched cats that are not in the inferred map, then the inferred list
		for( TaxoNode node:matchedCats.keySet() ) {
			Float ret = matchedCats.get(node); // returns null if not in hashMap
			float reliability = (ret==null)?0:ret.floatValue();
			int iRel = Math.min(9,Math.round(10*reliability));
			if( fDumpAsSQLInsert )
				writeObjectCatInsertSQL(id, node, false, iRel, writer, fFirst, fWithNewlines );
			else
				writeObjectCatLoadFileSQL(id, node, false, iRel, writer, fFirst, fWithNewlines );
			nCatsMatched++;
			fFirst = false;
		}
		// Now emit the inferred nodes
		for( TaxoNode node:inferredCats.keySet() ) {
			Float ret = inferredCats.get(node); // returns null if not in hashMap
			float reliability = (ret==null)?0:ret.floatValue();
			int iRel = Math.min(9,Math.round(10*reliability));
			if( fDumpAsSQLInsert )
				writeObjectCatInsertSQL(id, node, true, iRel, writer, fFirst, fWithNewlines );
			else
				writeObjectCatLoadFileSQL(id, node, true, iRel, writer, fFirst, fWithNewlines );
			nCatsMatched++;
			fFirst = false;
		}
		return nCatsMatched;
	}

	private static void writeObjectCatInsertSQL( int id, TaxoNode category, boolean inferred, int reliability,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines ) {
		String entry = "";
		try {
			if( !fFirst ) {
				writer.append(',');
				if( fWithNewlines )
					writer.newLine();
			}
			// obj_id, cat_id, inferred, reliability
			entry = "("+id+","+category.id+(inferred?",1,":",0,")+reliability+")";
			writer.append(entry);
		} catch( IOException e ) {
            debugTrace(2, e);
			throw new RuntimeException("Problem writing obj_cats entry: "+entry );
		} catch( RuntimeException e ) {
			String tmp = "SQLUtils.writeObjectCatInsertSQL: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	private static void writeObjectCatLoadFileSQL( int id, TaxoNode category, boolean inferred, int reliability,
			BufferedWriter writer, boolean fFirst, boolean fWithNewlines ) {
		String entry = "";
		String sep = "|";
		String newLn = "\n";
		try {
			// obj_id, cat_id, inferred, reliability
			entry = id+sep+category.id+sep+(inferred?"1":"0")+sep+reliability+newLn;
			writer.append(entry);
		} catch( IOException e ) {
            debugTrace(2, e);
			throw new RuntimeException("Problem writing obj_cats entry: "+entry );
		} catch( RuntimeException e ) {
			String tmp = "SQLUtils.writeObjectCatLoadFileSQL: Error encountered:"
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}



}
