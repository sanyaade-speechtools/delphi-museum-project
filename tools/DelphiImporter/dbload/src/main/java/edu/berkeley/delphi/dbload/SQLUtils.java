/**
 *
 */
package edu.berkeley.delphi.dbload;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.berkeley.delphi.config.DumpColumnConfigInfo;
import edu.berkeley.delphi.mdingest.ImagePathsReader;
import edu.berkeley.delphi.mdingest.MetaDataReader;
import edu.berkeley.delphi.media.ImageInfo;
import edu.berkeley.delphi.ontology.Facet;
import edu.berkeley.delphi.ontology.TaxoNode;
import edu.berkeley.delphi.utils.Pair;
import edu.berkeley.delphi.utils.StringUtils;

/**
 * @author pschmitz
 *
 */
/**
 * @author pschmitz
 *
 */
public class SQLUtils {

	public static final boolean WRITE_AS_INSERT = true;
	public static final boolean WRITE_AS_LOADFILE = false;

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
			String dbName, boolean fSaveHooks, BufferedWriter writer,
			boolean asSQLInsert, boolean fWithNewlines ) {
		String tablename = fSaveHooks ? "hooks":"exclusions";
		try {
			if(asSQLInsert) {
				writer.append("truncate "+tablename+";\n");
				writer.append("INSERT INTO "+tablename+" ( cat_id, token ) VALUES\n" );
			} else {
				writer.append("To load this file, use an sql command: \n");
				writer.append("SET NAMES utf8;\n");
				writer.append("LOAD DATA LOCAL INFILE '{filename}' INTO TABLE "+tablename+" CHARACTER SET utf8\n" );
				writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES\n" );
				writer.append("( cat_id, token );\n" );
			}
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				for( TaxoNode child : facet.children ) {
					// for each child of the facet, dump.
					// Once something has been saved, fFirst will be marked false.
					fFirst = SQLUtils.writeHookOrExclusionSQL( child, fSaveHooks, writer,
											true, fFirst, asSQLInsert, fWithNewlines );
				}
			}
			if(asSQLInsert) {
				writer.append(";\n");
			}
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
	private static boolean writeHookOrExclusionSQL(
			TaxoNode node, boolean fSaveHooks, BufferedWriter writer,
			boolean fRootNode, boolean fFirst,
			boolean asSQLInsert, boolean fWithNewlines ) {
		try {
			if( fSaveHooks ) {
				// Save the display name, even if marked as nomatch, since things like
				// design motifs should show up if they enter "bird" in kwd.
				if(!fFirst && asSQLInsert) {
					writer.append(',');
					if(fWithNewlines)
						writer.append('\n');
				}
				// We use display names (in most cases) as hooks, but do not
				// fold to lower in the TaxoNode structure. Hooks assumes all lower.
				// TODO this should properly deal with Unicode collation mechanisms.
				String name = node.displayName.toLowerCase().replace("\"", "\\\"").replace("'", "\\'");
				if(asSQLInsert) {
					writer.append("("+node.id+",\""+name+"\")");
				} else {
					writer.append(node.id+"|\""+name+"\"\n");
				}
				fFirst = false;
				// Save any synonyms as hooks
				if( node.synset != null )
					for( String hook : node.synset ) {
						name = hook.replace("\"", "\\\"").replace("'", "\\'");
						if(asSQLInsert) {
							writer.append(',');
							if( fWithNewlines )
								writer.append('\n');
							writer.append("("+node.id+",\""+name+"\")");
						} else {
							writer.append(node.id+"|\""+name+"\"\n");
						}
					}
			} else {
				// Save any exclusions
				if( node.exclset != null )
					for( String excl : node.exclset ) {
						String name = excl.replace("\"", "\\\"").replace("'", "\\'");
						if(!fFirst && asSQLInsert) {
							writer.append(',');
							if( fWithNewlines )
								writer.append('\n');
						}
						if(asSQLInsert) {
							writer.append("("+node.id+",\""+name+"\")");
						} else {
							writer.append(node.id+"|\""+name+"\"\n");
						}
						fFirst = false;
					}
			}
			// Now recurse to catch children
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					fFirst = writeHookOrExclusionSQL( child, fSaveHooks, writer,
												false, fFirst, asSQLInsert, fWithNewlines );
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
					String dbName, BufferedWriter writer,
					boolean asSQLInsert, boolean fWithNewlines ) {
		try {
			if(asSQLInsert) {
				writer.append("truncate facets;\n");
				writer.append(
				 "INSERT INTO facets( id, name, display_name, description, notes ) VALUES\n" );
			} else {
				writer.append("To load this file, use an sql command: \n");
				writer.append("SET NAMES utf8;\n");
				writer.append("LOAD DATA LOCAL INFILE '{filename}' INTO TABLE facets CHARACTER SET utf8\n" );
				writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES\n" );
				writer.append("(id, name, display_name, description, notes);\n" );
			}
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				if( fFirst )
					fFirst = false;
				else if(asSQLInsert) {
					writer.append(',');
					if(fWithNewlines)
						writer.append('\n');
				}
				String name = (facet.name==null||facet.name.isEmpty())?"":
								facet.name.replace("\"", "\\\"").replace("'", "\\'");
				String dname = (facet.displayName==null||facet.displayName.isEmpty())?"":
								facet.displayName.replace("\"", "\\\"").replace("'", "\\'");
				String desc = (facet.description==null||facet.description.isEmpty())?"":
								facet.description.replace("\"", "\\\"").replace("'", "\\'").replaceAll("[\\s]+", " ");
				String notes = (facet.notes==null||facet.notes.isEmpty())?"":
								facet.notes.replace("\"", "\\\"").replace("'", "\\'").replaceAll("[\\s]+", " ");
				if(asSQLInsert) {
					writer.append("("+facet.id+",\""+name+"\",\""+dname+"\", \""
													+desc+"\", \"" +notes + "\")");
				} else {
					writer.append(facet.id+"|\""+name+"\"|\""+dname+"\"|\""
													+desc+"\"|\"" +notes + "\"\n");
				}
			}
			if(asSQLInsert)
				writer.append(";\n");
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
	 * @param facetList The list of facets to write
	 * @param dbName Name of the database to use
	 * @param writer SQL output file
	 * @param fWithNewlines set to TRUE to add newlines for easier reading
	 */
	public static void writeCategoriesSQL( java.util.ArrayList<Facet> facetList,
			String dbName, BufferedWriter writer,
			boolean asSQLInsert, boolean fWithNewlines ) {
		try {
			// Write out the header
			if(asSQLInsert) {
				writer.append("truncate categories;\n");
				writer.append(
				"INSERT INTO categories(id, parent_id, name, display_name, facet_id, select_mode, always_inferred) VALUES\n" );
			} else {
				writer.append("To load this file, use an sql command: \n");
				writer.append("SET NAMES utf8;\n");
				writer.append("LOAD DATA LOCAL INFILE '{filename}' INTO TABLE categories CHARACTER SET utf8\n" );
				writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES\n" );
				writer.append("(id, parent_id, name, display_name, facet_id, select_mode, always_inferred);\n" );
			}
			boolean fFirst = true;
			for( Facet facet : facetList ) {
				for( TaxoNode child : facet.children ) {
					// for each child of the facet, dump. Mark as a root in facet.
					writeCategorySQL( child, writer, true, fFirst, asSQLInsert, fWithNewlines );
					fFirst = false;
				}
			}
			if(asSQLInsert) {
				writer.append(";\n");
			}
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
	private static void writeCategorySQL( TaxoNode node, BufferedWriter writer,
			boolean fRootNode, boolean fFirst,
			boolean asSQLInsert, boolean fWithNewlines ) {
		try {
			if( !fFirst ) {
				if(asSQLInsert) {
					writer.append(',');
					if( fWithNewlines )
						writer.append('\n');
				}
			}
			String parentID = (fRootNode || node.parent == null)?
								(asSQLInsert?"null":"\\N")
								:Integer.toString(node.parent.id);
			String select = (node.selectSingle)? "single":"multiple";
			int infer = (node.inferredByChildren)? 1:0;
			if(asSQLInsert) {
				writer.append("("+node.id+","+parentID+",\""+node.name+"\",\""+node.displayName+"\","
						+node.facetid+",\""+select+"\","+infer+")");
			} else {
				writer.append(node.id+"|"+parentID+"|\""+node.name+"\"|\""+node.displayName+"\"|"
						+node.facetid+"|\""+select+"\"|"+infer+'\n');
			}
			if( node.children != null )
				for( TaxoNode child : node.children ) {
					// Recurse for children. Note cannot be roots, nor first.
					writeCategorySQL( child, writer, false, false, asSQLInsert, fWithNewlines );
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
	public static void writeObjectsSQL( String filename, boolean asInsertFile,
										MetaDataReader metaDataReader,
										ImagePathsReader imagePathsReader ) {
    	String basefilename = null;
    	String extension = ".txt";
    	String currFilename = null;
    	int iOutputFile;
    	int nObjsOutMax = Integer.MAX_VALUE;
    	int nObjsProcessedFile = 0;
    	int nObjsProcessedTotal = 0;
    	int nObjsSkippedTotal = 0;
    	int nObjsPerDumpFile = 50000;
    	if(imagePathsReader!=null && !imagePathsReader.ready()) {
    		imagePathsReader = null;
    		debug(1, "SQLUtils.writeObjectsSQL: passed ImagePathsReader is not ready. Ignoring paths.");
    	}
		try {
			debug(1,"Build Objects SQL...");
			// We need ObjectID, ObjectNumber, ObjectName, Description
			int objIdCol = DumpColumnConfigInfo.getIdColumnIndex();
			int objNumCol = DumpColumnConfigInfo.getMuseumIdColumnIndex();
			int objNameCol = DumpColumnConfigInfo.getNameColumnIndex();

	    	int iDot = filename.lastIndexOf('.');
	    	if( iDot<=0 )
	    		throw new RuntimeException("writeObjectsSQL: bad output filename!");

	    	basefilename = filename.substring(0, iDot)+'_';
			BufferedWriter objWriter = null;
	    	iOutputFile = 0;
			boolean fFirst = true;
			boolean fWithNewlines = true;
			Pair<Integer,ArrayList<String>> objInfo = null;
			while((objInfo = metaDataReader.getNextObjectAsColumns()) != null ) {
				int id = objInfo.getFirst();
				ArrayList<String> objStrings = objInfo.getSecond();

				// Have a complete line - check for validity
				String objNumStr = objStrings.get(objNumCol);
				if(!DumpColumnConfigInfo.objNumIsValid(objNumStr)) {
					// Skip lines with no object number.
					debug(2,"writeObjectsSQL: Discarding line for id (bad objNum): "+id );
					nObjsSkippedTotal++;
				} else {
					if( objWriter == null ) {
			        	nObjsProcessedFile = 0;
			    		iOutputFile++;
						fFirst = true;
			    		currFilename = basefilename
			    						+ (asInsertFile?Integer.toString(iOutputFile):"all")
			    						+ extension;
						objWriter = new BufferedWriter(
									  new OutputStreamWriter(
										new FileOutputStream(currFilename),"UTF8"));
						if(asInsertFile) {
							// INSERT ALL in order:
							// id, objnum, name, description, thumb_path, med_img_path, lg_img_path, creation_time
							objWriter.append("INSERT IGNORE INTO objects(id, strid, objnum, name, description, hiddenNotes, img_path, creation_time) VALUES\n" );
						} else {
							objWriter.append("To load this file, use a sql command:\n");
							objWriter.append("SET NAMES utf8;\n");
							objWriter.append("LOAD DATA LOCAL INFILE '"+currFilename+"' INTO TABLE objects CHARACTER SET utf8\n");
							objWriter.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 6 LINES\n");
							objWriter.append("(id, objnum, name, description, hiddenNotes, img_path)\n");
							objWriter.append("SET creation_time=now();\n");
						}
					}
					// Otherwise, now we output the info for the last Line, and then transfer
					// the next line to Last line and loop
					String strid = objStrings.get(objIdCol);	
					// Get original string ID as well as numeric ID. If original (strid) was numeric, these will
					// look the same. If original strid was a UUID or something else, numeric will be different.
					writeObjectSQL( id, strid, objStrings, objNumCol, objNameCol, 
									objWriter,
									fFirst, asInsertFile, fWithNewlines, imagePathsReader );
					fFirst = false;
					nObjsProcessedFile++;
					nObjsProcessedTotal++;
					if( nObjsProcessedTotal >= nObjsOutMax ) {
						break;
					}
					if(asInsertFile && nObjsProcessedFile >= nObjsPerDumpFile ) {
						objWriter.append(";");
						objWriter.flush();
						objWriter.close();
						objWriter = null;	// signal to open next one
						debug(1,"Wrote "+nObjsProcessedFile+" objects to file: "+currFilename);
					}
				}
			}
			if( objWriter != null ) {
				if(asInsertFile) {
					objWriter.append(";");
					objWriter.newLine();
					objWriter.append("SHOW WARNINGS;");
				}
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

	private static void writeObjectSQL( int numid, String strid,
			ArrayList<String> line, 
			int objNumCol,
			int objNameCol,
			BufferedWriter writer,
			boolean fFirst, boolean asInsertFile, boolean fWithNewlines,
			ImagePathsReader imagePathsReader ) {
		try {
			// There are some pipes as field separators when Name is merged
			// from several lines or DB fields. Turn these into line breaks.
			// Since name is displayed, let's convert to semi-colon.
			String name = (line.get(objNameCol).length() == 0)?"(no name)"
					:(line.get(objNameCol).replace("\"", "\\\"").replace("'", "\\'")).replace("|", ";" );
			ArrayList<ImageInfo> imgInfo = null;
			if( imagePathsReader != null )
				imgInfo = imagePathsReader.GetInfoForID(strid);
			// Gather all the description columns together and append
			// Allow plenty of space to avoid re-allocs
			StringBuilder description = new StringBuilder(500);
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
				newDescLine = newDescLine.replace("\\", "\\\\");
				// newDescLine = newDescLine.replaceAll("\\(['\"]\\)", "\\\1");
				newDescLine = newDescLine.replace("\"", "\\\"");
				newDescLine = newDescLine.replace("'", "\\'");
				if( fFirstDescLine ) {
					fFirstDescLine = false;
					description.append(newDescLine);
				}
				else {
					// Check for duplicates
					// We might consider case here, but most fo the duplications are actually
					// straight copies, so we will ignore that for efficiency.
					// In addition, it could be argued that finding "yucatan" in a string
					// and dropping the single token "Yucatan" is a bad idea.
					// We'll also try the looser contains() model than a simple token
					// duplication, since the semantic index should pick up most concepts
					// anyway, and duplicating odd concepts from text is still ugly in the UI.
					if(description.indexOf(newDescLine)<0) { // not found, append
						description.append("<br />");	// Join with HTML line breaks between
						description.append(newDescLine);
					} else {
						debug(2, "writeObjSql["+strid+"] Discarding duplicate desc token: ["
								+newDescLine+"]");
					}
				}
			}
			// Now do the hiddenNotes column
			String hiddenNotes = "";
			boolean fFirstHNLine = true;
			for( int i=0; i<DumpColumnConfigInfo.getHiddenNotesCols().size(); i++ ) {
				// Take each hiddenNotes column and fold all white space
				// (especially the newlines) into a single space.
				String newHNLine = line.get(DumpColumnConfigInfo.getHiddenNotesCols().get(i)).trim();
				if( newHNLine.isEmpty() )
					continue;
				// There are some pipes as field separators when Description is merged
				// from several lines or DB fields. Convert to spaces, since hiddenNotes is keyword indexed.
				// Also, fold multiple spaces and newlines into single space.
				newHNLine = newHNLine.replaceAll("[|\\s]+", " " );
				newHNLine = newHNLine.replace("\\", "\\\\");
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
				if( asInsertFile )
					writer.append(',');
				if( fWithNewlines )
					writer.append('\n');
			}
			// Dump the data
			if( asInsertFile ) {
				writer.append("("+numid+",\""+strid+"\",\""+line.get(objNumCol).trim()+"\",\""+name+"\",\""
						+description+"\",\""+hiddenNotes+"\",");
				if(imgInfo == null)
					writer.append("null,");
				else {
					ImageInfo info = imgInfo.get(0);
					String imgPath = "\""+info.filepath+"\",";
					writer.append(imgPath);
				}
				writer.append("now())");
			} else {
				writer.append(numid+"|\""+strid+"\"|\""+line.get(objNumCol).trim()+"\"|\""+name+"\"|\""
						+description+"\"|\""+hiddenNotes+"\"|");
				if(imgInfo == null)
					writer.append("\\N");
				else {
					ImageInfo info = imgInfo.get(0);
					String imgPath = "\""+info.filepath+"\"";
					writer.append(imgPath);
				}
			}
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
			BufferedWriter writer, boolean fFirst, boolean fDumpAsSQLInsert, boolean fWithNewlines ) {

		try {
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
		} catch( Exception e ) {
			String tmp = "SQLUtils.writeObjCatsSQL: Error encountered writing id:"+id
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
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
