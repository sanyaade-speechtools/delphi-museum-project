<?
// Take row of image into and a wrapper class and produce the HTML for a thumb.
// Assumes row has: id, img_path, img_ar
// If also has name and/or owner, will put out additional text
// If size is > configured thumbmax, will use medium rather than thumb images.
// linkBase can be any URL (even javascript) that can take the id as suffix
function outputWrappedImage( $row, $wrapperClass, $linkBase, $size ) {
	global $CFG;

	if( isset( $CFG->thumbSize ))
		$thumbMax = $CFG->thumbSize;
	else
		$thumbMax = 120;

	$maxChars = 20;
	$ellipses = " ...";
	$eLen = strlen($ellipses);
	$imageOutput = "<div class=\"".$wrapperClass."\"><div class=\"thumb_img_wrapper".$size."\">";
	if( $size <= $thumbMax )
		$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
	else
		$pathToImg = $CFG->image_medium . "/" . $row['img_path'];
	$pathToDetails = $linkBase.$row['id'];

	if( $row['img_ar'] <= 0 ) {
		$imageOutput .= "<a href=\"".$pathToDetails."\"><img src=\"".$pathToImg."\" "
											." class=\"thumb_unk".$size."\" "
											." alt=\"".$row['name']."\" /></a>";
	} else {
		$imageOutput .= "<div class=\"shift\" style=\"position:relative;";
		if( $row['img_ar'] > 1 ) {	// Horizontal/landscape
			// y offset = ($size-(height))/2 == ($size-($size/img_ar))/2
			$imageOutput .= "left:0px;top:".(($size-$size/$row['img_ar'])/2)."px;\">";
			$imageOutput .= "<a href=\"".$pathToDetails."\"><img width=\"$size\"";
		} else { // Vertical/portrait
			// x offset = ($size-(width))/2 == ($size-($size*img_ar))/2
			$imageOutput .= "top:0px;left:".(($size-$size*$row['img_ar'])/2)."px;\">";
			$imageOutput .= "<a href=\"".$pathToDetails."\"><img height=\"$size\"";
		}
		$imageOutput .= " src=\"".$pathToImg."\" class=\"thumb\" "
										." alt=\"".$row['name']."\" /></a></div>";
	}
	$imageOutput .= "</div>"; // close shift wrapper divs

	if( isset($row['name']) ) {
		$text = $row['name'];

		if (strlen($text) > $maxChars) {
			$text = substr($text,0,$maxChars-$eLen);
			$text = substr($text,0,strrpos($text,' '));
			$text .= $ellipses;
		}

		$textOutput = "<div class=\"thumbLabel\"><a href=\""
										.$pathToDetails."\"><abbr title=\"".$row['name']."\">"
										.$text."</abbr></a></div>";
		$imageOutput .= $textOutput;
	}

	if( isset($row['owner']) ) {
		$text = $row['owner'];

		if (strlen($text) > $maxChars) {
			$text = substr($text,0,$maxChars-$eLen);
			$text = substr($text,0,strrpos($text,' '));
			$text .= $ellipses;
		}

		$textOutput = "<div class=\"ownerLabel\">Created by <span class=\"ownerName\">"
										.$text."</span></div>";
		$imageOutput .= $textOutput;
	}

	$imageOutput .= "</div>"; // close wrapper div

	return $imageOutput;

}
//
// Take row of image into and a wrapper class and produce the HTML for a thumb.
// Assumes row has: id, img_path, img_ar
// If $fCenter is true, will output the offset logic
// If size is > configured thumbmax, will use medium rather than thumb images.
// linkBase can be any URL (even javascript) that can take the id as suffix
function outputSimpleImage( $row, $size, $fCenter ) {
	global $CFG;

	if( isset( $CFG->thumbSize ))
		$thumbMax = $CFG->thumbSize;
	else
		$thumbMax = 120;

	if( $size <= $thumbMax )
		$pathToImg = $CFG->image_thumb . "/" . $row['img_path'];
	else
		$pathToImg = $CFG->image_medium . "/" . $row['img_path'];

	if( $row['img_ar'] <= 0 ) {
		$imageOutput .= "<img src=\"".$pathToImg."\" class=\"mid_unk".$size."\" />";
	} else {
		if( $row['img_ar'] > 1 ) {	// Horizontal/landscape
			if( isset($fCenter) && $fCenter ) {
				$imageOutput .= "<div class=\"shift\" style=\"position:relative;left:0px;";
				// y offset = ($size-(height))/2 == ($size-($size/img_ar))/2
				$imageOutput .= "top:".(($size-$size/$row['img_ar'])/2)."px;\">";
				$imageOutput .= "<img width=\"$size\" src=\"".$pathToImg."\" class=\"mid\" /></div>";
			} else {
				// no y offset for this case
				$imageOutput .= "<img width=\"$size\" src=\"".$pathToImg."\" class=\"mid\" />";
			}
		} else { // Vertical/portrait
			if( isset($fCenter) && $fCenter ) {
				$imageOutput .= "<div class=\"shift\" style=\"position:relative;top:0px;";
				// x offset = ($size-(width))/2 == ($size-($size*img_ar))/2
				$imageOutput .= "left:".(($size-$size*$row['img_ar'])/2)."px;\">";
				$imageOutput .= "<img height=\"$size\" src=\"".$pathToImg."\" class=\"mid\" /></div>";
			} else {
				$imageOutput .= "<img height=\"$size\" src=\"".$pathToImg."\" class=\"mid\" />";
			}
		}
	}

	return $imageOutput;

}
?>
