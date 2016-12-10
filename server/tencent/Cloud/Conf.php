<?php
namespace Cloud;

class Conf
{
    const PKG_VERSION = '2.0.1';

    const API_IMAGE_END_POINT = 'http://web.image.myqcloud.com/photos/v1/';

    const API_IMAGE_END_POINT_V2 = 'http://web.image.myqcloud.com/photos/v2/';

    const API_VIDEO_END_POINT = 'http://web.video.myqcloud.com/videos/v1/';

    const API_PRONDETECT_URL = 'http://service.image.myqcloud.com/detection/pornDetect';


    // spence
    const API_IMAGETAG_DETECT_URL = 'http://service.image.myqcloud.com/v1/detection/imagetag_detect';

    // 以下部分请您根据在qcloud申请到的项目id和对应的secret id和secret key进行修改

    const APPID = '10046811';

    const SECRET_ID = 'AKIDfAttKrrbeXZplvPg9mUVu0wM7NAK24Yi';

    const SECRET_KEY = '1peUHPWfQaRkzbWoGlm2OmG6cnf1sakF';

    const BUCKET = 'wwqpic';

    // 以上部分请您根据在qcloud申请到的项目id和对应的secret id和secret key进行修改

    public static function getUA()
    {
        return 'QcloudPHP/' . self::PKG_VERSION . ' (' . php_uname() . ')';
    }
}


//end of script