package com.lmos.spotter.MainInterface.Adapters;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by Kryssel on 6/14/2017.
 */

public class MainInterfaceAdapter extends RecyclerView.Adapter<MainInterfaceAdapter.MainInterfaceViewHolder>{

    private int testCount = 0;
    private int lastPosition = -1;

    private ActivityType activityType;

    private boolean isCheckBoxShow = false;

    private ArrayList<Boolean> checkBoxToggleList;

    private Context context;

    public MainInterfaceAdapter(Context con, ActivityType acType,  int tc) {

        Log.d("Debug", "MainInterfaceAdapter constructor");

        checkBoxToggleList = new ArrayList<Boolean>(tc);

        testCount = tc;
        context = con;
        activityType = acType;
    }

    public MainInterfaceAdapter setVisibilityCheckBox(boolean isShow) {
        isCheckBoxShow = isShow;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public MainInterfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d("Debug", "OnCreateViewHolder");

        return new MainInterfaceViewHolder(LayoutInflater.from(parent.getContext())
                                                         .inflate((activityType == ActivityType.HOME_ACTIVITY) ? R.layout.place_item_list :
                                                                                                                 R.layout.bookmarks_item_list,
                                                                 parent, false));
    }

    @Override
    public void onBindViewHolder(MainInterfaceViewHolder holder, int position) {

        Log.d("Debug", "item rendereed at position: " + String.valueOf(position));

        if (holder.cbDelete != null) {

            if (isCheckBoxShow)
                holder.cbDelete.setVisibility(View.VISIBLE);
            else
                holder.cbDelete.setVisibility(View.GONE);

        }

        setAnimation(holder.rowV.findViewById(R.id.placeItemRow), position);

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

   public class MainInterfaceViewHolder extends RecyclerView.ViewHolder {

        View rowV;

        public ImageView placeCompanyImage, gradeIcon;
        public TextView  txtPlaceName, txtLocation, txtPrice, txtGeneralRatingDigit, txtReview;

        public CheckBox cbDelete;

        public MainInterfaceViewHolder (View rowView) {

            super(rowView);

            Log.d("Debug", "MainInterfaceViewHolder constructor");

            if (activityType == ActivityType.BOOKMARKS_ACTIVITY) {
                cbDelete = (CheckBox) rowView.findViewById(R.id.cbDelete);

                rowView.findViewById(R.id.placeItemRow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CheckBox cbDelete = (CheckBox)v.findViewById(R.id.cbDelete);

                        cbDelete.toggle();



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
