package com.lmos.spotter.MainInterface.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    SharedPreferences userData;
    String message;


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        if (userData.getString("status", "").equals("Logged In")) {

            addPreferencesFromResource(R.xml.settings_user);

            userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

            String username = userData.getString("accountUsername", "");
            String accountEmail = userData.getString("accountEmail", "");
            String accountName = userData.getString("accountName", "");
            String accountPassword = userData.getString("accountPassword", "")
                    .replaceAll(".", "*");

            userData.registerOnSharedPreferenceChangeListener(this);

            Preference userName = findPreference("username");
            Preference email = findPreference("email");
            Preference password = findPreference("password");
            Preference name = findPreference("name");
            Preference clearFavoritesItem = findPreference("clearBookmarks");

            ((EditTextPreference) userName).setText(username);
            ((EditTextPreference) email).setText(accountEmail);
            ((EditTextPreference) password).setText(accountPassword);
            ((EditTextPreference) name).setText(accountName);

            userName.setSummary(username);
            email.setSummary(accountEmail);
            password.setSummary(accountPassword);
            name.setSummary(accountName);

            clearFavoritesItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Toast.makeText(getApplicationContext(), "Bookmarks erased", Toast.LENGTH_SHORT).show();

                    return false;
                }
            });

        } else {

            addPreferencesFromResource(R.xml.settings_guest);

            Preference signInPreference = findPreference("signIn");

            signInPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent launchLogin = new Intent(SettingsActivity.this, LoginActivity.class);
                    SettingsActivity.this.startActivity(launchLogin);

                    return false;
                }
            });

        }

    }

    private void updateAccountInfo () {

        final ProgressDialog updateProgressDialog = new ProgressDialog(getApplicationContext());

        updateProgressDialog.setIndeterminate(true);
        updateProgressDialog.setTitle("updating");
        updateProgressDialog.setMessage("updating your account");
        updateProgressDialog.setCancelable(false);

        updateProgressDialog.show();

        RequestQueue requestUpdateAccountInfo = Volley.newRequestQueue(getApplicationContext());

        StringRequest updateAccount = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/updateUserAccount.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("account updated")) {
                            updateProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    updateProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "please check your connection", Toast.LENGTH_SHORT).show();
                }
        }){

            protected Map<String, String> getParams () {
                return new HashMap<String, String>(){{
                   put("accountID", userData.getString("accountID", ""));
                   put("userName", userData.getString("accountUsername", ""));
                   put("name", userData.getString("accountName", ""));
                   put("email", userData.getString("accountEmail", ""));
                   put("password", userData.getString("accountPassword", ""));
                }};
            }

        };

        requestUpdateAccountInfo.add(updateAccount);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("debug", "change");

        Preference preference = findPreference(key);


        preference.setSummary( ((key.equals("password")) ? sharedPreferences.getString(key, "")
                                        .replaceAll(".", "*") : sharedPreferences.getString(key, "")));


        message = (key.equals("username")) ? "username changed" :
                    (key.equals("email")) ? "email change" : "password changed";

        updateAccountInfo();

    }

}
