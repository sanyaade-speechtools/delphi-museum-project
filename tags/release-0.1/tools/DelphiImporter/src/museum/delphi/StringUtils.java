/**
 *
 */
package museum.delphi;
import java.util.ArrayList;

/**
 * @author Patrick
 *
 */
public class StringUtils {

	protected static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			System.out.println( str );
	}

	public static String trimQuotes( String str ) {
		int len = str.length();
		String ret;
		if( str.charAt(0) == '"' ) {
			if( str.charAt(len-1) == '"' )
				ret = str.substring(1, len-1);
			else
				ret = str.substring(1, len);
		} else if( str.charAt(len-1) == '"' )
			ret = str.substring(0, len-1);
		else
			return str;
		debug( 2, "trimQuotes mapping ["+str+"] to ["+ret+"]");
		return ret;
	}

	public static String buildLineFromTokens( ArrayList<String> tokens, int sepChar ){
		StringBuilder sb = new StringBuilder();
		boolean fFirst = true;
		for( String str:tokens ) {
			if( fFirst )
				fFirst = false;
			else
				sb.append(sepChar);
			sb.append(str);
		}
		return sb.toString();
	}

}
