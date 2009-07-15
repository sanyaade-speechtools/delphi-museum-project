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

	protected static int _debugLevel = 1;

	/**
	 * @param dbSourceInfoNode
	 */
	public SQLServerMetaDataReader(Element dbSourceInfoNode) {
		super(dbSourceInfoNode);
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
			final String srcName = "src: "+iSrc+" ("+src.dbName+"."+src.tableName+"): ";
			String separator = src.getDestColSeparator();
			String connectionUrl = "jdbc:sqlserver://"+host
									+";databaseName="+src.dbName
									+";user="+user
									+";password="+password;
			debug(1, "SQLServerMDR.prepareSources Opening source: "+srcName );
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				jdbcConnection = DriverManager.getConnection(connectionUrl);
				src.prepareResults(jdbcConnection);
				Pair<Integer, HashMap<String, String>> objInfo = null;
				int nObjs = objIDs.size();
				int lastIndex = 0;
				int nRowsRead = 0;
				int nTillReport = 50000;
				int reportCountDown = nTillReport;
				while((objInfo = src.getNextDestinationColumns()) != null) {	// If the new source is beyond the base, stop processing.
					HashMap<String, String> newCols = objInfo.second;
					if(iSrc==0) {	// First source - just stuff values
						objIDs.add(objInfo.first);
						ArrayList<String> destCols = new ArrayList<String>(destColumns.length);
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
							debug( 3, myName+srcName+"has unmatched objID: "+currObjID);
							if(lastIndex>=nObjs) {
								debug( 1, myName+"abandoning source ["+srcName+"]; rest of IDs must be out of range.");
								break;			// No point in continuing with results - out of range
							}
						} else {
							ArrayList<String> destCols = objStringSets.get(lastIndex);
							for(int iCol=0; iCol<destColumns.length; iCol++) {
								String newCol = newCols.get(destColumns[iCol]);
								if( newCol != null && !newCol.isEmpty()) {
									String oldStr = destCols.get(iCol);
									String newStr;
									if(!oldStr.isEmpty())
										newStr = oldStr+separator+newCol;
									else
										newStr = newCol;
									destCols.set(iCol,newStr);
								}
							}
							objStringSets.set(lastIndex, destCols);
						}
					}
					++nRowsRead;
					if( --reportCountDown <= 0 ) {
						reportCountDown = nTillReport;
						debug(1, "SQLServerMDR.prepareSources Read: "+nRowsRead+" rows from "+srcName );
					}
				}
				debug(1, "SQLServerMDR.prepareSources Read: "+nRowsRead+" total rows from "+srcName );
			} catch ( ClassNotFoundException cnfe ) {
				String tmp = myName+"Cannot load the SQLServerDriver class.";
				debug(0, tmp+"\n"+cnfe.getMessage());
				throw new RuntimeException(tmp);
			} catch (SQLException se) {
				String tmp = myName+srcName+"Problem connecting to DB. URL: "
					+"\n"+connectionUrl+"\n"+ se.getMessage();
				debug(0, tmp);
				throw new RuntimeException( tmp );
			} catch (Exception e) {
				String tmp = myName+srcName+"\n"+ e.getMessage();
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
			cursor++;
		}
		return returnPair;
	}
}
