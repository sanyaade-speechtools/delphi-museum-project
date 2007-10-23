<?
// Take row of image into and a wrapper class and produce the HTML for a thumb.
// Assumes row has: id, name, img_path, img_ar
function outputThumbnail( $row, $wrapperClass ) {
	global $CFG;
	$maxChars = 18;
	$ellipses = " ...";
	$eLen = strlen($ellipses);
	$imageOutput = "<div class=\"".$wrapperClass."\"><div class=\"thumb_img_wrapper\">";
	$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
	$pathToDetails = $CFG->wwwroot . "/modules/browser/details.php?id=" . $row['id'];

	if( $row['img_ar'] <= 0 ) {
		$imageOutput .= "<a href=\"".$pathToDetails."\"><img src=\"".$pathToImg."\" "
											." class=\"thumb_unk\" "
											." alt=\"".$row['name']."\" /></a>";
	} else {
		$imageOutput .= "<div class=\"shift\" style=\"position:relative;";
		if( $row['img_ar'] > 1 ) {	// Horizontal/landscape
			// y offset = (95-(height))/2 == (95-(95/img_ar))/2
			$imageOutput .= "left:0px;top:".((95-95/$row['img_ar'])/2)."px;\">";
			$imageOutput .= "<a href=\"".$pathToDetails."\"><img width=\"95px\"";
		} else { // Vertical/portrait
			// x offset = (95-(width))/2 == (95-(95*img_ar))/2
			$imageOutput .= "top:0px;left:".((95-95*$row['img_ar'])/2)."px;\">";
			$imageOutput .= "<a href=\"".$pathToDetails."\"><img height=\"95px\"";
		}
		$imageOutput .= " src=\"".$pathToImg."\" class=\"thumb\" "
										." alt=\"".$row['name']."\" /></a></div>";
	}

	$text = $row['name'];

	if (strlen($text) > $maxChars) {
		$text = substr($text,0,$maxChars-$eLen);
		$text = substr($text,0,strrpos($text,' '));
		$text .= $ellipses;
	}

	$textOutput = "<div class=\"front_exampleLabel\"><a href=\""
									.$pathToDetails."\"><abbr title=\"".$row['name']."\">"
									.$text."</abbr></a></div>";

	$imageOutput .= "</div>".$textOutput."</div>"; // close wrapper divs

	return $imageOutput;

}
?>
