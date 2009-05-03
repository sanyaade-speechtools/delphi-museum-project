/**
 * Utility class to scan data from metadata dump files.
 */
package museum.delphi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import java.util.*;

//import org.w3c.dom.Element;
//import org.w3c.dom.Node;

//import org.w3c.dom.Node;

/**
 * @author Patrick Schmitz
 *
 */
public class MetaDataReader {
	protected BufferedReader reader = null;
	//protected HashSet<String> configInfo;

	private int _debugLevel = 1;
	private String filename;
	private String columnNames[] = null;
	private int colSep;
	private int nCols = 0;
	private int objIDCol = -1;
	private int currLine;
	// These are used to merge strings from multiple lines.
	private int lastIDVal = -1;
	private ArrayList<String> lastStrings = new ArrayList<String>();

	// TODO we should put the encoding into the colconfig file.
	//private static String encoding = "ISO-8859-1";
	private static String encoding = "UTF-8";

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public MetaDataReader( String inFileName, int inColSep, int numColumns, int objectIDColumnIndex ) {
		try {
			filename = inFileName;
			colSep = inColSep;
			if( objectIDColumnIndex < 0 )
				throw new RuntimeException("Illegal value for objectIDColumnIndex: "
											+ objectIDColumnIndex);
			objIDCol = objectIDColumnIndex;
			nCols = numColumns;
			//reader = new BufferedReader(new FileReader(filename));
			reader = new BufferedReader(
			          new InputStreamReader(new FileInputStream(filename),
			        		  encoding));
			currLine = 0;
			columnNames = readColumnHeaders();
			//skipTerms = new HashSet<String>();
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

	protected String getFileName() {
		return filename;
	}

	/*
	protected String getNextLine() {
		try {
			if( reader.ready() ) {
				// This needs to read lines until we have a full proper line with balanced quotes.
				// We find some embedded newlines in the file in the middle of strings. Sigh.
				String line = reader.readLine();
				currLine++;
				return line;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	protected Pair<Integer,ArrayList<String>> getNextObjectAsColumns() {
		ArrayList<String> nextLine = null;
		Pair<Integer,ArrayList<String>> returnValue = null;
		boolean fMoreLines = true;
		try {
			while( fMoreLines ) {
				int id = -1;
				if((nextLine = getNextLineAsColumns()) == null) {
					fMoreLines = false;
					// Handle case of empty file or EOF.
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
							debug(4,"Combining column["+i+"] for id: "+id+
												" ["+lastStrings.get(i)+"]");
						}
						continue;
					}
				}
				// Have a full set of info for an object. Pass the lastStrings
				returnValue = new Pair<Integer,ArrayList<String>>(lastIDVal, lastStrings);
				lastIDVal = id;
				if(nextLine != null) {
					lastStrings = nextLine;
				} else {
					lastStrings = new ArrayList<String>();	// Next time in will see it empty
				}
				break;	// We have a return value, so break out of loop
			}
		} catch( Exception e ) {
			String tmp = "MetaDataReader.getNextObjectAsColumns: Error encountered processing ID: " + lastIDVal
				+"\n"+e.toString();
			debug(1, tmp);
            //debugTrace(2, e);
			throw new RuntimeException( tmp );
		}

		return returnValue;
	}

	// TODO - this should be private. We should expose an interface that returns the next set of
	// columns for a given ID. This should do the line combining for the client. It will be
	// stateful, in that it will hold the last read line, but that is okay (localizes state
	// here, anyway. One variant returns array, another hashMap, etc.
	private ArrayList<String> getNextLineAsColumns() {
		// We will read chars up to newlines, parsing as we go.
		// If we hit a quote at the start, then we run to match a quote, even running over newlines
		// When not balancing a quote, run to colSep for a token, or to end of line.
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean fAtEOL = false;
		boolean fSeekQuote = false;
		boolean fEscape = false;
		boolean reported = false;
		try {
			while(reader.ready()){
				fAtEOL = false;
				int ch = reader.read();
				if(ch < 0) {
					tokens.add(sb.toString());
					fAtEOL = true;		// treat EOF as EOL
					break;
				}
				if(ch == '\\' && !fEscape ) {
					fEscape = true;
					continue;
				}
				if(ch == '"' && !fEscape ) {
					fSeekQuote = !fSeekQuote;
					continue;
				}
				if(!fSeekQuote && !fEscape ) {
					if(ch == colSep) {
						// Note that okay to be 0 length - it is an empty column
						tokens.add(sb.toString());
						int len = sb.length();
						if(len > 0)
							sb.delete(0, len);
						continue;
					} else if(ch == '\r'){	// absorb ^M chars
						continue;
					} else if(ch == '\n'){	// stop at newline
						tokens.add(sb.toString());
						int len = sb.length();
						if(len > 0)
							sb.delete(0, len);
						fAtEOL = true;
						break;
					}
				} // if fall through, just add the char
				sb.append((char)ch);
				if(sb.length() > 1000 && !reported) {
					debug(2, "Warning: long token encountered: ["+sb.toString()+"]" );
					reported = true;
				}
				fEscape = false;
			}
			// Deal with last line logic - if not at end of line but at EOF,
			// then have to add last column. Require at least 2 columns
			if( !reader.ready() && !fAtEOL && tokens.size()>1) {
				System.out.println("MDR.getNextLineAsColumns adding last token at EOF");
				tokens.add(sb.toString());
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(tokens.size()>0){
			currLine++;
			return tokens;
			//String[] arrTokens = new String[tokens.size()];
			//return tokens.toArray(arrTokens);
		}
		return null;
	}


	private String getNextLineColumn(int iCol) {
		ArrayList<String> tokens = getNextLineAsColumns();
		if( tokens != null ) {
			if( iCol >= tokens.size() ) {
				throw new RuntimeException( "Illegal column index: " + iCol
						+" (too few columns on line): " + currLine
						+"\n  Line:["+StringUtils.buildLineFromTokens(tokens, colSep)+"]");
			}
			return tokens.get(iCol);
		}
		return null;
	}

	private String[] readColumnHeaders() {
		if( currLine != 0 )
			throw new RuntimeException( "MDR.readColumnHeaders called when not at line 0");
		ArrayList<String> tokens = getNextLineAsColumns();
		if( tokens == null )
			throw new RuntimeException( "MDR.readColumnHeaders could not read any headers!");
		String[] arrTokens = new String[tokens.size()];
		debug(1, "MDR.readColHdrs found "+tokens.size()+" column names." );
		return tokens.toArray(arrTokens);
	}

	public final String[] getColumnNames() {
		return columnNames;
	}

	public final String getColumnName( int iCol ) {
		if(iCol < 0 || iCol > columnNames.length )
			return null;
		return columnNames[iCol];
	}

	public int getColumnIndex( String colName ) {
		for( int iCol=0; iCol < columnNames.length; iCol++ ) {
			if( colName.equals(columnNames[iCol]) )
				return iCol;
		}
		return -1;
	}

	protected void resetToLine1() {
		try{
			if(currLine != 1){
				debug(1, "MDR.resetToline1: Closing and re-opening file: "+filename);
				reader.close();
				reader = null;
				reader = new BufferedReader(new FileReader(filename));
				currLine= 0;
				getNextLineAsColumns();	// Get first line and discard
			} else
				debug(1, "MDR.resetToline1: Already at line 1 in file: "+filename);
			lastIDVal = -1;
			lastStrings = new ArrayList<String>();

		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not (re-)open input file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem closing metadata file: " + filename);
		}
	}

	protected Counter<String> compileUsageForColumn(int iCol, int minLength, int maxLength, int maxLines, MainApp app ) {
		Counter<String> vocabCounts = null;
		int nLinesEmpty = 0;
		int nLinesTooLong = 0;
		int reportFreq = 10000;
		// Should get the separators and the noise tokens for this column
		// add " and ", but note issues - if ends with fragments or bones, need to propagate to both
		// could just allow " and " if word n-1
		// include ". " for at least some cols
		// Strip '.' and ':' at end of tokens
		// Reduce number sequences to '#'
		// Consider mining especially for "used for", "used to", "used with", "used by", "for " etc.
		// run greps on all the report files for these. Also "made by"
		// remove "etc." and "etc" as noise words. Remove '?'.
		// remove fragment as noise word, although in the mining, want to reduce relevance if present.
		// Need to consider general purpose means of describing relevance reduction tokens
		//   e.g., if fragment(s) or "part of" there, or if preceded with "probably" or "maybe" or "possibly"
		//         or if includes "?"
		// TODO Need to detect when repeat of last line, and not bother with dupes.
		// TODO Need to put main work into a worker thread, so can report back into UI.
		String sepregex = "[;,]";
		HashSet<String> noiseTokens = new HashSet<String>();
		vocabCounts = new Counter<String>();
		// noiseTokens.add("each");
		if(maxLines < 0)
			maxLines = Integer.MAX_VALUE;
		if( iCol < 1 )
			throw new RuntimeException( "Illegal column index: " + iCol);
		try {
			resetToLine1();
			String token = null;
			while(((token = getNextLineColumn(iCol))!= null) && (currLine < maxLines)) {
				if( currLine%reportFreq == 0) {
					debug(1, "MDR.compileUsageForColumn("+iCol+") read "+currLine+" lines so far..."
							+"\n  Skipped "+nLinesEmpty+" empty lines and "+nLinesTooLong+" too long lines"
							+"\n  Found "+vocabCounts.size()+" distinct tokens (a total of: "+vocabCounts.totalCount()+" tokens)");
					/* Don't bother - we're running in UI thread, so cannot see updates.
					 * Once we put this into a worker thread, we can reconsider.
					 if( app != null ) {
						app.setStatus("Read "+currLine+" lines. Found "+vocabCounts.size()+" distinct tokens");
					}
					*/
				}
				if( token.length() < minLength ) {
					nLinesEmpty++;
					continue;
				}
				if( token.length() > maxLength ) {
					nLinesTooLong++;
					continue;
				}
				String[] subtokens = token.split(sepregex);
				for( String str : subtokens ) {
					String trimmed = str.trim().toLowerCase();
					if( noiseTokens.contains(trimmed) )
						continue;
					vocabCounts.incrementCount(trimmed, 1);
				}
			}
		}catch (RuntimeException e) {
			e.printStackTrace();
		}
		debug(1, "MDR.compileUsageForColumn("+iCol+") read "+currLine+" lines."
				+"\n  Skipped "+nLinesEmpty+" empty lines and "+nLinesTooLong+" too long lines"
				+"\n  Found "+vocabCounts.size()+" distinct tokens (a total of: "+vocabCounts.totalCount()+" tokens)");
		if( app != null )
			app.setStatus("Read "+currLine+" lines. Found "+vocabCounts.size()+" distinct tokens");
		return vocabCounts;
	}

}
