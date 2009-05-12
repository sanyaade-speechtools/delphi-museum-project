package museum.delphi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
//import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ListIterator;
//import java.util.Map;
//import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	private int nImagesTotal = 0;
	private int nImagesWithDims = 0;

	protected int _debugLevel = 1;

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	// Pass in a Document to add to? Then caller can set up
	// top level stuff like title, xsl spreadsheet, etc.
	// Could provide a convenience method in here to build the
	// default one with just a passed name default to Delphi or something.
	public ImagePathsReader( String inFileName ) {
		try {
			reader = new BufferedReader(new FileReader(inFileName));
			inFile = inFileName;
			imageInfoMap = new HashMap<Integer, ArrayList<ImageInfo>>();
			readInfo( Integer.MAX_VALUE, true);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open input file: " + inFileName);
		}
	}

	public ImagePathsReader(Element dbImageInfoNode) {
		final String myName = "ImagePathsReader.Ctor: ";
		Connection jdbcConnection = null;
		if(!"sqlserver".equals(dbImageInfoNode.getAttribute( "protocol" )))
			throw new RuntimeException(
			"ImagePathsReader: db spec currently only supports sqlserver protocol.");
	    String host = dbImageInfoNode.getAttribute( "host" );
	    if( host.length() <= 0 )
			throw new RuntimeException(myName+"No host specified.");
	    String dbName = dbImageInfoNode.getAttribute( "db" );
	    if( dbName.length() <= 0 )
			throw new RuntimeException(myName+"No db name specified.");
		boolean hasDims = "true".equalsIgnoreCase(dbImageInfoNode.getAttribute( "hasDims" ));
	    String user = dbImageInfoNode.getAttribute( "user" );
	    if( user.length() <= 0 )
			throw new RuntimeException(myName+"No user name specified.");
	    String password = dbImageInfoNode.getAttribute( "passwd" );
	    if( password.length() <= 0 )
			throw new RuntimeException(myName+"No password specified.");
		NodeList queryNodes = dbImageInfoNode.getElementsByTagName( "query" );
		if( queryNodes.getLength() != 1 ) {  // Must define exactly one.
			throw new RuntimeException(
					"ImagePathsReader: db spec must have exactly one query element.");
		}
		imageInfoMap = new HashMap<Integer, ArrayList<ImageInfo>>();
	    Element queryEl = (Element)queryNodes.item(0);
	    String queryString = queryEl.getTextContent();
		String connectionUrl = "jdbc:sqlserver://"+host
								+";databaseName="+dbName
								+";user="+user
								+";password="+password;
		Statement sqlStatement = null;
		ResultSet currResults = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			jdbcConnection = DriverManager.getConnection(connectionUrl);
			sqlStatement = jdbcConnection.createStatement();
			currResults = sqlStatement.executeQuery(queryString);
			while(currResults.next()) {
				int id = currResults.getInt(1);
				String path = currResults.getString(2);
				int iExt = path.lastIndexOf('.');
				if(iExt<=0) {
					debug(2, "IPR.readInfo Ignoring bad path for id: "+id+": "+path );
					continue;
				}
				path = path.substring(0, iExt+1)+"jpg";
				int width = 0;
				int height = 0;
				if(hasDims) {
					width = currResults.getInt(3);
					height = currResults.getInt(4);
				}
				ArrayList<ImageInfo> prevInfos = imageInfoMap.get(id);
				if(prevInfos == null) {
					prevInfos = new ArrayList<ImageInfo>();
					imageInfoMap.put(id, prevInfos);
				}
				ImageInfo ii = new ImageInfo(path, width, height);
				if( prevInfos.indexOf(ii) < 0 ) {
					prevInfos.add( ii );
					nImagesTotal++;
					if( width > 0  && height > 0 )
						nImagesWithDims++;
				} else {
					debug(2, "IPR.readInfo Saw duplicate entry for: ID: " + id
							+ "\n " + ii.toString() );
				}
			}
		} catch ( ClassNotFoundException cnfe ) {
			String tmp = myName+"Cannot load the SQLServerDriver class.";
			debug(0, tmp+"\n"+cnfe.getMessage());
			throw new RuntimeException(tmp);
		} catch (SQLException se) {
			String tmp = myName+"Problem connecting to DB. URL: "
				+"\n"+connectionUrl+"\n"+ se.getMessage();
			debug(0, tmp);
			throw new RuntimeException( tmp );
		} catch (Exception e) {
			String tmp = myName+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} finally {
			if (jdbcConnection != null) try { jdbcConnection.close(); } catch(Exception e) {}
			if (currResults != null) try { currResults.close(); } catch(Exception e) {}
		}
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
	public String GetPathForID( int id ) {
		ArrayList<ImageInfo> infos = imageInfoMap.get(id);
		if( infos != null && infos.size() > 0 ) {
			ImageInfo firstInfo = infos.get(0);
			return firstInfo.filepath;
		}
		return null;
	}

	// Each line is assumed to have these tokens:
	// ID|path|filename
	public void readInfo( int nLinesMax, boolean fClearFirst ) {
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
				if( prevInfos.indexOf(ii) < 0 ) {
					prevInfos.add( ii );
					nImagesTotal++;
					if( width > 0  && height > 0 )
						nImagesWithDims++;
				} else {
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
	}

	public int getNumObjs() {
		return nImagesTotal;
	}

	public int getNImagesWithDims() {
		return nImagesWithDims;
	}

	public void computeImageOrientations( String pathBase ) {
		debug(1,"Computing Image Orientations...");
		int nToReport = 2500;
		int nTillReport = nToReport;
		int nErrors = 0;
		nImagesWithDims = 0;
		java.util.Iterator<ArrayList<ImageInfo>> allImgsIterator = imageInfoMap.values().iterator();
		while( allImgsIterator.hasNext() ) {
			ArrayList<ImageInfo> imgsForId = allImgsIterator.next();
			java.util.ListIterator<ImageInfo> objImgIterator = imgsForId.listIterator();
			while( objImgIterator.hasNext() ) {
				ImageInfo ii = objImgIterator.next();
				if(ii.computeAspectR(pathBase)) {
					nImagesWithDims++;
			        debug(3, ii.toString() );
				} else {
			        debug(1, "Error processing image: " + ii.filepath);
			        nErrors++;
				}
				if( --nTillReport <= 0 ) {
			        debug(1, "Processed " + nImagesWithDims + " entries ("+nErrors+" errors)" );
					nTillReport = nToReport;
				}
			}
		}
        debug(1, "Processed a total of " + nImagesWithDims +
        		" (out of "+nImagesTotal+" total) entries. Errors: "+nErrors );
	}



	public void writeSQLMediaTableLoadFile( String outFileName, boolean omitMediaWithNoDims ) {
		try {
			writer = new BufferedWriter(new FileWriter(outFileName));
			debug(1,"Writing SQL LoadFile for Image Table...");
			// Write out the header
			writer.append("To load this file, use a sql command: ");
			writer.newLine();
			writer.append("LOAD DATA LOCAL INFILE '");
			// MySQL cannot handle Windows path separators
			if( java.io.File.separatorChar == '/' )
				writer.append(outFileName);
			else
				writer.append(outFileName.replace(java.io.File.separatorChar, '/'));
			writer.append("' INTO TABLE media\n" );
			writer.append("FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES\n" );
			writer.append("(obj_id, name, description, path, type, width, height)\n" );
			writer.append("SET aspectR=width/height, creation_time=now();\n" );
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
					if( omitMediaWithNoDims
						&& (ii.getAspectR() == ImageInfo.UNKNOWN_ORIENTATION))
						continue;
					writer.append(id + "|\\N|\\N|\"" + ii.filepath );
					writer.append("\"|\"image\"|" + ii.getWidth() + '|' + ii.getHeight()+'\n' );
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

	public void writeSQLMediaTableInsertFile( String outFileName, boolean omitMediaWithNoDims ) {
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
					if( omitMediaWithNoDims
						&& (ii.getAspectR() == ImageInfo.UNKNOWN_ORIENTATION))
						continue;
					if( fFirst )
						fFirst = false;
					else {
						writer.append("," );
						writer.newLine();
					}
					writer.append("("+id + ",\"" + ii.filepath );
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
