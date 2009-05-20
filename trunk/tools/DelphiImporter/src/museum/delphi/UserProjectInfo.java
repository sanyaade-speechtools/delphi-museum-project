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
	public String objectsSQLLoadfilePath = null;
	public String facetsSQLLoadfilePath = null;
	public String categoriesSQLLoadfilePath = null;
	public String hooksSQLLoadfilePath = null;
	public String exclusionsSQLLoadfilePath = null;
	public String obj_catsSQLLoadfilePath = null;
	public String mediaSQLLoadfilePath = null;
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
		this.vocabTermsPath = upi.vocabTermsPath;
		this.ontologyPath = upi.ontologyPath;
		this.metadataConfigPath = upi.metadataConfigPath;
		this.metadataPath = upi.metadataPath;
		this.imagePathsPath = upi.imagePathsPath;
		// Clear all the transients - force a reload when set paths
		vocabTermsReader = null;
		vocabHashTree = null;
		vocabOutputDoc = null;
		facetMapHashTree = null;
		categorizer = null;
		metaDataReader = null;
		dbMetaDataReader = null;
		imagePathsReader = null;
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

}
