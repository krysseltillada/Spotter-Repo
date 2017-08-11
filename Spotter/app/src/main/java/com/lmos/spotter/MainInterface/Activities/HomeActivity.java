package com.lmos.spotter.MainInterface.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.AccountInterface.Fragments.FragmentSignUp;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String pageCount = "10";
    SharedPreferences userData;
    static PlaceLoader placeLoader;
    static PlaceLoader itemLoader;
    NavigationView navigationView;
    private Window homeActivityWindow;
    private ImageView imgOfflineImage;
    private NestedScrollView homeNestedScrollView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DrawerLayout drawerLayout;
    private View actionBarView;
    private TextView txtOfflineMessage;
    private TextView txtHome;
    private TextView txtMostPopular;
    private TextView featureLabel;
    private SearchView searchBTN;
    private FloatingActionButton floatingActionButton;
    private AppBarLayout appBarLayout;
    private MainInterfaceAdapter mainInterfaceAdapter;
    private RecyclerView tabLayoutRecyclerView;
    private ProgressBar recycleViewProgressBar;
    private ProgressBar mostPopularProgressBar;
    private TabLayout tabLayout;
    private TextView mostPopularName;
    private ImageView userProfileImage;
    private TextView userName;
    private TextView userEmail;
    static private int startingIndex;
    static private int tableCount;
    static private String placeType = "";
    private Activity activity = this;
    private ActionBarDrawerToggle drawerToggle;
    private List<Place> placeDataList;
    private ViewFlipper mostPopularViewFlipper;
    private ViewFlipper featureViewFlipper;
    private ImageView[] mostPopularImages;
    private ImageView[] featuredImages;
    private Place[] mostPopularPlaces;
    private ArrayList<Place> featuredPlacesList;
    private SwipeRefreshLayout pullUpLoadLayout;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;

    boolean isTimeout = false;
    int timeoutCount = 5;
    int currentCount = 0;

    LinearLayout []frontLayouts;
    LinearLayout navHeaderLayout;

    String currentDate;

    static private boolean isLoadingItem = false;
    //static private boolean isLoadingPlace = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeDataList = new ArrayList<>();

        mostPopularImages = new ImageView[3];
        mostPopularPlaces = new Place[3];

        featuredImages = new ImageView[3];

        frontLayouts = new LinearLayout[4];

        featuredPlacesList = new ArrayList<>();


        for (int i = 0; i != mostPopularPlaces.length; ++i)
            mostPopularPlaces[i] = new Place();



        initComp();

        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        loadData("General");


    }

    private void loadData (String pt) {

        if (Utilities.checkNetworkState(this)) {

            isTimeout = false;
            currentCount = 0;


            imgOfflineImage.setVisibility(View.GONE);
            txtOfflineMessage.setVisibility(View.GONE);

            txtMostPopular.setText("Most Popular");

            if (homeNestedScrollView.getVisibility() != View.VISIBLE)
                homeNestedScrollView.setVisibility(View.VISIBLE);


            loadPlacesByType(pt, false);

        } else {

            txtHome.setText((pt.equals("General") ? "Home" : pt));

            imgOfflineImage.setVisibility(View.VISIBLE);
            txtOfflineMessage.setVisibility(View.VISIBLE);
            txtMostPopular.setText("no connection");
            homeNestedScrollView.setVisibility(View.GONE);

            appBarLayout.setExpanded(false);

        }

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /** Re-run placeLoader here **/
        displayUserInfo();

        if(!searchBTN.isIconified())
            searchBTN.setIconified(true);

    }

    private void checkTimeout () {
        if (timeoutCount >= currentCount) {

            isTimeout = true;

            pullUpLoadLayout.setEnabled(true);
            imgOfflineImage.setVisibility(View.VISIBLE);
            txtOfflineMessage.setVisibility(View.VISIBLE);
            txtMostPopular.setText("no connection");
            homeNestedScrollView.setVisibility(View.GONE);

            appBarLayout.setExpanded(false);

        }
        else
            ++currentCount;
    }

    private void loadPlacesFromServer(final String pType, final String sType) {


        if (itemLoader != null) {
            Log.d("debug", "item exist");
            itemLoader.cancel(true);
            itemLoader = null;
        }

        if (placeLoader != null) {
            placeLoader.cancel(true);
            placeLoader = null;
        }


        if (placeDataList.size() > 0) {

            placeDataList.clear();

            mainInterfaceAdapter.notifyDataSetChanged();

        }

        recycleViewProgressBar.setVisibility(View.VISIBLE);

        placeLoader = new PlaceLoader().setOnRespondError(new OnRespondError() {

            @Override
            public void onRespondError(String error) {


                Log.d("debug", "error trying to get the data again");
                Log.d("debug", "error");

                new PlaceLoader().setOnRespondError(this)
                                 .execute("0", pageCount, pType, sType);


               // HomeActivity.this.recycleViewProgressBar.setVisibility(View.GONE);
               // HomeActivity.this.itemListProgressBar.setVisibility(View.GONE);

            }

        });

        placeLoader.execute("0", pageCount, pType, sType);

        homeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

                                                           @Override
                                                           public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                                                               if (Utilities.checkIfLastScrolledItem(v, scrollX, scrollY, oldScrollX, oldScrollY)) {


                                                                   if (startingIndex < tableCount &&
                                                                           placeDataList.size() > 0 && !isLoadingItem) {


                                                                       placeDataList.add(null);
                                                                       mainInterfaceAdapter.notifyItemInserted(placeDataList.size() - 1);

                                                                       isLoadingItem = true;

                                                                       new Handler().postDelayed(new Runnable() {
                                                                           @Override
                                                                           public void run() {

                                                                               itemLoader = new PlaceLoader().setOnRespondError(new OnRespondError() {

                                                                                   @Override
                                                                                   public void onRespondError(String error) {


                                                                                       Log.d("debug", "error getting items from the server im tryingg");
                                                                                       Log.d("debug", error);

                                                                                       itemLoader = new PlaceLoader().setOnRespondError(this);
                                                                                       itemLoader.execute(String.valueOf(startingIndex), pageCount, placeType, sType);


                                                                                   }
                                                                               });

                                                                               itemLoader.execute(String.valueOf(startingIndex), pageCount, placeType, sType);


                                                                           }
                                                                       }, 200);

                                                                   }

                                                               }
                                                           }
                                                       }
        );


    }

    private void loadPlacesByType(final String type, boolean forceLoad) {

        Log.d("debug", "type: " + type + " placeType: " + placeType);

        if (!type.equals(placeType) || forceLoad) {

            appBarLayout.setExpanded(false);
            homeNestedScrollView.smoothScrollTo(0, 0);

            txtHome.setText((type.equals("General") ? "Home" : type));

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            mainInterfaceAdapter = new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.HOME_ACTIVITY,
                    PlaceType.NONE,
                    placeDataList);

            tabLayoutRecyclerView.setAdapter(mainInterfaceAdapter);

            String selectedSortType = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

            getFeaturedPlaces(currentDate, type);
            getMostPopular(type);
            loadPlacesFromServer(type, (selectedSortType.equals("Most Viewed") ? "Views" :
                    (selectedSortType.equals("Most Popular")) ? "Rating" : "Recommended"));

            tabLayoutRecyclerView.setNestedScrollingEnabled(false);

            tabLayoutRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:

                            floatingActionButton.setVisibility(View.GONE);

                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:

                            floatingActionButton.setVisibility(View.VISIBLE);

                            break;
                    }
                }
            });


            tabLayoutRecyclerView.setLayoutManager(linearLayoutManager);

            if (tabLayout.getTabCount() > 0) {

                tabLayoutRecyclerView.removeAllViews();
                tabLayout.clearOnTabSelectedListeners();

            }

            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                    searchBTN.setIconified(true);

                    if (homeNestedScrollView.getVisibility() != View.GONE) {

                        switch (tab.getPosition()) {
                            case 0:
                                loadPlacesFromServer(placeType, "Views");
                                break;
                            case 1:
                                loadPlacesFromServer(placeType, "Rating");
                                break;
                            case 2:
                                loadPlacesFromServer(placeType, "Recommended");
                                break;
                        }

                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                    searchBTN.setIconified(true);
                }
            });


            placeType = type;

        }

    }


    private void displayUserInfo () {

        navigationView.getMenu().clear();

        if (userData.getString("status", "").equals("Logged In")) {

            navigationView.inflateMenu(R.menu.main_drawer);

            Log.d("debug", "username: " + userData.getString("accountUsername", ""));
            Log.d("debug", "email: " + userData.getString("accountEmail", ""));
            Log.d("debug", "password: " + userData.getString("accountPassword", ""));
            Log.d("debug", "accountid: " + userData.getString("accountID", ""));
            Log.d("debug", "image: " + userData.getString("accountImage", ""));
            Log.d("debug", "name: " + userData.getString("accountName", ""));

            userProfileImage.setImageDrawable(new BitmapDrawable(getResources(), Utilities.BlurImg.stringToBitmap(userData.getString("accountImage", ""))));
            userName.setText(userData.getString("accountName", ""));
            userEmail.setText(userData.getString("accountEmail", ""));

            userEmail.setVisibility(View.VISIBLE);

        } else {

            navigationView.inflateMenu(R.menu.main_drawer_guest);

            userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.account));
            userName.setText("Guest");
            userEmail.setVisibility(View.GONE);

        }

        changeItemTitleColor(R.id.menuTitle, R.color.white);
        changeItemTitleColor(R.id.settingsTitle, R.color.white);

    }

    private void startCheckingConnection (final Activity activity) {

        final Snackbar snackBarCheckConnection = Snackbar.make(activity.getWindow().getDecorView(), "Your Device is Offline", Snackbar.LENGTH_INDEFINITE)
                .setAction("Go to Bookmarks", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        activity.startActivity(new Intent(activity, BookMarksActivity.class));
                    }
                })
                .setActionTextColor(activity.getResources().getColor(R.color.colorAccent));

            new Utilities.ConnectionNotifier(getApplicationContext())
                         .setDeviceOfflineListener(new Utilities.ConnectionNotifier.OnDeviceOfflineListener() {

                             @Override
                             public void OnDeviceOfflineListener(boolean networkState) {

                                 if (!networkState) {

                                     if (!snackBarCheckConnection.isShown())
                                         snackBarCheckConnection.show();

                                 } else {

                                     if (snackBarCheckConnection.isShown())
                                         snackBarCheckConnection.dismiss();

                                 }

                             }
                         }).start();


    }

    private void changeItemTitleColor (int itemID, int itemColor) {
        MenuItem menuItem = navigationView.getMenu().findItem(itemID);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(itemColor)), 0, s.length(), 0);
        menuItem.setTitle(s);
    }


    private void initComp() {

        MainInterfaceAdapter.setOnItemClickListener(new MainInterfaceAdapter.OnAdapterItemClickListener(){
            @Override
            public void onItemClick(Place place) {

                Intent displayResult = new Intent(getApplicationContext(), SearchResultsActivity.class);
                displayResult.putExtra("data", new String[]{ "Home", "" });
                displayResult.putExtra("Place", place);
                startActivity(displayResult);
            }
        });

        homeActivityWindow = getWindow();

        userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        setContentView(R.layout.activity_home_menu);

        startCheckingConnection(this);

        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded())
                    interstitialAd.show();
            }
        });


        bannerAdView = (AdView)findViewById(R.id.adBanner);

        bannerAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        pullUpLoadLayout = (SwipeRefreshLayout) findViewById(R.id.pullUpLoadLayout);


        pullUpLoadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.d("debug", "pulled up");

                pullUpLoadLayout.setRefreshing(false);

                if (!placeDataList.isEmpty()) {

                    placeDataList.clear();
                    mainInterfaceAdapter.notifyDataSetChanged();

                }

                if (Utilities.checkNetworkState(HomeActivity.this)) {

                    appBarLayout.setExpanded(false);

                    if (homeNestedScrollView.getVisibility() != View.VISIBLE) {
                        homeNestedScrollView.setVisibility(View.VISIBLE);
                        Log.d("debug", "connecting");

                        txtMostPopular.setText("Most Popular");
                        txtOfflineMessage.setVisibility(View.GONE);
                        imgOfflineImage.setVisibility(View.GONE);

                        placeType = txtHome.getText().toString();

                        loadPlacesByType(placeType.equals("Home") ? "General" : placeType, true);
                        return;
                    }

                    String selectedSortType = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

                    getFeaturedPlaces(currentDate, placeType);
                    getMostPopular(placeType);
                    loadPlacesFromServer(placeType, (selectedSortType.equals("Most Viewed") ? "Views" :
                            (selectedSortType.equals("Most Popular")) ? "Rating" : "Recommended"));

                } else {

                    pullUpLoadLayout.setEnabled(true);
                    imgOfflineImage.setVisibility(View.VISIBLE);
                    txtOfflineMessage.setVisibility(View.VISIBLE);
                    txtMostPopular.setText("no connection");
                    homeNestedScrollView.setVisibility(View.GONE);

                    appBarLayout.setExpanded(false);

                }

            }
        });

        pullUpLoadLayout.setProgressViewOffset(false, 0, 180);
        pullUpLoadLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        txtMostPopular = (TextView)findViewById(R.id.home_header_lbl);

        imgOfflineImage = (ImageView)findViewById(R.id.imgOfflineImage);

        txtOfflineMessage = (TextView)findViewById(R.id.txtOfflineMessage);

        mostPopularProgressBar = (ProgressBar)findViewById(R.id.mostPopularProgressBar);

        mostPopularViewFlipper = (ViewFlipper) findViewById(R.id.mostPopularViewFlipper);
        featureViewFlipper = (ViewFlipper) findViewById(R.id.featureViewFlipper);

        featureLabel = (TextView)findViewById(R.id.mostFeaturePlaceName);

        frontLayouts[0] = (LinearLayout) findViewById(R.id.mostPopularBlackBackground1);
        frontLayouts[1] = (LinearLayout) findViewById(R.id.mostPopularBlackBackground);
        frontLayouts[2] = (LinearLayout) findViewById(R.id.featureLabelBlackBackground);
        frontLayouts[3] = (LinearLayout) findViewById(R.id.featurePlaceBlackBackground);

        featuredImages[0] = (ImageView) findViewById(R.id.featureImageView1);
        featuredImages[1] = (ImageView) findViewById(R.id.featureImageView2);
        featuredImages[2] = (ImageView) findViewById(R.id.featureImageView3);

        mostPopularImages[0] = (ImageView) findViewById(R.id.popularImageView1);
        mostPopularImages[1] = (ImageView) findViewById(R.id.popularImageView2);
        mostPopularImages[2] = (ImageView) findViewById(R.id.popularImageView3);

        mostPopularName = (TextView) findViewById(R.id.most_pop_name);

        tabLayoutRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

        recycleViewProgressBar = (ProgressBar) findViewById(R.id.recycleViewProgressBar);

        homeNestedScrollView = (NestedScrollView) findViewById(R.id.homeContentScrollView);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);

        collapsingToolbarLayout.setTitleEnabled(false);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0 && tabLayout.getVisibility() != View.GONE) {

                    Utilities.animateViewColor(toolbar,
                                               getResources().getColor(R.color.colorPrimary),
                                               getResources().getColor(R.color.cardview_dark_background),
                                               250);

                    homeActivityWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    homeActivityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    homeActivityWindow.setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.colorAccent));

                    tabLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                    tabLayout.setVisibility(View.GONE);

                    tabLayoutRecyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                    tabLayoutRecyclerView.setVisibility(View.INVISIBLE);

                    navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
                    navigationView.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));


                } else if (verticalOffset < -250 && tabLayout.getVisibility() != View.VISIBLE) {

                    Utilities.animateViewColor(toolbar,
                                               getResources().getColor(R.color.blackTransparent),
                                               getResources().getColor(R.color.colorPrimary),
                                               250);

                    homeActivityWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    homeActivityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    homeActivityWindow.setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimary));

                    tabLayout.setVisibility(View.VISIBLE);
                    tabLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

                    tabLayoutRecyclerView.setVisibility(View.VISIBLE);
                    tabLayoutRecyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

                    navHeaderLayout.setBackground(getResources().getDrawable(R.drawable.littleleaf));
                    navigationView.setBackground(getResources().getDrawable(R.drawable.littleleaf));

                }

                if (homeNestedScrollView.getVisibility() != View.GONE) {

                    if (!pullUpLoadLayout.isRefreshing())
                        pullUpLoadLayout.setEnabled(verticalOffset == 0);

                }

            }
        });

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);

        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.searchbar, null);
        Utilities.setSearchBar(this, actionBarView);

        txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

        final SearchView searchButton = (SearchView) actionBarView.findViewById(R.id.search_view);
        searchBTN = searchButton;

        /** Set drawer and navigation layout **/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchButton.setIconified(true);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                Log.d("debug", "drawer open");

                pullUpLoadLayout.setEnabled(false);
                pullUpLoadLayout.setRefreshing(false);
                Log.d("debug", "pull state: " + pullUpLoadLayout.isEnabled());
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchButton.setIconified(true);

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                Log.d("debug", "drawer close");

                pullUpLoadLayout.setEnabled(true);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        navHeaderLayout = (LinearLayout) headerLayout.findViewById(R.id.navHeaderBackground);

        userName = (TextView)headerLayout.findViewById(R.id.userName);
        userEmail = (TextView)headerLayout.findViewById(R.id.userEmail);
        userProfileImage = (ImageView)headerLayout.findViewById(R.id.userProfileImage);

        userProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (userData.getString("status", "").equals("Logged In")) {

                    Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePic.resolveActivity(HomeActivity.this.getPackageManager()) != null) {
                        startActivityForResult(takePic, FragmentSignUp.TAKE_PHOTO_REQUEST);
                    }

                }
            }
        });

        displayUserInfo();

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.settingsTitle,
                R.style.navDrawerTitleStyle);

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.menuTitle,
                R.style.navDrawerTitleStyle);


        /** End of setting drawer and navigation drawer **/

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchBTN.setIconified(true);
                startActivity(
                        new Intent(getApplicationContext(), SearchResultsActivity.class)
                                .putExtra("data", new String[]{"Location", ""}));
            }



        });

    }

    private void getMostPopular(final String type) {

        mostPopularViewFlipper.stopFlipping();

        mostPopularViewFlipper.setVisibility(View.INVISIBLE);
        featureViewFlipper.setVisibility(View.INVISIBLE);

        for (LinearLayout frontLayout : frontLayouts)
            frontLayout.setVisibility(View.INVISIBLE);

        mostPopularProgressBar.setVisibility(View.VISIBLE);

        final RequestQueue request = Volley.newRequestQueue(getApplicationContext());

        final String ptype = type;

        String requestURL = "http://admin-spotter.000webhostapp.com/app_scripts/mostPopular.php";

        StringRequest mostPopularRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("debug", response);

                        mostPopularProgressBar.setVisibility(View.GONE);

                        try {

                            JSONObject jsonPopularData = new JSONObject(response);

                            JSONArray mostPopularDataArr = jsonPopularData.getJSONArray("mostPopularPlaces");

                            for (int i = 0; i != mostPopularDataArr.length(); ++i) {

                                JSONObject mostPopularData = new JSONObject(mostPopularDataArr.getString(i));

                                String imageLink = mostPopularData.getString("ImageLink");

                                if (imageLink.length() > 0) {

                                    JSONObject placeImage = new JSONObject(imageLink);

                                    JSONObject placeImages = new JSONObject(placeImage.getString("placeImages"));

                                    JSONArray imageLinks = placeImages.getJSONArray("previewImages");

                                    Log.d("debug", imageLinks.getString(0));

                                    Random randomImage = new Random();

                                    Picasso.with(getApplicationContext())
                                            .load(imageLinks.getString(randomImage.nextInt(imageLinks.length())))
                                            .placeholder(R.drawable.landscape_placeholder)
                                            .fit()
                                            .into(mostPopularImages[i]);


                                } else {

                                    Picasso.with(getApplicationContext())
                                            .load("http://vignette2.wikia.nocookie.net/date-a-live/images/5/57/Yoshino.%28Date.A.Live%29.full.1536435.jpg/revision/latest?cb=20140225221532")
                                            .placeholder(R.drawable.loadingplace)
                                            .fit()
                                            .into(mostPopularImages[i]);

                                }

                                Log.d("debug", mostPopularData.getString("Name"));

                                mostPopularPlaces[i].setPlaceID(mostPopularData.getString("placeID"));
                                mostPopularPlaces[i].setplaceName(mostPopularData.getString("Name"));
                                mostPopularPlaces[i].setplaceType(mostPopularData.getString("Type"));
                                mostPopularPlaces[i].setplaceAddress(mostPopularData.getString("Address"));
                                mostPopularPlaces[i].setPlaceLat(mostPopularData.getString("Latitude"));
                                mostPopularPlaces[i].setPlaceLng(mostPopularData.getString("Longitude"));
                                mostPopularPlaces[i].setplaceLocality(mostPopularData.getString("Locality"));
                                mostPopularPlaces[i].setplaceDescription(mostPopularData.getString("Description"));
                                mostPopularPlaces[i].setplaceImageLink(imageLink);
                                mostPopularPlaces[i].setplaceClass(mostPopularData.getString("Class"));
                                mostPopularPlaces[i].setplacePriceRange(mostPopularData.getString("PriceRange"));
                                mostPopularPlaces[i].setRecommended(mostPopularData.getString("Recommended"));
                                mostPopularPlaces[i].setRating(mostPopularData.getString("Rating"));
                                mostPopularPlaces[i].setUserReviews(mostPopularData.getString("userReviews"));
                                mostPopularPlaces[i].setBookmarks(mostPopularData.getString("Bookmarks"));

                            }


                        } catch (JSONException e) {
                            Log.d("debug", e.getMessage());
                        }

                        startMostPopularFlipping();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", "most popular trying to get data from server");

                request.stop();

                if (!isTimeout) {

                    checkTimeout();
                    getMostPopular(type);

                }

            }
        }) {
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("placeType", ptype);
                }};
            }
        };

        request.add(mostPopularRequest);

    }

    private void getFeaturedPlaces(final String date, final String pt) {

        RequestQueue requestFeaturedPlaces = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringFeaturedPlaces = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/getFeaturePlace.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        if (!response.equals("no data")) {

                            try {

                                Log.d("debug", response);

                                featuredPlacesList.clear();

                                JSONArray featuredPlaces = new JSONObject(response).getJSONArray("featurePlaces");

                                for (int i = 0; i != featuredPlaces.length(); ++i) {

                                    JSONObject featuredPlaceJSON = featuredPlaces.getJSONObject(i);

                                    Place featuredPlace = new Place();

                                    String imageLink = featuredPlaceJSON.getString("ImageLink");

                                    if (imageLink.length() > 0) {

                                        JSONObject placeImage = new JSONObject(imageLink);

                                        JSONObject placeImages = new JSONObject(placeImage.getString("placeImages"));

                                        JSONArray imageLinks = placeImages.getJSONArray("previewImages");

                                        Random randomImage = new Random();

                                        Picasso.with(getApplicationContext())
                                                .load(imageLinks.getString(randomImage.nextInt(imageLinks.length())))
                                                .placeholder(R.drawable.landscape_placeholder)
                                                .fit()
                                                .into(featuredImages[i]);


                                    } else {

                                        Picasso.with(getApplicationContext())
                                                .load("http://vignette2.wikia.nocookie.net/date-a-live/images/5/57/Yoshino.%28Date.A.Live%29.full.1536435.jpg/revision/latest?cb=20140225221532")
                                                .placeholder(R.drawable.loadingplace)
                                                .fit()
                                                .into(featuredImages[i]);

                                    }

                                    featuredPlace.setPlaceID(featuredPlaceJSON.getString("placeID"));
                                    featuredPlace.setplaceName(featuredPlaceJSON.getString("Name"));
                                    featuredPlace.setplaceType(featuredPlaceJSON.getString("Type"));
                                    featuredPlace.setplaceAddress(featuredPlaceJSON.getString("Address"));
                                    featuredPlace.setPlaceLat(featuredPlaceJSON.getString("Latitude"));
                                    featuredPlace.setPlaceLng(featuredPlaceJSON.getString("Longitude"));
                                    featuredPlace.setplaceLocality(featuredPlaceJSON.getString("Locality"));
                                    featuredPlace.setplaceDescription(featuredPlaceJSON.getString("Description"));
                                    featuredPlace.setplaceImageLink(imageLink);
                                    featuredPlace.setplaceClass(featuredPlaceJSON.getString("Class"));
                                    featuredPlace.setplacePriceRange(featuredPlaceJSON.getString("PriceRange"));
                                    featuredPlace.setRecommended(featuredPlaceJSON.getString("Recommended"));
                                    featuredPlace.setRating(featuredPlaceJSON.getString("Rating"));
                                    featuredPlace.setUserReviews(featuredPlaceJSON.getString("userReviews"));
                                    featuredPlace.setBookmarks(featuredPlaceJSON.getString("Bookmarks"));

                                    featuredPlacesList.add(featuredPlace);

                                }

                                startFeaturedFlipping();

                            } catch (JSONException e) {
                                Log.d("debug", e.getMessage());
                            }

                        } else {

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<String, String>(){{
                   put("date", date);
                   put("placeType", pt);
                }};
            }
        };

        requestFeaturedPlaces.add(stringFeaturedPlaces);

    }

    private void startFeaturedFlipping () {

        featureViewFlipper.setDisplayedChild(0);

        featureViewFlipper.setVisibility(View.VISIBLE);

        for (LinearLayout frontLayout : frontLayouts)
            frontLayout.setVisibility(View.VISIBLE);

        appBarLayout.setExpanded(true);

        featureViewFlipper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBTN.setIconified(true);
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                Intent sendFeaturedPlace = new Intent(getApplicationContext(), SearchResultsActivity.class);
                sendFeaturedPlace.putExtra("Place", featuredPlacesList.get(featureViewFlipper.getDisplayedChild()));
                sendFeaturedPlace.putExtra("data", new String[]{ "Home", "" });
                startActivity(sendFeaturedPlace);
            }

        });

        if (!placeType.equals("General"))
            featureViewFlipper.stopFlipping();
        else
            featureViewFlipper.startFlipping();

        featureViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        featureViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        featureViewFlipper.setFlipInterval(3000);

        featureViewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.d("debug", "index feature: " + featureViewFlipper.getDisplayedChild());

                featureLabel.setAnimation(AnimationUtils.loadAnimation(HomeActivity.this, android.R.anim.fade_in));
                featureLabel.setText(featuredPlacesList.get(featureViewFlipper.getDisplayedChild()).getPlaceName());

            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void startMostPopularFlipping() {

        mostPopularViewFlipper.setDisplayedChild(0);
        mostPopularName.setText(mostPopularPlaces[0].getPlaceName());

        mostPopularViewFlipper.setVisibility(View.VISIBLE);

        for (LinearLayout frontLayout : frontLayouts)
            frontLayout.setVisibility(View.VISIBLE);

        appBarLayout.setExpanded(true);

        mostPopularViewFlipper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBTN.setIconified(true);
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                Intent sendMostPopularPlace = new Intent(getApplicationContext(), SearchResultsActivity.class);
                sendMostPopularPlace.putExtra("Place", mostPopularPlaces[mostPopularViewFlipper.getDisplayedChild()]);
                sendMostPopularPlace.putExtra("data", new String[]{ "Home", "" });
                startActivity(sendMostPopularPlace);
            }

        });

        mostPopularViewFlipper.startFlipping();

        mostPopularViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        mostPopularViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        mostPopularViewFlipper.setFlipInterval(3000);

        mostPopularViewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.d("debug", "index mostpopular: " + mostPopularViewFlipper.getDisplayedChild());
                mostPopularName.setAnimation(AnimationUtils.loadAnimation(HomeActivity.this, android.R.anim.fade_in));
                mostPopularName.setText(mostPopularPlaces[mostPopularViewFlipper.getDisplayedChild()].getPlaceName());
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        switch (item.getItemId()) {

            case R.id.signIn:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.signOut:
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("status", "false");
                editor.apply();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.Home:
                loadData("General");
                break;
            case R.id.Hotels:
                loadData("Hotel");
                break;
            case R.id.Restaurants:
                loadData("Restaurant");
                break;
            case R.id.TouristSpots:
                loadData("Tourist Spot");
                break;
            case R.id.Favorites:
                Utilities.OpenActivity(getApplicationContext(),
                        BookMarksActivity.class, null);
                break;
            case R.id.Settings:

                Utilities.OpenActivity(getApplicationContext(),
                        SettingsActivity.class, null);

                break;

        }

        return true;
    }

    private void updateProfilePicture (final Bitmap profilePicture, final String accountID) {

        final ProgressDialog updateProfileProgress = new ProgressDialog(this);

        updateProfileProgress.setIndeterminate(true);
        updateProfileProgress.setCancelable(false);
        updateProfileProgress.setMessage("updating profile picture..");
        updateProfileProgress.show();

        final RequestQueue updateProfilePicture = Volley.newRequestQueue(getApplicationContext());

        StringRequest updateProfileRequest = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/updateProfilePicture.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        updateProfileProgress.dismiss();

                        if (response.equals("account updated")) {

                            LoginActivity.set_login_prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE).edit();

                            Toast.makeText(HomeActivity.this, "updated successfully..", Toast.LENGTH_LONG).show();
                            LoginActivity.set_login_prefs.putString("accountImage", Utilities.BlurImg.bitmapToString(profilePicture));
                            LoginActivity.set_login_prefs.apply();
                            userProfileImage.setImageDrawable(new BitmapDrawable(getResources(), Utilities.rotateBitmapCorrectly(profilePicture, HomeActivity.this)));


                        }
                        else
                            Toast.makeText(HomeActivity.this, "update failed", Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "please check your connection", Toast.LENGTH_LONG).show();
            }
        }){

            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{

                    put("accountID", accountID);
                    put("profileImage", Utilities.BlurImg.bitmapToString(profilePicture));
                }};
            }

        };

        updateProfilePicture.add(updateProfileRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FragmentSignUp.TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            updateProfilePicture((Bitmap)data.getExtras().get("data"), userData.getString("accountID", ""));
        }

        if (!searchBTN.isIconified()) {
            searchBTN.setIconified(true);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (!searchBTN.isIconified())
                searchBTN.setIconified(true);
            else
                super.onBackPressed();

        }
    }

    public interface OnRespondError {
        void onRespondError(String error);
    }

    private class PlaceLoader extends AsyncTask<String, Void, AppScript> {

        OnRespondError onRespondError;
        AppScript loadPlaces;

        public PlaceLoader setOnRespondError(OnRespondError onRespondError) {
            this.onRespondError = onRespondError;
            return this;
        }

        @Override
        protected AppScript doInBackground(final String... params) {

            loadPlaces = new AppScript(activity) {{
                setData("loadPlaces.php", new HashMap<String, String>() {{
                    put("currentRow", params[0]);
                    put("rowOffset", params[1]);
                    put("placeType", params[2]);
                    put("sortType", params[3]);
                }});
            }};


            return loadPlaces;
        }


        @Override
        protected void onPostExecute(AppScript loadPlaces) {
            super.onPostExecute(loadPlaces);

            try {

                Log.d("debug", "placeDatalist size: " + placeDataList.size());

                tableCount = Integer.parseInt(loadPlaces.getTableCount());
                startingIndex = Integer.parseInt(loadPlaces.getOffSet());

                if (isLoadingItem) {

                    placeDataList.remove(placeDataList.size() - 1);
                    mainInterfaceAdapter.notifyItemRemoved(placeDataList.size());

                    isLoadingItem = false;

                }

                List<Place> placeD = loadPlaces.getPlacesList();

                for (Place place : placeD)
                    placeDataList.add(place);

                mainInterfaceAdapter.notifyDataSetChanged();

                recycleViewProgressBar.setVisibility(View.GONE);

                Log.d("debug", "place data size: " + placeDataList.size());

                placeLoader = null;

            } catch (Exception e) {
                e.printStackTrace();
                onRespondError.onRespondError(e.getMessage());
            }

        }

    }
}



