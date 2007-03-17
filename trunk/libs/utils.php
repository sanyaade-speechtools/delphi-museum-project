<?php
/**
 * Returns true if the email looks valid
 */
function emailValid($email){
	$atCharPos = strpos( $email, '@');
	$dotCharROff = strrpos( $email, '.');
	if( !$dotCharROff )
		return false;

	$dotCharROff = strlen($email) - $dotCharROff - 1;
 
	// Domain suffix must be at least 2 chars, and at most 6 (.museum)
	return ( $atCharPos	> 0 ) && ( $dotCharROff >= 2 ) && ( $dotCharROff <= 6 );
}

function getRoles(){
	global $mysqli;
  /* Get all the roles */
	$q = "select name from role";
	$result = $mysqli->query($q);
	if(!$result)
		return false;
	else {
		while ($row = $result->fetch_array()) {
			$roles[] = $row[0];
		}
	}
	return $roles;
}

?>
