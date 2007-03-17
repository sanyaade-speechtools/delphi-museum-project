<?php 
/* Include Files *********************/
session_start(); 
include("dbconnect.php");
include("login.php");
/*************************************/
//$subtitle = "Login";

include("genheader.php");
displayLogin();
include("gentail.php");
?>
