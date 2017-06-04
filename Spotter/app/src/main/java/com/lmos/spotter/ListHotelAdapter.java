package com.lmos.spotter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Kryssel on 6/4/2017.
 */

public class ListHotelAdapter extends ArrayAdapter<String> {
    @NonNull

    private int layoutId;

    public ListHotelAdapter (Context con, int listLayoutId, String[]arr) {
        super(con, listLayoutId, arr);
        layoutId = listLayoutId;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String data = getItem(position);

        ListPlaceAdapter.ViewHolder viewHolder;
        View result;

        if (convertView == null) {

            viewHolder = new ListPlaceAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

            viewHolder.txtName = (TextView)convertView.findViewById(R.id.txtPlaceName);
            viewHolder.txtPlace = (TextView)convertView.findViewById(R.id.txtLocation);
            viewHolder.txtPrice = (TextView)convertView.findViewById(R.id.txtPrice);
            viewHolder.txtReview = (TextView)convertView.findViewById(R.id.txtReview);
            viewHolder.imageViewBackground = (ImageView)convertView.findViewById(R.id.itemRowBackground);
            viewHolder.imageViewLogo = (ImageView)convertView.findViewById(R.id.placeCompanyLogo);
            viewHolder.ratingBar = (RatingBar)convertView.findViewById(R.id.placeRating);

            ImageView backgroundRowItem = viewHolder.imageViewBackground;

            BitmapDrawable drawable;

            drawable = ((BitmapDrawable) this.getContext().getResources().getDrawable(R.drawable.traveler_bg3));

            Bitmap blurRowBackground = Utilities.BlurImg.blurImg(getContext(), drawable.getBitmap(), 25.0f);

            backgroundRowItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
            backgroundRowItem.setImageBitmap(blurRowBackground);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ListPlaceAdapter.ViewHolder)convertView.getTag();
            result = convertView;
        }

        return convertView;
    }
}
