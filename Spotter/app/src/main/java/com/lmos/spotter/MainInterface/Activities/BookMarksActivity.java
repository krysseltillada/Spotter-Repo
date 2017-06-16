package com.lmos.spotter.MainInterface.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;

public class BookMarksActivity extends AppCompatActivity {

    Menu bookMarksMenu;
    Toolbar toolbar;

    TabLayout bookMarksTabLayout;
    RecyclerView recyclerView;

    private void selectAllCheckBox (RecyclerView recView, ArrayList<Boolean> checkDeleteList) {
        for (int rowItemCount = 0; rowItemCount != recView.getAdapter().getItemCount(); ++rowItemCount) {

            checkDeleteList.set(rowItemCount, true);
            /*

            View view = recView.getLayoutManager().findViewByPosition(rowItemCount);

            if (view != null) {

                CheckBox cbDelete = (CheckBox) view.findViewById(R.id.cbDelete);
                cbDelete.setChecked(checkDeleteList.get(rowItemCount));

            } */


        }

        recView.getAdapter().notifyDataSetChanged();
        recView.getAdapter().notifyDataSetChanged();

        MainInterfaceAdapter.displayCheckListValues(checkDeleteList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

         bookMarksTabLayout = (TabLayout)findViewById(R.id.home_tabLayout);

        final RecyclerView bookMarksRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView = bookMarksRecyclerView;

        bookMarksTabLayout.addTab(bookMarksTabLayout.newTab().setText("Hotel"));

        bookMarksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                                                       LinearLayoutManager.VERTICAL,
                                                                       false));

        bookMarksRecyclerView.setNestedScrollingEnabled(false);

        bookMarksRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                                                  ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                                                  10));



        bookMarksTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        bookMarksTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                bookMarksRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                        ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                        10));



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookmarks");


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

                recyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                                                 ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                                                                 recyclerView.getAdapter().getItemCount()));


                break;

            case R.id.cancelDelete:

                Utilities.changeActionBarLayout(this,
                                                toolbar,
                                                bookMarksMenu,
                                                R.menu.book_marks_menu,
                                                "Bookmarks");


                recyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                                                 ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                                                 recyclerView.getAdapter().getItemCount()));

                break;

            case R.id.delete:

                ArrayList<Boolean> checkToggleList = ((MainInterfaceAdapter)recyclerView.getAdapter()).getCheckBoxToggleList();

                String deleteList = "";

                for (Boolean isChecked : checkToggleList)
                    deleteList += String.valueOf(isChecked) + "\n";

                Toast.makeText(getApplicationContext(),
                               deleteList,
                               Toast.LENGTH_LONG).show();


                break;

            case R.id.selectAll:

                selectAllCheckBox(recyclerView,
                                 ((MainInterfaceAdapter)recyclerView.getAdapter()).getCheckBoxToggleList());

                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
