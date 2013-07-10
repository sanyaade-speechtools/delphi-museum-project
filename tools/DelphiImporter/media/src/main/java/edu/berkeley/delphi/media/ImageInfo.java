/**
 *
 */
package edu.berkeley.delphi.media;

import javax.swing.ImageIcon;

import edu.berkeley.delphi.utils.StringUtils;

/**
 * @author Patrick
 *
 */
public class ImageInfo {
	public static final double UNKNOWN_ORIENTATION = 0;

	/**
	 * Represents the relative path to the file.
	 */
	public String	filepath = null;

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
		filepath = fpath+'/'+fname;
	}

	public ImageInfo( String fpath, String fname, int w, int h ) {
		filepath = fpath+'/'+fname;
		width = w;
		height = h;
		checkAspect();
	}

	public ImageInfo( String fpath ) {
		filepath = fpath;
	}

	public ImageInfo( String fpath, int w, int h ) {
		filepath = fpath;
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
		if( aspectR == UNKNOWN_ORIENTATION ) {
			try {
				// We need to load one of the image variants (mid, thumb)
				// and then compute and set the orientation
			    ImageIcon image = new ImageIcon(basePath + filepath );
			    setDims(image.getIconWidth(), image.getIconHeight());
			} catch( RuntimeException e ) {
				debug( 1, "Error encountered computing aspect ratios for image:[" +
						filepath +"]\n  " + e.toString() );
				debugTrace( 2, e );
			}
		}
		return ( aspectR != UNKNOWN_ORIENTATION );
	}

	/**
	 * @return true if aspectR is known and (width >= height)
	 */
	public boolean isLandscape() {
		return( aspectR >= 1 );
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

	public void setDims(int width, int height) {
		this.width = (width < 0 ? 0:width);
		this.height = (height < 0 ? 0:height);
		checkAspect();
	}

	public double getAspectR() {
		return aspectR;
	}

	@Override
	public String toString() {
		return( "Path: " + filepath + " W: " + width + " H: " + height
			+ " aR: " + getAspectR() + (isLandscape() ? " Landscape":" Portrait") );
	}

	@Override
	public boolean equals( Object oThat ) {
		if ( this == oThat ) return true;
		if ( !(oThat instanceof ImageInfo) ) return false;
		ImageInfo that = (ImageInfo)oThat;
		return( this.filepath.equals(that.filepath) );
	}

	@Override
	public int hashCode() {
		if( myHashCode == 0 ) {
			myHashCode = 13;		// Start with a non-zero prime
			if( filepath != null )
				myHashCode += filepath.hashCode();
		}
		return myHashCode;
	}

}
