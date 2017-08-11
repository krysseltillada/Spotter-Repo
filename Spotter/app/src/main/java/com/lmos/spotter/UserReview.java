package com.lmos.spotter;

import com.lmos.spotter.Utilities.UserAccount;

public class UserReview extends UserAccount {

    private String review;
    private String date;
    private float rating;
    private boolean isRecommend;

    public UserReview setReview (String rev) {
        review = rev;
        return this;
    }

    public UserReview setDate (String d) {
        date = d;
        return this;
    }

    public UserReview setRating (float r) {
        rating = r;
        return this;
    }

    public UserReview setRecommend (boolean rec) {
        isRecommend = rec;
        return this;
    }

    public String getReview () {
        return review;
    }

    public String getDate () {
        return date;
    }

    public float getRating () {
        return rating;
    }

    public boolean isRecommend () {
        return isRecommend;
    }

}
