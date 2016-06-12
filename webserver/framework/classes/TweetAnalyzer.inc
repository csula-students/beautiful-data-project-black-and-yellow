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
		$collection = array();
		$percentages = array();
		$start = new DateTime($startTimestamp);
		$end = new DateTime($endTimestamp);
		$adjustment = "1 ";
		
		switch($aggregation) {
			case self::AGGREGATION_YEAR:
				$start->modify("-" + $date->format('m') + " months");
				$end->modify("-" + $date->format('m') + " months");
				$adjustment .= "year";
			case self::AGGREGATION_MONTH:
				$start->modify("-" + $date->format('d') + " days");
				$end->modify("-" + $date->format('d') + " days");
				$adjustment .= "month";
			case self::AGGREGATION_DAY:
				$start->modify("-" + $date->format('H') + " hours");
				$end->modify("-" + $date->format('H') + " hours");
				$adjustment .= "day";
			case self::AGGREGATION_HOUR:
				$start->modify("-" + $date->format('i') + " minutes");
				$start->modify("-" + $date->format('s') + " seconds");
				$end->modify("-" + $date->format('i') + " minutes");
				$end->modify("-" + $date->format('s') + " seconds");
				$adjustment .= "hour";
		}
		
		while($start < $end) {
			
			$end->modify("-{$adjustment}");
		}
		
		if(!empty($tweets)) {
			foreach($tweets as $tweet) {
				$date = new DateTime($tweet->date);
				
				$key = $date->format('U');
				if(!array_key_exists($key,$collection)) {
					$collection[$key] = array();
				}
				
				$collection[$key][] = $tweet;
			}
			
			foreach($collection as $timestamp => $tweets) {
				$hits = 0;
				foreach($tweets as $tweet) {
					if(preg_match("/(\#|\@)?{$text}(\'.*)? /i",$tweet->text)){
						$hits++;
					}
				}
				
				$percentages[$timestamp] = $hits / count($tweets);
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