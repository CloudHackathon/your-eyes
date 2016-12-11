package com.youreye.tts.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.setting.TtsSettings;
import com.iflytek.speech.util.ApkInstaller;
import com.iflytek.sunflower.FlowerCollector;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.youreye.tts.qq.ThreadManager;
import com.youreye.tts.qq.UploadPicture;
import com.youreye.texttospeech.R;

public class YourEyesActivity extends Activity implements
		android.view.GestureDetector.OnGestureListener {
	private static String TAG = YourEyesActivity.class.getSimpleName();
	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 默认发音�?
	private String voicer = "xiaoyan";

	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;

	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语记安装助手
	ApkInstaller mInstaller;

	private SharedPreferences mSharedPreferences;

	private Camera mCamera;
	private ShowCamera showCamera;

	private long preScreenClickTime = 0;
	private long currentScreenClickTime = 0;

	private String picTakenStoragePath = null;

	// 定义手势检测器实例
	GestureDetector detector;
	public static Tencent mTencent;

	private MediaPlayer player;
	
	public String curTakenFilePath = null;


	public static Camera isCameraAvailiable() {
		Camera object = null;
		try {
			object = Camera.open();
		} catch (Exception e) {
		}
		return object;
	}

	private void initCamera() {

		mCamera = isCameraAvailiable();
		showCamera = new ShowCamera(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(showCamera);
		String sdCardState = Environment.getExternalStorageState();
		if (sdCardState.equals(Environment.MEDIA_MOUNTED)) {
			picTakenStoragePath = Environment.getExternalStorageDirectory()
					.getPath() + "/youreyes";
			File picTakenStorageDir = new File(picTakenStoragePath);
			if (!picTakenStorageDir.exists()) {
				picTakenStorageDir.mkdirs();
			}
		}
	}

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_scan_layout);

		SpeechUtility.createUtility(YourEyesActivity.this, "appid="
				+ getString(R.string.app_id));
		// 初始化合成对�?
		mTts = SpeechSynthesizer.createSynthesizer(YourEyesActivity.this,
				mTtsInitListener);

		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME,
				MODE_PRIVATE);

		mInstaller = new ApkInstaller(YourEyesActivity.this);

		initCamera();

		// 创建手势检测器
		detector = new GestureDetector(this, this);

		String mAppid = "222222";
		if (mTencent == null) {
			mTencent = Tencent.createInstance(mAppid, this);
		}
		
		TTSPlay("世界就在你眼前");
		

	}

	private void TTSPlay(String text) {

		// 移动数据分析，收集开始合成事�?
		FlowerCollector.onEvent(YourEyesActivity.this, "tts_play");

		// 设置参数
		setParam();
		int code = mTts.startSpeaking(text, mTtsListener);
		// /**
		// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
		// * text:要合成的文本，uri:�?要保存的音频全路径，listener:回调接口
		// */
		// String path =
		// Environment.getExternalStorageDirectory()+"/tts.pcm";
		// int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页�?
				mInstaller.install();
			} else {
				Log.e(TAG, "语音合成失败,错误码: " + code);
			}
		}
	}

	/**
	 * 初始化监听
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				Log.e(TAG, "初始化失败,错误码：" + code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的�?发�?�在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成�?
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};

	/**
	 * 合成回调监听�?
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
			Log.d(TAG, "开始播放");
		}

		@Override
		public void onSpeakPaused() {
			Log.d(TAG, "暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			Log.d(TAG, "继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
			Log.d(TAG, String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			Log.d(TAG, String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				Log.d(TAG, "播放完成");
			} else if (error != null) {
				Log.d(TAG, error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音�?
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
			// 设置合成语�??
			mTts.setParameter(SpeechConstant.SPEED,
					mSharedPreferences.getString("speed_preference", "50"));
			// 设置合成音调
			mTts.setParameter(SpeechConstant.PITCH,
					mSharedPreferences.getString("pitch_preference", "50"));
			// 设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME,
					mSharedPreferences.getString("volume_preference", "50"));
		} else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音�? voicer为空，默认�?�过语记界面指定发音人�??
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			/**
			 * TODO 本地合成不设置语速�?�音调�?�音量，默认使用语记设置 �?发�?�如�?自定义参数，请参考在线合成参数设�?
			 */
		}
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,
				mSharedPreferences.getString("stream_preference", "3"));
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记�?要更新版本才能生�?
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			currentScreenClickTime = System.currentTimeMillis();
			if ((currentScreenClickTime - preScreenClickTime) < 1000) {
				snapIt();
			}
			preScreenClickTime = currentScreenClickTime;	
									
			break;
		}
		return detector.onTouchEvent(event);

	}
	
		
	Runnable uploadSharedPic = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(curTakenFilePath == null){
				curTakenFilePath = Environment.getExternalStorageDirectory()
						.getPath() + "/youreyes/ouba.jpg";
			}

			UploadPicture uploadPic = new UploadPicture();
			uploadPic.uploadSharedPicToWebServer(curTakenFilePath);

		}		
		
		
	};

	
	
	Handler handler = new Handler() {  
	    @Override  
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub  
	        // 要做的事情  
	        super.handleMessage(msg);  
	    }  
	};  
	private final Timer timer = new Timer();  

	private TimerTask scanForNewVoidByMan = new TimerTask() {  
	    @Override  
	    public void run() {  
	    	
	    	
			UploadPicture uploadPic = new UploadPicture();
			String jsonResultsStr = uploadPic.requestAudioFromWebServer();
			String audioUrl = uploadPic.parseAudioUrl(jsonResultsStr);

			if(audioUrl == null)
				return;
	    	
			//voiceUrl ="http://youreyes-10046811.file.myqcloud.com/video/2016-12-11/1481416448_4138_2016-12-10T13-10-56.398Z.wav";

			//indentify.downLoadVoiceFile(url,"voice.wav");	
			
			String path = "/storage/emulated/0/youreyes/test.wav";
			// String urlPath
			String urlPath ="https://nj01ct01.baidupcs.com/file/d7cba29c8b5e00c2551791bb0391e20e?bkt=p3-1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&fid=1594766314-250528-121317461015097&time=1481393698&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-UFQxDJuyh4QIioGRiyAxSTPbbtU%3D&to=njhb&fm=Yan,B,T,t&sta_dx=154594&sta_cs=4&sta_ft=wav&sta_ct=0&sta_mt=0&fm2=Yangquan,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&sl=75366479&expires=8h&rt=sh&r=619735071&mlogid=8003697961347456853&vuk=-&vbdid=2613263531&fin=test.wav&fn=test.wav&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=8003697961347456853&dp-callid=0.1.1&hps=1&csl=417&csign=AkupYmpPcvLlEGRgbIeScoM%2Bsns%3D";
			//String urlPath = "http://nj01ct01.baidupcs.com/file/c5b40faab0a7f41de26745058ecdc936?bkt=p3-1400c5b40faab0a7f41de26745058ecdc9366c43cfbb00000243632c&fid=1594766314-250528-257223894258961&time=1481393965&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-W9VYR7BtU%2FU0Gw6Q3Ppw8NeVMnw%3D&to=njhb&fm=Qin,B,T,t&sta_dx=37970732&sta_cs=&sta_ft=wav&sta_ct=0&sta_mt=0&fm2=Qingdao,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400c5b40faab0a7f41de26745058ecdc9366c43cfbb00000243632c&sl=76480588&expires=8h&rt=sh&r=251604616&mlogid=8003769679568098904&vuk=-&vbdid=3274269265&fin=xdl.wav&fn=xdl.wav&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=8003769679568098904&dp-callid=0.1.1&csl=592&csign=dT63CthyeT984ekutrJZj%2B5uZy8%3D";

			player = new MediaPlayer();
			try {
				player.setDataSource(audioUrl);
				
				player.prepare();
				player.start();
				
				timer.cancel();

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	        Message message = new Message();  
	        message.what = 1;  
	        handler.sendMessage(message);  
	    }  
	};   


	
	Runnable downLoadFile = new Runnable() {

		@Override
		public void run() {
			// TODO			
					
			PictureIndentify indentify = new PictureIndentify();

			String textUrl = "http://118.89.25.65/";
			//indentify.checkVoiceExist(textUrl);
			
			String url ="https://nj01ct01.baidupcs.com/file/d7cba29c8b5e00c2551791bb0391e20e?bkt=p3-1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&fid=1594766314-250528-121317461015097&time=1481393698&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-UFQxDJuyh4QIioGRiyAxSTPbbtU%3D&to=njhb&fm=Yan,B,T,t&sta_dx=154594&sta_cs=4&sta_ft=wav&sta_ct=0&sta_mt=0&fm2=Yangquan,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&sl=75366479&expires=8h&rt=sh&r=619735071&mlogid=8003697961347456853&vuk=-&vbdid=2613263531&fin=test.wav&fn=test.wav&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=8003697961347456853&dp-callid=0.1.1&hps=1&csl=417&csign=AkupYmpPcvLlEGRgbIeScoM%2Bsns%3D";

			//indentify.downLoadVoiceFile(url,"voice.wav");	
			
		}
	};


	Runnable startPlayVoice = new Runnable() {

		@Override
		public void run() {
			// TODO

			try {
				
				UploadPicture uploadPic = new UploadPicture();
				String jsonResultsStr = uploadPic.requestAudioFromWebServer();
				String audioUrl = uploadPic.parseAudioUrl(jsonResultsStr);

				if(audioUrl == null)
					return;
				String path = "/storage/emulated/0/youreyes/test.wav";
				// String urlPath
				// ="https://nj01ct01.baidupcs.com/file/d7cba29c8b5e00c2551791bb0391e20e?bkt=p3-1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&fid=1594766314-250528-121317461015097&time=1481393698&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-UFQxDJuyh4QIioGRiyAxSTPbbtU%3D&to=njhb&fm=Yan,B,T,t&sta_dx=154594&sta_cs=4&sta_ft=wav&sta_ct=0&sta_mt=0&fm2=Yangquan,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400d7cba29c8b5e00c2551791bb0391e20ebe6976c6000000025be2&sl=75366479&expires=8h&rt=sh&r=619735071&mlogid=8003697961347456853&vuk=-&vbdid=2613263531&fin=test.wav&fn=test.wav&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=8003697961347456853&dp-callid=0.1.1&hps=1&csl=417&csign=AkupYmpPcvLlEGRgbIeScoM%2Bsns%3D";
				String urlPath = "http://nj01ct01.baidupcs.com/file/c5b40faab0a7f41de26745058ecdc936?bkt=p3-1400c5b40faab0a7f41de26745058ecdc9366c43cfbb00000243632c&fid=1594766314-250528-257223894258961&time=1481393965&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-W9VYR7BtU%2FU0Gw6Q3Ppw8NeVMnw%3D&to=njhb&fm=Qin,B,T,t&sta_dx=37970732&sta_cs=&sta_ft=wav&sta_ct=0&sta_mt=0&fm2=Qingdao,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400c5b40faab0a7f41de26745058ecdc9366c43cfbb00000243632c&sl=76480588&expires=8h&rt=sh&r=251604616&mlogid=8003769679568098904&vuk=-&vbdid=3274269265&fin=xdl.wav&fn=xdl.wav&slt=pm&uta=0&rtype=1&iv=0&isw=0&dp-logid=8003769679568098904&dp-callid=0.1.1&csl=592&csign=dT63CthyeT984ekutrJZj%2B5uZy8%3D";
				
				player = new MediaPlayer();
				player.setDataSource(audioUrl);
				player.prepare();
				player.start();

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
	

	Runnable picIndentifyThread = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			UploadPicture uploadFile = new UploadPicture();
			String jsonIndentifyStr = uploadFile.uploadPicToWebServer(curTakenFilePath);	
			
			String finalResults = uploadFile.parse(jsonIndentifyStr);
			if(finalResults != null){
				TTSPlay("这是" + finalResults);					
			}else{
				
				TTSPlay("伦家也不知道哦");
			}		
		}
	};

	private ShutterCallback mShutterCb = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
		}

	};

	private PictureCallback capturedIt = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bitmap == null) {
				Toast.makeText(getApplicationContext(), "not taken",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// mCamera.stopPreview();

			try {
				Date d = new Date(System.currentTimeMillis());
				String picName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(d);
				curTakenFilePath = picTakenStoragePath + "/" + picName + ".jpg";
				FileOutputStream outStream = new FileOutputStream(curTakenFilePath);

				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 3, outStream)) {
					outStream.flush();
					outStream.close();

				}
				
				TTSPlay("识别中");

				new Thread(picIndentifyThread).start();

				// PictureIndentify indentify = new PictureIndentify();
				// indentify.sendFileToIndentify(picTakenStoragePath + "/" +
				// picName + ".jpg");
				// indentify.postFile();

				// mCamera.startPreview();

			} catch (FileNotFoundException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(),
					"图像识别中", Toast.LENGTH_SHORT).show();

		}
	};

	public void snapIt() {
		mCamera.takePicture(mShutterCb, null, capturedIt);
	}

	/**
	 * 滑屏监测
	 * 
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float minMove = 120; // 最小滑动距离
		float minVelocity = 0; // 最小滑动速度
		float beginX = e1.getX();
		float endX = e2.getX();
		float beginY = e1.getY();
		float endY = e2.getY();

		if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) { // 左滑
			//Toast.makeText(this, velocityX + "左滑", Toast.LENGTH_SHORT).show();
		} else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) { // 右滑
			// Toast.makeText(this,velocityX+"右滑",Toast.LENGTH_SHORT).show();
			new Thread(uploadSharedPic).start();
			
			//new Thread(startPlayVoice).start();			
			timer.schedule(scanForNewVoidByMan, 30000, 5000);  

			shareLinkToQQ();
			
		} else if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) { // 上滑
			//Toast.makeText(this, velocityX + "上滑", Toast.LENGTH_SHORT).show();
		} else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) { // 下滑
			//Toast.makeText(this, velocityX + "下滑", Toast.LENGTH_SHORT).show();
		}

		return false;
	}
	
	

	public void shareLinkToQQ() {
		String targetUrl = "http://118.89.25.65/speech.html";
		String tittle = "分享图片，来自你是我的眼";
		String summary = "帮我说说这张图片的内容~";
		String appName = "你是我的眼";

		final Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_TITLE, tittle);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);

		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, 1);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
		doShareToQQ(params);
		// initCamera();
	}

	private void doShareToQQ(final Bundle params) {
		// QQ分享要在主线程做
		ThreadManager.getMainHandler().post(new Runnable() {

			@Override
			public void run() {
				if (null != mTencent) {
					mTencent.shareToQQ(YourEyesActivity.this, params,
							qqShareListener);
				}
			}
		});
	}

	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			// Util.toastMessage(this, "onError: " + e.errorMessage, "e");
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_QQ_SHARE) {
			Tencent.onActivityResultData(requestCode, resultCode, data,
					qqShareListener);
			initCamera();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// �?出时释放连接
		mTts.destroy();

		mCamera.release();
		timer.cancel();

	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(YourEyesActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(YourEyesActivity.this);
		super.onPause();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

}
