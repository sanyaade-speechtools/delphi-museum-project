<?php
require_once "apiSetup.php";
?>
<HTML>
<style>
tr.hdr { background-color:#DDD; font-family:Verdana, Arial; font-size:0.9em; }
td.facet { text-align:right; }
td.cat { text-align:left; font-weight:bold; }
td.matches { text-align:left; font-style:italic; }
td.hspace { height:5px; }
span.term { background-color:#ffff66; }
span.fullterm { background-color:#ff6666; }
</style>
<BODY>
<?php
	$lowLimit = 10;
	$highLimit = 1000;
	if( !empty($_GET['catlimit']))
		$catlimit = trim($_GET['catlimit']);
	else
		$catlimit = $lowLimit;
	if( !empty($_GET['termlimit']))
		$termlimit = trim($_GET['termlimit']);
	else
		$termlimit = $lowLimit;
	if( !empty($_GET['limithooks']))
		$limithooks = trim($_GET['limithooks']) > 0;
	else
		$limithooks = false;
	if( !empty($_GET['usepartial']))
		$usepartial = trim($_GET['usepartial']) > 0;
	else
		$usepartial = false;

	if( !empty($_GET['term'])) { 
		$term = trim($_GET['term']);
		$terms = explode(' ', $term);
		foreach($terms as $subterm) {
			$word = trim($subterm);
			if(!empty($word) ) {
				$kwdterms[] = '+'.$word.(($usepartial)?'*':'');
				$modterms[] = '<span class="term">'.$word.'</span>';
			}
		}
		$qString1 = 
				"SELECT f.display_name, c.display_name, h.token "
			 ." FROM hooks h JOIN categories c on h.cat_id=c.id JOIN facets f on c.facet_id=f.id"
			 ."	where h.token='".$term."' LIMIT ".$catlimit;
		$fullwordmatches = true;
		$qString2 = 
			"SELECT SQL_CALC_FOUND_ROWS f.display_name, c.display_name,"
			 .((!$limithooks)?"GROUP_CONCAT(h.token SEPARATOR '|')":
			    " SUBSTRING_INDEX(GROUP_CONCAT(h.token SEPARATOR '|'), '|', 5)")
			 ." FROM hooks h JOIN categories c on h.cat_id=c.id JOIN facets f on c.facet_id=f.id"
			 //."	where (h.token like '%".$term."%') AND (h.token!='".$term."') GROUP BY h.cat_id LIMIT ".$catlimit;
			 ." where h.token!='".$term
			 ."' AND MATCH(h.token) AGAINST('".implode(' ',$kwdterms)."' IN BOOLEAN MODE)"
			 ." GROUP BY h.cat_id LIMIT ".$catlimit;
		// Get the category matches and show in table
		$results =& $db->query($qString1);
		if (PEAR::isError($results)) {
				echo "Error in query1: ".$results->getMessage();
				echo "Query: ".$qString1;
		} else {
			$nrows1 = 0;
			while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
				if($nrows1==0) {
					echo '<h3>Ontology matches for term: "'.$term.'":</h3>'
						  .'<table id="ontomatches" cellspacing="0" cellpadding="0">'
							.'<tr class="hdr"><td class="facet">Facet:</td><td>Category</td><td>&nbsp;&nbsp;&nbsp;matches</td></tr><tr><td class="hspace" colspan="3"></td></tr>';
				}
				$fname = $row[0];
				$cname = $row[1];
				$match = str_replace($term, '<span class="fullterm">&nbsp;'.$term.'&nbsp;</span>', $row[2]);
				echo '<tr><td class="facet">'.$fname.':</td><td class="cat">'.$cname
							.'&nbsp;&nbsp;</td><td class="matches">'.$match.'</td></tr>';
				$nrows1++;
			}
			$results->free();
			// Now get the partial match results
			$results =& $db->query($qString2);
			if (PEAR::isError($results)) {
				echo "Error in query2: ".$results->getMessage()
					  ." Query: ".$qString2;
			} else {
				$nrows2 = 0;
				while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
					if($nrows1==0&&$nrows2==0) {
						echo '<h3>Ontology matches for term: "'.$term.'":</h3>'
								.'<table cellspacing="0" cellpadding="0">'
								.'<tr class="hdr"><td class="facet">Facet:</td><td>Category</td><td>&nbsp;&nbsp;&nbsp;matches</td></tr><tr><td class="hspace" colspan="3"></td></tr>';
					}
					$fname = $row[0];
					$cname = $row[1];
					$match = str_replace($term, '<span class="term">'.$term.'</span>', $row[2]);
					echo '<tr><td class="facet">'.$fname.':</td><td class="cat">'.$cname
								.'&nbsp;&nbsp;</td><td class="matches">'.$match.'</td></tr>';
					$nrows2++;
				}
				$results->free();
			}
			$results =& $db->query("SELECT FOUND_ROWS()");
			if(!PEAR::isError($results)&&($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED))) {
				$nrowsTotal = $row[0];
			} else {
				$nrowsTotal = -1;
			}

			if($nrows1==0&&$nrows2==0) {
				echo '<h3>No ontology matches found for term: "'.$term.'"</h3>';
			} else {
				if($nrows2==$lowLimit && $nrowsTotal>$lowLimit) {
					$pageURL = 'http://'.$_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"].'&catlimit='.$highLimit;
					echo '<tr><td></td><td colspan="2">...<a href="'.$pageURL.'">more</a>...</td></tr>';
				} else if($nrows2>=$highLimit && $nrowsTotal>$highLimit) {
					echo '<tr><td></td><td colspan="2">...(results truncated at '.$highLimit.')...</td></tr>';
				}
				echo '</table>';
			}
		echo '<hr>';
		//echo "<p>Query1: ".$qString1.'</p>';
		//echo "<p>Query2: ".$qString2.'</p>';
		}
		$qString3 =
				"SELECT term, count FROM termStats where term='".$term."'";
		$qString4 =
				"SELECT SQL_CALC_FOUND_ROWS term, count FROM termStats where term!='".$term."'"
			  ." AND MATCH(term) AGAINST('".implode(' ',$kwdterms)."' IN BOOLEAN MODE) "
				." LIMIT ".$termlimit;
		// Get the term matches and show in table
		$results =& $db->query($qString3);
		if (PEAR::isError($results)) {
				echo "Error in query: ".$results->getMessage();
				echo "Query: ".$qString3;
		} else {
			$nrows1 = 0;
			// Handle full term matches
			while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
				if($nrows1==0) {
					echo '<h3>Corpus matches for term: "'.$term.'":</h3>'
						  .'<table id="corpusmatches" cellspacing="0" cellpadding="0">'
							.'<tr class="hdr"><td class="freq">Frequency</td><td class="matches">&nbsp;&nbsp;&nbsp;Corpus matches</td></tr><tr><td class="hspace" colspan="2"></td></tr>';
				}
				$termR = $row[0];
				$countR = $row[1];
				$match = str_replace($term, '<span class="fullterm">'.$term.'</span>', $termR);
				echo '<tr><td class="freq">'.$countR.':</td><td class="matches">'
						.$match.'</td></tr>';
				$nrows1++;
			}
			$results->free();
			// Now get the partial match results
			$results =& $db->query($qString4);
			if (PEAR::isError($results)) {
					echo "Error in query: ".$results->getMessage();
					echo "Query: ".$qString4;
			} else {
				$nrows2 = 0;
				while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
					if($nrows1==0&&$nrows2==0) {
						echo '<h3>Corpus matches for term: "'.$term.'":</h3>'
						  .'<table id="corpusmatches" cellspacing="0" cellpadding="0">'
							.'<tr class="hdr"><td class="freq">Frequency</td><td class="matches">&nbsp;&nbsp;&nbsp;Corpus matches</td></tr><tr><td class="hspace" colspan="2"></td></tr>';
					}
					$termR = $row[0];
					$countR = $row[1];
					$match = str_replace($terms, $modterms, $termR);
					echo '<tr><td class="freq">'.$countR.':</td><td class="matches">'
							.$match.'</td></tr>';
					$nrows2++;
				}
				$results->free();
			}
			$results =& $db->query("SELECT FOUND_ROWS()");
			if(!PEAR::isError($results)&&($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED))) {
				$nrowsTotal = $row[0];
			} else {
				$nrowsTotal = -1;
			}

			if($nrows1==0&&$nrows2==0) {
				echo '<h3>No corpus matches found for term: "'.$term.'"</h3>';
			} else {
				if($nrows2==$lowLimit && $nrowsTotal>$lowLimit) {
					$pageURL = 'http://'.$_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"].'&termlimit='.$highLimit;
					echo '<tr><td></td><td colspan="2">...<a href="'.$pageURL.'">more</a>...</td></tr>';
				} else if($nrows2>=$highLimit && $nrowsTotal>$highLimit) {
					echo '<tr><td></td><td colspan="2">...(results truncated at '.$highLimit.')...</td></tr>';
				}
				echo '</table>';
			}
		echo '<hr>';
		//echo "<p>Query3: ".$qString3.'</p>';
		//echo "<p>Query4: ".$qString4.'</p>';
		}
	}
?>
	<h3>Enter the term you want to check.</h3>
	<form id="inputform" name="inputform" method="get">
		<p>Term: <input id="term" type="text" name="term" maxlength="100"
				 <?php if(!empty($term)) echo 'value="'.$term.'"'; ?> /></p>
		<p><input type="checkbox" name="limithooks" value="0" 
			<?php if(!$limithooks) echo "CHECKED"; ?> />
           &nbsp;Show all hook matches (uncheck to limit matches to 5 per concept)</p>
		<p><input type="checkbox" name="usepartial" value="1" 
			<?php if($usepartial) echo "CHECKED"; ?> />
           &nbsp;Allow partial matches of the term</p>
		<input type="submit" value="Check" />
	</form>

	<script type="text/javascript">
		document.inputform.term.focus();
	</script>

</BODY>
</HTML>
