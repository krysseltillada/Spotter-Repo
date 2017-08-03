package com.lmos.spotter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.Utilities;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.io.File;
import java.util.HashMap;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by emman on 6/12/2017.
 * This class will check if there's an
 * existing account in the SharedPreferences.
 * If an account exist, this class will start
 * HomeActivity class, otherwise, LoginActivity class.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String LOGIN_PREFS = "LoginSharedPreference";
    final int SPLASH_REQUEST = 1902;
    TextView splash_msg;

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                                                         getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();

        Twitter.initialize(config);

        setContentView(R.layout.splash_screen);

        final Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null) {

                    if (branchUniversalObject.getMetadata().containsKey("placeName")) {

                        Log.d("debug", "link clicked: " + branchUniversalObject.convertToJson().toString());

                        HashMap<String, String> linkMetaData = branchUniversalObject.getMetadata();

                        Intent send_data = new Intent(SplashScreen.this, SearchResultsActivity.class);
                        send_data.putExtra("data", new String[] {

                                linkMetaData.get("placeType"),
                                linkMetaData.get("placeName"),
                                linkMetaData.get("placeAddress"),
                                linkMetaData.get("placeLat"),
                                linkMetaData.get("placeLng")

                        });

                        SplashScreen.this.startActivity(send_data);
                        finish();

                    } else {
                        Log.d("debug", "onInitFinished");
                    }


                } else {
                    branch.initSession(this);
                    Log.i("debug", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SPLASH_REQUEST && resultCode == RESULT_OK)
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        else
            finish();

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
}
