package com.lmos.spotter.SearchInterface.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

/**
 * Created by Kryssel on 7/30/2017.
 */

public class TweetResultReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            Toast.makeText(context, "tweeted successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "please check your connection", Toast.LENGTH_LONG).show();
        }

    }

}
