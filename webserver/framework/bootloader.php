<?php
spl_autoload_register(function ($class_name) {
    include _DIR__ . "/classes/{$class_name}.inc";
});