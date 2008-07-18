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
	private int colSep;
	private int currLine;
	// TODO we should put the encoding into the colconfig file.
	//private static String encoding = "ISO-8859-1";
	private static String encoding = "UTF-8";

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public MetaDataReader( String inFileName, int inColSep ) {
		try {
			filename = inFileName;
			colSep = inColSep;
			//reader = new BufferedReader(new FileReader(filename));
			reader = new BufferedReader(
			          new InputStreamReader(new FileInputStream(filename),
			        		  encoding));
			currLine = 0;
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

	protected ArrayList<String> getNextLineAsColumns() {
		// We will read chars up to newlines, parsing as we go.
		// If we hit a quote at the start, then we run to match a quote, even running over newlines
		// When not balancing a quote, run to colSep for a token, or to end of line.
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean fAtEOL = false;
		boolean fSeekQuote = false;
		boolean fEscape = false;
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


	protected String getNextLineColumn(int iCol) {
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

	protected String[] readColumnHeaders() {
		if( currLine != 0 )
			throw new RuntimeException( "MDR.readColumnHeaders called when not at line 0");
		ArrayList<String> tokens = getNextLineAsColumns();
		if( tokens == null )
			return null;
		String[] arrTokens = new String[tokens.size()];
		debug(1, "MDR.readColHdrs found "+tokens.size()+" column names." );
		return tokens.toArray(arrTokens);
	}

	protected void resetToLine1() {
		try{
			if(currLine != 1){
				debug(1, "MDR.resetToline1: Closing and re-opening file: "+filename);
				reader.close();
				reader = null;
				reader = new BufferedReader(new FileReader(filename));
				currLine= 0;
				getNextLineAsColumns();	// Git first line and discard
			} else
				debug(1, "MDR.resetToline1: Already at line 1 in file: "+filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not (re-)open input file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem closing metadata file: " + filename);
		}
	}

	protected Counter<String> compileUsageForColumn(int iCol, int minLength, int maxLength, int maxLines ) {
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
				if( currLine%reportFreq == 0)
					debug(1, "MDR.compileUsageForColumn("+iCol+") read "+currLine+" lines so far..."
							+"\n  Skipped "+nLinesEmpty+" empty lines and "+nLinesTooLong+" too long lines"
							+"\n  Found "+vocabCounts.size()+" distinct tokens (a total of: "+vocabCounts.totalCount()+" tokens)");
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
		return vocabCounts;
	}

}
