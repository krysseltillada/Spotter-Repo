package com.lmos.spotter.SearchInterface.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.ReviewDialogFragment;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;

public class ReviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrollView reviewScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        reviewScrollView = (ScrollView)findViewById(R.id.activityReviewScrollView);

        reviewScrollView.smoothScrollTo(0, 0);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SearchReviewsAdapter());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.reviewFab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

               ReviewDialogFragment reviewDialogFragment = new ReviewDialogFragment();

               reviewDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
               reviewDialogFragment.show(getSupportFragmentManager(), "reviewDialogFragment");

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.review_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

}
