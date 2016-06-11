<?php
header("Content-Type: application/json");
if($_SERVER['REQUEST_METHOD'] == "GET") {
	require "bootloader.php";
	$ret = new stdClass();
	$from = array_key_exists('from',$_GET) && $_GET['from'] ? intval($_GET['from']) : 0;
	$size = array_key_exists('size',$_GET) &&  $_GET['size'] ? intval($_GET['size']) : 10;
	$size = $size < 10 ? 10 : ($size > 10000 ? 10000 : $size);
	
	$start = array_key_exists("start",$_GET) && $_GET['start'] ? intval($_GET['start']) : date('U');
	$end = array_key_exists("end",$_GET) && $_GET['end'] ? intval($_GET['end']) : date('U');
	
	$ret = new stdClass();
	
	if(array_key_exists("stock",$_GET) && !empty($_GET['stock'])) {
		$stock = strtolower(preg_replace("/[^A-Za-z0-9]/","",$_GET['stock']));
		
		$query = array(
			"must" => array(
					"term" => array("stock" => $stock),
			),
			"filter"=>array(
					"range" => array(
							"date" => array(
								"gte"=>$start,
								"lte"=>$end
							)
					)
			)
		);
		
		$ret->stocks = ElasticSearchDriver::getInstance()->search("stocks",ElasticSearchDriver::SEARCH_BOOL,$query,$size,$from)->getObject();
		if(!isset($_GET['from'])) {
			$amazon = ElasticSearchDriver::getInstance()->search("amazon",ElasticSearchDriver::SEARCH_TERM,array("ticker"=>$stock))->getNextRecord();
			if($amazon && property_exists($amazon,'name')) {
				$name = trim(preg_replace("/(inc\.?)/i","",$amazon->name));
				$ret->amazon = $amazon;
				
				$query = array(
						"must" => array(
								"match" => array("text" => strtolower("{$name}")),
						),
						"filter"=>array(
								"range" => array(
										"date" => array(
												"gte"=>$start,
												"lte"=>$end
										)
								)
						)
				);
				
				$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_BOOL,$query)->getRecords();
			}
		}		
	} elseif(array_key_exists("tweet",$_GET) && !empty($_GET['tweet'])) {
		$ret->tweets = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_MATCH,array("text"=>strtolower("{$name}")),$size,$from)->getRecords();
	} elseif(array_key_exists("amazon",$_GET) && intval($_GET['amazon']) === 1) {
		$ret->amazon = ElasticSearchDriver::getInstance()->search("amazon",ElasticSearchDriver::SEARCH_ALL,array(),100,0)->getObject();
	} elseif(array_key_exists("amazon",$_GET) && !empty($_GET['amazon'])) {
		$name = strtolower(preg_replace("/[^A-Za-z0-9]/","",$_GET['amazon']));
		$query = array(
				"name" => array(
						"query" => strtolower($name),
						"fuzziness" => 2
				)
		);
		$ret->amazon = ElasticSearchDriver::getInstance()->search("amazon",ElasticSearchDriver::SEARCH_MATCH,$query,100,0)->getObject();
	}
	
	http_response_code(200);
	echo json_encode($ret);
} else {
	http_response_code(400);
}
