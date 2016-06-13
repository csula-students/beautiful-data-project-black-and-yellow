<?php
class ElasticSearchResult {
	private $result;
	private $intItr = 0;
	function __construct(string $json) {
		$result = json_decode($json);
		if(!$result) {
			throw new Exception("Invalid json: " . $json);
		}
		
		$this->result = $result;
	}
	
	protected function filterObject(stdClass $Item): stdClass {
		if(property_exists($Item,'_source')) {
			$Item = $Item->_source;
		}
		return $Item;
	}
	
	public function getTook():int {
		return intval($this->result->took);
	}
	
	public function getTimedOut():boolean {
		return boolval($this->result->timed_out);
	}
	
	public function getRecordFound():int {
		return intval($this->result->hits->total);
	}
	
	public function getMaxScore():float {
		return floatval($this->result->hits->max_score);
	}
	
	public function getNextRecord():stdClass {
		$Record = new stdClass();
		if($this->intItr < count($this->result->hits->hits)) {
			$Record = $this->result->hits->hits[$this->intItr];
			$this->intItr++;
		}
		
		return $this->filterObject($Record);
	}
	
	public function &getRecords(): array {
		$Records = array();
		if($this->getRecordFound() > 0 && count($this->result->hits->hits) > 0) {
			foreach($this->result->hits->hits as $Item) {
				$Records[] = $this->filterObject($Item);
			}
		}
		return $Records;
	}
	
	public function getObject(): stdClass {
		$ret = new stdClass();
		$ret->maxItems = $this->getRecordFound();
		$ret->items = $this->getRecords();
		$ret->maxScore = $this->getMaxScore();
		$ret->time = $this->getTook();
		return $ret;
	}
}