package com.lmos.spotter;

import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchBtn;

    //static int currentImage = 0;

    /**
     * void startMostPopularAnimation (final Bitmap[] slideImages, final ImageView imageView) {
     * <p>
     * Timer slideImageTimer = new Timer ();
     * slideImageTimer.scheduleAtFixedRate(new TimerTask() {
     *
     * @Override public void run() {
     * <p>
     * runOnUiThread(new Runnable() {
     * @Override public void run() {
     * <p>
     * if (currentImage >= slideImages.length)
     * currentImage = 0;
     * <p>
     * imageView.setImageBitmap(slideImages[currentImage]);
     * <p>
     * imageView.setAnimation(
     * AnimationUtils.loadAnimation(getApplicationContext(),
     * R.anim.image_slide_left_to_right));
     * <p>
     * ++currentImage;
     * <p>
     * }
     * });
     * <p>
     * }
     * }, 0, 5000);
     * <p>
     * }
     **/

    private void startMostPopularFlipping() {

        ViewFlipper viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);

        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }

    String[] sampleWords = {"hello", "judy", "sample", "text", "june"};

    // GONNA make a generalized search query class for this

    void QuerySearchResults(String searchValue, SimpleCursorAdapter suggestion, String[] keywords) {

        MatrixCursor suggestions = new MatrixCursor(new String[]{BaseColumns._ID, "judy"});

        for (int i = 0; i != keywords.length; ++i) {
            if (keywords[i].toLowerCase().startsWith(searchValue.toLowerCase())) {
                Log.d("sample", searchValue);
                suggestions.addRow(new Object[]{i, keywords[i]});
            }
        }

        suggestion.changeCursor(suggestions);
    }

    static float dpToPx (Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    private void initializeUI() {
        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PlaceTabPagerAdapter placePagerAdapter = new PlaceTabPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(placePagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar homeActionBar = getSupportActionBar();

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);


        final View actionBarView = inflator.inflate(R.layout.searchbar, null);

        final String[] from = new String[]{"judy"};
        final int[] to = new int[]{android.R.id.text1};


        searchBtn = (SearchView) actionBarView.findViewById(R.id.search_view);
        final SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchBtn.setSuggestionsAdapter(searchAdapter);

        searchBtn.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                startActivity(new Intent(getApplicationContext(), SearchResultsActivity.class));
                return true;
            }
        });


        searchBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String searchValue) {
                QuerySearchResults(searchValue, searchAdapter, sampleWords);
                return false;
            }
        });


        searchBtn.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

                txtHome.setVisibility(View.GONE);

            }
        });

        searchBtn.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                TextView txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

                txtHome.setVisibility(View.VISIBLE);

                return false;

            }
        });

        /**
         *
         * this code detects a backpress event and a soft keyboard down event
         * created a keyboard state object to store a boolean wether the
         * keyboard is up or down gonna make this code a utility to be useful
         *
         */

        final LinearLayout activityRootView = (LinearLayout) findViewById(R.id.home_parent_layout);
        final KeyboardState keyboardState = new KeyboardState();


        activityRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchBtn.isIconified())
                    searchBtn.setIconified(true);
            }
        });

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                    if (heightDiff > dpToPx(getApplicationContext(), 200)) {
                        if (!keyboardState.isKeyboardUp)
                            keyboardState.isKeyboardUp = true;
                    } else {
                        if (keyboardState.isKeyboardUp) {
                            searchBtn.setIconified(true);
                            keyboardState.isKeyboardUp = false;
                        }

                    }


            }
        });


        homeActionBar.setCustomView(actionBarView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "GPS is required to detect places", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    /**
     * Bitmap getImageResource (int id) {
     * return ((BitmapDrawable)getResources().getDrawable(id)).getBitmap();
     * }
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeUI();
        startMostPopularFlipping();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (searchBtn.isIconified()) {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

        } else {
            searchBtn.setIconified(true);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}