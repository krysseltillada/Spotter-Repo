package com.lmos.spotter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by linker on 02/06/2017.
 */

public class SearchResultsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);

        //Set collapse & expanded title color
        collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);

        //Set the title on collapsing toolbar
        collapsingToolbarLayout.setTitle("");

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);

        //Set tab items
        tabLayout.addTab(tabLayout.newTab().setText("All"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Hotels"));
        tabLayout.addTab(tabLayout.newTab().setText("Foods"));
        tabLayout.addTab(tabLayout.newTab().setText("Tour"));


        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_content_holder, new FragmentSearchResultGeneral(), "General")
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }
}
