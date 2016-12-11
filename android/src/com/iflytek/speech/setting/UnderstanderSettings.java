package com.iflytek.speech.setting;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.iflytek.speech.util.SettingTextWatcher;
import com.iflytek.sunflower.FlowerCollector;
import com.youreye.texttospeech.R;

/**
 * 璇箟鐞嗚В璁剧疆鐣岄潰
 */
public class UnderstanderSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	private static final String TAG = UnderstanderSettings.class.getSimpleName();
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private EditTextPreference mVadbosPreference;
	private EditTextPreference mVadeosPreference;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.understand_setting);
		
		mVadbosPreference = (EditTextPreference)findPreference("understander_vadbos_preference");
		mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadbosPreference,0,10000));
		
		mVadeosPreference = (EditTextPreference)findPreference("understander_vadeos_preference");
		mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadeosPreference,0,10000));
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
	
	@Override
	protected void onResume() {
		// 寮�鏀剧粺璁� 绉诲姩鏁版嵁缁熻鍒嗘瀽
		FlowerCollector.onResume(UnderstanderSettings.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// 寮�鏀剧粺璁� 绉诲姩鏁版嵁缁熻鍒嗘瀽
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(UnderstanderSettings.this);
		super.onPause();
	}
}

