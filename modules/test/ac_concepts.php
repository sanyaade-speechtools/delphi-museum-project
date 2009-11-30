<?php

require_once("../../libs/env.php");
$themebase = $CFG->wwwroot.'/themes/'.$CFG->theme;

$style_block =
'<link rel="stylesheet" href="'.$themebase.'/style/jquery.autocomplete.css" type="text/css">';

$t->assign("style_block", $style_block);
$script_block = '
<script type="text/javascript" src="../../libs/jquery/jquery.autocomplete.pack.js"></script>
<script type="text/javascript" src="../../libs/jquery/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="../../libs/jquery/jquery.ajaxQueue.js"></script>
<script type="text/javascript">
$().ready(function() {

	$("#concept").autocomplete("'.$CFG->wwwroot.'/api/ac_concepts.php", {
		minChars: 2,
		scroll: true,
		scrollHeight: 180,
		max: 40,
		matchSubset: false
	});
	
	$("#concept").result(function(event, data, formatted) {
		if (data)
			$("#concept_id").val(data[1]);
	});

});

</script>';
$t->assign("script_block", $script_block);

// Display template
$t->display('ac_concept.tpl');

?>
