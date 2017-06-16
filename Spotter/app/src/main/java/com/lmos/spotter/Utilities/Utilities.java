package com.lmos.spotter.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.lmos.spotter.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kryssel on 6/2/2017.
 */


public class Utilities {

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

    public static void OpenActivity(Context con, Class<?> cname, String callingActivity) {
        Intent requestActivity = new Intent(con, cname);
        requestActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        con.startActivity(requestActivity);
    }

    public static void OpenActivityWithBundle (Context con, Class<?> cname, String callingActivity, Bundle bundle) {
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

    public static void changeActionBarLayout (AppCompatActivity activity, Toolbar toolbar, Menu menu,
                                              int menuLayout, String title) {

        toolbar.getMenu().clear();
        activity.getMenuInflater().inflate(menuLayout, menu);
        activity.getSupportActionBar().setTitle(title);

    }

    public static void setNavTitleStyle(AppCompatActivity appCompatActivity, int navId, int titleId, int styleId) {

        NavigationView navigationView = (NavigationView) appCompatActivity.findViewById(navId);

        Menu navigationMenu = navigationView.getMenu();

        MenuItem tools = navigationMenu.findItem(titleId);

        SpannableString spannableString = new SpannableString(tools.getTitle());

        spannableString.setSpan(new TextAppearanceSpan(appCompatActivity.getApplicationContext(), styleId), 0, spannableString.length(), 0);

        tools.setTitle(spannableString);

    }

    public static void loadGifImageView(Context context, ImageView target, int drawableId) {
        GlideDrawableImageViewTarget gifLoaderImage = new GlideDrawableImageViewTarget(target);
        Glide.with(context).load(drawableId).into(gifLoaderImage);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static boolean checkPlayServices(Activity activity) {

        final int PLAY_SERVICES_RESOLUTION_REQUEST = 1400;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(
                        resultCode,
                        activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST
                ).show();

            }

            return false;

        }

        return true;
    }

    public static class LocationHandler {

        private GoogleApiClient apiClient;
        private LocationRequest locationRequest;
        private Activity activity;
        private final int INTERVAL = 5000, FAST_INTERVAL = 2000;
        private String response = "";

        public LocationHandler(Activity activity) {
            this.activity = activity;
        }

        public String findLocation() {
            Log.d("LocationHandler", "Finding location...");
            setLocationRequest();
            buildGoogleClient();
            apiClient.connect();
            Log.d("LocationHandler", "Returning location");
            return response;
        }

        private void buildGoogleClient() {
            Log.d("LocationHandler", "Building client");
            apiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

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
                                            locationToString(activity, location.getLatitude(), location.getLongitude());
                                            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
                                        }
                                    }
                            );

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d("LocationHandler", "Connection Suspended");
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

        private void locationToString(Activity activity, double lat, double lng){
            Log.d("LocationHandler", "Parsing latitude and longitude");
            String locationName;

            Geocoder getLocationName = new Geocoder(activity, Locale.getDefault());

            try{

                List<Address> addresses = getLocationName.getFromLocation(lat, lng,1);
                Address address = addresses.get(0);
                locationName = address.getLocality();

            } catch (IOException e) {
                locationName = e.getMessage();
            }

           response = locationName;
        }

        private void setLocationRequest(){
            Log.d("LocationHandler", "Requesting location");
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(INTERVAL)
                    .setFastestInterval(FAST_INTERVAL);

        }

    }

}
