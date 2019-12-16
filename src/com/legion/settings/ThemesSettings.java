package com.legion.settings;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

public class ThemesSettings extends SettingsPreferenceFragment {

    private static final String CUSTOM_THEME_BROWSE = "theme_select_activity";
    private Preference mThemeBrowse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	addPreferencesFromResource(R.xml.themes_settings);
	mThemeBrowse = findPreference(CUSTOM_THEME_BROWSE);
        mThemeBrowse.setEnabled(isBrowseThemesAvailable());

    }

    private boolean isBrowseThemesAvailable() {
        PackageManager pm = getPackageManager();
	Intent browse = new Intent();
	browse.setClassName("com.android.customization", "com.android.customization.picker.CustomizationPickerActivity");
        return pm.resolveActivity(browse, 0) != null;

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.LEGION_SETTINGS;
    }

}
