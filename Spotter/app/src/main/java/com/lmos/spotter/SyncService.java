package com.lmos.spotter;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * Created by emman on 7/18/2017.
 */

public class SyncService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    DbHelper dbHelper;

    public SyncService(String name) {
        super(name);
        setIntentRedelivery(true);
        dbHelper = new DbHelper(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

       // AppScript appScript = new AppScript((Activity) getApplicationContext()){{
         //  setData("get-all-place-name.php", dbHelper.getLastKeyword());
        //}};

    }

}
