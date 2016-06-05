<?php
class ElasticSearchDriver {
	private static $Instance = null;
	protected $config;
	
	public static function getInstance() {
		if(self::$Instance == null) {
			self::$Instance = new static();
		}
	}
	
	protected function __construct() {
		$this->config = Config::getInstance()->__get("elasticsearch");
		if($this->config == null || !is_array($this->config)) {
			$this->config = new stdClass();
			$this->config->port = "9200";
			$this->config->host = "localhost";
			$this->config->index = "datascience";
		}
	}
	
	public function search($type,$query) {
		$ch = curl_init();
		
		curl_setopt($ch,CURLOPT_URL,"http://{$this->config->host}:{$this->config->port}/{$this->config->index}/{$type}");
		curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
		$ret = curl_exec($ch);
		curl_close($ch);
		return $ret;
	}
}