<?php
require('../qcloud/include.php');
use Cos\Cosapi;
use Util\Arr;
use Util\Config;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'data' => array());

$bucketName = 'youreyes';
Cosapi::setTimeout(180);
$bizAttr = "";
$insertOnly = 0;
$sliceSize = 3 * 1024 * 1024;

if (Arr::get($_GET, 'env') == 'dev') {
    $srcPath = '../2016-12-10T13-10-56.398Z.wav';
    $dstPath = '/video/2016-12-10T13-10-56.398Z' . time() . '.wav';
    $dstFolder = '/video/';
    $res = Cosapi::upload($bucketName, $srcPath, $dstPath, $bizAttr, $sliceSize, $insertOnly);
} else {
    $file = Arr::get($_FILES, 'file');

    if ($file === null OR $file['size'] == 0) {
        $response['message'] = '图片上传出错：内容为空';
        $response['code'] = 1001;
        echo json_encode($response);
        exit;
    }

    if (!empty($file['error'])) {
        $response['message'] = '图片上传出错：' . $file['error'];
        $response['code'] = 1002;
        echo json_encode($file['error']);
        exit;
    }

    $name = time() . '_' . rand(1, 10000) . '_' . $file["name"];
    move_uploaded_file($file["tmp_name"], "../upload/" . $name);

    $srcPath = "../upload/" . $name;
    $dstPath = '/video/' . date('Y-m-d') . '/' . $name;
    $dstFolder = '/video/';
    $res = Cosapi::upload($bucketName, $srcPath, $dstPath, $bizAttr, $sliceSize, $insertOnly);
}

$redis = new Redis();
$redis->connect(Config::REDIS_HOST);
$redis->auth(Config::REDIS_AUTH);
$redis->select(Config::REDIS_DB);

$code = Arr::get($res, 'code');
$access_url = Arr::path($res, 'data.access_url');
if ($code == 0 && $access_url) {
    $redis->set('audio_url_list', $access_url);
    $redis->zAdd('audio_url', time(), $access_url);
}
// {"code":0,"message":"SUCCESS","data":{"access_url":"http:\/\/youreyes-10046811.file.myqcloud.com\/video\/2016-12-11\/1481415051_6216206752016-12-10T13-10-56.398Z.wav","resource_path":"\/video\/2016-12-11\/1481415051_6216206752016-12-10T13-10-56.398Z.wav","source_url":"http:\/\/youreyes-10046811.cos.myqcloud.com\/video\/2016-12-11\/1481415051_6216206752016-12-10T13-10-56.398Z.wav","url":"http:\/\/web.file.myqcloud.com\/files\/v1\/video\/2016-12-11\/1481415051_6216206752016-12-10T13-10-56.398Z.wav"}}

echo json_encode($res);

exit;
