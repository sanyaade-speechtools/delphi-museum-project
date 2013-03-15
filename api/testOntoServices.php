<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";
?>
<HTML>
<BODY>
<?php

	if( empty($_GET['k'])) {
		echo "<h3>You have to specify a keyword string in the 'k' param</h3>";
	} else {
		echo "<p>Input Kwd string is<br>[".$_GET['k']."]</p>";
		echo "<h3>Checking ID matcher</h3>";
		if( empty($_GET['i']))
			$matchImages = true;
		else
			$matchImages = ($_GET['i'] != 0)?true:false;
		$values = getCategoryIDsForKwds($_GET['k'], $matchImages);
		if( isset($values['msg'] ))
			echo "<h4>Message returned from getCategoryIDsForKwds():<br />"
						.$values['msg']."</h4>";
		if( isset($values['query'] ))
			echo "<p>getCategoryIDsForKwds() Query:<br />"
						.$values['query']."</p>";
		$nKwds = count($values['kwds']);
		if( $nKwds <= 0 ) {
			echo "<p>No kwds after processing</p>";
		} else {
			echo "<p>Have ".$nKwds." kwds after processing:</p>";
			for( $i=0; $i < $nKwds; $i++ ) {
				echo "<p>[<b>".$values['kwds'][$i]."</b>]</p>";
			}	
		}	
		$nCats = count($values['cats']);
		if( $nCats <= 0 ) {
			echo "<p>No cats after processing</p>";
		} else {
			echo "<p>Have ".$nCats." cats after processing:</p>";
			for( $i=0; $i < $nCats; $i++ ) {
				echo "<p>[<b>".$values['cats'][$i]."</b>]</p>";
			}	
		}	
	}
	echo '<hr><form method="get">
				<p>Enter Keyword(s): <input type="text" name="k" maxlength="300" /></p>
				<p>With Images(s): <input type="checkbox" name="i" value="1" checked="true" /></p>
				<input type="submit" value="Test" />
				</form>';
?>
</BODY>
</HTML>
