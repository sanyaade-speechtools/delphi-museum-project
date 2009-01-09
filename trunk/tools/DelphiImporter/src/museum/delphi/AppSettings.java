package museum.delphi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AppSettings {
	private String settingsFilename = null;
	private static final String keyValueSep = "=";
	private static final String lastUserProjectPathKey = "LUPP";
	private String lastUserProjectPath = null;
	private static final String lastUserProjectFolderKey = "LUPF";
	private String lastUserProjectFolder = null;
	// TODO: keep track of last folder for projects, even if clear the path
	// TODO: keep track of last few projects, for "recent" menu.

	private static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected static void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	public AppSettings( String filename ) {
		settingsFilename = filename;
		loadSettings();
	}

	// Settings is just for things like last workspace. Does not persist workspace info.
	private void loadSettings() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(settingsFilename));
		// BEGIN Reading settings
			while ( reader.ready() ) {
				String line = reader.readLine();
				String[] keyValue = line.split( keyValueSep, 2 );
				if( keyValue[0].equals(lastUserProjectPathKey) )
					lastUserProjectPath = keyValue[1];
				if( keyValue[0].equals(lastUserProjectFolderKey) )
					lastUserProjectFolder = keyValue[1];
			}
		// END   Reading settings
		} catch (FileNotFoundException e) {
			// First time in - no settings file yet. Just ignore this.
			debug( 2, "Could not open settings file: " + settingsFilename);
		}catch (IOException e) {
			String tmp = "AppSettings.loadSettings: Problem reading:  "+settingsFilename
				+"\n"+e.toString();
			debug(1, tmp);
            debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	private void saveSettings() {
		try {
			// We create a new writer each time, to overwrite the old settings.
			BufferedWriter writer = new BufferedWriter(new FileWriter( settingsFilename ));
		// BEGIN Writing settings
			writer.append(lastUserProjectPathKey+keyValueSep+lastUserProjectPath+'\n');
			writer.append(lastUserProjectFolderKey+keyValueSep+lastUserProjectFolder+'\n');
		// END   Writing settings
			writer.flush();
			writer.close();
		} catch( IOException e ) {
			String tmp = "AppSettings.saveSettings: Problem writing to:  "+settingsFilename
			+"\n"+e.toString();
			debug(1, tmp);
	        debugTrace(2, e);
			throw new RuntimeException( tmp );
		}
	}

	public String getLastUserProjectPath() {
		return lastUserProjectPath;
	}

	public void setLastUserProjectPath( String path ) {
		lastUserProjectPath = path;
		lastUserProjectFolder = StringUtils.getBaseDirForPath(path);
		saveSettings();
	}

	public String getLastUserProjectFolder() {
		return lastUserProjectFolder;
	}
}
