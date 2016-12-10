<?php
require('../tencentyun/include.php');

use Tencentyun\ImageProcess;
use Tencentyun\ImageV2;
use Tencentyun\Conf;
use Tencentyun\Arr;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'tags' => array());

$fileContent = '';

if (Arr::get($_GET, 'env') == 'dev') {
    $fileContent = file_get_contents('../813569577103004665.jpg');//file_get_contents($file['tmp_name']);
} else {
    $file = Arr::get($_FILES, 'file');

    if ($file === null OR $file['size'] == 0) {
        $response['message'] = '图片上传出错：内容为空';
        $response['code'] = '1';
        echo json_encode($response);
        exit;
    }

    if (!empty($file['error'])) {
        $response['message'] = '图片上传出错：' . $file['error'];
        $response['code'] = '2';
        echo json_encode($response);
        exit;
    }

    $fileContent = file_get_contents($file['tmp_name']);
}

$res = ImageV2::upload_binary($fileContent, Conf::BUCKET);

if ($res['code'] == 0) {
    $res = ImageProcess::tagDetect($res['data']['downloadUrl']);
    echo $res;
} else {
    $response['message'] = $res['message'];
    $response['code'] = $res['code'];
    echo json_encode($response);
}

exit;
