<?php
require('../qcloud/include.php');

use Util\Config;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'data' => array());

$redis = new Redis();
$redis->connect(Config::REDIS_HOST);
$redis->auth(Config::REDIS_AUTH);
$redis->select(Config::REDIS_DB);

$url = $redis->get('image_url_list');
//$url = $redis->zRevRange('image_url', 0, 0);

if (!empty($url)) {
    $response['data']['url'] = $url;
}else{
    $response['code'] = '2001';
    $response['message'] = '没有找到分享的图片';
    $response['data']['url'] = '#';
}

echo json_encode($response);

exit;
