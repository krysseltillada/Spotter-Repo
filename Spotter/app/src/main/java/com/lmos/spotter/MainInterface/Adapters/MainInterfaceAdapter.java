package com.lmos.spotter.MainInterface.Adapters;

import android.content.Context;
import android.os.Debug;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;

import java.util.ArrayList;

/**
 * Created by Kryssel on 6/14/2017.
 */

public class MainInterfaceAdapter extends RecyclerView.Adapter<MainInterfaceAdapter.MainInterfaceViewHolder>{

    private int testCount = 0;
    private int lastPosition = -1;

    private ActivityType activityType;

    private static ArrayList<ArrayList<Boolean>> checkBoxToggleList = new ArrayList<>();

    public static PlaceType currentSelectedTab;
    public static boolean ifDoneInitialized = false;

    private Context context;

    public static int getPlaceTypeByIndex (PlaceType type) {
        return  (PlaceType.HOTEL == type) ? 0 :
                (PlaceType.RESTAURANT == type) ? 1 :
                                                 2;
    }

    public static void initCheckBoxToggleList () {

        if (!ifDoneInitialized) {

            checkBoxToggleList.add(new ArrayList<Boolean>());
            checkBoxToggleList.add(new ArrayList<Boolean>());
            checkBoxToggleList.add(new ArrayList<Boolean>());

            ifDoneInitialized = true;

        }
    }

    public static void clearCheckBoxToggleList () {

        if (checkBoxToggleList.size() > 0) {

            for (int i = 0; i != checkBoxToggleList.size(); ++i) {

                for (int j = 0; j != checkBoxToggleList.get(i).size(); ++j)
                    checkBoxToggleList.get(i).set(j, false);

            }

        }
    }


    public MainInterfaceAdapter(Context con, ActivityType acType, PlaceType placeType, int tc) {

        Log.d("Debug", "MainInterfaceAdapter constructor");

        if (acType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE ||
            acType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

            currentSelectedTab = placeType;

            int toggleIndex = getPlaceTypeByIndex(placeType);

            Log.d("Debug", "initialize three hotel restaurant and tourist spots list");

            initCheckBoxToggleList();

                if (checkBoxToggleList.get(toggleIndex).size() <= 0) {

                    for (int i = 0; i != tc; ++i)
                        checkBoxToggleList.get(toggleIndex).add(false);

                }

            displayCheckListValues(checkBoxToggleList);

        }


        testCount = tc;
        context = con;
        activityType = acType;
    }

    public static ArrayList<ArrayList<Boolean>> getCheckBoxToggleList () {
        return checkBoxToggleList;
    }


    private int getLayoutIdByType (ActivityType type) {

        switch (type) {
            case HOME_ACTIVITY:
                return R.layout.place_item_list;
            case BOOKMARKS_ACTIVITY_NORMAL_MODE:
                return R.layout.bookmarks_item_list;
            case BOOKMARKS_ACTIVITY_DELETE_MODE:
                return R.layout.bookmarks_item_list_delete_mode;
        }

        return 0;

    }


    @Override
    public MainInterfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d("Debug", "OnCreateViewHolder");

        return new MainInterfaceViewHolder(LayoutInflater.from(parent.getContext())
                                                                     .inflate(getLayoutIdByType(activityType), parent, false));

    }

    @Override
    public void onBindViewHolder(MainInterfaceViewHolder holder, int position) {

        if (activityType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

            if (holder.isRecyclable())
                holder.setIsRecyclable(false);


            int toggleIndex = getPlaceTypeByIndex(currentSelectedTab);

            if (holder.cbDelete != null)
                holder.cbDelete.setChecked(checkBoxToggleList.get(toggleIndex).get(position));


        }

        setAnimation(holder.rowV.findViewById(R.id.placeItemRow), position);

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    private void setAnimation (View view, int position) {

        if (position > lastPosition) {
            view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return testCount;
    }

    public static void displayCheckListValues (ArrayList<ArrayList <Boolean>> checkList) {

        Log.d("Debug", "Size for hotel: " + String.valueOf(checkList.get(0).size()));

        Log.d("Debug", "Size for Restaurant: " + String.valueOf(checkList.get(1).size()));

        Log.d("Debug", "Size for Tourist spots: " + String.valueOf(checkList.get(2).size()));

        for (int i = 0; i != checkList.size(); ++i) {

            Log.d("Debug", "for index: " + String.valueOf(i));

            for (int j = 0; j != checkList.get(i).size(); ++j) {
                Log.d("Debug", "position: " + String.valueOf(checkList.get(i).get(j)));
            }
        }

    }

   public class MainInterfaceViewHolder extends RecyclerView.ViewHolder {

        View rowV;

        public ImageView placeCompanyImage, gradeIcon;
        public TextView  txtPlaceName, txtLocation, txtPrice, txtGeneralRatingDigit, txtReview;

        public CheckBox cbDelete;

        public MainInterfaceViewHolder (View rowView) {

            super(rowView);

            Log.d("Debug", "MainInterfaceViewHolder constructor");

            if (activityType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {
                cbDelete = (CheckBox) rowView.findViewById(R.id.cbDelete);

                rowView.findViewById(R.id.placeItemRow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CheckBox cbDelete = (CheckBox)v.findViewById(R.id.cbDelete);

                        cbDelete.toggle();


                        int toggleIndex = getPlaceTypeByIndex(currentSelectedTab);


                        checkBoxToggleList.get(toggleIndex).set(MainInterfaceViewHolder.this.getPosition(),
                                                                cbDelete.isChecked());

                        displayCheckListValues(checkBoxToggleList);


                    }
                });

            }

            placeCompanyImage = (ImageView) rowView.findViewById(R.id.placeCompanyImage);
            gradeIcon = (ImageView) rowView.findViewById(R.id.gradeIcon);

            txtPlaceName = (TextView) rowView.findViewById(R.id.txtPlaceName);
            txtLocation = (TextView) rowView.findViewById(R.id.txtLocation);
            txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
            txtGeneralRatingDigit = (TextView) rowView.findViewById(R.id.txtGeneralRatingDigit);
            txtReview = (TextView) rowView.findViewById(R.id.txtReview);

            rowV = rowView;

        }


    }

}
