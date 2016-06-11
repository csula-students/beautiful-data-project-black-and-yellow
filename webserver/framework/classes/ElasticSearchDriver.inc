<?php
class ElasticSearchDriver {
	const SEARCH_MATCH = "match";
	const SEARCH_TERM = "term";
	const SEARCH_BOOL = "bool";
	const SEARCH_RANGE = "range";
	const SEARCH_ALL = "all";
	
	private static $Instance = null;
	private $Memcache = null;
	protected $config;
	
	public static function getInstance(): ElasticSearchDriver {
		if(self::$Instance == null) {
			self::$Instance = new static();
		}
		
		return self::$Instance;
	}
	
	protected function __construct() {
		$this->config = Config::getInstance()->elasticsearch;
		if($this->config == null || !is_object($this->config)) {
			$this->config = new stdClass();
			$this->config->port = "9200";
			$this->config->host = "localhost";
			$this->config->index = "datascience";
		}
		$this->Memcache = MemcachedDriver::getInstance();
	}
	
	public function search(string $type,string $dslType, array $query,int $limit = 10,$from = 0):ElasticSearchResult {
		$ch = curl_init();
		
		if($dslType === self::SEARCH_ALL) {
			$search = json_encode(array("size"=>$limit,"from"=>$from));
		} else {
			$search = json_encode(array("query"=>array($dslType=>$query),"size"=>$limit,"from"=>$from));
		}
		
		$key = "{$type}:{$dslType}:".$search;
		$ret = $this->Memcache->get($key);
		if(!$ret) {
			//var_dump($search);
			curl_setopt($ch,CURLOPT_URL,"http://{$this->config->host}:{$this->config->port}/{$this->config->index}/{$type}/_search");
			curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
			curl_setopt($ch,CURLOPT_POSTFIELDS,$search);
			$ret = curl_exec($ch);
			curl_close($ch);
			
			$this->Memcache->set($key,$ret,1);
		}
		return new ElasticSearchResult($ret);
	}
}