package com.lmos.spotter;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.Utilities.Utilities;


public class NearPlacesService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient fusedApiClient;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onLocationChanged(Location location) {

        LatLng pldtLocation = new LatLng(14.471676, 121.005982);

        double distance =  Utilities.CalculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()),
                                        pldtLocation);

        Toast.makeText(getApplicationContext(), "distance from here to pldt: " + distance, Toast.LENGTH_LONG).show();


    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            initFusedApi();

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;

        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
    }


    private void initFusedApi () {

        fusedApiClient = new GoogleApiClient.Builder(getApplicationContext())
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();

        if (!fusedApiClient.isConnected()) {
            Log.d("debug", "not connected");
            fusedApiClient.connect();
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest locationRequest = LocationRequest.create()
                                                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                                        .setInterval(5000);

        Log.d("debug", "running");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(fusedApiClient,locationRequest,this);

        Location currentLastLocation = LocationServices.FusedLocationApi.getLastLocation(fusedApiClient);

        if (currentLastLocation != null) {
            Log.d("debug", "last location: lat: " + currentLastLocation.getLatitude());
            Log.d("debug", "last location: long: " + currentLastLocation.getLongitude());
        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }


}
