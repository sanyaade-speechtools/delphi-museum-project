<?php 
/* Include Files *********************/
require_once("../../libs/env.php");
/*************************************/
// If the user isn't logged in, send to the login page.
if(($login_state != DELPHI_LOGGED_IN) && ($login_state != DELPHI_REG_PENDING)){
	header( 'Location: ' . $CFG->wwwroot .
					'/modules/auth/login.php?redir=' .$_SERVER['REQUEST_URI'] );
	die();
}

$t->assign('page_title', 'PAHMA/Delphi: Add Complex Inference');
$opmsg = "";

// This needs to verify perms. 
if( !currUserHasPerm( 'EditInferences' ) ) {
	$opmsg = "You do not have rights to Edit inferences. <br />
		Please contact your Delphi administrator for help.";
	$t->assign('addinf_error', $opmsg);

	$t->display('addInference.tpl');
	die();
}

$style_block = "<style>
.nochoice {color:#888; }
.intro {font-size:0.9em; font-weight:bold;  margin-bottom:0;}
.up {margin-bottom: 0.1em; }
.label {font-weight:bold; margin-bottom:0;}
#formCont {padding:0px 30px; }
#table1 {padding:10px; border: solid 1px black;}
#table1 td {padding:4px; }
#table1 .label {font-size:0.9em; }
#table1 input.numeric {text-align:right; }
</style>";

$t->assign("style_block", $style_block);

$themebase = $CFG->wwwroot.'/themes/'.$CFG->theme;

$script_block = '
<script type="text/javascript">
  var _themeroot = "'.$themebase.'";
</script>
<script type="text/javascript" src="'.$themebase.'/scripts/addInference.js"></script>';

$t->assign("script_block", $script_block);

if($opmsg!="")
	$t->assign('opmsg', $opmsg);

$t->display('addInference.tpl');
?>
