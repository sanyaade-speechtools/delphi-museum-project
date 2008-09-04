<?php

// Split a string on spaces, but respect quote grouping
function splitKwdString($kwds) {
	// If just split on quotes, then take all the unquoted
	// tokens and split them on spaces.
	$trimmed = trim($kwds);
	// If first char is a quote, then the odd-numbered tokens are unquoted,
	// else the even-numbered tokens are unquoted (note index=0 is even).
	//$modResultForUnquoted = ( $trimmed[0] == "'" || $trimmed[0] == "\"" )?1:0;
	//$modResultForUnquoted = ( $trimmed[0] == "\"" )?1:0;
	$quoteChunks = explode( "\"", $trimmed );
	$nChunks = count($quoteChunks);
	$tokens = array();
	for( $i = 0; $i < $nChunks; $i++ ) {
		$chunk = trim($quoteChunks[$i]);
		if( empty($chunk) )
			continue;
		if(( $i & 1 ) == 0 ) // $modResultForUnquoted )
			// Split all the unquoted word sequences on spaces, and merge into kwd array
			$tokens = array_merge( $tokens, explode( " ", $chunk ));
		else
			// Just add the quoted sequences as whole keywords
			$tokens[] = $chunk;
	}
	return $tokens;
}

function buildNGramsFromTokens($tokens) {
	
	$ngrams = array();
	$nTokens = count($tokens);
	// First, load the entire array as the longest n-gram
	$ngrams[] = array( 'len'   => $nTokens,
									 'start' => 0,
									 'ngram' => implode( " ", $tokens ));
	for( $len = $nTokens-1; $len > 0; $len-- ) {
		for( $start = 0; $start+$len <= $nTokens; $start++ ) {
			$ngrams[] = array( 'len'   => $len,
											 'start' => $start,
											 'ngram' => implode( " ", array_slice( $tokens, $start, $len )) );
		}	
	}	
	return $ngrams;
}

function requoteMultiTermKwdTokens($tokens) {
	$kwdArray = array();
	$nTokens = count($tokens);
	for( $i=0; $i<$nTokens; $i++ ) {
		if( strpos($tokens[$i], ' ' ) === FALSE )
			$kwdArray[] = $tokens[$i];
		else
			$kwdArray[] = '"'.$tokens[$i].'"';
	}
	return $kwdArray;
}

?>
