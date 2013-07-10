package edu.berkeley.delphi.dbextract;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.berkeley.delphi.utils.Pair;
import edu.berkeley.delphi.utils.StringUtils;

/**
 * @author pschmitz
 *
 */
public class DBSourceInfo {
	protected static class DBColumnInfo {
		protected String name;
		protected String destination;

		protected DBColumnInfo( String name, String destination ) {
			this.name = name;
			this.destination = destination;
		}
	}

	protected static class DBJoinColumnInfo extends DBColumnInfo {
		protected String joinTableName;
		protected String localKey;
		protected String joinKey;

		protected DBJoinColumnInfo( String name, String destination,
				String joinTableName, String localKey, String joinKey ) {
			super(name, destination);
			this.joinTableName = joinTableName;
			this.localKey = localKey;
			this.joinKey = joinKey;
		}
	}

	protected String dbName;
	protected String tableName;
	protected String objIDColName;
	protected String rowFilter;
	protected ArrayList<Pair<Pattern,String>> textRules;	// match/replace pairs
	protected ArrayList<DBColumnInfo> columns;
	private ArrayList<String> destinationColumnNames = null;
	private String destColSeparator = "|";
	private Statement sqlStatement = null;
	private ResultSet currResults = null;

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ) {
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ) {
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	protected static DBColumnInfo createColInfoFromConfig( Element columnEl, boolean isJoinCol ) {
		// Get the attributes and create the object
		final String myName = "DBColumnInfo.createColInfoFromConfig";
	    String name = columnEl.getAttribute( "name" );
	    if( name.length() <= 0 )
			throw new RuntimeException(myName+": No name on column.");
	    String destination = columnEl.getAttribute( "destination" );
	    if( destination.length() <= 0 )
	    	throw new RuntimeException(myName+": No destination on column.");
	    if(!isJoinCol) {
	    	return new DBSourceInfo.DBColumnInfo( name, destination);
	    }
		String joinTableName = columnEl.getAttribute( "jointable" );
	    if( joinTableName.length() <= 0 )
			throw new RuntimeException("DBColumnInfo.createFromConfig: No name on column.");
		String localKey = columnEl.getAttribute( "localkey" );
		String joinKey = columnEl.getAttribute( "joinkey" );
    	return new DBSourceInfo.DBJoinColumnInfo( name, destination, joinTableName, localKey, joinKey);
	}

	// Need to fill in all the info from a passed <source> element in the config
	protected static DBSourceInfo createFromConfig( Element sourceEl ) {
		// Get the attributes and create the object
	    String dbName = sourceEl.getAttribute( "db" );
	    if( dbName.length() <= 0 )
			throw new RuntimeException("DBSourceInfo.createFromConfig: No db name on source.");
	    String tableName = sourceEl.getAttribute( "table" );
	    if( tableName.length() <= 0 )
			throw new RuntimeException("DBSourceInfo.createFromConfig: No table name on source.");
	    String objIDColName = sourceEl.getAttribute( "objIDColName" );
	    if( objIDColName.length() <= 0 )
			throw new RuntimeException("DBSourceInfo.createFromConfig: No objID column name on source.");
	    DBSourceInfo newSourceInfo = new DBSourceInfo( dbName, tableName, objIDColName);

	    int nNodes;

		// Get all the columns, and add those
		NodeList colNodes = sourceEl.getElementsByTagName( "column" );
		int nCols = colNodes.getLength();
		if( nCols >= 1 ) {
			// For each info element, need to get all the fields.
			nNodes = colNodes.getLength();
			for( int iCol = 0; iCol < nNodes; iCol++) {
			    Element colInfoEl = (Element)colNodes.item(iCol);
			    DBColumnInfo colInfo = DBSourceInfo.createColInfoFromConfig(colInfoEl, false);
			    newSourceInfo.columns.add(colInfo);
			    newSourceInfo.destinationColumnNames.add(colInfo.destination);
			    debug( 2, "DBSourceInfo.createFromConfig: Added col info: "
			    		+colInfo.name+", "+colInfo.destination);
			}
		}
		colNodes = sourceEl.getElementsByTagName( "joincolumn" );
		if( colNodes.getLength() < 1 ) {
			// If no joincolumns, and also no columns, complain
			if(nCols < 1)
				throw new RuntimeException(
					"DBSourceInfo.createFromConfig: Source for: "
					+dbName+":"+tableName+" must have at least one column.");
		} else {
			// For each info element, need to get all the fields.
			nNodes = colNodes.getLength();
			for( int iCol = 0; iCol < nNodes; iCol++) {
			    Element colInfoEl = (Element)colNodes.item(iCol);
			    DBColumnInfo colInfo = DBSourceInfo.createColInfoFromConfig(colInfoEl, true);
			    newSourceInfo.columns.add(colInfo);
			    newSourceInfo.destinationColumnNames.add(colInfo.destination);
			    debug( 2, "DBSourceInfo.createFromConfig: Source for: "
			    		+dbName+":"+tableName+": Added col info: "
			    		+colInfo.name+", "+colInfo.destination);
			}
		}
		// Add any rowfilter
		NodeList filterNodes = sourceEl.getElementsByTagName( "rowfilter" );
		nNodes = filterNodes.getLength();
		if( nNodes > 1 ) {
			throw new RuntimeException(
				"DBSourceInfo.createFromConfig: Source for: "
				+dbName+":"+tableName+" can only have one rowfilter.");
		} else if( nNodes == 1 ) {
		    Element filterInfoEl = (Element)filterNodes.item(0);
		    String where = filterInfoEl.getAttribute( "where" );
		    if( where.length() <= 0 )
				throw new RuntimeException(
						"DBSourceInfo.createFromConfig: Source for: "
						+dbName+":"+tableName+" rowfilter must specify 'where' value.");
		    newSourceInfo.rowFilter = where;
		    debug( 2, "DBSourceInfo.createFromConfig: Source for: "
		    		+dbName+":"+tableName+": Added rowfilter: "+where );
		}
		// Add all the elide and replace rules
		NodeList elideNodes = sourceEl.getElementsByTagName( "elide" );
		nNodes = elideNodes.getLength();
		for( int iElide = 0; iElide < nNodes; iElide++) {
		    Element elideInfoEl = (Element)elideNodes.item(iElide);
		    String match = elideInfoEl.getAttribute( "match" );
		    if( match.length() <= 0 )
				throw new RuntimeException(
						"DBSourceInfo.createFromConfig: Source for: "
						+dbName+":"+tableName+" elide must specify 'match' value.");
		    newSourceInfo.addElidePattern(match);
		    debug( 2, "DBSourceInfo.createFromConfig: Source for: "
		    		+dbName+":"+tableName+": Added elide rule: "+match );
		}
		NodeList replaceNodes = sourceEl.getElementsByTagName( "replace" );
		nNodes = replaceNodes.getLength();
		for( int iElide = 0; iElide < nNodes; iElide++) {
		    Element replaceInfoEl = (Element)replaceNodes.item(iElide);
		    String match = replaceInfoEl.getAttribute( "match" );
		    if( match.length() <= 0 )
				throw new RuntimeException(
						"DBSourceInfo.createFromConfig: Source for: "
						+dbName+":"+tableName+" replace must specify 'match' value.");
		    String pattern = replaceInfoEl.getAttribute( "pattern" );
		    if( pattern.length() <= 0 )
				throw new RuntimeException(
						"DBSourceInfo.createFromConfig: Source for: "
						+dbName+":"+tableName+" replace must specify 'pattern' value.");
		    newSourceInfo.addReplacePattern(match, pattern);
		    debug( 2, "DBSourceInfo.createFromConfig: Source for: "
		    		+dbName+":"+tableName+": Added replace rule: "+match+","+pattern );
		}
		return newSourceInfo;
	}

	private DBSourceInfo( String db, String table, String objIDCol ) {
		dbName = db;
		tableName = table;
		objIDColName = objIDCol;
		// Get the connection, and set readonly mode on it
		rowFilter = null;
		textRules = new ArrayList<Pair<Pattern,String>>();
		columns = new ArrayList<DBColumnInfo>();
		destinationColumnNames = new ArrayList<String>();
	}

	protected void addReplacePattern( String match, String pattern ) {
		try {
			Pattern matchP = Pattern.compile(match);
			textRules.add( new Pair<Pattern, String>(matchP, pattern) );
			} catch (PatternSyntaxException pse) {
				debug(1, "DBSourceInfo.addReplacePattern: Bad pattern\n"+pse.getMessage());
			}
	}

	protected void addElidePattern( String match ) {
		try {
			Pattern matchP = Pattern.compile(match);
			textRules.add( new Pair<Pattern, String>(matchP, "") );
			} catch (PatternSyntaxException pse) {
				debug(1, "DBSourceInfo.addElidePattern: Bad pattern\n"+pse.getMessage());
			}
	}

	protected final String getDestColSeparator() {
		return destColSeparator;
	}

	protected void setDestColSeparator( String newSep ) {
		destColSeparator = newSep;
	}

	/**
	 * Create a select statement for the configured columns, filters, etc.
	 * Subclasses can override to paginate results, etc.
	 * @return SQL select string for this source
	 */
	protected String buildSelect() {
		StringBuilder select = new StringBuilder("SELECT "+tableName+"."+objIDColName);
		//StringBuilder select = new StringBuilder("SELECT TOP 50000 "+tableName+"."+objIDColName);
		//StringBuilder tables = new StringBuilder(" FROM "+dbName+".dbo."+tableName);
		StringBuilder tables = new StringBuilder(" FROM "+tableName);
		// Keep track of which tables already added to FROM
		ArrayList<String> tableNames = new ArrayList<String>();
		tableNames.add(tableName);
		int nCols = columns.size();
		// Loop across the columns
		for( int iCol=0; iCol < nCols; iCol++ ) {
			select.append(",");
			DBColumnInfo colInfo = columns.get(iCol);
			try {
				DBJoinColumnInfo joinColInfo = (DBJoinColumnInfo)colInfo;
				// If we have not added this table yet, add it to the FROM clause
				String joinTable = joinColInfo.joinTableName;
				if( !tableNames.contains(joinTable) ) {
					tableNames.add(joinTable);
					tables.append(" JOIN "+joinTable
								+" ON ("+tableName+"."+joinColInfo.localKey+
									"="+joinTable+"."+joinColInfo.joinKey+")");
				}
				select.append(joinTable+"."+colInfo.name);
			} catch( ClassCastException cce ) {
				select.append(tableName+"."+colInfo.name);
			}
		}
		select.append(tables);	// combine the select and the from clauses
		// Add the where clause, if any
		if(rowFilter!= null && !rowFilter.isEmpty())
			select.append(" WHERE NOT("+rowFilter+")");
		// Add the order clause
		select.append(" ORDER BY "+tableName+"."+objIDColName);
		debug(2, "DBSourceInfo.buildSelect: "+select.toString());
		return select.toString();
	}

	protected void prepareResults(Connection dbConnection) {
		// We will build a select string, execute the query, and store
		// the results into an associative array keyed by objID. Each
		// value is an ArrayList<String>. Later, we can consider allowing
		// column types to be other scalar values.
		String select = buildSelect();
		// Execute the query
		try {
			sqlStatement = dbConnection.createStatement();
			currResults = sqlStatement.executeQuery(select);
		} catch (SQLException se) {
			String tmp = "DBSourceInfo.prepareResults: Problem with select SQL: "
				+"\n"+ se.getMessage();
			debug(0, tmp);
			throw new RuntimeException( tmp );
		} catch (Exception e) {
			String tmp = "DBSourceInfo.prepareResults: "+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		}
	}

	protected void releaseResults() {
		if (sqlStatement != null)
			try { sqlStatement.close(); sqlStatement=null;} catch(Exception e) {}
		if (currResults != null)
			try { currResults.close(); currResults = null;} catch(Exception e) {}
	}

	protected Pair<Integer, HashMap<String, String>> getNextDestinationColumns() {
		Pair<Integer, HashMap<String, String>> retPair = null;

		if(currResults != null) try {
			if(currResults.next()) {
				int objID = currResults.getInt(1);
				int nCols = columns.size(); // ID column plus configured sources
				HashMap<String, String> destMap = new HashMap<String, String>();
				// Now, run through the src columns, process them through the filters,
				// and map them to the destination columns.
				// Results columns are indexed from 1, but also include the ObjectID,
				// while destinationColumnNames is indexed from 0, with no ObjectID entry
				for( int iCol=0; iCol<nCols; iCol++ ) {
					String colText = currResults.getString(iCol+2);	// SQL indexes from 1, not 0
					if( colText != null && !colText.isEmpty() ) {
						colText = applyTextRules(colText.trim());
						String destCol = destinationColumnNames.get(iCol);
						String currDestCol = destMap.get(destCol);
						if( currDestCol != null && !currDestCol.isEmpty() ) {
							// TODO Should we elide copies? Need to investigate impact
							colText = currDestCol+destColSeparator+colText;
						}
						destMap.put(destCol, colText);
					}
				}
				retPair = new Pair<Integer, HashMap<String, String>>(objID, destMap);
			} else {
				try { currResults.close();} catch(Exception e) {} finally {currResults = null;}
			}
		} catch (SQLException se) {
			String tmp = "DBSourceInfo.getNextDestinationColumns: SQL exception: "
				+"\n"+ se.getMessage();
			debug(0, tmp);
			throw new RuntimeException( tmp );
		} catch (Exception e) {
			String tmp = "DBSourceInfo.getNextDestinationColumns: "+"\n"+ e.toString();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		}
		return retPair;
	}

	// Need to establish what we will pass in
	protected String applyTextRules(String input) {
		// Run through all the patterns, one by one
		String output = input;
		int nRules = textRules.size();
		for( int iRule=0; iRule<nRules; iRule++) {
			Pair<Pattern,String> rule = textRules.get(iRule);
			try {
			output = rule.getFirst().matcher(output).replaceAll(rule.getSecond());
			} catch (Exception e ) {
				debug(1, "DBSourceInfo.applyTextRules rule:"+iRule
							+"["+rule.getFirst().toString()+","+rule.getSecond()+"]\n"+e.toString());
				throw new RuntimeException(e);
			}
		}
		return output;
	}

	// Need to figure out how the storage will work
	// Version 1: We just get another row of data into an associative array of columns
	// Version 2: We load all the data into a local store of rows. This simplifies the
	//   DB connection management.
	// Need to fetch the DB name to build the connection, and then pass that in to the get method.

	protected final ArrayList<String> getDestinationColumns() {
		return destinationColumnNames;
	}
}
