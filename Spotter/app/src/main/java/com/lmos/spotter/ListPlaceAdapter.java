package com.lmos.spotter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

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

    int layoutId;

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

        String data = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        return convertView;

    }
}
