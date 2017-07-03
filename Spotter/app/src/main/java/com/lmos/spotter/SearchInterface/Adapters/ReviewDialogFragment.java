package com.lmos.spotter.SearchInterface.Adapters;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmos.spotter.R;

/**
 * Created by Kryssel on 6/30/2017.
 */

public class ReviewDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         return new AlertDialog.Builder(getActivity())
                               .setTitle("post a review")
                               .setView(LayoutInflater.from(getContext()).inflate(R.layout.review_dialog, null))
                               .setPositiveButton("post review", new DialogInterface.OnClickListener() {

                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                   }
                               })
                               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {


                                   }
                               }).create();
    }
}
