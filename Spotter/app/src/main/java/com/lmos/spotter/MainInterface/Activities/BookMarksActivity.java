package com.lmos.spotter.MainInterface.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

public class BookMarksActivity extends AppCompatActivity {

    FloatingActionButton fab;
    Menu bookMarksMenu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.home_tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Hotel"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookmarks");


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.book_marks_menu, menu);
        bookMarksMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();

                break;

            case R.id.deleteBookMark:

                Utilities.changeActionBarLayout(this,
                                                toolbar,
                                                bookMarksMenu,
                                                R.menu.book_marks_delete_menu,
                                                "Delete");

                break;

            case R.id.cancelDelete:

                Utilities.changeActionBarLayout(this,
                                                toolbar,
                                                bookMarksMenu,
                                                R.menu.book_marks_menu,
                                                "Bookmarks");

                break;



        }

        return super.onOptionsItemSelected(item);
    }

}
