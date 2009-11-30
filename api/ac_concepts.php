<?php
require_once "apiSetup.php";

if (!isset($_GET["q"])) return;
$term = strtolower($_GET["q"]);
if(!$term) return;
$terms = explode(' ', $term);
foreach($terms as $subterm) {
	$word = trim($subterm);
	if(!empty($word) ) {
		$kwdterms[] = '+'.$word.'*';
	}
}

$limit = 0;
if(isset($_GET["limit"])){
	$limit = 0 + (int)$_GET["limit"];
}
if($limit<=1) {
	$limit = 40;
}
$qString = 
	"SELECT c.display_name, c.id, f.display_name"
	." FROM hooks h JOIN categories c on h.cat_id=c.id JOIN facets f on c.facet_id=f.id"
	." WHERE MATCH(h.token) AGAINST('".implode(' ',$kwdterms)."' IN BOOLEAN MODE)"
	." GROUP BY h.cat_id ORDER BY c.display_name LIMIT ".$limit;
$qString2 = 
	"SELECT c.display_name, c.id, f.display_name"
	." FROM categories c JOIN facets f on c.facet_id=f.id"
	." WHERE MATCH(c.display_name) AGAINST('".implode(' ',$kwdterms)."' IN BOOLEAN MODE)"
	." ORDER BY c.display_name LIMIT ".$limit;
$results =& $db->query($qString2);
if (PEAR::isError($results)) {
	return;
}
//echo $qString;
while ($row = $results->fetchRow(MDB2_FETCHMODE_ORDERED)) {
	echo "$row[0] ($row[2])|$row[1]\n";
}
$results->free();

?>
