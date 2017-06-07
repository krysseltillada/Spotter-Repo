package com.lmos.spotter.MainInterface.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import com.lmos.spotter.R;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        Preference clearFavoritesItem = (Preference)findPreference("clearFavorites");
        clearFavoritesItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                                                            @Override
                                                            public boolean onPreferenceClick(Preference preference) {
                                                                return false;
                                                            }
                                                        }
        );


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}