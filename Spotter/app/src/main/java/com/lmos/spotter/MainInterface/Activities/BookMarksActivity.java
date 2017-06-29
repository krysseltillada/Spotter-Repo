package com.lmos.spotter.MainInterface.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.TestData;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;

public class BookMarksActivity extends AppCompatActivity {

    Menu bookMarksMenu;
    Toolbar toolbar;

    TabLayout bookMarksTabLayout;
    RecyclerView recyclerView;

    private void changeBookMarkMode (ActivityType activityType, final RecyclerView recyclerViewTabLayout,
                                     TabLayout tabLayout) {

        tabLayout.clearOnTabSelectedListeners();

        if (activityType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE) {

            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                    MainInterfaceAdapter.currentSelectedTab,
                    null
                   ));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    switch (tab.getPosition()) {

                        case 0:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.HOTEL;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                    PlaceType.HOTEL,
                                    null));

                            break;

                        case 1:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.RESTAURANT;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                    PlaceType.RESTAURANT,
                                    null));
                            break;

                        case 2:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.TOURIST_SPOTS;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                    PlaceType.TOURIST_SPOTS,
                                    null));

                            break;

                    }

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
                    null));


            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    switch (tab.getPosition()) {

                        case 0:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.HOTEL;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                                    PlaceType.HOTEL,
                                    null));

                            break;

                        case 1:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.RESTAURANT;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                                    PlaceType.RESTAURANT,
                                    null));

                            break;

                        case 2:

                            MainInterfaceAdapter.currentSelectedTab = PlaceType.TOURIST_SPOTS;

                            recyclerViewTabLayout.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE,
                                    PlaceType.TOURIST_SPOTS,
                                    null));

                            break;

                    }

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

                CheckBox cbDelete = (CheckBox) view.findViewById(R.id.cbDelete);
                cbDelete.setChecked(checkDeleteList.get(rowItemCount));

            }


        }

        MainInterfaceAdapter.displayCheckListValues(MainInterfaceAdapter.getCheckBoxToggleList());

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
        bookMarksTabLayout.addTab(bookMarksTabLayout.newTab().setText("Restaurants"));
        bookMarksTabLayout.addTab(bookMarksTabLayout.newTab().setText("Tourist Spots"));

        bookMarksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                                                       LinearLayoutManager.VERTICAL,
                                                                       false));

        bookMarksRecyclerView.setNestedScrollingEnabled(false);

        bookMarksRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                                                  ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                                                  PlaceType.HOTEL,
                                                                  null));


        bookMarksTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                           bookMarksRecyclerView,
                           bookMarksTabLayout);


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

                MainInterfaceAdapter.clearCheckBoxToggleList();

                changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                        recyclerView,
                        bookMarksTabLayout);

                break;

            case R.id.delete:


                final ArrayList<ArrayList<Boolean>> checkToggleList = (((MainInterfaceAdapter)recyclerView.getAdapter())).getCheckBoxToggleList();

                if (checkToggleList.get(0).contains(true) ||
                    checkToggleList.get(1).contains(true) ||
                    checkToggleList.get(2).contains(true)) {

                    final AlertDialog deleteDialog =
                            new AlertDialog.Builder(this)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String deleteList = "";

                                            for (int i = 0; i != checkToggleList.size(); ++i) {

                                                deleteList += "For index array: " + i + "\n";

                                                for (int j = 0; j != checkToggleList.get(i).size(); ++j) {

                                                    deleteList += "position: " + j + " value: " + checkToggleList.get(i).get(j) + "\n";

                                                }

                                                deleteList += "\n";

                                            }

                                            Log.d("debug:bookmark", deleteList);

                                            Toast.makeText(getApplicationContext(),
                                                    deleteList,
                                                    Toast.LENGTH_LONG).show();

                                            MainInterfaceAdapter.clearCheckBoxToggleList();

                                            Utilities.changeActionBarLayout(BookMarksActivity.this,
                                                    toolbar,
                                                    bookMarksMenu,
                                                    R.menu.book_marks_menu,
                                                    "Bookmarks");

                                            changeBookMarkMode(ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE,
                                                    recyclerView,
                                                    bookMarksTabLayout);

                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();


                    deleteDialog.setTitle("Delete");
                    deleteDialog.setMessage("delete selected bookmarks?");

                    deleteDialog.show();

                } else {

                    new AlertDialog.Builder(BookMarksActivity.this)
                                   .setTitle("warning")
                                   .setMessage("Please select any places")
                                   .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                       }
                                   })
                                   .show();

                }

                break;


            case R.id.selectAll:

                    selectAllCheckBox(recyclerView,
                            (MainInterfaceAdapter.getCheckBoxToggleList()
                                    .get(MainInterfaceAdapter.getPlaceTypeByIndex(MainInterfaceAdapter.currentSelectedTab))),
                                    true);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
