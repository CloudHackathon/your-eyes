<?php
require('../tencent/include.php');

use CloudImage\ImageProcess;

use Util\Arr;

header('Content-type: application/json');

$response = array('code' => 0, 'message' => '', 'tags' => array());

$fileContent = '';

$url = Arr::get($_GET, 'url');

if ($url) {
    echo ImageProcess::tagDetect($url);
} else {
    $response['message'] = '没有图片url';
    $response['code'] = '1004';
    echo json_encode($response);
}
//http://118.89.25.65/image-tag-detect.php?url=http://p.qpic.cn/wwqpic/0/f9806d25479f649e2761268360b47e79.jpg/0
exit;
