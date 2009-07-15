<?php
require_once "apiSetup.php";
require_once "../libs/ontology/ontoServices.php";

?>
<HTML>
<BODY>
<?php
$nums = getNumObjs();
if( !$nums || count($nums) == 0 ) {
	echo "<h2>Cannot get num Info...</h2>";
} else {
?>
	<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="5px">
	<TR>
		<TD><p>Total Objects indexed: </p> </TD>
		<TD><p align="right"><?php echo $nums['allObjs']; ?></p> </TD>
	</TR>
	<TR>
		<TD><p>Objects with Images: </p> </TD>
		<TD><p align="right"><?php echo $nums['withImgs']; ?></p> </TD>
	</TR>
	</TABLE>

<?php
}
?>
</BODY>
</HTML>
