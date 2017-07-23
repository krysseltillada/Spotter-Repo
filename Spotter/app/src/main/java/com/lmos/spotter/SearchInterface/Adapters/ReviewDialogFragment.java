package com.lmos.spotter.SearchInterface.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.ReviewActivity;
import com.lmos.spotter.UserReview;
import com.lmos.spotter.Utilities.Utilities;

import java.util.HashMap;

/**
 * Created by Kryssel on 6/30/2017.
 */

public class ReviewDialogFragment extends DialogFragment {

    public interface OnReviewPost {
        public void reviewPost(String placeID);
    }

    OnReviewPost onReviewPost;

    TextInputEditText reviewEditText;
    RatingBar reviewRatingBar;
    CheckBox isRecommended;
    ProgressDialog postReviewProgress;

    public void setOnReviewPost(OnReviewPost onReviewPost) {
        this.onReviewPost = onReviewPost;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog errorDialog = new AlertDialog.Builder(getActivity()).setTitle("error")
                .setMessage("check your internet connection")
                .setNeutralButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        View  view = getActivity().getLayoutInflater().inflate(R.layout.review_dialog, null);

        reviewEditText = (TextInputEditText)view.findViewById(R.id.textInputEditText);
        reviewRatingBar = (RatingBar)view.findViewById(R.id.reviewRating);
        isRecommended = (CheckBox)view.findViewById(R.id.checkboxIsRecommended);

         final AlertDialog postReview = new AlertDialog.Builder(getActivity())
                 .setTitle("post a review")
                 .setView(view)
                 .setPositiveButton("post review", new DialogInterface.OnClickListener() {

                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                         if (reviewEditText.getText().length() > 0) {

                             final SharedPreferences userPreference = ReviewDialogFragment.this.getActivity().getSharedPreferences(LoginActivity.LOGIN_PREFS,
                                     Context.MODE_PRIVATE);

                             postReviewProgress = new ProgressDialog(ReviewDialogFragment.this.getContext());

                             postReviewProgress.setIndeterminate(true);
                             postReviewProgress.setMessage("posting your review");
                             postReviewProgress.setCancelable(false);
                             postReviewProgress.show();

                             RequestQueue postReviewRequest = Volley.newRequestQueue(getActivity());

                             StringRequest postReviewString = new StringRequest(Request.Method.POST,
                                     "http://admin-spotter.000webhostapp.com/app_scripts/postReview.php",
                                     new Response.Listener<String>() {

                                         @Override
                                         public void onResponse(String response) {

                                             Log.d("debug", response);

                                             postReviewProgress.dismiss();

                                             if (!response.equals("review posted")) {
                                                 errorDialog.show();
                                             } else {
                                                 onReviewPost.reviewPost(ReviewActivity.placeID);
                                             }

                                         }
                                     },
                                     new Response.ErrorListener() {

                                         @Override
                                         public void onErrorResponse(VolleyError error) {
                                             Log.d("debug", error.getMessage() + "aa");
                                             postReviewProgress.dismiss();
                                             errorDialog.show();
                                         }
                                     }) {
                                 protected HashMap<String, String> getParams() {
                                     return new HashMap<String, String>() {{
                                         put("accountID", userPreference.getString("accountID", ""));
                                         put("placeID", ReviewActivity.placeID);
                                         put("Review", reviewEditText.getText().toString());
                                         put("Rating", Float.toString(reviewRatingBar.getRating()));
                                         put("isRecommended", Boolean.toString(isRecommended.isChecked()));
                                     }};
                                 }
                             };

                             Log.d("debug", "review: " + reviewEditText.getText().toString());
                             Log.d("debug", "Rating: " + Float.toString(reviewRatingBar.getRating()));
                             Log.d("debug", "IsRecommended: " + Boolean.toString(isRecommended.isChecked()));

                             postReviewRequest.add(postReviewString);

                         } else {
                             Toast.makeText(getContext(), "write a review", Toast.LENGTH_LONG).show();
                         }

                     }
                 })
                 .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {


                         dialog.dismiss();

                     }
                 }).create();

        postReview.show();


         return postReview;
    }

}
