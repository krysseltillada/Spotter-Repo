package com.lmos.spotter;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lmos.spotter.Utilities.Utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emman on 7/18/2017.
 */

public class SyncService extends IntentService {

    public SyncService(){
        super("SyncService");
    }

    public SyncService(String name) {
        super(name);
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {


        AppScript appScript = new AppScript(getApplicationContext());
        final Map<String, String> map_data = new HashMap<String, String>();

        assert intent != null;
        if(intent.getStringExtra("action").equals("save")){

            Log.d("SyncService", "Starting ....");
            map_data.put("action", intent.getStringExtra("action"));
            map_data.put("accountID", intent.getStringExtra("accountID"));
            map_data.put("placeID", intent.getStringExtra("placeID"));

            appScript.setData("bookmark.php", map_data);

        }
        else if(intent.getStringExtra("action").equals("updateView")){

            if(Utilities.checkNetworkState(getApplicationContext())){

                Log.d("UpdateView", intent.getStringExtra("placeID"));
                map_data.put("placeID", intent.getStringExtra("placeID"));
                appScript.setData("updatePlaceView.php", map_data);

            }

        }

    }

}
