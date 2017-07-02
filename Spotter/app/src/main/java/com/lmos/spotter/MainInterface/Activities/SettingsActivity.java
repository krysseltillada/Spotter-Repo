package com.lmos.spotter.MainInterface.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private String prevUserName;
    private String prevEmail;
    private String prevPass;
    private String prevName;


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference userName = findPreference("username");
        Preference email = findPreference("email");
        Preference password = findPreference("password");
        Preference name = findPreference("name");
        Preference clearFavoritesItem = findPreference("clearBookmarks");

        userName.setSummary(sharedPreferences.getString("username", ""));
        email.setSummary(sharedPreferences.getString("email", ""));
        password.setSummary(sharedPreferences.getString("password", ""));
        name.setSummary(sharedPreferences.getString("name", ""));

        password.setSummary(sharedPreferences.getString("password", "")
                                                 .replaceAll(".", "*"));

        clearFavoritesItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Toast.makeText(getApplicationContext(), "Bookmarks erased", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);

        if (!key.equals("notifyGPS")) {

            if (key.equals("email")) {

            }

            preference.setSummary( ((key.equals("password")) ? sharedPreferences.getString(key, "")
                                        .replaceAll(".", "*") : sharedPreferences.getString(key, "")));


            String message = (key.equals("username")) ? "username changed" :
                    (key.equals("email")) ? "email change" : "password changed";

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        }

    }

}
