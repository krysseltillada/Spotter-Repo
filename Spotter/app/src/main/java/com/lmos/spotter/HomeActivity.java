package com.lmos.spotter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int currentImage = 0;

    void startMostPopularAnimation (final Bitmap[] slideImages, final ImageView imageView) {

        Timer slideImageTimer = new Timer ();
        slideImageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (currentImage >= slideImages.length)
                            currentImage = 0;

                        imageView.setImageBitmap(slideImages[currentImage]);

                        imageView.setAnimation(
                                AnimationUtils.loadAnimation(getApplicationContext(),
                                        R.anim.image_slide_left_to_right));

                        ++currentImage;

                    }
                });

            }
        }, 0, 5000);

    }

    void initializeUI () {

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        PlaceTabPagerAdapter placePagerAdapter = new PlaceTabPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(placePagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(getApplicationContext(), String.valueOf(tab.getPosition()), Toast.LENGTH_LONG).show();
                viewPager.setCurrentItem(tab.getPosition());

                final Snackbar message = Snackbar.make(getCurrentFocus(), "GPS will be use on this app", Snackbar.LENGTH_LONG);
                message.setActionTextColor(Color.WHITE);
                message.setAction("Ok", new View.OnClickListener () {

                            @Override
                            public void onClick(View v) {
                                message.dismiss();
                            }
                        }

                );

                message.show();

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



        ActionBar homeActionBar = getSupportActionBar();

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final View actionBarView = inflator.inflate(R.layout.searchbar, null);

        SearchView searchBtn = (SearchView)actionBarView.findViewById(R.id.search_view);

        searchBtn.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView txtHome =  (TextView)actionBarView.findViewById(R.id.txtHome);

                txtHome.setVisibility(View.GONE);


            }
        });

        searchBtn.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                TextView txtHome =  (TextView)actionBarView.findViewById(R.id.txtHome);

                txtHome.setVisibility(View.VISIBLE);

                return false;

            }
        });


        homeActionBar.setCustomView(actionBarView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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

    Bitmap getImageResource (int id) {
        return ((BitmapDrawable)getResources().getDrawable(id)).getBitmap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        startMostPopularAnimation(new Bitmap[] {
                getImageResource(R.drawable.traveler_bg),
                getImageResource(R.drawable.traveler_bg2),
                getImageResource(R.drawable.traveler_bg3)
        }, (ImageView)findViewById(R.id.popularImageView));

        initializeUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

