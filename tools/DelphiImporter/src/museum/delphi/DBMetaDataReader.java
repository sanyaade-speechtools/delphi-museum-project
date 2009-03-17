package museum.delphi;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class DBMetaDataReader {

	/*** how to store all the info - this is generic, not protocol specific.
	Need to model each source as a class, etc.
	Column is a local class of the source.
	JoinColumn subclasses this? May be no need, despite overlap.
	Source has:
		ArrayList<Column> that has both columns types (yes on subclass)
		RowFilter info (only one)
		ArrayList<something> that specifies replace rules
		ArrayList<something> that specifies elide rules (combine with above?)
	*/
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
		// Must be overridden for each protocol
	}

	protected Pair<Integer,ArrayList<String>> getNextObjectAsColumns() {
		Pair<Integer,ArrayList<String>> returnValue = null;
		// Code here!
		return returnValue;
	}
}
