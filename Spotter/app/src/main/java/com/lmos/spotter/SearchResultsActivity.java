package com.lmos.spotter;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by linker on 02/06/2017.
 *
 * This class will display the result/s of places
 * and/or specific hotels, restaurants and tourist spots.
 *
 */

public class SearchResultsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_content_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        initComp();

        Bundle fetch_intent = getIntent().getExtras();

        switch (fetch_intent.getString("type")){

            case "General":
                setHeaderText("Batangas", "Bayang ng Magigiting");
                attachFragment(new FragmentSearchResultGeneral(), "General");
                break;
            case "Hotel":
                setHeaderText("City of Dreams", "Inside Nightmare");
                attachFragment(new FragmentSearchResult(), "Hotel");
                break;

        }
    }

    private void setHeaderText(String name, String description){
        place_name.setText(name);
        place_content_desc.setText(description);
    }

    private void attachFragment(Fragment fragment, String tag){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_content_holder, fragment, tag)
                .commit();
    }

    private void initComp(){

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        place_name = (TextView) findViewById(R.id.place_name);
        place_content_desc = (TextView) findViewById(R.id.place_content_description);

        //Set collapse & expanded title color
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));

        //Set the title on collapsing toolbar
        collapsingToolbarLayout.setTitle("");

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }
}
