<?php
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


/*
	Returns a div containing a centered image. If a linkURL is provided, the image
	will be linked to the given URL. If size is > configured thumbmax, will use 
	medium rather than thumb images.

	Example of returned markup:
	
	<div style="width:30px;height:30px;position:relative">
		<a href="#">
			<img src="image.png" style="width:30px;position:absolute;left:0px;top:10px" />
		</a>
	</div>
	
	Args is an array of options. Some are required!
	
	$args = array(	'img_path' => $row['img_path'], 				//REQUIRED!
							'size' => 40, 									//REQUIRED!
							'img_ar' => $row['img_ar'], 					//REQUIRED!
							'linkURL' => $CFG->shortbase."/set/".$row['set_id'], 	//optional
							'vAlign' => "top", 								//optional {top,center,bottom} (default: center)
							'hAlign' => "center" 							//optional {left,center,right} (default: center)
						);

*/
function outputSimpleImage($args) {
	global $CFG;

	if( isset( $CFG->thumbSize ))
		$thumbMax = $CFG->thumbSize;
	else
		$thumbMax = 120;

	if( $args['size'] <= $thumbMax )
		$pathToImg = $CFG->image_thumb . "/" . $args['img_path'];
	else
		$pathToImg = $CFG->image_medium . "/" . $args['img_path'];

	if($args['img_path'] == "noSetObjects"){
		$pathToImg = "$CFG->wwwroot/themes/$CFG->theme/images/sets_zeroObjects.gif";
	}
	if($args['img_path'] == "noObjectImage"){
		$pathToImg = "$CFG->wwwroot/themes/$CFG->theme/images/noObjectImage_thumb.gif";
	}

	$imageOutput = "<div style='width:".$args['size']."px;height:".$args['size']."px;position:relative;'>";
	if (isset($args['linkURL'])) $imageOutput .= "<a href='".$args['linkURL']."'>";
	
	if( $args['img_ar'] <= 0 ) {
		$imageOutput .= "<img src='".$pathToImg."' style='width:".$args['size']."px;'/>";
	} else {
		$vAlign = "";
		$hAlign = ""; 
		$width = "";
		$height = "";
		if( $args['img_ar'] > 1 ) {	// Horizontal/landscape
			$width = "width:".$args['size']."px;";
			$offset = (($args['size']-$args['size']/$args['img_ar'])/2);
			if(!isset($args['vAlign']) || $args['vAlign'] == "center"){
				$vAlign = "top:".$offset."px;";
			} elseif( $args['vAlign'] == "top"){
				$vAlign = "top:0px;";
			} elseif($args['vAlign'] == "bottom"){
				$vAlign = "top:".($offset*2)."px;";
			}
		} else { // Vertical/portrait
			$height = "height:".$args['size']."px;";
			$offset = (($args['size']-$args['size']*$args['img_ar'])/2);
			if(!isset($args['hAlign']) || $args['hAlign'] == "center"){
				$hAlign = "left:".$offset."px;";
			} elseif( $args['hAlign'] == "left"){
				$hAlign = "left:0px;";
			} elseif($args['hAlign'] == "right"){
				$hAlign = "left:".($offset*2)."px;";
			}
		}
		$imageOutput .= "<img src='".$pathToImg."' style='position:relative;".$vAlign.$hAlign.$width.$height."'/>";
	}
	if (isset($args['linkURL'])) $imageOutput .= "</a>";
	$imageOutput .= "</div>";

	return $imageOutput;

}
?>
