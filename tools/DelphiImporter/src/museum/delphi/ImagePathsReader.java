package museum.delphi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Patrick
 *
 */
public class ImagePathsReader {
	protected BufferedReader reader = null;
	private HashMap<Integer, ArrayList<Pair<String,String>>> imageInfoMap = null;

	protected int _debugLevel = 2;

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public ImagePathsReader( String inFileName ) {
		try {
			reader = new BufferedReader(new FileReader(inFileName));
			imageInfoMap = new HashMap<Integer, ArrayList<Pair<String,String>>>();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open input file: " + inFileName);
		}
	}

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			System.out.println( str );
	}

	public ArrayList<Pair<String,String>> GetBasePathsForID( int id ) {
		return imageInfoMap.get(id);
	}


	/**
	 * Takes the first configured path for the id, if any, and adds a subpath.
	 * @param id Object id to get path for
	 * @param subPath Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public String GetSimpleSubPathForID( int id, String subPath ) {
		ArrayList<Pair<String,String>> paths = imageInfoMap.get(id);
		if( paths != null && paths.size() > 0 ) {
			Pair<String,String> firstPath = paths.get(0);
			return firstPath.first + "/" + subPath + "/" + firstPath.second;
		}
		return null;
	}

	/**
	 * Takes the first configured path for the id, if any, and adds the subpaths.
	 * @param id Object id to get path for
	 * @param subPath Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public Pair<String,String> GetSimpleSubPathsForID( int id, String subPath1, String subPath2 ) {
		ArrayList<Pair<String,String>> paths = imageInfoMap.get(id);
		if( paths != null && paths.size() > 0 ) {
			Pair<String,String> firstPath = paths.get(0);
			String retString1 = firstPath.first + "/" + subPath1 + "/" + firstPath.second;
			String retString2 = firstPath.first + "/" + subPath2 + "/" + firstPath.second;
			return new Pair<String,String>(retString1, retString2);
		}
		return null;
	}

	// Each line is assumed to have these tokens:
	// ID|path|filename
	public int readInfo( int nLinesMax, boolean fClearFirst ) {
		int reportSize = 1000;
		int nLinesAdded = 0;
		try {
			if( fClearFirst )
				imageInfoMap.clear();	// Start over;
			//int lastObjID = -1;
			while ( reader.ready() && (nLinesAdded<nLinesMax) ) {
				String line = reader.readLine();
				String[] tokens = line.split( "[|]" );
				if( tokens.length != 3 )
					throw new RuntimeException( "IPR.readInfo: Bad syntax - wrong # of terms.\n" + line );
				int id = Integer.parseInt(tokens[0]);
				String path = StringUtils.trimQuotes(tokens[1]);
				String filename = StringUtils.trimQuotes(tokens[2]);
				ArrayList<Pair<String,String>> prevPaths = imageInfoMap.get(id);
				if(prevPaths == null) {
					prevPaths = new ArrayList<Pair<String,String>>();
					imageInfoMap.put(id, prevPaths);
				}
				prevPaths.add(new Pair<String,String>(path, filename));
				nLinesAdded++;
				if(( nLinesAdded % reportSize ) == 0 )
					debug(1, "IPR.readInfo Processed "+nLinesAdded+" lines, "
							+imageInfoMap.size()+" objects." );
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		debug(1, "IPR.readInfo Processed "+nLinesAdded+" lines, "
				+imageInfoMap.size()+" objects." );
		return imageInfoMap.size();
	}

}
