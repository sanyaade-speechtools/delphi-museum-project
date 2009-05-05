/**
 *
 */
package museum.delphi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

/**
 * @author pschmitz
 *
 */
public class SQLServerMetaDataReader extends DBMetaDataReader {

	// We keep two big lists, associating objIDs to the arrays of strings.
	// We use lists since we get the info ordered by objID, and so it
	// is not too expensive to hunt for the next objID, and much less
	// expensive than to convert a hashMap into a ordered list for output or scanning.
	protected ArrayList<Integer> objIDs = null;
	protected ArrayList<ArrayList<String>> objStringSets = null;
	protected int cursor = 0;

	/**
	 * @param dbSourceInfoNode
	 */
	public SQLServerMetaDataReader(Element dbSourceInfoNode) {
		super(dbSourceInfoNode);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch ( ClassNotFoundException cnfe ) {
			String tmp = "SQLServerMetaDataReader: Cannot load the SQLServerDriver class.";
			debug(0, tmp+"\n"+cnfe.getMessage());
			throw new RuntimeException(tmp);
		}
	}

	protected void resetLists() {
		if(objIDs!=null)
			objIDs.clear();
		else
			objIDs = new ArrayList<Integer>();
		if(objStringSets!=null)
			objStringSets.clear();
		else
			objStringSets = new ArrayList<ArrayList<String>>();
	}

	protected void prepareSources( String destColumns[] ) {
		// We build up a DB of destination columns associated to objID
		// We do this by looping over the sources, pulling all the data
		// out of each source and putting it into a big list of Pairs,
		// binding objID to destination columns
		final String myName = "SQLServerMetaDataReader.prepareSources: ";
		Connection jdbcConnection = null;
		resetLists();
		int nSrcs = sources.size();
		for( int iSrc=0; iSrc<nSrcs; iSrc++ ) {
			DBSourceInfo src = sources.get(iSrc);
			String separator = src.getDestColSeparator();
			String connectionUrl = "jdbc:sqlserver://"+host
									+";databaseName="+src.dbName
									+";user="+user
									+";password="+password;
			try {
				jdbcConnection = DriverManager.getConnection(connectionUrl);
				src.prepareResults(jdbcConnection);
				Pair<Integer, HashMap<String, String>> objInfo = null;
				int nObjs = objIDs.size();
				int lastIndex = 0;
				while((objInfo = src.getNextDestinationColumns()) != null) {
					HashMap<String, String> newCols = objInfo.second;
					if(iSrc==0) {	// First source - just stuff values
						objIDs.add(objInfo.first);
						ArrayList<String> destCols = new ArrayList<String>();
						for(int iCol=0; iCol<destColumns.length; iCol++) {
							String newCol = newCols.get(destColumns[iCol]);
							if( newCol == null || newCol.isEmpty())
								destCols.add("");
							else
								destCols.add(newCol);
						}
						objStringSets.add(destCols);
					} else {	// merge with previous source(s)
						int currObjID = objInfo.first;
						int nextIDInList = objIDs.get(lastIndex);
						while(currObjID > nextIDInList) {
							if(++lastIndex>=nObjs)
								break;
							nextIDInList = objIDs.get(lastIndex);
						}
						if(currObjID != nextIDInList) {
							debug( 1, myName+"source ["+iSrc+"] has unmatched objID: "+currObjID);
						} else {
							ArrayList<String> destCols = objStringSets.get(lastIndex);
							for(int iCol=0; iCol<destColumns.length; iCol++) {
								String newCol = newCols.get(destColumns[iCol]);
								if( newCol != null && !newCol.isEmpty()) {
									String newStr = destCols.get(iCol)+separator+newCol;
									destCols.set(iCol,newStr);
								}
							}
							objStringSets.set(lastIndex, destCols);
						}
					}
				}
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
				src.releaseResults();
			}
		}
	}

	public boolean hasNext() {
		return (objIDs!=null && cursor<objIDs.size());
	}

	protected int getCursor() throws IllegalStateException {
		if(objIDs!=null && cursor<objIDs.size())
			return cursor;
		throw new IllegalStateException("Object info not yet prepared.");
	}

	protected void setCursor(int newCursor) throws IndexOutOfBoundsException {
		if(objIDs!=null && newCursor<objIDs.size())
			cursor = newCursor;
		else
			throw new IndexOutOfBoundsException("NewCursor value out of range: "+newCursor);
	}

	protected Pair<Integer,ArrayList<String>> getNextObjectAsColumns() {
		Pair<Integer,ArrayList<String>> returnPair = null;
		if(objIDs!=null && cursor<objIDs.size()) {
			returnPair = new Pair<Integer,ArrayList<String>>(objIDs.get(cursor),
													   objStringSets.get(cursor));
		}
		return returnPair;
	}
}
