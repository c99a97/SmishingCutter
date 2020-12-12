<?php
	$db_url = "database-c99a97.cf6z54xvsafa.ap-northeast-2.rds.amazonaws.com";
	$db_id = "cswin";
	$db_pw = "cswin123!";
	$db_name = "SC_c99a97";
	$db_conn = mysqli_connect($db_url, $db_id, $db_pw, $db_name);
	mysqli_query($db_conn,'set names utf8');
?>
