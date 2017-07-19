package com.lmos.spotter;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.Utilities.Utilities;

import java.io.File;

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
    final int SPLASH_REQUEST = 1902;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /*

        File getPhoneDirectory = getFilesDir();

        File createAppDirectory = new File (getPhoneDirectory.getAbsoluteFile() + "/BookmarkImages");

        if (!createAppDirectory.exists())
            Log.d("debug", "created directory: " + createAppDirectory.getAbsolutePath() + " " + createAppDirectory.mkdir());
        else
            Log.d("debug", createAppDirectory.getAbsolutePath() + " already exists"); */

        splash_msg = (TextView) findViewById(R.id.splash_msg);

        new GetPlaceNames(this).execute();

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

    @Override
    protected void onResume() {
        super.onResume();
        if(Utilities.checkNetworkState(this))
            new GetPlaceNames(this).execute();
    }

    class GetPlaceNames extends AsyncTask<Void, String, Void>{

        Activity activity;

        public GetPlaceNames(Activity activity){
            this.activity = activity;
        }

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

                if(Utilities.checkNetworkState(activity)){

                    publishProgress("We're setting things up for you, please wait a moment.");
                    AppScript appScript = new AppScript(activity){{
                        setData("get-all-place-name.php", null);
                    }};

                    String result = appScript.getResult();
                    if(result != null && result.equals("Data loaded."))
                        publishProgress("Almost done...");

                    dbHelper.savePlaceName(appScript.getPlaceNames());
                    Log.d("Splash", String.valueOf(appScript.getPlaceNames()));

                }
                else{
                    Utilities.showDialogActivity(activity, SPLASH_REQUEST, R.string.loading_msg_5);
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finishSplash();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SPLASH_REQUEST && resultCode == RESULT_OK)
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        else
            finish();

    }
}
