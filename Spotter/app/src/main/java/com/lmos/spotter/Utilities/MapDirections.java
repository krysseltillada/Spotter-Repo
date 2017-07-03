package com.lmos.spotter.Utilities;

import android.app.VoiceInteractor;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kryssel on 6/25/2017.
 */

public class MapDirections  {

    public interface OnDoneDrawDirectionListener {
        void onDoneDrawDirection (String duration, String distance);
    }

    OnDoneDrawDirectionListener onDoneDrawDirectionListener;

    GoogleMap googleMap;
    Context context;
    LatLng src;
    LatLng dest;
    int lineColor;
    int lineWidth;

    public MapDirections (Context con, GoogleMap map, LatLng source, LatLng destination, int lc, int lw) {
        googleMap = map;
        src = source;
        dest = destination;
        context = con;
        lineColor = lc;
        lineWidth = lw;
    }

    public MapDirections setOnDoneDrawDirectionListener (OnDoneDrawDirectionListener onDoneDrawDirectionListener) {
        this.onDoneDrawDirectionListener = onDoneDrawDirectionListener;
        return this;
    }

    public MapDirections drawDirections () {
       getGoogleDirectionResponse(src, dest);
       return this;
       // new MapDirectionDrawer(googleMap).execute(getGoogleDirectionResponse(src, dest));
    }

    private void getGoogleDirectionResponse (LatLng s, LatLng d) {


        String requestURL = "https://maps.googleapis.com/maps/api/directions/json?origin="+s.latitude +
                            "," + s.longitude + "&destination="+d.latitude+","+d.longitude+
                            "&sensor=false";


        RequestQueue requestDirection = Volley.newRequestQueue(context);

        StringRequest requestStringDirection = new StringRequest(Request.Method.GET, requestURL
                    , new Response.Listener<String>() {

                        public void onResponse (String serverResponse) {
                            new MapDirectionDrawer(googleMap, lineColor, lineWidth).execute(serverResponse);
                        }
                    }, new Response.ErrorListener() {
                           public void onErrorResponse(VolleyError volleyError) {

                           }
                    });

        requestDirection.add(requestStringDirection);

    }


    private class MapDirectionDrawer extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        GoogleMap gMap;
        int lColor;
        int lWidth;

        public MapDirectionDrawer (GoogleMap map, int lc, int lw) {
            gMap = map;
            lColor = lc;
            lWidth = lw;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{

                Log.d("debbbbb", jsonData[0]);

                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration =  (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(lWidth);
                lineOptions.color(lColor);

            }

            onDoneDrawDirectionListener.onDoneDrawDirection(duration, distance);

            // Drawing polyline in the Google Map for the i-th route
            gMap.addPolyline(lineOptions);
        }
    }

}
