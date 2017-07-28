package com.lmos.spotter;

import android.app.Application;

import io.branch.referral.Branch;

/**
 * Created by Kryssel on 7/26/2017.
 */

public final class CustomApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Branch object
        Branch.getAutoInstance(this);
    }
}