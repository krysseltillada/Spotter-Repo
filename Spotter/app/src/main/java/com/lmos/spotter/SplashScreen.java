package com.lmos.spotter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    ViewPager slidePager;
    int slideLayouts[];

    int slideStatusBarColors[];

    ImageView prevButton;
    ImageView nextButton;

    ImageView loadingSplashImage;
    ImageView logoImage;

    LinearLayout layoutDots;

    TextView skipButton;

    TextView[] dots;

    boolean isGettingData = false;

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        LoginActivity.login_prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);
        LoginActivity.set_login_prefs = LoginActivity.login_prefs.edit();

        initComp();

        if (!isGettingData)
            new GetPlaceNames(SplashScreen.this).execute();

        Log.d("debug", "first: " + LoginActivity.login_prefs.getBoolean("isFirstInstalled", true));

    }

    private void initComp() {

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();

        Twitter.initialize(config);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        loadingSplashImage = (ImageView) findViewById(R.id.loadingSplashImage);
        logoImage = (ImageView) findViewById(R.id.logoImage);

        slideStatusBarColors = new int[] {
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.cardview_dark_background,
                R.color.sprayBlue
        };

        slideLayouts = new int[] {
                R.layout.intro_slide_1,
                R.layout.intro_slide_2,
                R.layout.intro_slide_3,
                R.layout.intro_slide_4
        };

        layoutDots = (LinearLayout)findViewById(R.id.layoutDots);

        slidePager = (ViewPager)findViewById(R.id.introViewPagerSlider);

        slidePager.setAdapter(new IntroSliderPageAdapter());
        slidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.d("debug", "position: " + position);


                if (position >= 0 && position < slideLayouts.length - 1) {
                    if (!skipButton.getText().equals("Skip")) {
                        skipButton.setTextSize(20);
                        skipButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                        skipButton.setText("Skip");
                    }
                } else {
                    if (!skipButton.getText().equals("Start")) {
                        skipButton.setTextSize(35);
                        skipButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                        skipButton.setText("Start");
                    }
                }

                prevButton.setVisibility((position <= 0) ? View.GONE : View.VISIBLE);
                nextButton.setVisibility((position >= slideLayouts.length - 1 ? View.GONE : View.VISIBLE));

                addCircleDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Utilities.changeStatusBarColor(this, R.color.colorAccent);

        prevButton = (ImageView)findViewById(R.id.prevButton);

        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (slidePager.getCurrentItem() - 1 > -1)
                    slidePager.setCurrentItem(slidePager.getCurrentItem() - 1);

            }
        });

        nextButton = (ImageView)findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (slidePager.getCurrentItem() + 1 <= slidePager.getChildCount())
                    slidePager.setCurrentItem(slidePager.getCurrentItem() + 1);


            }
        });

        skipButton = (TextView)findViewById(R.id.skipButton);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSplash(0);
            }
        });

        prevButton.setVisibility(View.GONE);
        addCircleDots(0);



        /*

        Intent startNearService = new Intent(this, NearPlacesService.class);
        startService(startNearService); */

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

        splash_msg = (TextView) findViewById(R.id.splash_msg);

    }


    private void addCircleDots (int position) {

        dots = new TextView[slideLayouts.length];

        layoutDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.blackTransparent));
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[position].setTextColor(getResources().getColor(R.color.white));


    }

    private void finishSplash(int seconds){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences login_prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
                String status = login_prefs.getString("status", null);
                if(status != null)
                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                else
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));

                finish();


            }
        }, seconds * 1000);


    }

    @Override
    protected void onResume() {
        super.onResume();


        if(Utilities.checkNetworkState(this) && !isGettingData)
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
        protected void onPreExecute() {

            Utilities.loadGifImageView(getApplicationContext(), loadingSplashImage, R.drawable.loadingplaces);

            isGettingData = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            loadingSplashImage.setVisibility(View.GONE);
            splash_msg.setVisibility(View.GONE);

            Log.d("debug", "arrrrrrr");

            if (LoginActivity.login_prefs.getBoolean("isFirstInstalled", true)) {

                slidePager.setVisibility(View.VISIBLE);
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                layoutDots.setVisibility(View.VISIBLE);
                skipButton.setVisibility(View.VISIBLE);

                slidePager.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                prevButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                nextButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                layoutDots.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));
                skipButton.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup));

                LoginActivity.set_login_prefs.putBoolean("isFirstInstalled", false);
                LoginActivity.set_login_prefs.apply();

            } else {
                logoImage.setVisibility(View.VISIBLE);
                finishSplash(1);
            }
        }
    }

    private class IntroSliderPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return slideLayouts.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View inflatedView = layoutInflater.inflate(slideLayouts[position], container, false);

            container.addView(inflatedView);

            return inflatedView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
