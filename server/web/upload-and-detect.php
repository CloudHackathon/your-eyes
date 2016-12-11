<?php
require('../qcloud/include.php');

use CI\ImageProcess;
use CI\ImageV2;
use CI\Conf;
use Util\Arr;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'tags' => array());

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

$res = ImageV2::upload_binary($fileContent, Conf::BUCKET);

if ($res['code'] == 0) {
    echo ImageProcess::tagDetect($res['data']['downloadUrl']);
} else {
    $response['message'] = $res['message'];
    $response['code'] = $res['code'];
    echo json_encode($response);
}

exit;
