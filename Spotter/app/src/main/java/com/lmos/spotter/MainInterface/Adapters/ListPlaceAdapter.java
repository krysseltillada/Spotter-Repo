package com.lmos.spotter.MainInterface.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by Kryssel on 6/1/2017.
 *
 * this class is used to create a custom listview by
 * inheriting and overriding method getView which renders
 * a view(layout) on each item in a listview in the class ArrayAdapter
 *
 *
 */

public class ListPlaceAdapter extends ArrayAdapter <String> {

    // creating a constructor that accepts a context a layout id and a collection

    private int layoutId;

    public ListPlaceAdapter (Context con, int listLayoutId, String[] arr) {
        // calling the base class constructor which is the constructor of the array adapter
        super(con, listLayoutId, arr);
        layoutId = listLayoutId;
    }
    @NonNull
    @Override

    /**
     *
     * overriding the getView method from the arrayadapter to get the view of the current
     * layout on each item in the listview the (position) is the current position of the row
     * the convertView is the current View of the row in the listview and the viewgroup is the current
     * container of view in each row of the listview
     *
     */

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            String data = getItem(position);

            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

            ArrayList<View> views = new ArrayList<View>();

            views.add((TextView) convertView.findViewById(R.id.txtPlaceName));
            views.add((TextView) convertView.findViewById(R.id.txtLocation));
            views.add((TextView) convertView.findViewById(R.id.txtPrice));
            views.add((TextView) convertView.findViewById(R.id.txtReview));
            views.add((ImageView) convertView.findViewById(R.id.itemRowBackground));
            views.add((ImageView) convertView.findViewById(R.id.placeCompanyLogo));
            //views.add((RatingBar) convertView.findViewById(R.id.placeRating));

            ImageView backgroundRowItem = (ImageView) views.get(4);

            BitmapDrawable drawable;

            drawable = ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.traveler_bg3));

            new ImageBlurLoader(backgroundRowItem,
                    getContext()).execute(drawable.getBitmap());

            for (View view : views)
                view.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));


        }


        return convertView;

    }

    class ImageBlurLoader extends AsyncTask<Bitmap, Void, Bitmap> {

        ImageView imageView;
        Context context;

        public ImageBlurLoader (ImageView imgView, Context con) {
            imageView = imgView;
            context = con;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            return Utilities.fastblur(params[0], 0.4f, 10);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bitmap);

        }
    }
}
