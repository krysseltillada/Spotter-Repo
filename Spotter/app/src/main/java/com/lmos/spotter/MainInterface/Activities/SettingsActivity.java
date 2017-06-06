package com.lmos.spotter.MainInterface.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.lmos.spotter.R;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PreferenceManager.getDefaultSharedPreferences(this)
                         .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        
    }
}
