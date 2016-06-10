<?php
header("Content-Type: application/json");
if($_SERVER['REQUEST_METHOD'] == "GET" && array_key_exists("stock",$_GET) && !empty($_GET['stock'])) {
	require "bootloader.php";
	$ret = new stdClass();
	$stock = preg_replace("/[^A-Za-z0-9]/","",$_GET['stock']);
	
	$ret->stocks = ElasticSearchDriver::getInstance()->search("stocks",ElasticSearchDriver::SEARCH_TERM,array("stock"=>strtolower($stock)))->getRecords();
	$amazon = ElasticSearchDriver::getInstance()->search("amazon",ElasticSearchDriver::SEARCH_TERM,array("ticker"=>strtolower($stock)))->getNextRecord();
	if($amazon) {
		$name = trim(preg_replace("/(inc\.?)/i","",$amazon->name));
		$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_MATCH,array("text"=>strtolower("{$name}")));
	}
	echo json_encode($ret);
} else {
	http_status_code(400);
}
