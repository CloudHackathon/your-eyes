<?php

namespace Ci;

class ImageProcess
{
    const IMAGE_FILE_NOT_EXISTS = -1;
    const TIME_OUT = 1000;

    /**
     * 智能鉴黄
     * @param  string $pronDetectUrl 要进行黄图检测的图片url
     */
    public static function pornDetect($pronDetectUrl)
    {
        $sign = Auth::getPornDetectSign($pronDetectUrl);
        if (false === $sign) {
            $data = array("code" => 9,
                "message" => "Secret id or key is empty.",
                "data" => array());

            return $data;
        }
        $data = array(
            'bucket' => Conf::BUCKET,
            'appid' => Conf::APPID,
            'url' => ($pronDetectUrl));

        $reqData = json_encode($data);
        $req = array(
            'url' => Conf::API_PRONDETECT_URL,
            'method' => 'post',
            'timeout' => self::TIME_OUT,
            'header' => array(
                'Authorization:' . $sign,
                'Content-Type:application/json',
            ),
            'data' => $reqData,
        );
        $ret = Http::send($req);
        return $ret;
    }

    /**
     * 智能鉴黄-Urls
     * @param  string $pornUrl 要进行黄图检测的图片url列表
     */
    public static function pornDetectUrl($pornUrl)
    {
        $sign = Auth::getPornDetectSign();
        if (false === $sign) {
            $data = array("code" => 9,
                "message" => "Secret id or key is empty.",
                "data" => array());

            return $data;
        }
        $data = array(
            'bucket' => Conf::BUCKET,
            'appid' => Conf::APPID,
            'url_list' => ($pornUrl));

        $reqData = json_encode($data);
        $req = array(
            'url' => Conf::API_PRONDETECT_URL,
            'method' => 'post',
            'timeout' => self::TIME_OUT,
            'header' => array(
                'Authorization:' . $sign,
                'Content-Type:application/json',
            ),
            'data' => $reqData,
        );

        $ret = Http::send($req);

        return $ret;
    }

    /**
     * 智能鉴黄-Files
     * @param  string $pornFile 要进行黄图检测的图片File列表
     */
    public static function pornDetectFile($pornFile)
    {
        $sign = Auth::getPornDetectSign();
        if (false === $sign) {
            $data = array("code" => 9,
                "message" => "Secret id or key is empty.",
                "data" => array());

            return $data;
        }

        $data = array(
            'appid' => Conf::APPID,
            'bucket' => Conf::BUCKET,
        );

        for ($i = 0; $i < count($pornFile); $i++) {
            if (PATH_SEPARATOR == ';') {    // WIN OS
                $pornFile[$i] = iconv("UTF-8", "gb2312", $pornFile[$i]);
            }
            $srcPath = realpath($pornFile[$i]);
            if (!file_exists($srcPath)) {
                return array('httpcode' => 0, 'code' => self::IMAGE_FILE_NOT_EXISTS, 'message' => 'file ' . $pornFile[$i] . ' not exists', 'data' => array());
            }
            if (function_exists('curl_file_create')) {
                $data['image[' . (string)$i . ']'] = curl_file_create($srcPath, NULL, $pornFile[$i]);
            } else {
                $data['image[' . (string)$i . ']'] = '@' . $srcPath;
            }
        }

        $req = array(
            'url' => Conf::API_PRONDETECT_URL,
            'method' => 'post',
            'timeout' => self::TIME_OUT,
            'data' => $data,
            'header' => array(
                'Authorization:' . $sign,
            ),
        );

        $rsp = Http::send($req);

        return $rsp;
    }

    /**
     * 图片鉴别
     * @param  string $url 要进行鉴别图片url
     */
    public static function tagDetect($url)
    {
        $sign = Auth::getPornDetectSign($url);
        if (false === $sign) {
            $data = array("code" => 9,
                "message" => "Secret id or key is empty.",
                "data" => array());

            return $data;
        }
        $data = array(
            'bucket' => Conf::BUCKET,
            'appid' => Conf::APPID,
            'url' => ($url));

        $reqData = json_encode($data);
        $req = array(
            'url' => Conf::API_IMAGETAG_DETECT_URL,
            'method' => 'post',
            'timeout' => self::TIME_OUT,
            'header' => array(
                'Authorization:' . $sign,
                'Content-Type:application/json',
            ),
            'data' => $reqData,
        );
        $ret = Http::send($req);
        return $ret;
    }

    /**
     * 图片鉴别-Files
     * @param  string $files 要进行检测的图片File列表
     */
    public static function tagDetectFile($files)
    {
        $sign = Auth::getPornDetectSign();
        if (false === $sign) {
            $data = array("code" => 9,
                "message" => "Secret id or key is empty.",
                "data" => array());

            return $data;
        }

        $data = array(
            'appid' => Conf::APPID,
            'bucket' => Conf::BUCKET,
        );

        for ($i = 0; $i < count($files); $i++) {
            if (PATH_SEPARATOR == ';') {    // WIN OS
                $files[$i] = iconv("UTF-8", "gb2312", $files[$i]);
            }
            $srcPath = realpath($files[$i]);
            if (!file_exists($srcPath)) {
                return array('httpcode' => 0, 'code' => self::IMAGE_FILE_NOT_EXISTS, 'message' => 'file ' . $files[$i] . ' not exists', 'data' => array());
            }
//            if (function_exists('curl_file_create')) {
//                $data['image[' . (string)$i . ']'] = curl_file_create($srcPath, NULL, $files[$i]);
//            } else {
//                $data['image[' . (string)$i . ']'] = '@' . $srcPath;
//            }
//            $data['image'] = '@' . $srcPath;//file_get_contents($srcPath);

            $content = file_get_contents($srcPath);
            $type = pathinfo($srcPath, PATHINFO_EXTENSION);
            $data['image'] = 'data:image/' . $type . ';base64,' . base64_encode($content);
        }

        $req = array(
            'url' => Conf::API_IMAGETAG_DETECT_URL,
            'method' => 'post',
            'timeout' => self::TIME_OUT,
            'data' => $data,
            'header' => array(
                'Authorization:' . $sign,
            ),
        );

        var_dump($data);
        $rsp = Http::send($req);

        return $rsp;
    }

}
