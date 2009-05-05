<?php
require_once "apiSetup.php";
?>
<HTML>
<BODY>
<?php
	$lowLimit = 10;
	$highLimit = 1000;
	if( !empty($_GET['term'])) { 
		$term = trim($_GET['term']);
		if( !empty($_GET['catlimit']))
			$catlimit = trim($_GET['catlimit']);
		else
			$catlimit = $lowLimit;
		$results =& $db->query(
				"SELECT f.display_name facet, c.display_name category,"
			 ." SUBSTRING_INDEX(GROUP_CONCAT(h.token SEPARATOR '|'), '|', 5) matches"
			 ." FROM hooks h JOIN categories c on h.cat_id=c.id JOIN facets f on c.facet_id=f.id"
			 ."	where h.token like '%".$term."%' GROUP BY h.cat_id LIMIT ".$catlimit);
		if (PEAR::isError($results)) {
				echo "Error in query: ".$results->getMessage();
		} else {
			$nrows = 0;
			while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
				if($nrows==0) {
					echo '<h3>Results of check against term: "'.$_GET['term'].'":</h3>'
							.'<p style="text-decoration: underline;"><em>Facet.Category :   matches</p><p>';
				}
				$fname = $row[0];
				$cname = $row[1];
				$count;
				$matches = str_replace("|", ", ", $row[2], $count);
				echo '<b>'.$fname.".".$cname.'</b> :  '.$matches;
				if($count>=4)
					echo ' ...';
				echo '<br />';
				$nrows++;
			}
			if($nrows==0) {
					echo '<h3>No matches found for term: "'.$_GET['term'].'"</h3>';
			} else if($nrows==$lowLimit) {
				$pageURL = 'http://'.$_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"].'&catlimit='.$highLimit;
				echo '...<em><a href="'.$pageURL.'">more</a>...<br /></p>';
			} else if($nrows==$highLimit) {
				echo '...<em>(results truncated at '.$highLimit.')</em>...<br /></p>';
			} else {
				echo '</p>';
			}
		echo '<hr>';
		}
	}
?>
	<h3>Enter the term you want to check.</h3>
	<form id="inputform" name="inputform" method="get">
		<p>Term: <input id="term" type="text" name="term" maxlength="300" /></p>
		<input type="submit" value="Check" />
	</form>

	<script type="text/javascript">
		document.inputform.term.focus();
	</script>

</BODY>
</HTML>
