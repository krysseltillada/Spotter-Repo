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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kryssel on 6/14/2017.
 */

public class MainInterfaceAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public static PlaceType currentSelectedTab;
    public static boolean ifDoneInitialized = false;
    private static ArrayList<ArrayList<Boolean>> checkBoxToggleList = new ArrayList<>();
    private int lastPosition = -1;
    private ActivityType activityType;
    private List<Place> places;
    private Context context;

    public MainInterfaceAdapter(Context con, ActivityType acType, PlaceType placeType, List<Place> pl) {

        places = pl;

        if (acType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE ||
            acType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

            currentSelectedTab = placeType;

            int toggleIndex = getPlaceTypeByIndex(placeType);

            Log.d("Debug", "initialize three hotel restaurant and tourist spots list");

            initCheckBoxToggleList();

                if (checkBoxToggleList.get(toggleIndex).size() <= 0) {

                    for (int i = 0; i != places.size(); ++i)
                        checkBoxToggleList.get(toggleIndex).add(false);

                }

            displayCheckListValues(checkBoxToggleList);

        }


        //testCount = tc;
        context = con;
        activityType = acType;
    }

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d("debug", "viewtype: " + viewType);

        if (viewType == VIEW_TYPE_ITEM) {

            return new MainInterfaceViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(getLayoutIdByType(activityType), parent, false));

        } else {

            return new LoadingItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof  MainInterfaceViewHolder) {

            MainInterfaceViewHolder mainInterfaceViewHolder = (MainInterfaceViewHolder)viewHolder;

            try {

                String frontPlaceImageLink = new JSONObject(new JSONObject(places.get(position)
                                                                             .getPlaceImageLink())
                                                                             .getString("placeImages"))
                                                                             .getString("frontImage");

                Picasso.with(context)
                        .load(frontPlaceImageLink)
                        .resize(0, mainInterfaceViewHolder.placeCompanyImage.getHeight())
                        .centerCrop()
                        .placeholder(R.drawable.loadingplace)
                        .into(mainInterfaceViewHolder.placeCompanyImage);


            } catch (JSONException e) {
                Log.d("debug", e.getMessage());
            }


            mainInterfaceViewHolder.txtPlaceName.setText(places.get(position).getPlaceName());
            mainInterfaceViewHolder.txtLocation.setText(places.get(position).getPlaceLocality());

            String recommend = places.get(position).getRecommended();

            mainInterfaceViewHolder.txtRecommend.setText(recommend + " people recommend this");
            mainInterfaceViewHolder.txtGeneralRatingDigit.setText(places.get(position).getRating());

        /*

        double userRating = (double)places.get(position)[3];
        double userPriceMin = (double)places.get(position)[5];
        double userPriceMax = (double)places.get(position)[6]; */

        /*

        int userReviews = (int)places.get(position)[4];

        String reviewInfo = "Good(" + userReviews + " reviews)";
        String priceRangeInfo = "₱" + userPriceMin + " - " + userPriceMax;

        holder.txtReview.setText(reviewInfo);
        holder.txtGeneralRatingDigit.setText(String.valueOf(userRating)); */

            mainInterfaceViewHolder.txtPrice.setText(places.get(position).getPlacePriceRange());

            if (activityType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

                if (mainInterfaceViewHolder.isRecyclable())
                    mainInterfaceViewHolder.setIsRecyclable(false);


                int toggleIndex = getPlaceTypeByIndex(currentSelectedTab);

                if (mainInterfaceViewHolder.cbDelete != null)
                    mainInterfaceViewHolder.cbDelete.setChecked(checkBoxToggleList.get(toggleIndex).get(position));


            }


            setAnimation(mainInterfaceViewHolder.rowV.findViewById(R.id.placeItemRow), position);

        } else if (viewHolder instanceof LoadingItemViewHolder) {

            LoadingItemViewHolder loadingItemViewHolder = (LoadingItemViewHolder) viewHolder;

            loadingItemViewHolder.itemProgressBar.setIndeterminate(true);

        }



    }

    @Override
    public int getItemViewType(int position) {

        return places.get(position) == null ? VIEW_TYPE_LOADING :
                                              VIEW_TYPE_ITEM;

    }

    private void setAnimation (View view, int position) {

        if (position > lastPosition) {
            view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return places.size();
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

    public class LoadingItemViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar itemProgressBar;

        public LoadingItemViewHolder(View view) {
            super (view);
            itemProgressBar = (ProgressBar) view.findViewById(R.id.progressItem);
        }
    }

   public class MainInterfaceViewHolder extends RecyclerView.ViewHolder {

        public ImageView placeCompanyImage, gradeIcon;
        public TextView  txtPlaceName, txtLocation, txtPrice, txtGeneralRatingDigit, txtReview, txtRecommend;

        public CheckBox cbDelete;
        View rowV;

        public MainInterfaceViewHolder (View rowView) {

            super(rowView);

            placeCompanyImage = (ImageView) rowView.findViewById(R.id.placeCompanyImage);
            gradeIcon = (ImageView) rowView.findViewById(R.id.gradeIcon);

            txtPlaceName = (TextView) rowView.findViewById(R.id.txtPlaceName);
            txtLocation = (TextView) rowView.findViewById(R.id.txtLocation);
            txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
            txtGeneralRatingDigit = (TextView) rowView.findViewById(R.id.txtGeneralRatingDigit);
            txtReview = (TextView) rowView.findViewById(R.id.txtReview);
            txtRecommend = (TextView) rowView.findViewById(R.id.txtRecommend);

            rowV = rowView;

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

            } else if (activityType == ActivityType.HOME_ACTIVITY ||
                       activityType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE) {

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context,
                                       "place: " + txtPlaceName.getText() + "\n" +
                                       "row item position: " + getPosition()    ,
                                       Toast.LENGTH_LONG).show();

                    }
                });

            }



        }


    }
}
