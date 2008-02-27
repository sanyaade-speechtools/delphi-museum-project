package museum.delphi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
//import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ListIterator;
//import java.util.Map;
//import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

//import javax.swing.ImageIcon;
//import javax.swing.JOptionPane;

/**
 * @author Patrick
 *
 */
public class ImagePathsReader {
	protected BufferedReader reader = null;
	protected BufferedWriter writer = null;
	private HashMap<Integer, ArrayList<ImageInfo>> imageInfoMap = null;
	private String inFile = null;

	protected int _debugLevel = 1;

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public ImagePathsReader( String inFileName ) {
		try {
			reader = new BufferedReader(new FileReader(inFileName));
			inFile = inFileName;
			imageInfoMap = new HashMap<Integer, ArrayList<ImageInfo>>();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open input file: " + inFileName);
		}
	}

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			System.out.println( str );
	}

	public ArrayList<ImageInfo> GetInfoForID( int id ) {
		return imageInfoMap.get(id);
	}

	public Collection<ArrayList<ImageInfo>> GetAllAsList() {
		return imageInfoMap.values();
	}

	/**
	 * Takes the first configured path for the id, if any, and adds a subpath.
	 * @param id Object id to get path for
	 * @param subPath Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public String GetSimpleSubPathForID( int id, String subPath ) {
		ArrayList<ImageInfo> infos = imageInfoMap.get(id);
		if( infos != null && infos.size() > 0 ) {
			ImageInfo firstInfo = infos.get(0);
			return firstInfo.GetSimpleSubPath(subPath);
		}
		return null;
	}

	/**
	 * Takes the first configured path for the id, if any, and adds the subpaths.
	 * @param id Object id to get path for
	 * @param subPath1 Pass in a simple relative path, with no leading or trailing slashes
	 * @param subPath2 Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public Pair<String,String> GetSimpleSubPathsForID( int id, String subPath1, String subPath2 ) {
		ArrayList<ImageInfo> infos = imageInfoMap.get(id);
		if( infos != null && infos.size() > 0 ) {
			ImageInfo firstInfo = infos.get(0);
			return firstInfo.GetSimpleSubPaths(subPath1, subPath2);
		}
		return null;
	}

	// Each line is assumed to have these tokens:
	// ID|path|filename
	public int readInfo( int nLinesMax, boolean fClearFirst ) {
		int reportSize = 1000;
		int nLinesAdded = 0;
		int nDupes = 0;
		try {
			if( fClearFirst )
				imageInfoMap.clear();	// Start over;
			//int lastObjID = -1;
			while ( reader.ready() && (nLinesAdded<nLinesMax) ) {
				String line = reader.readLine();
				String[] tokens = line.split( "[|]" );
				// Either have id, path, filename, or that and width, height
				if( tokens.length != 3 && tokens.length != 5 )
					throw new RuntimeException( "IPR.readInfo: Bad syntax - wrong # of terms.\n" + line );
				int id = Integer.parseInt(tokens[0]);
				String path = StringUtils.trimQuotes(tokens[1]);
				String filename = StringUtils.trimQuotes(tokens[2]);
				// look for dims
				int width = 0;
				int height = 0;
				if( tokens.length == 5 ) {
					width = Integer.parseInt(tokens[3]);
					height = Integer.parseInt(tokens[4]);
				}
				ArrayList<ImageInfo> prevInfos = imageInfoMap.get(id);
				if(prevInfos == null) {
					prevInfos = new ArrayList<ImageInfo>();
					imageInfoMap.put(id, prevInfos);
				}
				ImageInfo ii = new ImageInfo(path, filename, width, height);
				//if( !prevInfos.contains(ii))
				if( prevInfos.indexOf(ii) < 0 )
					prevInfos.add( ii );
				else {
					nDupes++;
					debug(2, "IPR.readInfo Saw duplicate entry for: ID: " + id
							+ "\n " + ii.toString() );
				}
				nLinesAdded++;
				if(( nLinesAdded % reportSize ) == 0 )
					debug(1, "IPR.readInfo Processed "+nLinesAdded+" lines, "
							+imageInfoMap.size()+" objects." );
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		debug(1, "IPR.readInfo Processed "+nLinesAdded+" lines, "
				+imageInfoMap.size()+" objects. Found " + nDupes + " duplicates." );
		return imageInfoMap.size();
	}

	public void writeContents( String outFileName ) {
		try {
			writer = new BufferedWriter(new FileWriter(outFileName));
			debug(1,"Writing Image Info...");
			TreeSet<Integer> sortedKeys = new TreeSet<Integer>(imageInfoMap.keySet());
			Iterator<Integer> keysIterator = sortedKeys.iterator();
			// Provides id's in sorted order, and images are in original order.
			// Take the first one for each id, for the objects table.
			while( keysIterator.hasNext() ) {
				int id = keysIterator.next();
				ArrayList<ImageInfo> imgs = imageInfoMap.get(id);
				if( imgs == null )
					continue;
				ListIterator<ImageInfo> objImgIterator = imgs.listIterator();
				while( objImgIterator.hasNext() ) {
					ImageInfo ii = objImgIterator.next();
					writer.append(id + "|\"" + ii.path + "\"|\"" + ii.filename
							+ "\"|" + ii.getWidth() + "|" + ii.getHeight() );
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open output file: " + outFileName);
		} catch (IOException e) {
			throw new RuntimeException("Named file is a directory!: " + outFileName);
		}
	}

	/* OBSOLETE
	public void writeSQLObjTableUpdates( String outFileName ) {
		try {
			writer = new BufferedWriter(new FileWriter(outFileName));
			debug(1,"Writing SQL Updates for Image Info...");
			TreeSet<Integer> sortedKeys = new TreeSet<Integer>(imageInfoMap.keySet());
			Iterator<Integer> keysIterator = sortedKeys.iterator();
			// Provides id's in sorted order, and images are in original order.
			// Take the first one for each id, for the objects table.
			while( keysIterator.hasNext() ) {
				int id = keysIterator.next();
				ArrayList<ImageInfo> imgs = imageInfoMap.get(id);
				if( imgs == null )
					continue;
				ImageInfo ii = imgs.get(0);
				double img_ar = ii.getAspectR();
				if( img_ar != ImageInfo.UNKNOWN_ORIENTATION ) {
					writer.append("UPDATE objects SET img_ar=" +img_ar
							+ " WHERE id=" + id + ";");
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open output file: " + outFileName);
		} catch (IOException e) {
			throw new RuntimeException("Named file is a directory!: " + outFileName);
		}
	}
	*/

	public void writeSQLMediaTableLoadFile( String outFileName ) {
		try {
			writer = new BufferedWriter(new FileWriter(outFileName));
			debug(1,"Writing SQL LoadFile for Image Table...");
			// Write out the header
			writer.append("To load this file, use a sql command: ");
			writer.newLine();
			writer.append("LOAD DATA INFILE '");
			// MySQL cannot handle Windows path separators
			if( java.io.File.separatorChar == '/' )
				writer.append(outFileName);
			else
				writer.append(outFileName.replace(java.io.File.separatorChar, '/'));
			writer.append("' INTO TABLE media" );
			writer.newLine();
			writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES" );
			writer.newLine();
			writer.append("(obj_id, name, description, path, type, width, height)" );
			writer.newLine();
			writer.append("SET aspectR=width/height, creation_time=now();" );
			writer.newLine();
			TreeSet<Integer> sortedKeys = new TreeSet<Integer>(imageInfoMap.keySet());
			Iterator<Integer> keysIterator = sortedKeys.iterator();
			while( keysIterator.hasNext() ) {
				int id = keysIterator.next();
				ArrayList<ImageInfo> imgs = imageInfoMap.get(id);
				if( imgs == null )
					continue;
				ListIterator<ImageInfo> objImgIterator = imgs.listIterator();
				while( objImgIterator.hasNext() ) {
					ImageInfo ii = objImgIterator.next();
					writer.append(id + "|\\N|\\N|\"" + ii.path + '/' + ii.filename );
					writer.append("\"|\"image\"|" + ii.getWidth() + '|' + ii.getHeight() );
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open output file: " + outFileName);
		} catch (IOException e) {
			throw new RuntimeException("Named file is a directory!: " + outFileName);
		}
	}

	public void writeSQLMediaTableInsertFile( String outFileName ) {
		try {
			writer = new BufferedWriter(new FileWriter(outFileName));
			debug(1,"Writing SQL Insert for Image Table...");
			TreeSet<Integer> sortedKeys = new TreeSet<Integer>(imageInfoMap.keySet());
			Iterator<Integer> keysIterator = sortedKeys.iterator();
			writer.append("INSERT INTO media "
					+ "(obj_id, path, type, width, height, aspectR, creation_time )"
					+ " VALUES " );
			writer.newLine();
			boolean fFirst = true;
			while( keysIterator.hasNext() ) {
				int id = keysIterator.next();
				ArrayList<ImageInfo> imgs = imageInfoMap.get(id);
				if( imgs == null )
					continue;
				ListIterator<ImageInfo> objImgIterator = imgs.listIterator();
				while( objImgIterator.hasNext() ) {
					ImageInfo ii = objImgIterator.next();
					if( fFirst )
						fFirst = false;
					else {
						writer.append("," );
						writer.newLine();
					}
					writer.append("("+id + ",\"" + ii.path + '/' + ii.filename );
					int w = ii.getWidth();
					int h = ii.getHeight();
					double ar = ii.getAspectR();
					writer.append("\",\"image\"," + w + ',' + h + ',' + ar + ",now())" );
				}
			}
			writer.append(";" );
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open output file: " + outFileName);
		} catch (IOException e) {
			throw new RuntimeException("Named file is a directory!: " + outFileName);
		}
	}

	public String getInFile() {
		return inFile;
	}

}
