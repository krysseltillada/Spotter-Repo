package com.lmos.spotter.MainInterface.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SampleEndlessRecyclerView extends RecyclerView.Adapter  {


    final int PLACE_VIEW = 0;
    final int LOADING_ITEM = 1;

    private ArrayList<Object[]> testDatas;
    private Activity act;

    public SampleEndlessRecyclerView(Activity activity, ArrayList<Object[]> td) {
        testDatas = td;
        act = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_list, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return testDatas.get(position) != null ? PLACE_VIEW : LOADING_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PlaceHolder) {

            PlaceHolder placeHolder = (PlaceHolder)holder;

            Picasso.with(act)
                    .load((int) testDatas.get(position)[0])
                    .placeholder(R.drawable.loadingplace)
                    .into(placeHolder.placeCompanyImage);

            placeHolder.txtPlaceName.setText((String) testDatas.get(position)[1]);
            placeHolder.txtLocation.setText((String) testDatas.get(position)[2]);

            double userRating = (double) testDatas.get(position)[3];
            double userPriceMin = (double) testDatas.get(position)[5];
            double userPriceMax = (double) testDatas.get(position)[6];

            int userReviews = (int) testDatas.get(position)[4];

            String reviewInfo = "Good(" + userReviews + " reviews)";
            String priceRangeInfo = "₱" + userPriceMin + " - " + userPriceMax;

            placeHolder.txtReview.setText(reviewInfo);
            placeHolder.txtGeneralRatingDigit.setText(String.valueOf(userRating));
            placeHolder.txtPrice.setText(priceRangeInfo);

        } else {
            LoadHolder loadHolder = (LoadHolder)holder;
            loadHolder.progressDialog.setIndeterminate(true);
        }

    }



    @Override
    public int getItemCount() {
        return testDatas.size();
    }

    static class LoadHolder extends RecyclerView.ViewHolder {

        ProgressBar progressDialog;

        public LoadHolder(View view) {
            super (view);
            progressDialog = (ProgressBar) view.findViewById(R.id.progressItem);
        }
    }
    static class PlaceHolder extends RecyclerView.ViewHolder {

        public ImageView placeCompanyImage, gradeIcon;
        public TextView txtPlaceName, txtLocation, txtPrice, txtGeneralRatingDigit, txtReview;

        PlaceHolder(View rowView) {
            super(rowView);

            placeCompanyImage = (ImageView) rowView.findViewById(R.id.placeCompanyImage);
            gradeIcon = (ImageView) rowView.findViewById(R.id.gradeIcon);

            txtPlaceName = (TextView) rowView.findViewById(R.id.txtPlaceName);
            txtLocation = (TextView) rowView.findViewById(R.id.txtLocation);
            txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
            txtGeneralRatingDigit = (TextView) rowView.findViewById(R.id.txtGeneralRatingDigit);
            txtReview = (TextView) rowView.findViewById(R.id.txtReview);

        }
    }
}