<?php 
class Config {
	private static $Instance = null;
	private $config;
	public static function getInstance() {
		if(self::$Instance == null) {
			self::$Instance = new self();
		}
		
		return self::$Instance;
	}
	
	protected function __construct() {
		$this->config = json_decode(file_get_contents(dirname(__DIR__)."/config.json"));
		if(!$this->config) {
			if(!file_exists(dirname(__DIR__)."/config.json")) {
				throw new Exception("File not found: " . dirname(__DIR__)."/config.json");
			}
			throw new Exception("Unable to parse config file");
		}
	}
	
	/**
	 * 
	 * @param String $param
	 * @return multi
	 */
	public function __get($param) {
		return $this->config->$param;
	}
	
	/**
	 * @param String $param
	 * @return bool
	 */
	public function __isset($param) {
		return array_key_exists($param,get_object_vars($this->config));
	}
}