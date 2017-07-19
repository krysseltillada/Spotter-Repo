package com.lmos.spotter.MainInterface.Activities;

import android.accounts.Account;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.UserAccount;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends PreferenceActivity  {

    SharedPreferences userData;
    String message;
    String typeInfo;

    UserAccount modifiedAccount = new UserAccount();

    EditTextPreference userEditText;
    EditTextPreference emailEditText;
    EditTextPreference passwordEditText;
    EditTextPreference nameEditText;

    RequestQueue requestUpdateAccountInfo;

    boolean isReInitInfo = false;

    private void updateUserPreferences () {

        SharedPreferences.Editor editUserData = userData.edit();

        editUserData.putString("accountUsername", modifiedAccount.userName);
        editUserData.putString("accountEmail", modifiedAccount.email);
        editUserData.putString("accountName", modifiedAccount.name);
        editUserData.putString("accountPassword", modifiedAccount.password);

        editUserData.apply();

    }


    SharedPreferences.OnSharedPreferenceChangeListener userChangePreference = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (!isReInitInfo) {

                Preference preference = findPreference(key);

                modifiedAccount.userName = sharedPreferences.getString("username", "");
                modifiedAccount.email = sharedPreferences.getString("email", "");
                modifiedAccount.name = sharedPreferences.getString("name", "");
                modifiedAccount.password = sharedPreferences.getString("password", "");

                Log.d("debug", userData.getString("accountID", ""));

                Log.d("debug", userData.getString("accountUsername", ""));

                Log.d("debug", userData.getString("accountName", ""));

                Log.d("debug", userData.getString("accountPassword", ""));

                Log.d("debug", userData.getString("accountEmail", ""));


                preference.setSummary(((key.equals("password")) ? sharedPreferences.getString(key, "")
                        .replaceAll(".", "*") : sharedPreferences.getString(key, "")));

                typeInfo = key;


                message = (key.equals("username")) ? "username changed" :
                        (key.equals("email")) ? "email change" : (key.equals("name")) ? "name changed" : "password changed";


                updateAccountInfo();

            } else {
                isReInitInfo = false;
            }

        }
    };

    private void initUserInfo () {

        String username = userData.getString("accountUsername", "");
        String accountEmail = userData.getString("accountEmail", "");
        String accountName = userData.getString("accountName", "");
        String accountPassword = userData.getString("accountPassword", "")
                .replaceAll(".", "*");


        Preference userName = findPreference("username");
        Preference email = findPreference("email");
        Preference password = findPreference("password");
        Preference name = findPreference("name");


        userEditText = (EditTextPreference) userName;
        emailEditText = (EditTextPreference) email;
        passwordEditText = (EditTextPreference) password;
        nameEditText = (EditTextPreference) name;

        userEditText.setText(username);
        emailEditText.setText(accountEmail);
        passwordEditText.setText(accountPassword);
        nameEditText.setText(accountName);

        userName.setSummary(username);
        email.setSummary(accountEmail);
        password.setSummary(accountPassword);
        name.setSummary(accountName);

    }


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        if (userData.getString("status", "").equals("Logged In")) {

            addPreferencesFromResource(R.xml.settings_user);


            initUserInfo();

            Preference clearFavoritesItem = findPreference("clearBookmarks");

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(userChangePreference);

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

    @Override
    protected void onPause() {
        super.onPause();
    }



    private void updateAccountInfo () {

        final ProgressDialog updateProgressDialog = new ProgressDialog(this);
        updateProgressDialog.setIndeterminate(true);
        updateProgressDialog.setMessage("updating your account");
        updateProgressDialog.setCancelable(false);

        if (!isFinishing()) {
            updateProgressDialog.show();
        }

        requestUpdateAccountInfo = Volley.newRequestQueue(getApplicationContext());

        StringRequest updateAccount = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/updateUserAccount.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("account updated")) {
                            updateProgressDialog.dismiss();
                            SettingsActivity.this.updateUserPreferences();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            updateProgressDialog.dismiss();
                            isReInitInfo = true;
                            initUserInfo();
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    updateProgressDialog.dismiss();

                    Log.d("debug", error.getMessage());

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.d("debug", "Error. HTTP Status Code:"+networkResponse.statusCode);
                    }

                    if (error instanceof TimeoutError) {
                        Log.d("debug", "TimeoutError");
                    }else if(error instanceof NoConnectionError){
                        Log.d("debug", "NoConnectionError");
                    } else if (error instanceof AuthFailureError) {
                        Log.d("debug", "AuthFailureError");
                    } else if (error instanceof ServerError) {
                        Log.d("debug", "ServerError");
                    } else if (error instanceof NetworkError) {
                        Log.d("debug", "NetworkError");
                    } else if (error instanceof ParseError) {
                        Log.d("debug", "ParseError");
                    }


                    Toast.makeText(getApplicationContext(), "please check your connection", Toast.LENGTH_SHORT).show();
                }
        }){

            protected Map<String, String> getParams () {
                return new HashMap<String, String>(){{


                   put("accountID", userData.getString("accountID", ""));
                   put("userName", modifiedAccount.userName);
                   put("name", modifiedAccount.name);
                   put("email", modifiedAccount.email);
                   put("password", modifiedAccount.password);
                   put("typeInfo", typeInfo);

                    /*
                    put("accountID", "201707050318202546");
                    put("userName", "judyando");
                    put("name", "finalexistence");
                    put("email", "judyando@gmail.com");
                    put("password", "kryssel2821"); */


                }};
            }

        };

        requestUpdateAccountInfo.add(updateAccount);

    }


}
