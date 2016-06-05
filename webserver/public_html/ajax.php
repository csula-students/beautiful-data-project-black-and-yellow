<?php
header("Content-Type: application/json");
if($_SERVER['REQUEST_METHOD'] == "GET" && array_key_exists("stock",$_GET) && !empty($_GET['stock'])) {
	$ret = new stdClass();
	$stock = preg_replace("/[^A-Za-z0-9]/",$_GET['stock']);
	$ret->stocks = ElasticSearchDriver::getInstance()->search("stock",array("stock"=>$stock));
	$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets",array("text"=>"*{$stock}*"));
	echo json_encode($ret);
} else {
	http_status_code(400);
}
