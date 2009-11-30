<?php

require_once("../../libs/env.php");
$themebase = $CFG->wwwroot.'/themes/'.$CFG->theme;

$style_block = '<link rel="stylesheet" href="'.$themebase.'/style/jquery.autocomplete.css" 
type="text/css">';

$t->assign("style_block", $style_block);
$script_block = '
<script type="text/javascript" src="../../libs/jquery/jquery.autocomplete.pack.js"></script>
<script type="text/javascript">
$().ready(function() {

	$("#singleBirdRemote").autocomplete("'.$CFG->wwwroot.'/api/ac_search.php", {
		width: 260,
		minChars: 2,
		max: 20,
		scrollHeight: 200
	});
	
	$("#singleBirdRemote").result(function(event, data, formatted) {
		if (data)
			$("#singleBirdResult").val(data[1]);
	});

});

</script>';
$t->assign("script_block", $script_block);

// Display template
$t->display('ac_test.tpl');

?>
