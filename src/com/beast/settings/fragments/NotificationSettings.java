package com.beast.settings.fragments;

import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import com.beast.settings.preferences.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.beast.settings.preferences.SystemSettingSwitchPreference;
import com.beast.settings.preferences.CustomSeekBarPreference;

import com.android.internal.logging.nano.MetricsProto;

public class NotificationSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private ListPreference mFlashlightOnCall;
    private Preference mChargingLeds;
	private ListPreference mTickerAnimationMode;
	private CustomSeekBarPreference mTickerAnimationDuration;

     @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

         addPreferencesFromResource(R.xml.beast_settings_notifications);

         PreferenceScreen prefScreen = getPreferenceScreen();
		final ContentResolver resolver = getActivity().getContentResolver();		 
		 
        mChargingLeds = (Preference) findPreference("charging_light");
         if (mChargingLeds != null
                 && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }

      mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 1);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);

         if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(FlashOnCall);
        }
		
        mTickerAnimationMode = (ListPreference) findPreference("status_bar_ticker_animation_mode");
        mTickerAnimationMode.setOnPreferenceChangeListener(this);
        int tickerAnimationMode = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_TICKER_ANIMATION_MODE, 0);
        mTickerAnimationMode.setValue(String.valueOf(tickerAnimationMode));
        updateTickerAnimationModeSummary(tickerAnimationMode);

	mTickerAnimationDuration = (CustomSeekBarPreference) findPreference("status_bar_ticker_tick_duration");
        int tickerAnimationDuration = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_TICKER_TICK_DURATION, 3000, UserHandle.USER_CURRENT);
        mTickerAnimationDuration.setValue(tickerAnimationDuration);
        mTickerAnimationDuration.setOnPreferenceChangeListener(this);

    }

    private void updateTickerAnimationModeSummary(int value) {
        Resources res = getResources();
         if (value == 0) {
            // Fade
             mTickerAnimationMode.setSummary(res.getString(R.string.ticker_animation_mode_alpha_fade));
        } else if (value == 1) {
            // Scroll
            mTickerAnimationMode.setSummary(res.getString(R.string.ticker_animation_mode_scroll));
        }
    }

     public boolean onPreferenceChange(Preference preference, Object newValue) {
         ContentResolver resolver = getActivity().getContentResolver();

             if (preference == mFlashlightOnCall) {
               int flashlightValue = Integer.parseInt(((String) newValue).toString());
               Settings.System.putInt(resolver,
                     Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
               mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
               mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
               return true;
        } else if (preference == mTickerAnimationMode) {
            int tickerAnimationMode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
		     Settings.System.STATUS_BAR_TICKER_ANIMATION_MODE, tickerAnimationMode);
            updateTickerAnimationModeSummary(tickerAnimationMode);
            return true;
        } else if (preference == mTickerAnimationDuration) {
            int tickerAnimationDuration = (Integer) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_TICKER_TICK_DURATION, tickerAnimationDuration,
                    UserHandle.USER_CURRENT);
            return true;
	}
        return false;
    }

     @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST;
    }
}
