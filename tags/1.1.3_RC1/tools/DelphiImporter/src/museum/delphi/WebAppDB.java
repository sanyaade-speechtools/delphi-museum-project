package museum.delphi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author pschmitz
 *
 */
public class WebAppDB {
	private String connectionUrl = null;

	protected int _debugLevel = 1;

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr( str );
	}

	protected void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	public WebAppDB(String protocol, String host, String dbName, String user, String password ) {
		final String myName = "WebAppDB.Ctor: ";
		if(!"mysql".equals(protocol))
			throw new RuntimeException(
				myName+"db currently only supports mysql protocol.");
	    if( host.length() <= 0 )
			throw new RuntimeException(myName+"No host specified.");
	    if( dbName.length() <= 0 )
			throw new RuntimeException(myName+"No db name specified.");
	    if( user.length() <= 0 )
			throw new RuntimeException(myName+"No user name specified.");
	    if( password.length() <= 0 )
			throw new RuntimeException(myName+"No password specified.");

		connectionUrl = "jdbc:mysql://"+host
								+"/"+dbName
								+"?user="+user
								+"&password="+password;
		}

	/**
	 * @param pathToLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean setAppLockout(boolean lockoutActive ) {
		final String myName = "WebAppDB.setAppLockout: ";
		String mainStatement =
			"UPDATE DBInfo SET lockoutActive="+(lockoutActive?"true":"false");
		boolean success = false;
		Statement sqlStatement = null;
		Connection jdbcConnection = openConnection();
		try {
			if(jdbcConnection != null) {
				sqlStatement = jdbcConnection.createStatement();
				debug(1, myName+"Setting lockout to: "+(lockoutActive?"true":"false"));
				sqlStatement.execute(mainStatement);
				success = true;
			}
		} catch (Exception e) {
			String tmp = myName+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} finally {
			closeConnection(jdbcConnection);
		}
		return success;
	}

	/**
	 * @param pathToLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean uploadObjectsMetadata(String pathToLoadfile) {
		// Map WinDoz paths to normal ones.
		pathToLoadfile = pathToLoadfile.replace('\\', '/');
		String mainStatement =
			"LOAD DATA LOCAL INFILE '"+pathToLoadfile+"' INTO TABLE objects CHARACTER SET utf8"
			+ " FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 6 LINES"
			+ " (id, objnum, name, description, hiddenNotes, img_path)"
			+ " SET creation_time=now()";
		return uploadData( "objects", mainStatement, null);
	}


	/**
	 * @param pathToLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean uploadCatalogCardMetadata(String pathToLoadfile) {
		// Map WinDoz paths to normal ones.
		pathToLoadfile = pathToLoadfile.replace('\\', '/');
		String deleteStmt = "DELETE FROM TABLE media WHERE type='catCard'";
		String mainStmt =
			"LOAD DATA LOCAL INFILE '"+pathToLoadfile+"' INTO TABLE media CHARACTER SET utf8"
			+ " FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' "
			+ " (obj_id,path,type,width,height,aspectR,creation_time)";
		return uploadData( "media", mainStmt, deleteStmt);
	}

	/**
	 * @param pathToLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean uploadImageMediaMetadata(String pathToLoadfile) {
		// Map WinDoz paths to normal ones.
		pathToLoadfile = pathToLoadfile.replace('\\', '/');
		String deleteStmt = "DELETE FROM TABLE media WHERE type='image'";
		String mainStmt =
			"LOAD DATA LOCAL INFILE '"+pathToLoadfile+"' INTO TABLE media CHARACTER SET utf8"
			+ " FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES "
			+ " (obj_id, name, description, path, type, width, height)"
			+ " SET aspectR=width/height, creation_time=now()";
		return uploadData( "media", mainStmt, deleteStmt);
	}

	/**
	 * @param pathToFacetsLoadfile Full path on the local system to a UTF8 encoded data file
	 * @param pathToConceptsLoadfile Full path on the local system to a UTF8 encoded data file
	 * @param pathToHooksLoadfile Full path on the local system to a UTF8 encoded data file
	 * @param pathToExclusionsLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean uploadOntologyMetadata(
			String pathToFacetsLoadfile, String pathToConceptsLoadfile,
			String pathToHooksLoadfile, String pathToExclusionsLoadfile ) {
		// Map WinDoz paths to normal ones.
		pathToFacetsLoadfile		= pathToFacetsLoadfile.replace('\\', '/');
		pathToConceptsLoadfile		= pathToConceptsLoadfile.replace('\\', '/');
		pathToHooksLoadfile			= pathToHooksLoadfile.replace('\\', '/');
		pathToExclusionsLoadfile	= pathToExclusionsLoadfile.replace('\\', '/');

		String mainStatement;
		if(pathToFacetsLoadfile != null && !pathToFacetsLoadfile.isEmpty()) {
			mainStatement =
				"LOAD DATA LOCAL INFILE '"+pathToFacetsLoadfile+"' INTO TABLE facets CHARACTER SET utf8"
				+" FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES"
				+" (id, name, display_name, description, notes)";
			if(!uploadData( "facets", mainStatement, null))
				return false;
		}
		if(pathToConceptsLoadfile != null && !pathToConceptsLoadfile.isEmpty()) {
			mainStatement =
				"LOAD DATA LOCAL INFILE '"+pathToConceptsLoadfile+"' INTO TABLE categories CHARACTER SET utf8"
				+" FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES"
				+" (id, parent_id, name, display_name, facet_id, select_mode, always_inferred)";
			if(!uploadData( "categories", mainStatement, null))
				return false;
			// Now, truncate the obj_cats since we redefined the categories.
			if(!uploadData( "obj_cats", mainStatement, null))
				return false;
		}
		if(pathToHooksLoadfile != null && !pathToHooksLoadfile.isEmpty()) {
			mainStatement =
				"LOAD DATA LOCAL INFILE '"+pathToHooksLoadfile+"' INTO TABLE hooks CHARACTER SET utf8"
				+" FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES"
				+" ( cat_id, token )";
			if(!uploadData( "hooks", mainStatement, null))
				return false;
		}
		if(pathToExclusionsLoadfile != null && !pathToExclusionsLoadfile.isEmpty()) {
			mainStatement =
				"LOAD DATA LOCAL INFILE '"+pathToExclusionsLoadfile+"' INTO TABLE exclusions CHARACTER SET utf8"
				+" FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 5 LINES"
				+" ( cat_id, token )";
			if(!uploadData( "exclusions", mainStatement, null))
				return false;
		}
		return true;
	}

	/**
	 * @param pathToLoadfile Full path on the local system to a UTF8 encoded data file
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean uploadObjectCategoryAssociations(String pathToLoadfile) {
		// Map WinDoz paths to normal ones.
		pathToLoadfile = pathToLoadfile.replace('\\', '/');
		String mainStatement =
			"LOAD DATA LOCAL INFILE '"+pathToLoadfile+"' INTO TABLE obj_cats CHARACTER SET utf8"
			+ " FIELDS TERMINATED BY '|' OPTIONALLY ENCLOSED BY '\"' IGNORE 4 LINES"
			+ " (obj_id, cat_id, inferred, reliability)";
		return uploadData( "obj_cats", mainStatement, null);
	}

	private boolean uploadData(String table, String mainStmt, String deleteStmt) {
		final String myName = "WebAppDB.uploadData["+table+"]: ";
		boolean success = false;
		Statement sqlStatement = null;
		Connection jdbcConnection = openConnection();
		String utf8SetupStatement = "SET NAMES utf8";
		String disableKeysStatement = "ALTER TABLE "+table+" DISABLE KEYS";
		String truncateStatement = "TRUNCATE "+table;
		String enableKeysStatement = "ALTER TABLE "+table+" ENABLE KEYS";
		String getCountStatement = "SELECT COUNT(*) FROM "+table;
		try {
			if(jdbcConnection != null) {
				sqlStatement = jdbcConnection.createStatement();
				if(deleteStmt!=null) {
					debug(1, myName+"Deleting entries from "+table+" table...");
					sqlStatement.execute(deleteStmt);
				} else {
					debug(1, myName+"Truncating "+table+" table...");
					sqlStatement.execute(truncateStatement);
				}
				if(mainStmt!=null){
					debug(1, myName+"Setting UTF* for names...");
					sqlStatement.execute(utf8SetupStatement);
					debug(1, myName+"Disabling keys...");
					sqlStatement.execute(disableKeysStatement);
					debug(1, myName+"Executing Load statement...");
					sqlStatement.execute(mainStmt);
					debug(1, myName+"(Re-)enabling keys...");
					sqlStatement.execute(enableKeysStatement);
					debug(1, myName+"Getting "+table+" count...");
					ResultSet results = sqlStatement.executeQuery(getCountStatement);
					if(results.next()) {
						int count = results.getInt(1);
						debug(1, myName+table+" table now reports: "+count+" total rows.");
					} else {
						debug(0, "Problem querying for "+table+" count.");
					}
				}
				success = true;
			}
		} catch (Exception e) {
			String tmp = myName+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} finally {
			closeConnection(jdbcConnection);
		}
		return success;
	}

	/**
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean updateObjectMediaCache() {
		final String myName = "WebAppDB.updateObjectMediaCache: ";
		final String clearAllStmt =
			"UPDATE objects SET objects.img_path=NULL, objects.img_ar=NULL";
		final String mainStmt =
			"UPDATE objects, media SET objects.img_path=media.path, objects.img_ar=media.aspectR"
			+" WHERE objects.id=media.obj_id AND media.type='image'";
		boolean success = false;
		Statement sqlStatement = null;
		Connection jdbcConnection = openConnection();
		try {
			if(jdbcConnection != null) {
				sqlStatement = jdbcConnection.createStatement();
				debug(1, myName+"Executing Clear All statement...");
				sqlStatement.execute(clearAllStmt);
				debug(1, myName+"Executing main statement...");
				sqlStatement.execute(mainStmt);
				success = true;
			}
		} catch (Exception e) {
			String tmp = myName+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} finally {
			closeConnection(jdbcConnection);
		}
		return success;
	}

	/**
	 * @return true if load succeeds, else false.
	 * @throws RuntimeException for any SQL or other errors.
	 */
	protected boolean updateOntologyCategoryCounts() {
		final String myName = "WebAppDB.updateCategoryCounts: ";
		final String clearCatCountsStmt =
			"UPDATE categories SET n_matches=0, n_matches_w_img=0";
		final String catCountsDropTableStmt =
			"DROP TABLE IF EXISTS cat_counts";
		final String catCountsSetupStmt =
			"CREATE TABLE cat_counts ("
			+ " `id` bigint(20) NOT NULL,"
			+ " `count` int(10) NOT NULL"
			+ ") ENGINE=MyIsam DEFAULT CHARSET=latin1";
		final String calcCountsStmt =
			"INSERT INTO cat_counts( id, count )"
			+" SELECT cat_id, count(*) FROM obj_cats GROUP BY cat_id ";
		final String updateFullCountsStmt =
			"UPDATE categories, cat_counts SET categories.n_matches=cat_counts.count"
			+" WHERE categories.id=cat_counts.id";
		final String catCountsTruncateTableStmt =
			"TRUNCATE cat_counts";
		final String calcCountsWImgsStmt =
			"INSERT INTO cat_counts( id, count )"
			+" SELECT oc.cat_id, count(*) FROM obj_cats oc, objects o"
			+" WHERE oc.obj_id=o.id AND NOT o.img_path IS NULL GROUP BY oc.cat_id";
		final String updateCountsWImgsStmt =
			"UPDATE categories, cat_counts SET categories.n_matches_w_img=cat_counts.count"
			+" WHERE categories.id=cat_counts.id";
		boolean success = false;
		Statement sqlStatement = null;
		Connection jdbcConnection = openConnection();
		try {
			if(jdbcConnection != null) {
				sqlStatement = jdbcConnection.createStatement();
				sqlStatement = jdbcConnection.createStatement();
				debug(1, myName+"Clearing current counts...");
				sqlStatement.execute(clearCatCountsStmt);
				debug(1, myName+"Dropping any old temp table...");
				sqlStatement.execute(catCountsDropTableStmt);
				debug(1, myName+"Creating temp table...");
				sqlStatement.execute(catCountsSetupStmt);
				debug(1, myName+"Calculating full counts...");
				sqlStatement.execute(calcCountsStmt);
				debug(1, myName+"Pushing full counts into categories table...");
				sqlStatement.execute(updateFullCountsStmt);
				debug(1, myName+"Truncating temp table...");
				sqlStatement.execute(catCountsTruncateTableStmt);
				debug(1, myName+"Calculating counts with images...");
				sqlStatement.execute(calcCountsWImgsStmt);
				debug(1, myName+"Pushing counts with images into categories table...");
				sqlStatement.execute(updateCountsWImgsStmt);
				debug(1, myName+"Dropping temp table...");
				sqlStatement.execute(catCountsDropTableStmt);
				success = true;
			}
		} catch (Exception e) {
			String tmp = myName+"\n"+ e.getMessage();
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} finally {
			closeConnection(jdbcConnection);
		}
		return success;
	}

	private Connection openConnection() {
		final String myName = "WebAppDB.getConnection: ";
		Connection jdbcConnection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			jdbcConnection = DriverManager.getConnection(connectionUrl);
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
		}
		return jdbcConnection;
	}

	private void closeConnection(Connection jdbcConnection) {
		if (jdbcConnection != null) try { jdbcConnection.close(); } catch(Exception e) {}
	}

	public static void main(String[] args) {
		final String myName = "WebAppDB.main: ";
		try {
			WebAppDB testDB = new WebAppDB("mysql", "localhost", "delphi", "delphi_admin", "please");
			boolean success = testDB.setAppLockout(true);
			StringUtils.outputDebugStr( myName+"setAppLockout(true):"+(success?"succeeded":"failed"));
			String filename = "E:/PAHMA/Metadata dumps/DBLoad/DBDump_090503b_100_SQL_all.txt";
			success = testDB.uploadObjectsMetadata(filename);
			StringUtils.outputDebugStr( myName+"uploadObjectsMetadata("
										+filename+"):"+((true)?"succeeded":"failed"));
			String facetsFile = "E:/PAHMA/Metadata dumps/DBLoad/mainVocab_facets.txt";
			String catsFile = "E:/PAHMA/Metadata dumps/DBLoad/mainVocab_cats.txt";
			String hooksFile = "E:/PAHMA/Metadata dumps/DBLoad/mainVocabHooks.txt";
			String exclusionsFile = "E:/PAHMA/Metadata dumps/DBLoad/mainVocabExclusions.txt";
			success = testDB.uploadOntologyMetadata(facetsFile, catsFile, hooksFile, exclusionsFile);
			StringUtils.outputDebugStr( myName+"uploadOntologyMetadata():"
												+((true)?"succeeded":"failed"));
			filename = "E:/PAHMA/Metadata dumps/DBLoad/obj_cats_all_1.sql";
			success = testDB.uploadObjectCategoryAssociations(filename);
			StringUtils.outputDebugStr( myName+"uploadObjectCategoryAssociations("
										+filename+"):"+((true)?"succeeded":"failed"));
			success = testDB.updateOntologyCategoryCounts();
			StringUtils.outputDebugStr( myName+"updateOntologyCategoryCounts():"
												+((true)?"succeeded":"failed"));
		} catch ( Exception e ) {
			StringUtils.outputDebugStr( myName+"Failed:"+e.getMessage() );
		}


	}
}
