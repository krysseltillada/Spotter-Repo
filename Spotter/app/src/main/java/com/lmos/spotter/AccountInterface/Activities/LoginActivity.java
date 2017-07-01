package com.lmos.spotter.AccountInterface.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lmos.spotter.AccountInterface.Fragments.FragmentRecover;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignIn;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignUp;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private static final String LOGIN_PREFS = "LoginSharedPreference";

    ImageView imgHolder;
    SharedPreferences login_prefs;
    public static SharedPreferences.Editor set_login_prefs;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        login_prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        set_login_prefs = login_prefs.edit();

        imgHolder = (ImageView) findViewById(R.id.background_img_holder);
        imgHolder.setImageBitmap(
                Utilities.BlurImg.blurImg(
                        this,
                        ((BitmapDrawable) getResources().getDrawable(R.drawable.traveler_bg)).getBitmap(),
                        2f
                )
        );

        getSupportFragmentManager().beginTransaction()
                .add(R.id.account_fragment_holder,new FragmentSignIn(), "Sign In")
                .commit();

    }

    public void clickListener(View view){

        switch (view.getId()){

            case R.id.sign_up:
                switchFragment(new FragmentSignUp());
                return;
            case R.id.forgot_pass:
                switchFragment(new FragmentRecover());
                return;
            case R.id.skip_login:
                startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                set_login_prefs.putString("status", "true");
                set_login_prefs.apply();
                finish();
                return;
            default:
                break;

        }

    }

    private void switchFragment(Fragment fragment){

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.account_fragment_holder, fragment, "Sign Up")
                .addToBackStack("Sign In")
                .commit();

    }

    /**
     *
     *  This method will start AccountHandler
     *  Arguments will be coming from Login and Registration Fragment
     *  and it will be passed to AccountHandler to process request.
     *
     **/
    public final void runAccountHandler(String transaction, Map<String, Object> params){
        new AccountHandler(this,transaction, params).execute();
    }

    private class AccountHandler extends AsyncTask<Void, Void, String>{

        ProgressDialog pd;
        String transaction;
        Map<String, Object> map_data;

        private AccountHandler(Context context, String transaction, Map<String, Object> map_data){
            this.transaction = transaction;
            this.map_data = map_data;
            pd = new ProgressDialog(context){{
                setIndeterminate(true);
                setProgressStyle(STYLE_SPINNER);
                setMessage("Signing in...");
                setFinishOnTouchOutside(false);
                setCanceledOnTouchOutside(false);
            }};
        }

        @Override
        protected String doInBackground(Void... params) {

            final AppScript appScript = new AppScript(){{
                setRequestURL("http://192.168.4.173/projects/spotter/app_scripts/");
                setData(transaction, map_data);
            }};

            return appScript.getResult();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pd.dismiss();

            if(response.equals("Sign in success.") || response.equals("Account has been registered.")){

                set_login_prefs.putString("status", "Logged In");
                set_login_prefs.apply();
                Utilities.OpenActivity(getApplicationContext(), HomeActivity.class, activity);

            }
            else
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

        }

    }

}
