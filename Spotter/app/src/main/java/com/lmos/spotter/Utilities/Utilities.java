package com.lmos.spotter.Utilities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.NestedScrollView;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.DialogActivity;
import com.lmos.spotter.MainInterface.Activities.HomeActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResult;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

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
 *
 *
 */


public class Utilities {

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public static void changeStatusBarColor (Activity activity, int color) {

        Window windowActivity = activity.getWindow();

        if (Build.VERSION.SDK_INT >= 21) {

            windowActivity.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            windowActivity.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            windowActivity.setStatusBarColor(ContextCompat.getColor(activity, color));

        }

    }

    public static void animateViewColor (final View view, int colorFrom, int colorTo, int duration) {

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimation.start();
    }

    public static void generateLinkPlace (final Place place, final Activity activity, final OnGenerateLinkListener onGenerateLinkListener, final String socialType) {

        RandomString codeLinkGenerator = new RandomString(10);

        final String linkCode = "spotterlmos/" + codeLinkGenerator.nextString();

        Log.d("debug", "link code: " + linkCode);

        String shareImage = "";

        try {

            shareImage = new JSONObject(new JSONObject(place.getPlaceImageLink()).getString("placeImages"))
                                                                                 .getString("shareImage");

            Log.d("debug", "ImageLink: " + place.getPlaceImageLink());

            Log.d("debug", "shareImage: " + shareImage);

        } catch (JSONException e) {
            Log.d("debug", e.getMessage());
        }

        final BranchUniversalObject placeLinkData = new BranchUniversalObject()
                .setCanonicalIdentifier(linkCode)
                .setContentImageUrl(shareImage)
                .setTitle(place.getPlaceName())
                .setContentDescription(place.getPlaceDescription())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

        placeLinkData.addContentMetadata(new HashMap<String, String>() {{
            put ("placeName", place.getPlaceName());
            put ("placeType", place.getPlaceType());
            put ("placeAddress", place.getPlaceAddress());
            put ("placeLat", place.getPlaceLat());
            put ("placeLng", place.getPlaceLng());
        }});

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("social")
                .setFeature("travel");


        final ProgressDialog generateLinkProgressDialog = new ProgressDialog(activity);

        generateLinkProgressDialog.setMessage("please wait..");
        generateLinkProgressDialog.setCancelable(false);
        generateLinkProgressDialog.setIndeterminate(true);
        generateLinkProgressDialog.show();

        placeLinkData.generateShortUrl(activity, linkProperties, new Branch.BranchLinkCreateListener() {

                @Override
                public void onLinkCreate(String url, BranchError error) {

                    if (error == null) {

                        if (!socialType.equals("twitter")) {

                            generateLinkProgressDialog.dismiss();

                            Log.d("debug", "link generated: " + url);

                            onGenerateLinkListener.OnGenerateLink(url);

                        } else {

                            BranchUniversalObject pd = new BranchUniversalObject()
                                    .setCanonicalIdentifier(linkCode)
                                    .setContentImageUrl(placeLinkData.getImageUrl())
                                    .setTitle(place.getPlaceName())
                                    .setContentDescription(place.getPlaceDescription())
                                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

                            pd.addContentMetadata(new HashMap<String, String>() {{
                                put ("placeName", place.getPlaceName());
                                put ("placeType", place.getPlaceType());
                                put ("placeAddress", place.getPlaceAddress());
                                put ("placeLat", place.getPlaceLat());
                                put ("placeLng", place.getPlaceLng());
                            }});

                            LinkProperties lp = new LinkProperties()
                                    .addControlParameter("$android_url", url)
                                    .setChannel("social")
                                    .setFeature("travel");

                            pd.generateShortUrl(activity, lp, new Branch.BranchLinkCreateListener() {

                                // TODO DO THIS THING

                                @Override
                                public void onLinkCreate(String url2, BranchError error) {

                                    Log.d("debug", "url 2");
                                    Log.d("debug", "url2: " + url2);

                                    generateLinkProgressDialog.dismiss();

                                    if (error == null) {

                                        onGenerateLinkListener.OnGenerateLink(url2);

                                    } else {

                                        onGenerateLinkListener.OnGenerateLink("error");

                                    }
                                }
                            });

                        }

                    } else {

                        generateLinkProgressDialog.dismiss();

                        onGenerateLinkListener.OnGenerateLink("error");

                    }

                }

        });
    }

    public static boolean validateEmail (String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean checkIfLastScrolledItem (NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if(v.getChildAt(v.getChildCount() - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY)
                return true;
        }

        return false;
    }

    public static void hideSoftKeyboard(View currentView, AppCompatActivity appCompatActivity) {
        if (currentView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static Bitmap rotateBitmapCorrectly (Bitmap image, Activity activity) {

        Uri tempUri = getImageUri(activity.getApplicationContext(), image);

        File finalFile = new File(getRealPathFromURI(tempUri, activity));

        Bitmap rotatedBitmap = null;

        try {

            ExifInterface ei = new ExifInterface(finalFile.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(image, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(image, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(image, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = image;
            }
        } catch (Exception e) {
            Log.d("debug", e.getMessage());
        }

        return rotatedBitmap;
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

    public static void setSearchBar(final AppCompatActivity activity, final View actionBarView){

        final int[] to = new int[]{
                R.id.searchview_place_name,
                R.id.searchview_place_address,
                R.id.searchview_place_type,
                R.id.searchview_place_lat,
                R.id.searchview_place_lng,
        };
        final DbHelper dbHelper = new DbHelper(activity);

        ActionBar homeActionBar = activity.getSupportActionBar();
        final SearchView searchView = (SearchView) actionBarView.findViewById(R.id.search_view);
        final TextView title = (TextView) actionBarView.findViewById(R.id.txtHome);

        final SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(
                activity,
                R.layout.search_view_list_item,
                null,
                null,
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

                if(activity instanceof HomeActivity){
                    Intent send_data = new Intent(activity, SearchResultsActivity.class);
                    send_data.putExtra("data", getSuggestionText(position, searchAdapter));
                    activity.startActivity(send_data);
                }
                else{
                    ((SearchResultsActivity) activity).runTask(getSuggestionText(position, searchAdapter));
                }

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

                    if(activity instanceof HomeActivity){
                        Intent send_data = new Intent(activity, SearchResultsActivity.class);
                        send_data.putExtra("data", new String[] { "Undefined", query });
                        activity.startActivity(send_data);
                    }
                    else{
                        ((SearchResultsActivity) activity).runTask("Undefined", query);
                    }

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchValue) {

                searchAdapter.changeCursorAndColumns(
                        dbHelper.querySearch(new String[]{ searchValue + "*"}),
                        new String[]{
                                "name",
                                "address",
                                "type",
                                "latitude",
                                "longitude"},
                        to
                );


                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int cx = searchView.getRight() - 30;
                int cy = searchView.getBottom() - 60;
                int finalRadius = Math.max(searchView.getWidth(), searchView.getHeight());
                Animator anim = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    anim = ViewAnimationUtils.createCircularReveal(searchView, cx, cy, 0, finalRadius);
                }
                anim.start();

                if(activity instanceof SearchResultsActivity){
                    if(searchView.isIconified())
                        ((SearchResultsActivity) activity).setShowBookmarkInAppBar(true);
                    else
                        ((SearchResultsActivity) activity).setShowBookmarkInAppBar(false);
                }

                title.setVisibility(View.GONE);

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                int cx = searchView.getRight() - 30;
                int cy = searchView.getBottom() - 60;
                int initialRadius = searchView.getWidth();
                Animator anim = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    anim = ViewAnimationUtils.createCircularReveal(searchView, cx, cy, initialRadius, 0);
                }
                anim.start();

                if(activity instanceof SearchResultsActivity &&
                   activity.getSupportFragmentManager().findFragmentByTag("") instanceof FragmentSearchResult) {
                    ((SearchResultsActivity) activity).setShowBookmarkInAppBar(true);
                }


                title.setVisibility(View.VISIBLE);
                return false;
            }
        });

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);

        homeActionBar.setCustomView(actionBarView);

    }

    private static String[] getSuggestionText(int position, SimpleCursorAdapter searchAdapter){

        Cursor searchCursor = searchAdapter.getCursor();
        String[] selectedItem = new String[0];
        if(searchCursor.moveToPosition(position)) {
            selectedItem = new String[]{
                    searchCursor.getString(5),
                    searchCursor.getString(1),
                    searchCursor.getString(2),
                    searchCursor.getString(3),
                    searchCursor.getString(4)};
        }
        return selectedItem;
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

    public static void setNavTitleStyle(AppCompatActivity appCompatActivity, int navId, int titleId, int styleId) {

        NavigationView navigationView = (NavigationView) appCompatActivity.findViewById(navId);

        Menu navigationMenu = navigationView.getMenu();

        MenuItem tools = navigationMenu.findItem(titleId);

        SpannableString spannableString = new SpannableString(tools.getTitle());

        spannableString.setSpan(new TextAppearanceSpan(appCompatActivity.getApplicationContext(), styleId), 0, spannableString.length(), 0);

        tools.setTitle(spannableString);

    }

    public static boolean isDateInRange(Date startDate, Date inDate, Date endDate) {
        return !(inDate.before(startDate) || inDate.after(endDate));
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

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static boolean checkNetworkState(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showDialogActivity(Activity activity, int requestCode ,int stringId){

        Intent showDialogActivity = new Intent(activity, DialogActivity.class);
        showDialogActivity.putExtra("message", stringId);
        activity.startActivityForResult(showDialogActivity, requestCode);

    }

    public static void showSnackBar(View container, String message,
                                    int duration, String action_msg,
                                    View.OnClickListener onClickListener, BaseTransientBottomBar.BaseCallback callback){

        Snackbar sb = Snackbar.make(
                container,
                message,
                duration
        );

        if(action_msg != null)
            sb.setAction(action_msg, onClickListener);

        sb.addCallback(callback);
        sb.show();

    }

    public static boolean checkPlayServices(Activity activity, DialogInterface.OnDismissListener onDismissListener) {


        final int PLAY_SERVICES_RESOLUTION_REQUEST = 1400;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {

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

    /** Logger **/

    public static void logError(Context context, String error){

        final String FILE_NAME = "error_log";
        String log_msg = DateFormat.getDateTimeInstance().format(new Date()) + ":" + error + "\n";
        FileOutputStream outputStream;

        try{

            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
            outputStream.write(log_msg.getBytes());
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnGenerateLinkListener {
        void OnGenerateLink (String generatedLink);
    }

    /** Intefaces **/

    public interface OnLocationFoundListener {
        void onLocationFoundCity(String city);
        void onLocationFoundLatLng(double lat, double lng, float bearing);
    }

    public interface OnDbResponseListener{
        void onDbResponse(String response, String undo_id);
    }

    public static class ConnectionNotifier {

        static OnDeviceOfflineListener onDeviceOfflineListener;

        static Thread connectionNotifierThread;
        Context context;

        public ConnectionNotifier (Context con) {
            context = con;
        }

        public ConnectionNotifier start () {

            if (connectionNotifierThread == null) {
                connectionNotifierThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for(;true;) {

                            if (onDeviceOfflineListener != null && context != null)
                                onDeviceOfflineListener.OnDeviceOfflineListener(Utilities.checkNetworkState(context));

                        }
                    }
                });
                connectionNotifierThread.start();
            }

            return this;
        }

        public ConnectionNotifier setDeviceOfflineListener (OnDeviceOfflineListener offlineListener) {
            onDeviceOfflineListener = offlineListener;
            return this;
        }

        public interface OnDeviceOfflineListener {

            void OnDeviceOfflineListener (boolean networkState);

        }

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

        public static Bitmap stringToBitmap (String strBitmap) {
            try {
                byte[] encodedByteBitmap = Base64.decode(strBitmap, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(encodedByteBitmap, 0, encodedByteBitmap.length);
            } catch (Exception e) {
                return null;
            }
        }

        public static String bitmapToString (Bitmap bitmap) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteBitmap = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteBitmap, Base64.DEFAULT);
            } catch (Exception e) {
                Log.d("debug", e.getMessage());
                return null;
            }
        }

    }

    public static class LocationHandler
            implements
                LocationListener {

        private final int INTERVAL = 1000, FAST_INTERVAL = 500;
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
                    if(apiClient.isConnected())
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
                                response = connectionResult.toString();}
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
                    this
            );

        }

        public void stopLocationRequest(){
            if(apiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }

        public void getLocality(Double lat, Double lng){
            new getLocationName().execute(lat, lng);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d("FragmentResult", "Location found!");
            OnLocationFoundListener.onLocationFoundLatLng(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getBearing()
            );

        }

        class getLocationName extends AsyncTask<Double, Void, Void>{

            private double latitude;
            private double longitude;

            @Override
            protected Void doInBackground(Double... params) {

                while (true) {

                    Geocoder getLocationName = new Geocoder(activity, Locale.getDefault());
                    List<Address> addresses;

                    latitude = params[0];
                    longitude = params[1];

                    try {

                        addresses = getLocationName.getFromLocation(latitude, longitude, 1);
                        response = addresses.get(0).getLocality();

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
            }
        }

    }

    public static class REQUEST_CODES{

        public static final int LOCATION_REQUEST_CODE = 1400;
        public static final int CHECK_SETTING_REQUEST_CODE = 1500;
        public static final int LOGIN_REQUEST = 1932;
    }

    /** End of Interfaces **/

    public static class SortPlaces implements Comparator<Place>{

        @Override
        public int compare(Place o1, Place o2) {

            int returnVal = 0;

            if(Double.parseDouble(o1.getDistance()) < Double.parseDouble(o2.getDistance()))
                returnVal = -1;
            else if(Double.parseDouble(o1.getDistance()) > Double.parseDouble(o2.getDistance()))
                returnVal = 1;
            else if(Double.parseDouble(o1.getDistance()) == Double.parseDouble(o2.getDistance()))
                returnVal = 0;

            return returnVal;
        }
    }

}
