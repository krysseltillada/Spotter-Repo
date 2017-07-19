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

import com.lmos.spotter.MainInterface.Activities.BookMarksActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kryssel on 6/14/2017.
 */

public class MainInterfaceAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;
    public static OnAdapterItemClickListener onAdapterItemClickListener;
    public static PlaceType currentSelectedTab;
    private static HashMap<String, ArrayList<Boolean>> checkBoxToggleStates = new HashMap<>();
    private static HashMap<String, ArrayList<String>> checkBoxToggleMap = new HashMap<>();
    private int lastPosition = -1;
    private ActivityType activityType;
    private List<Place> places;
    private Context context;
    public MainInterfaceAdapter(Context con, ActivityType acType, PlaceType placeType, List<Place> pl) {

        places = pl;

        if (acType == ActivityType.BOOKMARKS_ACTIVITY_NORMAL_MODE ||
            acType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

            currentSelectedTab = placeType;

            Log.d("debug", "currently tab: " + currentSelectedTab);

            checkCreateToggleList(BookMarksActivity.getPlaceTypeStr(currentSelectedTab));

            displayCheckListKeyValues(checkBoxToggleMap);
            displayCheckListStates(checkBoxToggleStates);

        }

        context = con;
        activityType = acType;
    }


    public static void setAllCheckToggleStates (boolean isChecked) {
        for (String placeKey : checkBoxToggleStates.keySet()) {
            for (int i = 0; i != checkBoxToggleStates.get(placeKey).size(); ++i)
                checkBoxToggleStates.get(placeKey).set(i, isChecked);
        }

        displayCheckListStates(checkBoxToggleStates);
    }

    public static boolean checkToggleState() {

        for (String placeKey : checkBoxToggleStates.keySet()) {
           if (checkBoxToggleStates.get(placeKey).contains(true))
               return true;
        }

        return false;
    }

    public static HashMap<String, ArrayList<Boolean>> getCheckToggleStates () {
        return checkBoxToggleStates;
    }

    public static HashMap<String, ArrayList<String>> getCheckToggleMap () {
        return checkBoxToggleMap;
    }

    public static void displayCheckListKeyValues (HashMap<String, ArrayList<String>> cbtm) {
        for (String placeKey : cbtm.keySet()) {

            Log.d("debug,", "for place: " + placeKey);

            for (String checkPlaces : cbtm.get(placeKey))
                Log.d("debug", checkPlaces);

        }
    }

    public static void displayCheckListStates (HashMap<String, ArrayList<Boolean>> chls) {
        for (String placeKey : chls.keySet()) {

            Log.d("debug,", "for place: " + placeKey);

            for (int i = 0; i != chls.get(placeKey).size(); ++i)
                Log.d("debug", "index: " + i + " value: " + chls.get(placeKey).get(i));

        }
    }

    public void setOnItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener){
        MainInterfaceAdapter.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    public  void checkCreateToggleList (String keyPlace) {

        if (!checkBoxToggleMap.containsKey(keyPlace)) {
            checkBoxToggleMap.put(keyPlace, new ArrayList<String>());

            ArrayList<Boolean> checkStates = new ArrayList<>();

            for (int i = 0; i != places.size(); ++i)
                checkStates.add(false);

            checkBoxToggleStates.put(keyPlace, checkStates);
        }

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

            double placeRating = Double.parseDouble(places.get(position).getPlaceRating());
            int userReviews = Integer.parseInt(places.get(position).getUserReviews());

            String ratingScale = "";

            if (placeRating >= 0.0 && placeRating <= 1.0)
                ratingScale = "Poor";
            else if (placeRating <= 2.0 && placeRating >= 1.0)
                ratingScale = "Fair";
            else if (placeRating <= 3.0 && placeRating >= 2.0)
                ratingScale = "Average";
            else if (placeRating <= 4.0 && placeRating >= 3.0)
                ratingScale = "Good";
            else if (placeRating <= 5.0 && placeRating >= 4.0)
                ratingScale = "Excellent";

            if (activityType == ActivityType.HOME_ACTIVITY) {

                try {

                    String frontPlaceImageLink = new JSONObject(new JSONObject(places.get(position)
                            .getPlaceImageLink())
                            .getString("placeImages"))
                            .getString("frontImage");

                    Picasso.with(context)
                            .load(frontPlaceImageLink)
                            .resize(90, 90)
                            .placeholder(R.drawable.loadingplace)
                            .into(mainInterfaceViewHolder.placeCompanyImage);


                } catch (JSONException e) {
                    Log.d("debug", e.getMessage());
                }

                String recommend = places.get(position).getRecommended();


                mainInterfaceViewHolder.txtReview.setText(ratingScale + "(" + userReviews + " reviews)");
                mainInterfaceViewHolder.txtRecommend.setText(recommend + " people recommend this");
                mainInterfaceViewHolder.txtGeneralRatingDigit.setText(places.get(position).getRating());

            }


            if (activityType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {

                if (mainInterfaceViewHolder.isRecyclable())
                    mainInterfaceViewHolder.setIsRecyclable(false);

                String toggleKey = BookMarksActivity.getPlaceTypeStr(currentSelectedTab);

                if (mainInterfaceViewHolder.cbDelete != null)
                    mainInterfaceViewHolder.cbDelete.setChecked(checkBoxToggleStates.get(toggleKey).get(position));


            }

            mainInterfaceViewHolder.txtPlaceName.setText(places.get(position).getPlaceName());
            mainInterfaceViewHolder.txtLocation.setText(places.get(position).getPlaceLocality());
            mainInterfaceViewHolder.txtPrice.setText(places.get(position).getPlacePriceRange());

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

    public interface OnAdapterItemClickListener{
        void onItemClick(Place place);
    }

    public class LoadingItemViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar itemProgressBar;

        public LoadingItemViewHolder(View view) {
            super (view);
            itemProgressBar = (ProgressBar) view.findViewById(R.id.progressItem);
        }
    }

   public class MainInterfaceViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

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

            if (activityType == ActivityType.BOOKMARKS_ACTIVITY_DELETE_MODE) {
                cbDelete = (CheckBox) rowView.findViewById(R.id.cbDelete);

                rowView.findViewById(R.id.placeItemRow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CheckBox cbDelete = (CheckBox)v.findViewById(R.id.cbDelete);

                        cbDelete.toggle();

                        String placeName = ((TextView)v.findViewById(R.id.txtPlaceName)).getText().toString();

                        if (!checkBoxToggleMap.get(BookMarksActivity.getPlaceTypeStr(currentSelectedTab)).contains(placeName)) {
                            checkBoxToggleMap.get(BookMarksActivity.getPlaceTypeStr(currentSelectedTab)).add(placeName);
                            checkBoxToggleStates.get(BookMarksActivity.getPlaceTypeStr(currentSelectedTab)).set(getPosition(), true);
                        }
                        else {
                            checkBoxToggleMap.get(BookMarksActivity.getPlaceTypeStr(currentSelectedTab)).remove(placeName);
                            checkBoxToggleStates.get(BookMarksActivity.getPlaceTypeStr(currentSelectedTab)).set(getPosition(), false);
                        }

                        displayCheckListStates(checkBoxToggleStates);
                        displayCheckListKeyValues(checkBoxToggleMap);

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

            rowView.setOnClickListener(this);

        }

       @Override
       public void onClick(View v) {
           onAdapterItemClickListener.onItemClick(places.get(getAdapterPosition()));
       }
   }
}
