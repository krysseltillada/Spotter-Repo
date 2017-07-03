package com.lmos.spotter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.Utilities.Utilities;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by emman on 6/12/2017.
 * This class will check if there's an
 * existing account in the SharedPreferences.
 * If an account exist, this class will start
 * HomeActivity class, otherwise, LoginActivity class.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String LOGIN_PREFS = "LoginSharedPreference";
    TextView splash_msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        splash_msg = (TextView) findViewById(R.id.splash_msg);

        if(Utilities.checkNetworkState(this))
            new GetPlaceNames().execute();
        else
            splash_msg.setText("You're not connected to the outside world.");

    }

    private void finishSplash(){

        SharedPreferences login_prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        String status = login_prefs.getString("status", null);
        if(status != null)
            startActivity(new Intent(this, HomeActivity.class));
        else
            startActivity(new Intent(this, LoginActivity.class));

        finish();

    }

    public class GetPlaceNames extends AsyncTask<Void, String, Void>{

        @Override
        protected void onProgressUpdate(String... values) {
            splash_msg.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            File database = getApplicationContext().getDatabasePath("Spotter");
            DbHelper dbHelper = new DbHelper(getApplicationContext());


            if(!database.exists()){
                publishProgress("We're setting things up for you, please wait a moment.");
                AppScript appScript = new AppScript(){{
                   setData("get-all-place-name.php", null);
                }};

                String result = appScript.getResult();
                if(result != null && result.equals("Data loaded."))
                    publishProgress("Almost done...");
                    dbHelper.savePlaceName(appScript.getPlaceNames());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finishSplash();
        }
    }

}
