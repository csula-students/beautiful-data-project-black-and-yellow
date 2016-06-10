<?php
class ElasticSearchResult {
	private $result;
	private $intItr = 0;
	function __construct(string $json) {
		$result = json_decode($json);
		if(!$result) {
			throw new Exception("Invalid json");
		}
		
		$this->result = $result;
	}
	
	protected function filterObject(stdClass $Item): stdClass {
		$Item = $Item->_source;
		return $Item;
	}
	
	public function getTook():int {
		return $this->result->took;
	}
	
	public function getTimedOut():boolean {
		return $this->result->timed_out;
	}
	
	public function getRecordFound():int {
		return $this->result->hits->total;
	}
	
	public function getMaxScore():float {
		return $this->result->hits->max_score;
	}
	
	public function getNextRecord():stdClass {
		$Record = null;
		if($this->intItr < count($this->result->hits->hits)) {
			$Record = $this->result->hits->hits[$this->intItr];
			$this->intItr++;
		}
		
		return $this->filterObject($Record);
	}
	
	public function &getRecords(): array {
		$Records = array();
		if(count($this->result->hits->hits) > 0) {
			foreach($this->result->hits->hits as $Item) {
				$Records[] = $this->filterObject($Item);
			}
		}
		return $Records;
	}
}