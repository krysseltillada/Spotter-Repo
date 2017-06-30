package com.lmos.spotter.SearchInterface.Adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmos.spotter.R;

/**
 * Created by Kryssel on 6/30/2017.
 */

public class ReviewDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View reviewLayout = inflater.inflate(R.layout.review_dialog, container, false);

        getDialog().setTitle("post a review");

        return reviewLayout;
    }
}
