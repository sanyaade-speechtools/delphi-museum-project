package museum.delphi;

import java.util.ArrayList;

public class NGramStack extends ArrayList<NGram> {

	private static final long serialVersionUID = 1L;

	public NGramStack() {
	}

	/**
	 * Constructs a stack of NGrams from the passed wordlist, up to
	 * length maxLen. Longest, earliest NGrams are added first;
	 * the last on the stack will be the last word in the wordlist.
	 * @param wordlist the list of words from which to build NGrams
	 * @param maxLen the longest NGram to build.
	 */
	public NGramStack(ArrayList<String> wordlist, int maxLen) {
		// Build out all the members.
		int nWords = wordlist.size();
		if(maxLen>=nWords)
			maxLen = nWords-1;
		for( int offset = maxLen-1; offset >= 0; offset-- ) {
			for( int start = 0; start+offset < nWords; start++ ) {
				add(NGram.Create(wordlist, start, start+offset));
			}
		}
	}

	/**
	 * @return First (longest, earliest) NGram on stack, or null if stack empty.
	 */
	public NGram pop() {
		return isEmpty()?null:remove(0);
	}

	/**
	 * Removes all NGrams that overlap with the passed NGram (as defined by
	 * NGram.overlap());
	 * @param match an NGram for which the range should be filtered.
	 * @see NGram.overlap()
	 */
	public void filterMatch(NGram match) {
		for(int i=size()-1; i>=0; i--) {
			if(match.overlaps(get(i)))
				remove(i);
		}
	}

}
