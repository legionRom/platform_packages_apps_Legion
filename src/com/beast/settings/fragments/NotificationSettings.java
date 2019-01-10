package com.beast.settings.fragments;
import com.android.internal.logging.nano.MetricsProto;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import com.beast.settings.preferences.Utils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.content.Context;
import android.content.ContentResolver;
import android.os.UserHandle;
import android.provider.Settings;

 public class NotificationSettings extends SettingsPreferenceFragment
                         implements OnPreferenceChangeListener {

    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private ListPreference mFlashlightOnCall;
    private Preference mChargingLeds;

     @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

         addPreferencesFromResource(R.xml.beast_settings_notifications);

         PreferenceScreen prefScreen = getPreferenceScreen();
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
            }
        return false;
    }

     @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST;
    }
}
