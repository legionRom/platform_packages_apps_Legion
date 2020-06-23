/*
 * Copyright (C) 2019 legionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.legion.settings.preference.CustomSeekBarPreference;
import com.legion.settings.preference.SystemSettingSwitchPreference;

import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.List;

public class StatusbarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener, Indexable {

    private static final String SHOW_LTE_FOURGEE = "show_lte_fourgee";
    private static final String BATTERY_BAR = "statusbar_battery_bar";

    private SwitchPreference mShowLteFourGee;
    private SystemSettingMasterSwitchPreference mBatteryBar;

    private boolean mIsBarSwitchingMode = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.statusbar_settings);
        PreferenceScreen prefSet = getPreferenceScreen();

        mHandler = new Handler();

        mShowLteFourGee = (SwitchPreference) findPreference(SHOW_LTE_FOURGEE);
        mShowLteFourGee.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SHOW_LTE_FOURGEE, 0) == 1));
        mShowLteFourGee.setOnPreferenceChangeListener(this);

        mBatteryBar = (SystemSettingMasterSwitchPreference) findPreference(BATTERY_BAR);
        mBatteryBar.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1));
        mBatteryBar.setOnPreferenceChangeListener(this);
    }

   @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.LEGION_SETTINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

	if (preference == mShowLteFourGee) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_LTE_FOURGEE, value ? 1 : 0);
            return true;
	} else if (preference == mBatteryBar) {
            if (mIsBarSwitchingMode) {
                return false;
            }
            mIsBarSwitchingMode = true;
            boolean showing = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mBatteryBar.setChecked(showing);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsBarSwitchingMode = false;
                }
            }, 1500);
            return true;
        }
        return false;
    }


    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.statusbar_settings;
                    result.add(sir);
                    return result;
                }
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };
}
