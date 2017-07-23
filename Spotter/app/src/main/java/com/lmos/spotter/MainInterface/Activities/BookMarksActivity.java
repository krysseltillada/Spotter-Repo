package com.lmos.spotter.MainInterface.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMarksActivity extends AppCompatActivity {

    Menu bookMarksMenu;
    Toolbar toolbar;

    TabLayout bookMarksTabLayout;
    RecyclerView recyclerView;
    TextView bookmarkEmptyText;
    TextView bookmarkEmptyText1;
    TabLayout.Tab firstTab;

    SharedPreferences userData;

    RecyclerView bookMarksRecyclerView;

    HashMap<String, List<Place>> bookmarkedPlaceList = new HashMap<>();

    PlaceType currentlySelectedPlace;

    DbHelper bookmarksDB;


    public static PlaceType getPlaceType (String placeType) {
        return  (placeType.equals("Hotel")) ? PlaceType.HOTEL :
                (placeType.equals("Restaurant")) ? PlaceType.RESTAURANT :
                                                   PlaceType.TOURIST_SPOTS;
    }

    public static String getPlaceTypeStr (PlaceType placeType) {
        return  (placeType == PlaceType.HOTEL) ? "Hotel" :
                (placeType == PlaceType.RESTAURANT) ? "Restaurant" :
                                                      "Tourist Spot";
    }

    private void changeBookMarkMode (ActivityType activityType, final RecyclerView recyclerViewTabLayout,
                                     TabLayout tabLayout) {

        tabLayout.clearOnTabSelectedListeners();

        if (activityType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE) {

            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                    currentlySelectedPlace,
                    bookmarkedPlaceList.get(getPlaceTypeStr(currentlySelectedPlace))
                   ));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    String placeType = tab.getText().toString();

                    MainInterfaceAdapter.currentSelectedTab = getPlaceType(placeType);
                    currentlySelectedPlace = MainInterfaceAdapter.currentSelectedTab;

                    recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                currentlySelectedPlace,
                                bookmarkedPlaceList.get(placeType)));

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {



                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {




                }


            });

        } else {

            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                    MainInterfaceAdapter.currentSelectedTab,
                    bookmarkedPlaceList.get(getPlaceTypeStr(currentlySelectedPlace))
            ));


            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    String placeType = tab.getText().toString();

                    MainInterfaceAdapter.currentSelectedTab = getPlaceType(placeType);
                    currentlySelectedPlace = MainInterfaceAdapter.currentSelectedTab;

                    recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                            ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                            currentlySelectedPlace,
                            bookmarkedPlaceList.get(placeType)));

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {



                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {




                }

            });

        }
    }

    private void selectAllCheckBox (final RecyclerView recView, ArrayList<Boolean> checkDeleteList, boolean isSelected) {

        for (int rowItemCount = 0; rowItemCount != checkDeleteList.size(); ++rowItemCount) {

            checkDeleteList.set(rowItemCount, isSelected);

            View view = recView.getLayoutManager().findViewByPosition(rowItemCount);

            if (view != null) {

                TextView placeNameTextView = (TextView)view.findViewById(R.id.txtPlaceName);

                String placeName = placeNameTextView.getText().toString();

                if (!MainInterfaceAdapter.getCheckToggleMap().get(getPlaceTypeStr(currentlySelectedPlace)).contains(placeName)) {

                    MainInterfaceAdapter.getCheckToggleMap().get(getPlaceTypeStr(currentlySelectedPlace))
                            .add(placeName);

                }

                CheckBox cbDelete = (CheckBox) view.findViewById(R.id.cbDelete);
                cbDelete.setChecked(checkDeleteList.get(rowItemCount));

            }


        }

    }

    private void checkAndInitBookmarks(String placeType) {

        if (bookmarksDB.getBookmarks(placeType).size() > 0) {
            bookMarksTabLayout.addTab(bookMarksTabLayout.newTab().setText(placeType));
            bookmarkedPlaceList.put(placeType, bookmarksDB.getBookmarks(placeType));
         }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// TODO SYNC A BOOKMARK ON CLOUD

        setContentView(R.layout.activity_book_marks);

        userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        bookMarksRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        bookMarksTabLayout = (TabLayout)findViewById(R.id.home_tabLayout);

        bookmarkEmptyText = (TextView)findViewById(R.id.messageRecyclerView);
        bookmarkEmptyText1 = (TextView)findViewById(R.id.messageRecyclerView2);

        bookmarksDB = new DbHelper(getApplicationContext());

        refreshBookmarks();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookmarks");


    }

    private void refreshBookmarks () {

        bookMarksTabLayout.clearOnTabSelectedListeners();
        bookMarksTabLayout.removeAllTabs();

        MainInterfaceAdapter.checkBoxToggleStates.clear();
        MainInterfaceAdapter.checkBoxToggleMap.clear();
        bookmarkedPlaceList.clear();

        checkAndInitBookmarks("Hotel");
        checkAndInitBookmarks("Restaurant");
        checkAndInitBookmarks("Tourist Spot");

        firstTab = bookMarksTabLayout.getTabAt(0);

        if (firstTab != null) {

            currentlySelectedPlace = getPlaceType(firstTab.getText().toString());


            recyclerView = bookMarksRecyclerView;

            bookMarksRecyclerView.getRecycledViewPool().setMaxRecycledViews(MainInterfaceAdapter.VIEW_TYPE_ITEM, 0);

            bookMarksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL,
                    false));

            bookMarksRecyclerView.setNestedScrollingEnabled(false);

            bookMarksTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                    bookMarksRecyclerView,
                    bookMarksTabLayout);

            bookMarksRecyclerView.setVisibility(View.VISIBLE);
            bookMarksTabLayout.setVisibility(View.VISIBLE);
            bookmarkEmptyText.setVisibility(View.GONE);
            bookmarkEmptyText1.setVisibility(View.GONE);

        } else {

            bookMarksRecyclerView.setVisibility(View.GONE);
            bookMarksTabLayout.setVisibility(View.GONE);
            bookmarkEmptyText.setVisibility(View.VISIBLE);
            bookmarkEmptyText1.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (firstTab != null)
            getMenuInflater().inflate(R.menu.book_marks_menu, menu);
        else
            getMenuInflater().inflate(R.menu.book_marks_menu_empty, menu);

        bookMarksMenu = menu;

        getSupportActionBar().setTitle("Bookmarks");

        return super.onCreateOptionsMenu(menu);
    }

    private void syncBookmarks (final String accountID) {

        RequestQueue requestBookmark = Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog syncProgressDialog = new ProgressDialog(this);

        syncProgressDialog.setIndeterminate(true);
        syncProgressDialog.setMessage("syncing bookmarks");
        syncProgressDialog.show();

        StringRequest stringBookmarkRequest = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/syncBookmarks.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        if (response.equals("empty")) {
                            Toast.makeText(getApplicationContext(), "no bookmarks on cloud", Toast.LENGTH_LONG).show();
                            syncProgressDialog.dismiss();
                            return;
                        }

                        new SyncBookmarkTask(getApplicationContext(),
                                             syncProgressDialog,
                                             bookmarksDB).execute(response);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){

                protected Map<String, String> getParams() {
                    return new HashMap<String, String>(){{
                        put("accountID", accountID);
                    }};
                }

        };

        requestBookmark.add(stringBookmarkRequest);

    }

    private String[] getDeletedBookmarks () {
        List<String> placeNameList = new ArrayList<>();

        for (String placeKey : MainInterfaceAdapter.getCheckToggleMap().keySet()) {

            for (int i = 0; i != MainInterfaceAdapter.getCheckToggleMap().get(placeKey).size(); ++i) {

                String placeName = MainInterfaceAdapter.getCheckToggleMap().get(placeKey).get(i);

                for (String bookmarkKey : bookmarkedPlaceList.keySet()) {

                    for (int a = 0; a != bookmarkedPlaceList.get(bookmarkKey).size(); ++a) {

                        Place bookmarkPlace = bookmarkedPlaceList.get(bookmarkKey).get(a);

                        if (bookmarkPlace.getPlaceName().equals(placeName)) {
                            placeNameList.add(bookmarkPlace.getPlaceID());
                        }

                    }

                }

            }

        }

        String[] placeIDArray = new String[placeNameList.size()];
        placeIDArray = placeNameList.toArray(placeIDArray);

        return placeIDArray;
    }

    private void deleteBookmarksOnCloud (final String accountID, final String jsonDeletedBookmarks) {


        RequestQueue deleteRequest = Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog deleteBookmarksProgress = new ProgressDialog(this);

        deleteBookmarksProgress.setIndeterminate(true);
        deleteBookmarksProgress.setMessage("deleting bookmarks on cloud");
        deleteBookmarksProgress.setCancelable(true);
        deleteBookmarksProgress.show();

        StringRequest deleteStringRequest = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/deleteBookmarks.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("true")) {

                            deleteBookmarksProgress.dismiss();

                            resetDelete();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.d("debug", "Error. HTTP Status Code:"+networkResponse.statusCode);
                        }

                        if (error instanceof TimeoutError) {
                            Log.d("debug", "TimeoutError");
                        }else if(error instanceof NoConnectionError){
                            Log.d("debug", "NoConnectionError");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("debug", "AuthFailureError");
                        } else if (error instanceof ServerError) {
                            Log.d("debug", "ServerError");
                        } else if (error instanceof NetworkError) {
                            Log.d("debug", "NetworkError");
                        } else if (error instanceof ParseError) {
                            Log.d("debug", "ParseError");
                        }

                        deleteBookmarksProgress.dismiss();
                    }
                }){
                protected Map<String, String> getParams() {
                    return new HashMap<String, String>(){{
                        put("accountID", accountID);
                        put("placeIDJSON", jsonDeletedBookmarks);
                    }};
                }
        };

        deleteRequest.add(deleteStringRequest);

    }

    private void resetDelete () {

        refreshBookmarks();

        invalidateOptionsMenu();

        MainInterfaceAdapter.setAllCheckToggleStates(false);
        MainInterfaceAdapter.getCheckToggleMap().clear();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.syncBookmarks:

                if (userData.getString("status", "").equals("Logged In")) {

                    if (Utilities.checkNetworkState(this)) {
                        syncBookmarks(userData.getString("accountID", ""));
                    } else {
                        Toast.makeText(getApplicationContext(), "cant sync no connection", Toast.LENGTH_LONG).show();
                        return true;
                    }

                } else {

                    new AlertDialog.Builder(this).setTitle("Cannot Sync")
                                                 .setMessage("you need to sign in to sync your bookmarks")
                                                 .setPositiveButton("sign in", new DialogInterface.OnClickListener() {

                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         BookMarksActivity.this.startActivity(new Intent(BookMarksActivity.this,
                                                                                                         LoginActivity.class));
                                                     }
                                                 })
                                                 .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         dialog.dismiss();
                                                     }
                                                 })
                                                 .show();
                }

                break;

            case R.id.sortByAdded:

                bookmarkedPlaceList.clear();

                bookMarksTabLayout.clearOnTabSelectedListeners();

                int prevTab = bookMarksTabLayout.getSelectedTabPosition();

                bookMarksTabLayout.removeAllTabs();

                checkAndInitBookmarks("Hotel");
                checkAndInitBookmarks("Restaurant");
                checkAndInitBookmarks("Tourist Spot");

                bookMarksTabLayout.getTabAt(prevTab).select();

                changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE, recyclerView, bookMarksTabLayout);

                break;

            case R.id.sortByPlace:

                for (String placeKey : bookmarkedPlaceList.keySet()) {

                    Collections.sort(bookmarkedPlaceList.get(placeKey), new Comparator<Place>() {

                        @Override
                        public int compare(Place o1, Place o2) {
                            return o1.getPlaceName().compareTo(o2.getPlaceName());
                        }
                    });

                }

                changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE, recyclerView, bookMarksTabLayout);

                break;

            case android.R.id.home:

                onBackPressed();

                break;

            case R.id.deleteBookMark:

                Utilities.changeActionBarLayout(this,
                                                toolbar,
                                                bookMarksMenu,
                                                R.menu.book_marks_delete_menu,
                                                "Delete");

                changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                                   recyclerView,
                                   bookMarksTabLayout);

                break;

            case R.id.cancelDelete:

                Utilities.changeActionBarLayout(this,
                                                toolbar,
                                                bookMarksMenu,
                                                R.menu.book_marks_menu,
                                                "Bookmarks");

                MainInterfaceAdapter.setAllCheckToggleStates(false);

                changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                        recyclerView,
                        bookMarksTabLayout);

                break;

            case R.id.delete:

                if (MainInterfaceAdapter.checkToggleState()) {

                    new AlertDialog.Builder(this).setTitle("warning")
                            .setMessage("delete selected bookmarks?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new AlertDialog.Builder(BookMarksActivity.this)
                                                   .setMessage("do you want also to delete your bookmarks on cloud?")
                                                   .setPositiveButton("yes", new DialogInterface.OnClickListener() {

                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {

                                                           if (Utilities.checkNetworkState(BookMarksActivity.this)) {

                                                               try {

                                                                   JSONArray deleteBookmarkArray = new JSONArray();
                                                                   JSONObject deleteUserBookmarks = new JSONObject();

                                                                   String[] placeIDArray = getDeletedBookmarks();

                                                                   for (String placeID : placeIDArray)
                                                                       deleteBookmarkArray.put(placeID);

                                                                   deleteUserBookmarks.put("userDeletedBookmarks", deleteBookmarkArray);

                                                                   bookmarksDB.deleteBookmark(placeIDArray);

                                                                   deleteBookmarksOnCloud(userData.getString("accountID", ""),
                                                                           deleteUserBookmarks.toString());

                                                                   Log.d("debug", deleteUserBookmarks.toString());

                                                               } catch (JSONException e) {
                                                                   Log.d("debug", e.getMessage());
                                                               }

                                                           } else {

                                                               resetDelete();

                                                               Toast.makeText(getApplicationContext(), "cant delete no connection", Toast.LENGTH_LONG).show();
                                                           }

                                                       }


                                                   })
                                                   .setNegativeButton("no", new DialogInterface.OnClickListener() {

                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           bookmarksDB.deleteBookmark(getDeletedBookmarks());
                                                           resetDelete();
                                                       }
                                                   })
                                                   .show();



                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();

                } else {

                    new AlertDialog.Builder(this)
                                   .setTitle("warning")
                                   .setMessage("please select a place")
                                   .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                       }
                                   })
                                   .create()
                                   .show();

                }

                break;


            case R.id.selectAll:

                    selectAllCheckBox(recyclerView,
                                      MainInterfaceAdapter.getCheckToggleStates().get(getPlaceTypeStr(currentlySelectedPlace)),
                                      true);



                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private class SyncBookmarkTask extends AsyncTask<String, Integer, Void> {

        ProgressDialog syncProgressDialog;
        DbHelper bookmarkDB;
        Context context;

        public SyncBookmarkTask(Context con, ProgressDialog spD, DbHelper bd) {
            context = con;
            syncProgressDialog = spD;
            bookmarkDB = bd;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                if (bookmarkDB.isNotEmpty())
                    bookmarkDB.clearBookmarks();

                JSONObject userBookmark = new JSONObject(params[0]);

                JSONArray bookmarks = userBookmark.getJSONArray("userBookmarks");

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
                    place.setPlaceRating(bookmarkElement.getString("Rating"));
                    place.setRecommended(bookmarkElement.getString("Recommended"));
                    place.setBookmarks(bookmarkElement.getString("Bookmarks"));

                    bookmarkDB.addToFavorites(place);

                    publishProgress(i + 1, bookmarks.length());

                }


            } catch (JSONException e) {
                Log.d("debug", e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            syncProgressDialog.dismiss();
            refreshBookmarks();
            invalidateOptionsMenu();

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            syncProgressDialog.setMessage("syncing " + values[0] + " out of " + values[1] + " bookmarks");

        }
    }

}
