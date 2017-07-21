package com.lmos.spotter.AccountInterface.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Fragments.FragmentRecover;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignIn;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignUp;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    public static final String LOGIN_PREFS = "LoginSharedPreference";
    ImageView imgHolder;
    public static SharedPreferences login_prefs;
    public static SharedPreferences.Editor set_login_prefs;
    DbHelper bookmarksDB;
    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


        bookmarksDB = new DbHelper(activity, null);

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
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.account_fragment_holder, fragment, "Sign Up")
                .addToBackStack("Sign In")
                .commit();

    }

    private void syncBookmarks(final String accountID) {

        RequestQueue requestBoomarks = Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog syncProgressDialog = new ProgressDialog(this);

        syncProgressDialog.setMessage("syncing bookmarks");
        syncProgressDialog.setIndeterminate(true);
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.show();

        StringRequest stringRequestBookmarks = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/syncBookmarks.php"
                , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject userBookmarks = new JSONObject(response);
                    JSONArray  bookmarks = userBookmarks.getJSONArray("userBookmarks");

                    for (int i = 0; i != bookmarks.length(); ++i) {

                        JSONObject bookmarkElement = bookmarks.getJSONObject(i);
                        Place place = new Place();

                        place.setPlaceID(bookmarkElement.getString("placeID"));
                        place.setplaceName(bookmarkElement.getString("Name"));
                        place.setplaceAddress(bookmarkElement.getString("Address"));
                        place.setplaceLocality(bookmarkElement.getString("Locality"));
                        place.setplaceDescription(bookmarkElement.getString("Description"));
                        place.setplaceClass(bookmarkElement.getString("Class"));
                        place.setplaceType(bookmarkElement.getString("Type"));
                        place.setplacePriceRange(bookmarkElement.getString("PriceRange"));
                        place.setplaceImageLink(bookmarkElement.getString("Image"));
                        place.setPlaceLat(bookmarkElement.getString("Latitude"));
                        place.setPlaceLng(bookmarkElement.getString("Longitude"));

                        bookmarksDB.checkAndAddBookmark(place);

                    }

                    syncProgressDialog.dismiss();

                    Utilities.OpenActivity(getApplicationContext(), HomeActivity.class, activity);

                } catch(JSONException e) {
                    Log.d("debug", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("accountID", accountID);
                }};
            }
        };

        requestBoomarks.add(stringRequestBookmarks);

    }

    /**
     *
     *  This method will start AccountHandler
     *  Arguments will be coming from Login and Registration Fragment
     *  and it will be passed to AccountHandler to process request.
     *
     **/
    public final void runAccountHandler(String transaction, Map<String, String> params){
        new AccountHandler(this,transaction, params).execute();
    }

    private class AccountHandler extends AsyncTask<Void, Void, String>{

        ProgressDialog pd;
        String transaction;
        Map<String, String> map_data;

        private AccountHandler(Context context, String transaction, Map<String, String> map_data){
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

            final AppScript appScript = new AppScript(activity){{
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

            Log.d("debug", response);

            if(response.equals("Sign in success.")){

                set_login_prefs.putString("status", "Logged In");
                set_login_prefs.apply();

                if (login_prefs.getString("accountHasBookmark", "").equals("true")) {

                    new AlertDialog.Builder(LoginActivity.this)
                                   .setTitle("sync bookmarks?")
                                   .setMessage("it seems your bookmarks is saved online " +
                                               "would you like to sync your bookmarks from this device?")
                                   .setPositiveButton("sync now", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {

                                           if (login_prefs.getBoolean("isDiffAccount", false))
                                               bookmarksDB.clearBookmarks();

                                           syncBookmarks(login_prefs.getString("accountID", ""));

                                       }
                                   })
                                   .setNegativeButton("not now", new DialogInterface.OnClickListener() {

                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {

                                           if (login_prefs.getBoolean("isDiffAccount", false))
                                               bookmarksDB.clearBookmarks();

                                           startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                                       }
                                   })
                                   .show();

                }
                else {

                    if (login_prefs.getBoolean("isDiffAccount", false))
                        bookmarksDB.clearBookmarks();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                }


            } else if (response.equals("Account has been registered.")) {

                set_login_prefs.putString("status", "Logged In");
                set_login_prefs.apply();

                bookmarksDB.clearBookmarks();

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            } else {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }

        }

    }

}
