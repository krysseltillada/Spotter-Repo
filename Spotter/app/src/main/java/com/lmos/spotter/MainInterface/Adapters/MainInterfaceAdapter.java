package com.lmos.spotter.MainInterface.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;

/**
 * Created by Kryssel on 6/14/2017.
 */

public class MainInterfaceAdapter extends RecyclerView.Adapter<MainInterfaceAdapter.MainInterfaceViewHolder>{

    private int testCount = 0;
    private int lastPosition = -1;

    private ActivityType activityType;

    private Context context;

    public MainInterfaceAdapter(Context con, ActivityType acType,  int tc) {
        testCount = tc;
        context = con;
        activityType = acType;
    }

    @Override
    public MainInterfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainInterfaceViewHolder(LayoutInflater.from(parent.getContext())
                                                         .inflate((activityType == ActivityType.HOME_ACTIVITY) ? R.layout.place_item_list :
                                                                                                                 R.layout.bookmarks_item_list,
                                                                 parent, false));
    }

    @Override
    public void onBindViewHolder(MainInterfaceViewHolder holder, int position) {

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

   public class MainInterfaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        View rowV;

        public ImageView placeCompanyImage, gradeIcon;
        public TextView  txtPlaceName, txtLocation, txtPrice, txtGeneralRatingDigit, txtReview;

        CheckBox cbDelete;

        public MainInterfaceViewHolder (View rowView) {

            super(rowView);

            if (activityType == ActivityType.BOOKMARKS_ACTIVITY)
                cbDelete = (CheckBox)rowView.findViewById(R.id.cbDelete);

            placeCompanyImage = (ImageView) rowView.findViewById(R.id.placeCompanyImage);
            gradeIcon = (ImageView) rowView.findViewById(R.id.gradeIcon);

            txtPlaceName = (TextView) rowView.findViewById(R.id.txtPlaceName);
            txtLocation = (TextView) rowView.findViewById(R.id.txtLocation);
            txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
            txtGeneralRatingDigit = (TextView) rowView.findViewById(R.id.txtGeneralRatingDigit);
            txtReview = (TextView) rowView.findViewById(R.id.txtReview);

            rowV = rowView;

            rowView.findViewById(R.id.placeItemRow).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

    }

}
