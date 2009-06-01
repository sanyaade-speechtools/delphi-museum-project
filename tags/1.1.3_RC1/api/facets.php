<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

if(empty($_GET['f'])) 
	$fid = -1;
else {
	$facetname = $_GET['f'];
	$fid = findFacetID( $facetname );
}
?>
<HTML>
<BODY>
<?php
$facetinfo = getFacets();
if( !$facetinfo || count($facetinfo) == 0 ) {
	echo "<h2>Cannot get facets...</h2>";
} else if( isset($facetname) && $fid < 0 ) {
	echo "<h2>Bad facet name: ".$facetname."</h2>";
} else {
?>
	<h2>Facet info:</h2>
	<TABLE BORDER=2 CELLSPACING=0 CELLPADDING=0>
	<TR>
		<TD><h3>Facet</h3></TD>
		<TD><h3>ID</h3></TD>
	</TR>
<?php
	foreach( $facetinfo as $facet ) {
		if( $fid < 0 || $facet['id'] == $fid )
			echo "<TR><TD><p>".$facet['name']."</p></TD>
						<TD><p>".$facet['id']."</p></TD></TR>";
	}
}
?>
	  </TABLE>

</BODY>
</HTML>
