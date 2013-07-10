/**
 *
 */
package edu.berkeley.delphi.config;
import java.util.ArrayList;

import org.w3c.dom.Element;

import edu.berkeley.delphi.utils.Pair;

/**
 * @author Patrick
 *
 */
public class ConfigStringUtils {

	protected static int _debugLevel = 1;

	protected static void debug( int level, String str ){
		if( level <= _debugLevel )
			outputDebugStr( str );
	}

	public static void outputDebugStr( String str ){
		System.out.println( str );
	}

	public static void outputExceptionTrace( Exception e ){
        e.printStackTrace( System.out );
	}

	/**
	 * Tokenizes an input string according to column info rules.
	 * Also maps all tokens and words to lower case.
	 * @param source input String
	 * @param colInfo columnInfo for this string
	 * @return List of Pairs: first is token, second is words in that token
	 */
	public static ArrayList<Pair<String,ArrayList<String>>> prepareSourceTokens(
			String source, DumpColumnConfigInfo colInfo ) {  //  @jve:decl-index=0:
		ArrayList<Pair<String,ArrayList<String>>> returnList =
						new ArrayList<Pair<String,ArrayList<String>>>();
		// First we apply reduction. This cleans up certain oddities in the source
		String reducedSource = source.toLowerCase();
		for(Pair<String, String> reduce : colInfo.reduceRules) {
			reducedSource = reducedSource.replaceAll(reduce.getFirst(), reduce.getSecond());
		}
		// Next, we tokenize with the token separators
		String[] tokens_strings;
		if( colInfo.tokenSeparators.size() == 0 ) {
			// throw new RuntimeException( "No Token separators for column: " + colInfo.name);
			tokens_strings = new String[1];
			tokens_strings[0] = reducedSource;
		} else {
			String regex = "\\||"+colInfo.tokenSeparators.get(0);
			for( int i=1; i<colInfo.tokenSeparators.size(); i++)
				regex += "|"+colInfo.tokenSeparators.get(i);
			tokens_strings = reducedSource.split(regex);
		}
		// Next, we further split up each token on space and certain punctuation and remove the noise items
		// We also build the words list for Colors
		for( int i=0; i< tokens_strings.length; i++ ){
			String token = tokens_strings[i].trim();
			if(token.isEmpty())
				continue;
			// Split into words, but preserve hyphenated words, and embedded single quotes
			ArrayList<String> words = new ArrayList<String>();
			Pair<String,ArrayList<String>> pair = null;
				new Pair<String,ArrayList<String>>(token,words);
			String[] words_strings = token.split("[\\W&&[^\\-\']]");
			boolean noNoiseTokens = colInfo.noiseTokens.size() == 0;
			StringBuilder sb = new StringBuilder();
			for( int iW=0; iW<words_strings.length; iW++)
				if( noNoiseTokens || !colInfo.noiseTokens.contains(words_strings[iW]) ) {
					String word = words_strings[iW].trim();
					if(word.length()>0) {
						if( iW > 0)
							sb.append(' ');
						sb.append(word);
						words.add(word);
					}
				}
			if(sb.length()>0) {
				pair = new Pair<String,ArrayList<String>>(sb.toString(),words);
				returnList.add(pair);
			}
		}
		return returnList;
	}
}
