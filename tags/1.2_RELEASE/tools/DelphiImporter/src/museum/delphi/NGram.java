package museum.delphi;

import java.util.ArrayList;

public class NGram {
	private String	str = null;
	private int		begin = 0;
	private int		end = 0;

	/**
	 * Construct an NGram from the string which represents the
	 * range of words indexed by begin and end
	 * @param string
	 * @param begin
	 * @param end
	 */
	public NGram(String string, int begin, int end) {
		this.str = string;
		this.begin = begin;
		this.end = end;
		if(string==null || string.isEmpty())
			throw new IllegalArgumentException("Null or empty string.");
		if(begin<0 || end<0 || end<begin)
			throw new IllegalArgumentException("Invalid begin/end indices.");
	}

	/**
	 * Factory method to create an NGram from a wordlist and begin and end indices.
	 * @param wordlist the words from which to build NGrams
	 * @param begin 0-based index of the first word
	 * @param end 0-based index of the last word
	 * @return new NGram
	 * @throws IllegalArgumentException if end<begin or range exceeds wordlist size
	 */
	public static NGram Create(ArrayList<String> wordlist, int begin, int end) {
		int nWords = wordlist.size();
		if(begin<0 || end<0 || end<begin || end>=nWords)
			throw new IllegalArgumentException("Invalid begin/end indices.");
		StringBuilder sb = new StringBuilder();
		for(int i=begin; i<=end; i++) {
			if(i>begin)
				sb.append(' ');
			sb.append(wordlist.get(i));
		}
		return new NGram(sb.toString(), begin, end);
	}

	/**
	 * Performs an endpoint-inclusive comparison of word ranges.
	 * @param ngram NGram to check against this one
	 * @return TRUE if the ranges overlap, else FALSE
	 */
	public boolean overlaps( NGram ngram ) {
		return (begin>=ngram.begin && begin<=ngram.end)
				|| (end>=ngram.begin && end<=ngram.end);
	}

	public String getString() {
		return str;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public int getLength() {
		return (end-begin)+1;
	}
}
