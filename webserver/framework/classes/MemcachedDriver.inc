<?php
class MemcachedDriver {
	private static $Instance = null;
	private $Service = null;
	
	public static function getInstance(): MemcachedDriver {
		if(self::$Instance == null) {
			self::$Instance = new static();
		}
		
		return self::$Instance;
	}
	
	protected function __construct() {
		$config = Config::getInstance()->memcache;
		if($config) {
			if(class_exists("Memcache",false) && (!$config->driver || (strtolower($config->driver) == "memcache"))) {
				$this->Service = new Memcache();
				
				if($config->servers && is_array($config->servers) && !empty($config->servers)) {
					foreach($config->servers as $server) {
						$this->Service->addServer($server->host,$server->port ? $server->port : 11211, $server->persistent ? $server->persistent : 1, $server->weight ? $server->weight: 1);
					}
				}
			} elseif(class_exists("Memcached",false) && (!$config->driver || (strtolower($config->driver) == "memcached"))) {
				$this->Service = new Memcached();
				
				if($config->servers && is_array($config->servers) && !empty($config->servers)) {
					foreach($config->servers as $server) {
						$this->Service->addServer($server->host,$server->port ? $server->port : 11211, $server->weight ? $server->weight: 1);
					}
				}
			} else {
				throw new Exception("No memcache modules found");
			}
		}
	}
	
	public function get($key) {
		if($this->Service == null) {
			return null;
		}
		
		return $this->Service->get($key);
	}
	
	public function set($key,&$value,$ttl=10) {
		if($this->Service == null) {
			return false;
		}
		
		return $this->Service->set($key,$value,$ttl);
	}
	
	public function delete($key) {
		if($this->Service == null) {
			return false;
		}
		
		return $this->Service->delete($key);
	}
}