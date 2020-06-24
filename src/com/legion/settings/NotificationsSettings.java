/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.legion.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.legion.settings.preference.SystemSettingMasterSwitchPreference;

public class NotificationsSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "NotificationsSettings";
    private static final String LIGHTS_CATEGORY = "notification_lights";
    private static final String BATTERY_LIGHT_ENABLED = "battery_light_enabled";

    private PreferenceCategory mLightsCategory;
    private SystemSettingMasterSwitchPreference mBatteryLightEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        mBatteryLightEnabled = (SystemSettingMasterSwitchPreference) findPreference(BATTERY_LIGHT_ENABLED);
        mBatteryLightEnabled.setOnPreferenceChangeListener(this);
        int batteryLightEnabled = Settings.System.getInt(getContentResolver(),
                BATTERY_LIGHT_ENABLED, 1);
        mBatteryLightEnabled.setChecked(batteryLightEnabled != 0);

        mLightsCategory = (PreferenceCategory) findPreference(LIGHTS_CATEGORY);
        if (!getResources().getBoolean(com.android.internal.R.bool.config_hasNotificationLed)) {
            getPreferenceScreen().removePreference(mLightsCategory);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mBatteryLightEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            BATTERY_LIGHT_ENABLED, value ? 1 : 0);
            return true;
	}
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.LEGION_SETTINGS;
    }
}
