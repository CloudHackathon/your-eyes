package com.youreye.tts.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
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
import com.youreye.texttospeech.R;

public class TtsActivity extends Activity implements OnClickListener {
	private static String TAG = TtsActivity.class.getSimpleName();
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
	// 语记安装助手�?
	ApkInstaller mInstaller;

	private Toast mToast;
	private SharedPreferences mSharedPreferences;

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ttsdemo);

		SpeechUtility.createUtility(TtsActivity.this, "appid="
				+ getString(R.string.app_id));
		initLayout();
		// 初始化合成对�?
		mTts = SpeechSynthesizer.createSynthesizer(TtsActivity.this,
				mTtsInitListener);

		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME,
				MODE_PRIVATE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mInstaller = new ApkInstaller(TtsActivity.this);
	}

	/**
	 * 初始化Layout�?
	 */
	private void initLayout() {
		findViewById(R.id.tts_play).setOnClickListener(TtsActivity.this);
		}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// 收到onCompleted 回调时，合成结束、生成合成音�?
		// 合成的音频格式：只支持pcm格式
		case R.id.tts_play:
			// 移动数据分析，收集开始合成事
			TTSPlay();
			break;		
		default:
			break;
			
		}
	}
	
	private void TTSPlay(){
		
		// 移动数据分析，收集开始合成事�?
		FlowerCollector.onEvent(TtsActivity.this, "tts_play");

		String text = ((EditText) findViewById(R.id.tts_text)).getText()
				.toString();
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
				showTip("语音合成失败,错误�?: " + code);
			}
		}
	}

	/**
	 * 初始化监听�??
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
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
			showTip("�?始播�?");
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				//showTip("播放完成");
			} else if (error != null) {
				//showTip(error.getPlainDescription(true));
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

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

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
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// �?出时释放连接
		mTts.destroy();
	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(TtsActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(TtsActivity.this);
		super.onPause();
	}

}
