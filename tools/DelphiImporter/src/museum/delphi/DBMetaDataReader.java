package museum.delphi;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DBMetaDataReader {

	protected String host;
	protected String user;
	protected String password;
	protected ArrayList<DBSourceInfo> sources;

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ) {
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ) {
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	protected DBMetaDataReader(Element dbSourceInfoNode) {
		final String myName = "DBMetaDataReader.Ctor: ";
	    host = dbSourceInfoNode.getAttribute( "host" );
	    if( host.length() <= 0 )
			throw new RuntimeException(myName+"No host specified.");
	    user = dbSourceInfoNode.getAttribute( "user" );
	    if( user.length() <= 0 )
			throw new RuntimeException(myName+"No user name specified.");
	    password = dbSourceInfoNode.getAttribute( "passwd" );
	    if( password.length() <= 0 )
			throw new RuntimeException(myName+"No password specified.");
		sources = new ArrayList<DBSourceInfo>();
		scanDBSourceInfo(dbSourceInfoNode);
	    debug( 2, "DBMetaDataReader.Ctor: Added "+sources.size()+" sources.");
	}

	protected void scanDBSourceInfo(Element dbSourceInfoNode) {
		NodeList srcNodes = dbSourceInfoNode.getElementsByTagName( "source" );
		if( srcNodes.getLength() < 1 ) {  // Must define at least one.
			throw new RuntimeException(
					"DBMetaDataReader: file must have at least one source element.");
		}
		// For each info element, need to get all the fields.
		int nSrcs = srcNodes.getLength();
		for( int iSrc = 0; iSrc < nSrcs; iSrc++) {
		    Element srcInfoEl = (Element)srcNodes.item(iSrc);
		    DBSourceInfo srcInfo = DBSourceInfo.createFromConfig(srcInfoEl);
		    sources.add(srcInfo);
		    debug( 2, "DBMetaDataReader.scanDBSourceInfo: Added src for: "
		    		+srcInfo.dbName+":"+srcInfo.tableName);
		}
	}

	/**
	 * Subclasses must define this method. Can be used to initiate queries, set up caches,
	 * etc. When data is fetched, it must conform to the passed columns.
	 * @param destColumns specifies the names and order of logical columns to return.
	 */
	protected abstract void prepareSources(String destColumns[]);

	protected abstract boolean hasNext();
	protected abstract void setCursor(int value) throws IndexOutOfBoundsException;
	protected abstract int getCursor() throws IllegalStateException;

	protected abstract Pair<Integer,ArrayList<String>> getNextObjectAsColumns();
}
