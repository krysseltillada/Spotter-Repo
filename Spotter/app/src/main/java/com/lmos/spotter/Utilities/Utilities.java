package com.lmos.spotter.Utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.lmos.spotter.DialogActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kryssel on 6/2/2017.
 *
 * This class will hold static methods and classes
 * that is reusable in any given time.
 * Included in this are the following:
 *  **Classes**
 *    BlurImg - (public methods: blurImg, toString, getBitmap)
 *    LocationHandler
 *     (public methods: changeApiState, findLocation, buildApi)
 */


public class Utilities {

    public static void hideSoftKeyboard(View currentView, AppCompatActivity appCompatActivity) {
        if (currentView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    public static boolean checkIfLastItem(int firstVisibleItem, int visibleItem,
                                          int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItem;

        return (lastItem == totalItemCount);
    }

    public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static void OpenActivity(Context con, Class<?> cname, Activity callingActivity) {
        Intent requestActivity = new Intent(con, cname);
        requestActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        con.startActivity(requestActivity);
        if(callingActivity != null)
            callingActivity.finish();
    }

    public static void OpenActivityWithBundle(Context con, Class<?> cname, String callingActivity, Bundle bundle) {
        Intent requestActivity = new Intent(con, cname);
        requestActivity.putExtras(bundle);
        requestActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        con.startActivity(requestActivity);
    }

    public static void QuerySearchResults(String searchValue, SimpleCursorAdapter suggestion, String[] keywords) {

        MatrixCursor suggestions = new MatrixCursor(new String[]{BaseColumns._ID, "judy"});

        for (int i = 0; i != keywords.length; ++i) {
            if (keywords[i].toLowerCase().startsWith(searchValue.toLowerCase())) {
                Log.d("sample", searchValue);
                suggestions.addRow(new Object[]{i, keywords[i]});
            }
        }

        suggestion.changeCursor(suggestions);
    }

    public static void setSearchBar(final AppCompatActivity activity, final View actionBarView){

        String[] from = new String[]{"Judy"};
        int[] to = new int[]{android.R.id.text1};
        final String[] sampleWords = {"hello", "judy", "sample", "text", "june", "General", "Hotel", "Resto", "Tourist Spot"};

        ActionBar homeActionBar = activity.getSupportActionBar();
        final SearchView searchView = (SearchView) actionBarView.findViewById(R.id.search_view);
        final TextView title = (TextView) actionBarView.findViewById(R.id.txtHome);

        final SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        searchView.setSuggestionsAdapter(searchAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Intent send_data = new Intent(activity, SearchResultsActivity.class);
                send_data.putExtra("type", getSuggestionText(position, searchAdapter));
                activity.startActivity(send_data);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!searchView.isIconified()) {

                    Utilities.hideSoftKeyboard(activity.getCurrentFocus(), activity);
                    searchView.setIconified(true);
                    onQueryTextSubmit(query);

                } else {

                    Intent send_data = new Intent(activity, SearchResultsActivity.class);
                    send_data.putExtra("type", query);
                    activity.startActivity(send_data);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchValue) {
                Utilities.QuerySearchResults(searchValue, searchAdapter, sampleWords);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
                return false;
            }
        });

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);

        homeActionBar.setCustomView(actionBarView);

    }

    private static String getSuggestionText(int position, SimpleCursorAdapter searchAdapter){

        String selected_item = "";
        Cursor searchCursor = searchAdapter.getCursor();
         if(searchCursor.moveToPosition(position)){
         selected_item = searchCursor.getString(1);
         }
        return selected_item;
    }

    public static void changeActionBarLayout (AppCompatActivity activity, Toolbar toolbar, Menu menu,
                                              int menuLayout, String title) {

        toolbar.getMenu().clear();
        activity.getMenuInflater().inflate(menuLayout, menu);
        activity.getSupportActionBar().setTitle(title);

    }

    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
                                   int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight,
                true);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static void setNavTitleStyle(AppCompatActivity appCompatActivity, int navId, int titleId, int styleId) {

        NavigationView navigationView = (NavigationView) appCompatActivity.findViewById(navId);

        Menu navigationMenu = navigationView.getMenu();

        MenuItem tools = navigationMenu.findItem(titleId);

        SpannableString spannableString = new SpannableString(tools.getTitle());

        spannableString.setSpan(new TextAppearanceSpan(appCompatActivity.getApplicationContext(), styleId), 0, spannableString.length(), 0);

        tools.setTitle(spannableString);

    }

    public static void inflateOptionItem(Context activity, View parent, int layout, PopupMenu.OnMenuItemClickListener onMenuItemClickListener){

        PopupMenu optionMenu = new PopupMenu(activity, parent, Gravity.END);
        optionMenu.getMenuInflater().inflate(layout, optionMenu.getMenu());
        optionMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        optionMenu.show();

    }

    public static void loadGifImageView(Context context, ImageView target, int drawableId) {
        GlideDrawableImageViewTarget gifLoaderImage = new GlideDrawableImageViewTarget(target);
        Glide.with(context).load(drawableId).into(gifLoaderImage);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static boolean checkNetworkState(Activity activity){

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showDialogActivity(Activity activity, int requestCode ,int stringId){

        Intent showDialogActivity = new Intent(activity, DialogActivity.class);
        showDialogActivity.putExtra("message", stringId);
        activity.startActivityForResult(showDialogActivity, requestCode);

    }

    public static void showSnackBar(View container, String message, int duration, String action_msg, View.OnClickListener onClickListener){

        Snackbar sb = Snackbar.make(
                container,
                message,
                duration
        );

        if(action_msg != null)
            sb.setAction(action_msg, onClickListener);

        sb.show();

    }

    public static boolean checkPlayServices(Activity activity, DialogInterface.OnDismissListener onDismissListener) {


        final int PLAY_SERVICES_RESOLUTION_REQUEST = 1400;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                        resultCode,
                        activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                );

                errorDialog.setOnDismissListener(onDismissListener);

                errorDialog.show();

            }

            return false;

        }

        return true;
    }

    /** Intefaces **/

    public interface OnLocationFoundListener {
        void onLocationFoundCity(String city);
        void onLocationFoundLatLng(double lat, double lng);
    }

    public interface OnDbResponseListener{
        void onDbResponse(String response);
    }

    public static class BlurImg {

        private static final float BITMAP_SCALE = 0.4f;

        public static Bitmap blurImg(Context context, Bitmap blurme, float blurValue) {

            int width = Math.round(blurme.getWidth() * BITMAP_SCALE);
            int height = Math.round(blurme.getHeight() * BITMAP_SCALE);

            Bitmap input_bitmap = Bitmap.createScaledBitmap(blurme, width, height, true);
            Bitmap output_bitmap = Bitmap.createBitmap(input_bitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur sblur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            Allocation tmpIn = Allocation.createFromBitmap(rs, input_bitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, output_bitmap);

            sblur.setRadius((blurValue > 25) ? 25 : (blurValue < 0) ? 0 : blurValue);
            sblur.setInput(tmpIn);
            sblur.forEach(tmpOut);

            tmpOut.copyTo(output_bitmap);

            return output_bitmap;
        }

        public static String toString(Bitmap bitmap) {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
            byte[] b = byteOutputStream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }

        public static Bitmap getBitmap(String encodedBitmap) {
            byte[] decodeBitmap = Base64.decode(encodedBitmap, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeBitmap, 0, decodeBitmap.length);
        }

    }

    public static class LocationHandler {

        private final int INTERVAL = 2000, FAST_INTERVAL = 500;
        OnLocationFoundListener OnLocationFoundListener;
        private GoogleApiClient apiClient;
        private LocationRequest locationRequest;
        private Activity activity;
        private String response = " HI ";

        public LocationHandler(Activity activity, OnLocationFoundListener OnLocationFoundListener) {
            this.activity = activity;
            this.OnLocationFoundListener = OnLocationFoundListener;
        }

        public void findLocation() {
            setLocationRequest();
        }

        public void changeApiState(String state) {

            switch (state){

                case "connect":
                    if(!apiClient.isConnected())
                        apiClient.connect();
                        if (apiClient.isConnecting())
                            Log.d("LocationHandler", "Connecting...");
                    break;
                case "disconnect":
                    apiClient.disconnect();
                    break;
                case "reconnect":
                    apiClient.reconnect();
                    break;
            }

        }

        public void checkPermission(){

            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, Utilities.REQUEST_CODES.LOCATION_REQUEST_CODE);

            }


        }

        public boolean checkApiState(){ return apiClient.isConnected(); }

        private void checkLocationSettingsState(){

            Log.d("LocationHandler", "Checking GPS status");

            LocationSettingsRequest.Builder buildSettingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            buildSettingsRequest.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> resultPendingResult =
                    LocationServices.SettingsApi.checkLocationSettings(
                            apiClient,
                            buildSettingsRequest.build()
                    );


            resultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {


                    final Status status = locationSettingsResult.getStatus();

                    switch (status.getStatusCode()){

                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(activity, REQUEST_CODES.CHECK_SETTING_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;

                    }

                }
            });

        }

        public void buildGoogleClient() {

            checkPermission();

            Log.d("LocationHandler", "Building client");
            apiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d("LocationHandler", "Connected");
                            setLocationRequest();
                            checkLocationSettingsState();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            response = "Connection suspended";
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            response = connectionResult.toString();
                        }
                    })
                    .build();

        }

        private void setLocationRequest() {
            Log.d("LocationHandler", "Requesting location");
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(INTERVAL)
                    .setFastestInterval(FAST_INTERVAL);

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&

                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient,
                    locationRequest,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d("LocationHandler", "Lat:" + location.getLatitude() + " Lng:" + location.getLongitude());
                            if(checkNetworkState(activity)) {
                                new getLocationName().execute(location.getLatitude(), location.getLongitude());
                                LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
                            }
                        }
                    }
            );

        }

        class getLocationName extends AsyncTask<Double, Void, Void>{

            private double latitude;
            private double longtitude;

            @Override
            protected Void doInBackground(Double... params) {

                Log.d("LocationHandler", "Parsing latitude and longitude");
                // params[0] = latitude, params[1] = longitude

                while (true) {

                    Geocoder getLocationName = new Geocoder(activity, Locale.getDefault());
                    List<Address> addresses;

                    latitude = params[0];
                    longtitude = params[1];

                    try {

                        addresses = getLocationName.getFromLocation(latitude, longtitude, 1);

                        /*

                        for (int i = 0; i != addresses.get(0).getMaxAddressLineIndex(); ++i)
                            response += "[" + i + "]" + addresses.get(0).getAddressLine(i) + "\n";


                        response += addresses.get(0).getLocality();
                        response += addresses.get(0).getAdminArea();
                        response += addresses.get(0).getCountryName();
                        response += addresses.get(0).getPostalCode();
                        response += addresses.get(0).getFeatureName();

                        Log.d("debug", response); */

                        response = addresses.get(0).getLocality();
                        Log.d("debug", response);

                        break;

                    } catch (IOException e) {
                        response = e.getMessage();
                        Log.d("LocationHandler", response);
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                OnLocationFoundListener.onLocationFoundCity(response);
                OnLocationFoundListener.onLocationFoundLatLng(latitude, longtitude);
            }
        }

    }

    public static class REQUEST_CODES{

        public static final int LOCATION_REQUEST_CODE = 1400;
        public static final int CHECK_SETTING_REQUEST_CODE = 1500;

    }

    /** End of Interfaces **/
}
