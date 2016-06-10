<?php
class MemcacheDriver {
	private static $Instance = null;
	
	public static function getInstance() {
		if(self::$Instance == null) {
			self::$Instance = new static();
		}
	}
	
	protected function __construct() {
		
	}
	
	public function &get($key) {
		
	}
	
	public function set($key,&$value) {
		
	}
	
	public function delete($key) {
		
	}
}