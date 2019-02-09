/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.beast.settings.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.beast.settings.preferences.Utils;
import com.beast.settings.preferences.CustomSeekBarPreference;

import com.android.internal.logging.nano.MetricsProto;

 public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_FACE_AUTO_UNLOCK = "face_auto_unlock";
    private static final String KEY_FACE_UNLOCK_PACKAGE = "com.android.facelock";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String WEATHER_UNIT = "weather_lockscreen_unit";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
	private static final String LOCK_DATE_FONTS = "lock_date_fonts";
	private static final String CLOCK_FONT_SIZE = "lockclock_font_size";
    private static final String DATE_FONT_SIZE = "lockdate_font_size";

    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintVib;
    private SwitchPreference mFaceUnlock;
	
    ListPreference mWeatherUnit;
    ListPreference mLockClockFonts;
	ListPreference mLockDateFonts;
	private CustomSeekBarPreference mClockFontSize;
    private CustomSeekBarPreference mDateFontSize;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.beast_settings_lockscreen);
         ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        mFaceUnlock = (SwitchPreference) findPreference(KEY_FACE_AUTO_UNLOCK);
        if (!Utils.isPackageInstalled(getActivity(), KEY_FACE_UNLOCK_PACKAGE)){
            prefScreen.removePreference(mFaceUnlock);
        } else {
            mFaceUnlock.setChecked((Settings.Secure.getInt(getContext().getContentResolver(),
                    Settings.Secure.FACE_AUTO_UNLOCK, 0) == 1));
            mFaceUnlock.setOnPreferenceChangeListener(this);
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (!mFingerprintManager.isHardwareDetected()){
            prefScreen.removePreference(mFingerprintVib);
        } else {
        mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
        mFingerprintVib.setOnPreferenceChangeListener(this);
        }
        mWeatherUnit = (ListPreference) findPreference(WEATHER_UNIT);
        mWeatherUnit.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.WEATHER_LOCKSCREEN_UNIT, 0)));
        mWeatherUnit.setSummary(mWeatherUnit.getEntry());
        mWeatherUnit.setOnPreferenceChangeListener(this);

        // Lockscren Clock Fonts
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 0)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);
		
	// Lockscren Date Fonts
        mLockDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mLockDateFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_DATE_FONTS, 0)));
        mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        mLockDateFonts.setOnPreferenceChangeListener(this);
		
 	// Lock Clock Size
        mClockFontSize = (CustomSeekBarPreference) findPreference(CLOCK_FONT_SIZE);
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKCLOCK_FONT_SIZE, 64));
        mClockFontSize.setOnPreferenceChangeListener(this);

        // Lock Date Size
        mDateFontSize = (CustomSeekBarPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKDATE_FONT_SIZE,16));
        mDateFontSize.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mFaceUnlock) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.FACE_AUTO_UNLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mWeatherUnit) {
            Settings.System.putInt(getContentResolver(), Settings.System.WEATHER_LOCKSCREEN_UNIT,
                    Integer.valueOf((String) newValue));
            mWeatherUnit.setValue(String.valueOf(newValue));
            mWeatherUnit.setSummary(mWeatherUnit.getEntry());
            return true;
        } else if (preference == mLockClockFonts) {
            		Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                    	Integer.valueOf((String) newValue));
            		mLockClockFonts.setValue(String.valueOf(newValue));
            		mLockClockFonts.setSummary(mLockClockFonts.getEntry());
	        return true; 
	}       else if (preference == mLockDateFonts) {
			Settings.System.putInt(getContentResolver(), Settings.System.LOCK_DATE_FONTS,
			Integer.valueOf((String) newValue));
			mLockDateFonts.setValue(String.valueOf(newValue));
			mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        		return true;
	}      else if (preference == mClockFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKCLOCK_FONT_SIZE, top*1);
			return true;
        }      else if (preference == mDateFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKDATE_FONT_SIZE, top*1);
			return true;
        }
        return false;
    }
     @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST;
    }

}
