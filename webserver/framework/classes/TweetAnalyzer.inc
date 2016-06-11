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
	
	public function algorithmPercentageOfCollected(string $text, array $tweets, int $aggregation): array {
		$collection = array();
		$percentages = array();
		
		if(!empty($tweets)) {
			foreach($tweets as $tweet) {
				$date = new DateTime($tweet->date);
				switch($aggregation) {
					case self::AGGREGATION_HOUR:
						$date->modify("-" + $date->format('i') + " minutes");
						$date->modify("-" + $date->format('s') + " seconds");
					case self::AGGREGATION_DAY:
						$date->modify("-" + $date->format('H') + " hours");
					case self::AGGREGATION_MONTH:
						$date->modify("-" + $date->format('d') + " days");
					case self::AGGREGATION_YEAR:
						$date->modify("-" + $date->format('m') + " months");
				}
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