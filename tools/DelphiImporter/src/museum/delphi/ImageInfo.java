/**
 *
 */
package museum.delphi;

import javax.swing.ImageIcon;

/**
 * @author Patrick
 *
 */
public class ImageInfo {
	public static final double UNKNOWN_ORIENTATION = 0;

	/**
	 * Represents the top of the path to the file. We
	 * may append another folder underneath (e.g., "thumbs") for specific
	 * derivatives.
	 */
	public String	path = null;
	/**
	 * Simple filename. All images/derivatives are .jpg's
	 */
	public String	filename = null;

	private int		width = 0;
	private int		height = 0;

	private int		myHashCode = 0;

	private int _debugLevel = 1;

	protected void debug( int level, String str ){
		if( level <= _debugLevel )
			StringUtils.outputDebugStr(str);
	}

	protected void debugTrace( int level, Exception e ){
		if( level <= _debugLevel )
			StringUtils.outputExceptionTrace(e);
	}

	/**
	 * Represents width/height, or 0 if unknown.
	 * An aspectR > 1 implies landscape orientation.
	 * An aspectR < 1 implies portrait orientation.
	 * aspectRs that are >> 1.5 or << 0.75 are very thin.
	 * At some point, we may add hints for thin images to display one
	 * end or the other, the mid point, etc., since the scaled image
	 * may well be very hard to interpret visually.
	 */
	private double	aspectR = UNKNOWN_ORIENTATION;	// Represents W/H

	public ImageInfo( String fpath, String fname ) {
		path = fpath;
		filename = fname;
	}

	public ImageInfo( String fpath, String fname, int w, int h ) {
		path = fpath;
		filename = fname;
		width = w;
		height = h;
		checkAspect();
	}

	private void checkAspect() {
		if( width > 0 && height > 0 )
			aspectR = (double)width/(double)height;
		else
			aspectR = UNKNOWN_ORIENTATION;
	}

	/**
	 * Force the computation of the aspect ratio, if it is not already
	 * set. Loads the image to set dimensions, and computes aspect.
	 * @param basePath The path to the media repository root.
	 * @return true if aspectR is known and (width >= height)
	 */
	public boolean computeAspectR( String basePath) {
		if( aspectR != UNKNOWN_ORIENTATION ) {
			return false;
		}
		try {
			// We need to load one of the image variants (mid, thumb)
			// and then compute and set the orientation
		    ImageIcon image = new ImageIcon(basePath + path + "/" + filename);
		    setWidth(image.getIconWidth());
		    setHeight(image.getIconHeight());
		} catch( RuntimeException e ) {
			debug( 1, "Error encountered computing aspect ratios for image:[" +
					path + "/" + filename+"]\n  " + e.toString() );
			debugTrace( 2, e );
			throw e;
		}
		return true;
	}

	/**
	 * @return true if aspectR is known and (width >= height)
	 */
	public boolean isLandscape() {
		return( aspectR >= 1 );
	}

	/**
	 * Takes the first configured path for the id, if any, and adds a subpath.
	 * @param subPath Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public String GetSimpleSubPath( String subPath ) {
		if( subPath != null )
			return path + "/" + subPath + "/" + filename;
		return path + "/" + filename;
	}

	/**
	 * Takes the first configured path for the id, if any, and adds the subpaths.
	 * @param subPath1 Pass in a simple relative path, with no leading or trailing slashes
	 * @param subPath2 Pass in a simple relative path, with no leading or trailing slashes
	 * @return
	 */
	public Pair<String,String> GetSimpleSubPaths( String subPath1, String subPath2 ) {
		String retString1 = path + "/" + subPath1 + "/" + filename;
		String retString2 = path + "/" + subPath2 + "/" + filename;
		return new Pair<String,String>(retString1, retString2);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = (height < 0 ? 0:height);
		checkAspect();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = (width < 0 ? 0:width);
		checkAspect();
	}

	public double getAspectR() {
		return aspectR;
	}

	public String toString() {
		return( "Path: " + path + '/' + filename + " W: " + width + " H: " + height
			+ " aR: " + getAspectR() + (isLandscape() ? " Landscape":" Portrait") );
	}

	public boolean equals( Object oThat ) {
		if ( this == oThat ) return true;
		if ( !(oThat instanceof ImageInfo) ) return false;
		ImageInfo that = (ImageInfo)oThat;
		return( this.path.equals(that.path) && this.filename.equals(that.filename) );
	}

	public int hashCode() {
		if( myHashCode == 0 ) {
			myHashCode = 13;		// Start with a non-zero prime
			if( path != null )
				myHashCode += path.hashCode();
			if( filename != null )
				myHashCode = myHashCode*37 + filename.hashCode();
		}
		return myHashCode;
	}

}
