<?php
require('../qcloud/include.php');

use CI\ImageV2;
use Util\Config;
use Util\Arr;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'data' => array());

$fileContent = '';

if (Arr::get($_GET, 'env') == 'dev') {
    $fileContent = file_get_contents('../813569577103004665.jpg');
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

    $fileContent = file_get_contents($file['tmp_name']);
}

$res = ImageV2::upload_binary($fileContent, 'wwqpic');

//{"httpcode":200,"code":0,"message":"SUCCESS","data":{"url":"http:\/\/web.image.myqcloud.com\/photos\/v2\/10046811\/wwqpic\/0\/0b6ccb75-f369-4cc7-87aa-5212a52fceb0","downloadUrl":"http:\/\/wwqpic-10046811.image.myqcloud.com\/0b6ccb75-f369-4cc7-87aa-5212a52fceb0","fileid":"0b6ccb75-f369-4cc7-87aa-5212a52fceb0","info":[[{"height":1280,"width":1060}]]}}

$redis = new Redis();
$redis->connect(Config::REDIS_HOST);
$redis->auth(Config::REDIS_AUTH);
$redis->select(Config::REDIS_DB);

$code = Arr::get($res, 'code');
$downloadUrl = Arr::path($res, 'data.downloadUrl');
if ($code == 0 && $downloadUrl) {
    $redis->zAdd('share_url', time(), $downloadUrl);
}

echo json_encode($res);

exit;
