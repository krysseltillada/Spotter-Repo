package com.lmos.spotter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;

/**
 * Created by emman on 6/12/2017.
 * This class will check if there's an
 * existing account in the SharedPreferences.
 * If an account exist, this class will start
 * HomeActivity class, otherwise, LoginActivity class.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String LOGIN_PREFS = "LoginSharedPreference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences login_prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        String acc_status = login_prefs.getString("status", "false");
        if(acc_status.equals("false")){
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            startActivity(new Intent(this, HomeActivity.class));
        }

    }

}
