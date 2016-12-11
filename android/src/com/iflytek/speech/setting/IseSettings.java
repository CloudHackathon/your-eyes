/**
 * 
 */
package com.iflytek.speech.setting;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.youreye.texttospeech.R;

/**
 * 璇勬祴璁剧疆鐣岄潰
 */
public class IseSettings extends PreferenceActivity {
	private final static String PREFER_NAME = "ise_settings";
	
	private ListPreference mLanguagePref;
	private ListPreference mCategoryPref;
	private ListPreference mResultLevelPref;
	private EditTextPreference mVadBosPref;
	private EditTextPreference mVadEosPref;
	private EditTextPreference mSpeechTimeoutPref;
	
	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.ise_settings);
		
		initUI();
	}

	private void initUI() {
		mLanguagePref = (ListPreference) findPreference(SpeechConstant.LANGUAGE);
		mCategoryPref = (ListPreference) findPreference(SpeechConstant.ISE_CATEGORY);
		mResultLevelPref = (ListPreference) findPreference(SpeechConstant.RESULT_LEVEL);
		mVadBosPref = (EditTextPreference) findPreference(SpeechConstant.VAD_BOS);
		mVadEosPref = (EditTextPreference) findPreference(SpeechConstant.VAD_EOS);
		mSpeechTimeoutPref = (EditTextPreference) findPreference(SpeechConstant.KEY_SPEECH_TIMEOUT);

		mToast = Toast.makeText(IseSettings.this, "", Toast.LENGTH_LONG);
		
		mLanguagePref.setSummary("褰撳墠锛�" + mLanguagePref.getEntry());
		mCategoryPref.setSummary("褰撳墠锛�" + mCategoryPref.getEntry());
		mResultLevelPref.setSummary("褰撳墠锛�" + mResultLevelPref.getEntry());
		mVadBosPref.setSummary("褰撳墠锛�" + mVadBosPref.getText() + "ms");
		mVadEosPref.setSummary("褰撳墠锛�" + mVadEosPref.getText() + "ms");
		
		String speech_timeout = mSpeechTimeoutPref.getText();
		String summary = "褰撳墠锛�" + speech_timeout;
		if (!"-1".equals(speech_timeout)) {
			summary += "ms";
		}
		mSpeechTimeoutPref.setSummary(summary);
		
		mLanguagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ("zh_cn".equals(newValue.toString())) {
					if ("plain".equals(mResultLevelPref.getValue())) {
						showTip("姹夎璇勬祴缁撴灉鏍煎紡涓嶆敮鎸乸lain璁剧疆");
						return false;
					}
				} else {
					if ("read_syllable".equals(mCategoryPref.getValue())) {
						showTip("鑻辫璇勬祴涓嶆敮鎸佸崟瀛�");
						return false;
					}
				}
				
				int newValueIndex = mLanguagePref.findIndexOfValue(newValue.toString());
				String newEntry = (String) mLanguagePref.getEntries()[newValueIndex];
				mLanguagePref.setSummary("褰撳墠锛�" + newEntry);
				return true;
			}
		});
		
		mCategoryPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ("en_us".equals(mLanguagePref.getValue()) && "read_syllable".equals(newValue.toString())) {
					showTip("鑻辫璇勬祴涓嶆敮鎸佸崟瀛楋紝璇烽�夊叾浠栭」");
					return false;
				}
				
				int newValueIndex = mCategoryPref.findIndexOfValue(newValue.toString());
				String newEntry = (String) mCategoryPref.getEntries()[newValueIndex];
				mCategoryPref.setSummary("褰撳墠锛�" + newEntry);
				return true;
			}
		});
		
		mResultLevelPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ("zh_cn".equals(mLanguagePref.getValue()) && "plain".equals(newValue.toString())) {
					showTip("姹夎璇勬祴涓嶆敮鎸乸lain锛岃閫夊叾浠栭」");
					return false;
				}
				
				mResultLevelPref.setSummary("褰撳墠锛�" + newValue.toString());
				return true;
			}
		});
		
		mVadBosPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		mVadBosPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int bos;
				try {
					bos = Integer.parseInt(newValue.toString());
				} catch (Exception e) {
					showTip("鏃犳晥杈撳叆锛�");
					return false;
				}
				if (bos < 0 || bos > 30000) {
					showTip("鍙栧�艰寖鍥翠负0~30000");
					return false;
				}
				
				mVadBosPref.setSummary("褰撳墠锛�" + bos + "ms");
				return true;
			}
		});
		
		mVadEosPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		mVadEosPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int eos;
				try {
					eos = Integer.parseInt(newValue.toString());
				} catch (Exception e) {
					showTip("鏃犳晥杈撳叆锛�");
					return false;
				}
				if (eos < 0 || eos > 30000) {
					showTip("鍙栧�艰寖鍥翠负0~30000");
					return false;
				}
				
				mVadEosPref.setSummary("褰撳墠锛�" + eos + "ms");
				return true;
			}
		});
		
		mSpeechTimeoutPref.getEditText().setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_CLASS_NUMBER);
		mSpeechTimeoutPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int speech_timeout;
				try {
					speech_timeout = Integer.parseInt(newValue.toString());
				} catch (Exception e) {
					showTip("鏃犳晥杈撳叆锛�");
					return false;
				}
				 
				if (speech_timeout < -1) {
					showTip("蹇呴』澶т簬绛変簬-1");
					return false;
				}
				
				if (speech_timeout == -1) {
					mSpeechTimeoutPref.setSummary("褰撳墠锛�-1");
				} else {
					mSpeechTimeoutPref.setSummary("褰撳墠锛�" + speech_timeout + "ms");
				}
				
				return true;
			}
		});
	}
	
	private void showTip(String str) {
		if(!TextUtils.isEmpty(str)) {
			mToast.setText(str);
			mToast.show();
		}
	}
}
