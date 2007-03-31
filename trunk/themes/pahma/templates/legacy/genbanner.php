<?php
function addSubTitles(){
	global $subtitle;
	global $subtitle2;
	global $subtitle3;
	global $subtitle4;
	if(isset($subtitle)){
	 echo '<a href="main.php">Delphi</a>->'.$subtitle; 
		if(isset($subtitle2)){
		 echo "->".$subtitle2; 
			if(isset($subtitle3)){
			 echo "->".$subtitle3; 
				if(isset($subtitle4)){
				 echo "->".$subtitle4; 
				}
			}
		}
	} else {
		echo 'Delphi';
	}
}
?>
<body alink="#7A2F10" bgcolor="#E9E3C8" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
 <table width="749" cellpadding="0" cellspacing="0" border="0">
  <tr valign="top">
	 <td align="right">
		 <img src="http://hearstmuseum.berkeley.edu/images/logo.gif"
						width="465" height="59" border="0">
   </td>
  </tr>
 </table>
 <center>
 <div style="width:80%; text-align:left; padding-top:10px; padding-left:3px;">
	<h5><?php addSubTitles(); ?></h5>
 </div>
 <div style="width:80%; text-align:left; border:3 solid white; background-color:#DDCD9F; padding:20px;">


