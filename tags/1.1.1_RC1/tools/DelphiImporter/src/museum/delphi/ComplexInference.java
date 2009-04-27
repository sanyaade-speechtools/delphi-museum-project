/**
 *
 */
package museum.delphi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.HashMap;

//import museum.delphi.DoubleHashTree.FacetInfo;

/**
 * @author pschmitz
 * Models the complex inferences in Delphi.
 */
public class ComplexInference {
	public String name;						// Just for UI
	public String note;						// For UI, maintenance
	public int id;							// DB key
	public TaxoNode infer;					// The target of inference
	public float reliability;				// Configured confidence in rule
	public ArrayList<TaxoNode> required;	// the list of required concepts
	boolean fRequireAll;					// TRUE to require all, FALSE for any
	public ArrayList<TaxoNode> excluded;	// the list of excluded concepts
	boolean fExcludeAll;					// TRUE to require all to exclude, FALSE for any

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	public ComplexInference() {
		this( null, null, -1, null, (float)0.0, true, false );
	}

	public ComplexInference( String name, String note, int id, TaxoNode infer,
			float reliability, boolean fRequireAll, boolean fExcludeAll ) {
		this.name = name;
		this.note = note;
		this.id = id;
		this.infer = infer;
		this.reliability = reliability;
		this.required = null;
		this.fRequireAll = fRequireAll;
		this.excluded = null;
		this.fExcludeAll = fExcludeAll;
	}

	public void addRequiredConcept( TaxoNode req ) {
		if( required == null )
			required = new ArrayList<TaxoNode>();
		required.add(req);
	}

	public void addExcludedConcept( TaxoNode excl ) {
		if( excluded == null )
			excluded = new ArrayList<TaxoNode>();
		excluded.add(excl);
	}

	public String toString() {
		return "{"+name+"["+note+"]"+"}";
	}

	/**
	 * Loads a set of inferences from a MySQL database, assuming a standard schema
	 * documented on the project wiki.
	 * @param jdbcCon an open connection to the MySQL database that stores the inferences.
	 * @param facetMapHashTree the ontology as a set of TaxoNodes
	 * @param categorizer if not null, will be called to map each ComplexInference loaded.
	 * @return ArrayList of class instances.
	 */
	public static ArrayList<ComplexInference> loadInferencesFromMySQL(
			Connection jdbcCon,
			DoubleHashTree facetMapHashTree,
			Categorizer categorizer ) {
		String mainSqlQuery = "SELECT id, name, notes, infer, reliability, req_all, excl_all"
							+ " FROM inferred_concepts";
		String requiredsSqlQuery = "SELECT cat_id FROM inf_required WHERE inf_id=?";
		String excludedsSqlQuery = "SELECT cat_id FROM inf_excluded WHERE inf_id=?";
		ArrayList<ComplexInference> inferencesArrayList = new ArrayList<ComplexInference>();
		ResultSet mainResultSet = null;
		ResultSet requiredsResultSet = null;
		ResultSet excludedsResultSet = null;
		Statement mainQueryStatement = null;
		PreparedStatement requiredsQueryStatement = null;
		PreparedStatement excludedsQueryStatement = null;
		try {
			// Create and execute an SQL statement that returns some data.
			mainQueryStatement = jdbcCon.createStatement();
			requiredsQueryStatement = jdbcCon.prepareCall(requiredsSqlQuery);
			excludedsQueryStatement = jdbcCon.prepareCall(excludedsSqlQuery);
			mainResultSet = mainQueryStatement.executeQuery(mainSqlQuery);
			// Iterate through the data in the result set and display it.
			while (mainResultSet.next()) {
				// note column order in query string above
				int id = mainResultSet.getInt(0);
				String name = mainResultSet.getString(1);
				String notes = mainResultSet.getString(2);
				int inferID = mainResultSet.getInt(3);
				float reliability = mainResultSet.getFloat(4);
				boolean matchAllRequired = mainResultSet.getBoolean(5);
				boolean matchAllExcluded = mainResultSet.getBoolean(6);
				TaxoNode inferTaxoNode = facetMapHashTree.FindNodeByID(inferID);
				if( inferTaxoNode == null ) {
					debug( 0, "ComplexInference.loadInferencesFromMySQL "
							+" could not find concept for inferred ID: "+inferID
							+ " inference named: "+name);
				} else {
					ComplexInference ci =
						new ComplexInference( name, notes, id, inferTaxoNode,
								reliability, matchAllRequired, matchAllExcluded );
					requiredsQueryStatement.setInt(1, id);
					requiredsResultSet = requiredsQueryStatement.executeQuery();
					// Iterate through the data in the result set and display it.
					while (requiredsResultSet.next()) {
						int req_id = requiredsResultSet.getInt(0);
						TaxoNode requiredNode = facetMapHashTree.FindNodeByID(req_id);
						if( requiredNode == null ) {
							debug( 0, "ComplexInference.loadInferencesFromMySQL "
									+" could not find concept for required node: "+req_id
									+ " inference named: "+name);
						} else {
							ci.addRequiredConcept(requiredNode);
						}
					}
					try { requiredsResultSet.close(); } catch(Exception e) {}
					excludedsQueryStatement.setInt(1, id);
					excludedsResultSet = excludedsQueryStatement.executeQuery();
					// Iterate through the data in the result set and display it.
					while (excludedsResultSet.next()) {
						int excl_id = excludedsResultSet.getInt(0);
						TaxoNode excludedNode = facetMapHashTree.FindNodeByID(excl_id);
						if( excludedNode == null ) {
							debug( 0, "ComplexInference.loadInferencesFromMySQL "
									+" could not find concept for excluded node: "+excl_id
									+ " inference named: "+name);
						} else {
							ci.addRequiredConcept(excludedNode);
						}
					}
					try { excludedsResultSet.close(); } catch(Exception e) {}
					inferencesArrayList.add(ci);
					if( categorizer != null ) {
						categorizer.mapComplexInference(ci);
					}
				}
			}
		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mainQueryStatement != null)
				try { mainQueryStatement.close(); } catch(Exception e) {}
			if (requiredsQueryStatement != null)
				try { requiredsQueryStatement.close(); } catch(Exception e) {}
			if (excludedsQueryStatement != null)
				try { excludedsQueryStatement.close(); } catch(Exception e) {}
			if (mainResultSet != null)
				try { mainResultSet.close(); } catch(Exception e) {}
			if (requiredsResultSet != null)
				try { requiredsResultSet.close(); } catch(Exception e) {}
			if (excludedsResultSet != null)
				try { excludedsResultSet.close(); } catch(Exception e) {}
		}
		return inferencesArrayList;
	}

}
