package museum.delphi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

import org.w3c.dom.Document;

public class UserProjectInfo implements Serializable {
	private static final long serialVersionUID = 42L; // The answer to everything...
	protected String name = null;
	private String vocabTermsPath = null;
	public transient VocabTermsReader vocabTermsReader = null;
	public transient DoubleHashTree vocabHashTree = null;
	public transient Document vocabOutputDoc = null;  //  @jve:decl-index=0:
	private String ontologyPath = null;
	public transient DoubleHashTree facetMapHashTree = null;  //  @jve:decl-index=0:
	public transient Categorizer categorizer = null;  //  @jve:decl-index=0:
	private String metadataConfigPath = null;
	private String metadataPath = null;
	public transient MetaDataReader metaDataReader = null;
	public transient DBMetaDataReader dbMetaDataReader = null;
	private String imagePathsPath = null;
	public transient ImagePathsReader imagePathsReader = null;
	private String objectsSQLLoadfilePath = null;
	private String facetsSQLLoadfilePath = null;
	private String categoriesSQLLoadfilePath = null;
	private String hooksSQLLoadfilePath = null;
	private String exclusionsSQLLoadfilePath = null;
	private String obj_catsSQLLoadfilePath = null;
	private String mediaSQLLoadfilePath = null;
	private String catCardsSQLLoadfilePath = null;
	private String webAppDB_host = null;
	private String webAppDB_dbName = null;
	private String webAppDB_user = null;
	private String webAppDB_password = null;
	public transient WebAppDB webAppDB = null;

	private transient boolean dirty = false;

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	public UserProjectInfo( String newName ) {
		this.name = newName;
	}

	public boolean isDirty() {
		return dirty;
	}

	public static UserProjectInfo loadFromPath( String path ) {
		UserProjectInfo newInfo = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(path);
			in = new ObjectInputStream(fis);
			newInfo = (UserProjectInfo)in.readObject();
			newInfo.dirty = false;
		} catch (FileNotFoundException e) {
			// First time in - no settings file yet. Just ignore this.
			String tmp = "UserProjectInfo.loadFromPath: Could not open project file: " + path ;
			debug(1, tmp);
			throw new RuntimeException( tmp );
		} catch (IOException e) {
			String tmp = "UserProjectInfo.loadFromPath: Problem reading:  "+path
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		} catch (ClassNotFoundException e) {
			String tmp = "UserProjectInfo.loadFromPath: ClassNotFound:  "+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
		return newInfo;
	}

	public static void saveToPath( UserProjectInfo upi, String path ) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try	{
			fos = new FileOutputStream(path);
			out = new ObjectOutputStream(fos);
			out.writeObject(upi);
			out.close();
			upi.dirty = false;
		} catch (IOException e) {
			String tmp = "UserProjectInfo.saveToPath: Problem writing to file:  "+path
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	public void copyFrom( UserProjectInfo upi ) {
		// copy all the paths
		this.vocabTermsPath				= upi.vocabTermsPath;
		this.ontologyPath				= upi.ontologyPath;
		this.metadataConfigPath			= upi.metadataConfigPath;
		this.metadataPath				= upi.metadataPath;
		this.imagePathsPath				= upi.imagePathsPath;
		this.objectsSQLLoadfilePath		= upi.objectsSQLLoadfilePath;
		this.facetsSQLLoadfilePath		= upi.facetsSQLLoadfilePath;
		this.categoriesSQLLoadfilePath	= upi.categoriesSQLLoadfilePath;
		this.hooksSQLLoadfilePath		= upi.hooksSQLLoadfilePath;
		this.exclusionsSQLLoadfilePath	= upi.exclusionsSQLLoadfilePath;
		this.obj_catsSQLLoadfilePath	= upi.obj_catsSQLLoadfilePath;
		this.mediaSQLLoadfilePath		= upi.mediaSQLLoadfilePath;
		this.catCardsSQLLoadfilePath	= upi.catCardsSQLLoadfilePath;
		this.webAppDB_host				= upi.webAppDB_host;
		this.webAppDB_dbName			= upi.webAppDB_dbName;
		this.webAppDB_user				= upi.webAppDB_user;
		this.webAppDB_password			= upi.webAppDB_password;
		// Clear all the transients - force a reload when set paths
		vocabTermsReader = null;
		vocabHashTree = null;
		vocabOutputDoc = null;
		facetMapHashTree = null;
		categorizer = null;
		metaDataReader = null;
		dbMetaDataReader = null;
		imagePathsReader = null;
		webAppDB = null;
		dirty = true;

	}

	public String getName() {
		return name;
	}

	public void setName( String newVal ) {
		if(((name == null) != (newVal == null))
			|| ((name != null) && !name.equals(newVal))) {
			dirty = true;
			name = newVal;
		}
	}

	public String getVocabTermsPath() {
		return vocabTermsPath;
	}

	public void setVocabTermsPath( String newVal ) {
		if(((vocabTermsPath == null) != (newVal == null))
			|| ((vocabTermsPath != null) && !vocabTermsPath.equals(newVal))) {
			dirty = true;
			vocabTermsPath = newVal;
		}
	}

	public String getOntologyPath() {
		return ontologyPath;
	}

	public void setOntologyPath( String newVal ) {
		if(((ontologyPath == null) != (newVal == null))
			|| ((ontologyPath != null) && !ontologyPath.equals(newVal))) {
			dirty = true;
			ontologyPath = newVal;
		}
	}

	public String getMetadataConfigPath() {
		return metadataConfigPath;
	}

	public void setMetadataConfigPath( String newVal ) {
		if(((metadataConfigPath == null) != (newVal == null))
			|| ((metadataConfigPath != null) && !metadataConfigPath.equals(newVal))) {
			dirty = true;
			metadataConfigPath = newVal;
		}
	}

	public String getMetadataPath() {
		return metadataPath;
	}

	public void setMetadataPath( String newVal ) {
		if(((metadataPath == null) != (newVal == null))
			|| ((metadataPath != null) && !metadataPath.equals(newVal))) {
			dirty = true;
			metadataPath = newVal;
		}
	}

	public String getImagePathsPath() {
		return imagePathsPath;
	}

	public void setImagePathsPath( String newVal ) {
		if(((imagePathsPath == null) != (newVal == null))
			|| ((imagePathsPath != null) && !imagePathsPath.equals(newVal))) {
			dirty = true;
			imagePathsPath = newVal;
		}
	}

	public String getObjectsSQLLoadfilePath() {
		return objectsSQLLoadfilePath;
	}

	public void setObjectsSQLLoadfilePath( String newVal ) {
		if(((objectsSQLLoadfilePath == null) != (newVal == null))
			|| ((objectsSQLLoadfilePath != null) && !objectsSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			objectsSQLLoadfilePath = newVal;
		}
	}

	public String getFacetsSQLLoadfilePath() {
		return facetsSQLLoadfilePath;
	}

	public void setFacetsSQLLoadfilePath( String newVal ) {
		if(((facetsSQLLoadfilePath == null) != (newVal == null))
			|| ((facetsSQLLoadfilePath != null) && !facetsSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			facetsSQLLoadfilePath = newVal;
		}
	}

	public String getCategoriesSQLLoadfilePath() {
		return categoriesSQLLoadfilePath;
	}

	public void setCategoriesSQLLoadfilePath( String newVal ) {
		if(((categoriesSQLLoadfilePath == null) != (newVal == null))
			|| ((categoriesSQLLoadfilePath != null) && !categoriesSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			categoriesSQLLoadfilePath = newVal;
		}
	}

	public String getHooksSQLLoadfilePath() {
		return hooksSQLLoadfilePath;
	}

	public void setHooksSQLLoadfilePath( String newVal ) {
		if(((hooksSQLLoadfilePath == null) != (newVal == null))
			|| ((hooksSQLLoadfilePath != null) && !hooksSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			hooksSQLLoadfilePath = newVal;
		}
	}

	public String getExclusionsSQLLoadfilePath() {
		return exclusionsSQLLoadfilePath;
	}

	public void setExclusionsSQLLoadfilePath( String newVal ) {
		if(((exclusionsSQLLoadfilePath == null) != (newVal == null))
			|| ((exclusionsSQLLoadfilePath != null) && !exclusionsSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			exclusionsSQLLoadfilePath = newVal;
		}
	}

	public String getObj_catsSQLLoadfilePath() {
		return obj_catsSQLLoadfilePath;
	}

	public void setObj_catsSQLLoadfilePath( String newVal ) {
		if(((obj_catsSQLLoadfilePath == null) != (newVal == null))
			|| ((obj_catsSQLLoadfilePath != null) && !obj_catsSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			obj_catsSQLLoadfilePath = newVal;
		}
	}

	public String getMediaSQLLoadfilePath() {
		return mediaSQLLoadfilePath;
	}

	public void setMediaSQLLoadfilePath( String newVal ) {
		if(((mediaSQLLoadfilePath == null) != (newVal == null))
			|| ((mediaSQLLoadfilePath != null) && !mediaSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			mediaSQLLoadfilePath = newVal;
		}
	}

	public String getCatCardsSQLLoadfilePath() {
		return catCardsSQLLoadfilePath;
	}

	public void setCatCardsSQLLoadfilePath( String newVal ) {
		if(((catCardsSQLLoadfilePath == null) != (newVal == null))
			|| ((catCardsSQLLoadfilePath != null) && !catCardsSQLLoadfilePath.equals(newVal))) {
			dirty = true;
			catCardsSQLLoadfilePath = newVal;
		}
	}

	public String getWebAppDB_host() {
		return webAppDB_host;
	}

	public void setWebAppDB_host( String newVal ) {
		if(((webAppDB_host == null) != (newVal == null))
			|| ((webAppDB_host != null) && !webAppDB_host.equals(newVal))) {
			dirty = true;
			webAppDB_host = newVal;
		}
	}

	public String getWebAppDB_dbName() {
		return webAppDB_dbName;
	}

	public void setWebAppDB_dbName( String newVal ) {
		if(((webAppDB_dbName == null) != (newVal == null))
			|| ((webAppDB_dbName != null) && !webAppDB_dbName.equals(newVal))) {
			dirty = true;
			webAppDB_dbName = newVal;
		}
	}

	public String getWebAppDB_user() {
		return webAppDB_user;
	}

	public void setWebAppDB_user( String newVal ) {
		if(((webAppDB_user == null) != (newVal == null))
			|| ((webAppDB_user != null) && !webAppDB_user.equals(newVal))) {
			dirty = true;
			webAppDB_user = newVal;
		}
	}

	public String getWebAppDB_password() {
		return webAppDB_password;
	}

	public void setWebAppDB_password( String newVal ) {
		if(((webAppDB_password == null) != (newVal == null))
			|| ((webAppDB_password != null) && !webAppDB_password.equals(newVal))) {
			dirty = true;
			webAppDB_password = newVal;
		}
	}

	public void resetWebAppDB() {
		if(   (webAppDB_host == null)	  || webAppDB_host.isEmpty()
			||(webAppDB_dbName == null)	  || webAppDB_dbName.isEmpty()
			||(webAppDB_user == null)	  || webAppDB_user.isEmpty()
			||(webAppDB_password == null) || webAppDB_password.isEmpty() ) {
			webAppDB = null;
		} else {
			webAppDB = new WebAppDB("mysql", webAppDB_host, webAppDB_dbName,
									webAppDB_user, webAppDB_password);
		}
	}

}
