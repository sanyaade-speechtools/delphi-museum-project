<?php
require_once "../libs/ontology/onto_utils.php";
?>
<HTML>
<BODY>
<?php

	if( empty($_GET['k'])) {
		echo "<h3>You have to specify a keyword string in the 'k' param</h3>";
	} else {
		echo "<p>Input Kwd string is<br>[".$_GET['k']."]</p>";
		echo "<h3>Checking tokenizer</h3>";
		$tokens = splitKwdString($_GET['k']);
		$nTokens = count($tokens);
		if( $nTokens <= 0 ) {
			echo "<p>Found no tokens in string???</p>";
		} else {
			echo "<p>Found ".$nTokens." tokens in string:</p>";
			for( $i=0; $i < $nTokens; $i++ ) {
				echo "<p>[<b>".$tokens[$i]."</b>]</p>";
			}	
		}	
		// Now check the n-Gram builder
		echo "<br /><br /><h3>Checking nGram builder</h3>";
		$ngrams = buildNGramsFromTokens($tokens);
		$nNgrams = count( $ngrams );
		if( $nNgrams <= 0 ) {
			echo "<p>Found no nGrams in tokens???</p>";
		} else {
			echo "<p>Found ".$nNgrams." nGrams:</p>";
			echo "<table><tr><td>Len</td><td>Start</td><td>nGram</td></tr>";
			for( $i = 0; $i < $nNgrams; $i++ ) {
				echo "<tr><td>".$ngrams[$i]['len']."</td><td>".$ngrams[$i]['start']
						."</td><td>".$ngrams[$i]['ngram']."</td></tr>";
			}	
			echo "</table>";
		}	
		/*
		*/

	}
	echo '<hr><p>Enter Keyword: <form method="get">
				<input type="text" name="k" maxlength="300" />
				<input type="submit" value="Test" />
				</form></p>';
?>
</BODY>
</HTML>
