<?php
header("Content-Type: application/json");
if($_SERVER['REQUEST_METHOD'] == "GET") {
	require "bootloader.php";
	$ret = new stdClass();
	$from = $_GET['from'] ? intval($_GET['from']) : 0;
	$size = $_GET['size'] ? intval($_GET['size']) : 10;
	
	$ret = new stdClass();
	
	if(array_key_exists("stock",$_GET) && !empty($_GET['stock'])) {
		$stock = preg_replace("/[^A-Za-z0-9]/","",$_GET['stock']);
		
		$ret->stocks = ElasticSearchDriver::getInstance()->search("stocks",ElasticSearchDriver::SEARCH_TERM,array("stock"=>strtolower($stock)),$size,$from)->getRecords();
		if(!isset($_GET['from'])) {
			$amazon = ElasticSearchDriver::getInstance()->search("amazon",ElasticSearchDriver::SEARCH_TERM,array("ticker"=>strtolower($stock)))->getNextRecord();
			if($amazon) {
				$name = trim(preg_replace("/(inc\.?)/i","",$amazon->name));
				$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_MATCH,array("text"=>strtolower("{$name}")))->getRecords();
			}
		}		
	} elseif(array_key_exists("tweet",$_GET) && !empty($_GET['tweet'])) {
		$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_MATCH,array("text"=>strtolower("{$name}")),$size,$from)->getRecords();
	}
	
	echo json_encode($ret);
} else {
	http_status_code(400);
}
