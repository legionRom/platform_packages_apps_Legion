package com.legion.settings;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

public class ThemesSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.themes_settings);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.LEGION_SETTINGS;
    }

}
