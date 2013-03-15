<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";
?>
<HTML>
<BODY>
<?php

	if( empty($_GET['c']) && empty($_GET['k'])) {
		echo "<h3>You have to specify concepts ('c' param) and/or a keyword string ('k' param)</h3>";
	} else {
		$matchImages = ( empty($_GET['i']))? false : (($_GET['i'] != 0)?true:false);
		$page = ( empty($_GET['p']))? 1 : $_GET['p'];
		$pageSize = ( empty($_GET['s']))? 36 : $_GET['s'];

		empty($_GET['c']) ? $catIDs = array() : $catIDs = explode(",",$_GET['c']);
		empty($_GET['k']) ? $kwds = false : $kwds = $_GET['k'];

		if( count($catIDs) <= 1 )
			$qCatIDs =& $catIDs;
		else
			$qCatIDs =& orderCatsForQuery( $catIDs, $matchImages );

		$objsquery = prepareObjsQuery($qCatIDs, $kwds, $matchImages, $page-1, $pageSize);
		$catsquery = prepareResultsCatsQuery($qCatIDs, $kwds, $matchImages);

		echo "<h3>Objects Query:</h3><pre>".$objsquery."</pre>"
		 			."<h3>Objects Query:</h3><pre>".$catsquery."</pre>";
	}
	echo '<hr><form method="get">
				<p>Enter Concept(s): <input type="text" name="c" maxlength="300" /></p>
				<p>Enter Keyword(s): <input type="text" name="k" maxlength="300" /></p>
				<p>With Images(s): <input type="checkbox" name="i" value="1" checked="true" /></p>
				<p>Page Num:  <input type="text" name="p" maxlength="10" /></p>
				<p>Page Size: <input type="text" name="s" maxlength="10" /></p>
				<input type="submit" value="Show Query" />
				</form>';
?>
</BODY>
</HTML>
