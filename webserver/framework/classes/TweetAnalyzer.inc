<?php
class TweetAnalyzer {
	const AGGREGATION_HOUR = 1;
	const AGGREGATION_DAY = 2;
	const AGGREGATION_MONTH = 3;
	const AGGREGATION_YEAR = 4;
	
	private static $Instance = null;
	
	public static function getInstance(): TweetAnalyzer {
		if(self::$Instance == null) {
			self::$Instance = new static();
		}
		
		return self::$Instance;
	}
	
	protected function __construct() {
		
	}
	
	public function algorithmPercentageOfCollected(string $text, int $startTimestamp, int $endTimestamp, int $aggregation = self::AGGREGATION_DAY): array {
		$percentages = array();
		$start = new DateTime("@{$startTimestamp}");
		$end = new DateTime("@{$endTimestamp}");
		$adjustment = "";
		
		switch($aggregation) {
			case self::AGGREGATION_YEAR:
				$start->modify("-{$start->format('m')} months");
				$end->modify("-{$end->format('m')} months");
				if(empty($adjustment)) {
					$adjustment = "year";
				}
			case self::AGGREGATION_MONTH:
				$start->modify("-{$start->format('d')} days");
				$end->modify("-{$end->format('d')} days");
				if(empty($adjustment)) {
					$adjustment = "month";
				}
			case self::AGGREGATION_DAY:
				$start->modify("-{$start->format('H')} hours");
				$end->modify("-{$end->format('H')} hours");
				if(empty($adjustment)) {
					$adjustment = "day";
				}
			case self::AGGREGATION_HOUR:
				$start->modify("-{$start->format('i')} minutes");
				$start->modify("-{$start->format('s')} seconds");
				$end->modify("-{$end->format('i')} minutes");
				$end->modify("-{$end->format('s')} seconds");
				if(empty($adjustment)) {
					$adjustment = "hour";
				}
		}
		
		while($start <= $end) {
			$date = clone($end);
			$date->modify("+1 {$adjustment}");
			
			$startTimestamp = $end->format('U');
			$endTimestamp = $date->format('U');

			$query = array(
				"must" => array(
					"match" => array("text" => 
							array(
								"query" => strtolower($text),
								"fuzziness" => 2
							)
					)	
				),
				"filter" => array(
					"range" => array(
						"date" => array(	
							"gte"=>$startTimestamp,
							"lte"=>$endTimestamp
						)
					)
				)
			);
			$Found = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_BOOL,$query,0,0);
			
			$query = array("date" => array(
					"gte"=>$startTimestamp,
					"lte"=>$endTimestamp
				)
			);
			
			$Total = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_RANGE,$query,0,0);

			if($Total->getRecordFound() > 0) {
				$percentages[] = array("date"=>$endTimestamp,"value"=>$Found->getRecordFound() / $Total->getRecordFound());
			}	

			$end->modify("-1 {$adjustment}");
		}
		
		return $percentages;
	}
	
	public function algorithmPercentageChange(string $text, int $startTimestamp, int $endTimestamp, int $aggregation = self::AGGREGATION_DAY): array {
		$percentages = array();
		$start = new DateTime("@{$startTimestamp}");
		$end = new DateTime("@{$endTimestamp}");
		$adjustment = "";
	
		switch($aggregation) {
			case self::AGGREGATION_YEAR:
				$start->modify("-{$start->format('m')} months");
				$end->modify("-{$end->format('m')} months");
				if(empty($adjustment)) {
					$adjustment = "year";
				}
			case self::AGGREGATION_MONTH:
				$start->modify("-{$start->format('d')} days");
				$end->modify("-{$end->format('d')} days");
				if(empty($adjustment)) {
					$adjustment = "month";
				}
			case self::AGGREGATION_DAY:
				$start->modify("-{$start->format('H')} hours");
				$end->modify("-{$end->format('H')} hours");
				if(empty($adjustment)) {
					$adjustment = "day";
				}
			case self::AGGREGATION_HOUR:
				$start->modify("-{$start->format('i')} minutes");
				$start->modify("-{$start->format('s')} seconds");
				$end->modify("-{$end->format('i')} minutes");
				$end->modify("-{$end->format('s')} seconds");
				if(empty($adjustment)) {
					$adjustment = "hour";
				}
		}
	
		while($start <= $end) {
			$date = clone($end);
			$date->modify("+1 {$adjustment}");
				
			$startTimestamp = $end->format('U');
			$endTimestamp = $date->format('U');
			
			$end->modify("-1 {$adjustment}");
			$date = clone($end);
			$date->modify("+1 {$adjustment}");
			
			$prevStartTimestamp = $end->format('U');
			$prevEndTimestamp = $date->format('U');
	
			$query = array(
					"must" => array(
							"match" => array("text" =>
									array(
											"query" => strtolower($text),
											"fuzziness" => 2
									)
							)
					),
					"filter" => array(
							"range" => array(
									"date" => array(
											"gte"=>$startTimestamp,
											"lte"=>$endTimestamp
									)
							)
					)
			);
			$Found = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_BOOL,$query,0,0);
			
			if($Found->getRecordFound() == 0) {
				continue;
			}
			$query = array(
					"must" => array(
							"match" => array("text" =>
									array(
											"query" => strtolower($text),
											"fuzziness" => 2
									)
							)
					),
					"filter" => array(
							"range" => array(
									"date" => array(
											"gte"=>$prevStartTimestamp,
											"lte"=>$prevEndTimestamp
									)
							)
					)
			);
				
			$Previous = ElasticSearchDriver::getInstance()->search("tweets-5.0",ElasticSearchDriver::SEARCH_BOOL,$query,0,0);
	
			if($Previous->getRecordFound() > 0) {
				$percentages[] = array("date"=>$endTimestamp,"value"=> ($Found->getRecordFound() - $Previous->getRecordFound()) / $Previous->getRecordFound());
			}
	
			
		}
	
		return $percentages;
	}
	
	public function getAlgorithms(): array {
		$list = array();
		
		$methods = get_class_methods($this);
		foreach($methods as $method) {
			if(strpos($method,"algorithm") === 0) {
				$list[] = $method;
			}
		}
		
		return $list;
	}
}
