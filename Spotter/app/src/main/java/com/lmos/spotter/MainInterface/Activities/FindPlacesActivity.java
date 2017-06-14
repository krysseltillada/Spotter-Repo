package com.lmos.spotter.MainInterface.Activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.lmos.spotter.MainInterface.Fragments.FindedPlacesFragment;
import com.lmos.spotter.R;

public class FindPlacesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_places);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*getSupportFragmentManager().beginTransaction()
                                   .add(R.id.findPlaceFragment, new FindPlaceFragment(), "FindPlace")
                                   .commit();*/

        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.findPlaceFragment, new FindedPlacesFragment(), "FindedPlace")
                                   .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }


}
