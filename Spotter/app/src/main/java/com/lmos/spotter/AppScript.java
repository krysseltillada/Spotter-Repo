package com.lmos.spotter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by emman on 6/22/2017.
 * This class will handle the app's transaction
 * to the server.
 *
 * setData method will accept data to be sent to
 * the server and determine what type of transaction
 * is being requested.
 * -----------
 * connect method will establish the app's
 * connection to server using HttpUrlConnection.
 * -----------
 * parseResult method will decode JSON response
 * from the server. JSON response will be categorized
 * as response_code and response_message. This method
 * will also determine how will the app use the
 * information based on response_code.
 * -----------
 * getResult method will return the server
 * response_message to calling class.
 */

public class AppScript {

    Activity activity;
    private List<Place> placeNames;
    private String response;
    private String offSet;
    private String tableCount;
    private List<Place> placeList;

    protected AppScript(Activity activity){ this.activity = activity; }

    public void setData(String url, Map<String, String> params){

        String post_data = "";
        final String default_url = "http://admin-spotter.000webhostapp.com/app_scripts/";

        if(params != null){

            Iterator<Map.Entry<String, String>> entry = params.entrySet().iterator();

            while(entry.hasNext()){

                Map.Entry<String, String> index_item = entry.next();
                try {

                    Log.d("debug", "key: " + index_item.getKey() + " value: " + index_item.getValue());

                    post_data += URLEncoder.encode(index_item.getKey(), "UTF-8") + "="
                              +  URLEncoder.encode(index_item.getValue(), "UTF-8");

                    if(entry.hasNext())
                        post_data += "&";

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

        }

        connect(default_url + url, post_data);

    }

    private void connect(String setUrl, String post_data) {

        Log.d("SplashScreen", "Connecting");

        Log.d("debug", setUrl + post_data);

        final int CONNECTION_TIME_OUT = 60000;
        final String REQUEST_METHOD = "POST";

        try {

            /** Initialize connection to server **/
            URL url = new URL(setUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(REQUEST_METHOD);
            httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT);
            httpURLConnection.setRequestProperty("connection", "close");
            httpURLConnection.connect();
            /** End of initializing connection to server. **/

            /** Initialize Output Stream that will
             * pass data to server.
             */
            if(!post_data.equals("")){
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                // Pass data to server.
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
            /** End of passing data to server. **/

            /** Initialize InputStream that will
             * listen and accept data coming from
             * the server.
             */
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            // Get the result from the server.
            String line, result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            // Close connection to server.
            httpURLConnection.disconnect();

            parseResult(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Utilities.logError(activity, e.getMessage());
            response = e.getMessage();
        }catch(UnknownHostException e){
            Utilities.logError(activity, e.getMessage());
            e.printStackTrace();
            response = "Couldn't connect to server. Make sure you have stable internet connection.";
        }catch (SocketTimeoutException | ConnectException e){
            e.printStackTrace();
            Utilities.logError(activity, e.getMessage());
            Log.d("debug", e.getMessage());
            response = "Couldn't connect to server.";
        }catch (IOException e) {
            e.printStackTrace();
            Utilities.logError(activity, e.getMessage());
            response = e.getMessage();
        }

    }

    private void loadUserData (JSONArray accountProfile, String response_code) {

        try {

            if (response_code.equals("0x01")) {

                if (!LoginActivity.login_prefs.getString("accountID", "").equals(accountProfile.get(0).toString()))
                    LoginActivity.set_login_prefs.putBoolean("isDiffAccount", true);
                else
                    LoginActivity.set_login_prefs.putBoolean("isDiffAccount", false);

                LoginActivity.set_login_prefs.putString("accountHasBookmark", accountProfile.get(6).toString());

            } else if (response_code.equals("0x02")){
                LoginActivity.set_login_prefs.putBoolean("isDiffAccount", true);
                LoginActivity.set_login_prefs.putString("accountHasBookmark", "false");
            }

            LoginActivity.set_login_prefs.putString("accountID", accountProfile.get(0).toString());
            LoginActivity.set_login_prefs.putString("accountName", accountProfile.get(1).toString());
            LoginActivity.set_login_prefs.putString("accountUsername", accountProfile.get(2).toString());
            LoginActivity.set_login_prefs.putString("accountEmail", accountProfile.get(3).toString());
            LoginActivity.set_login_prefs.putString("accountImage", accountProfile.get(4).toString());
            LoginActivity.set_login_prefs.putString("accountPassword", accountProfile.get(5).toString());

            LoginActivity.set_login_prefs.apply();

        } catch (JSONException err) {
            Log.d("debug", err.getMessage());
        }

    }

    private void parseResult(String processResult){

        try {

            final JSONObject jsonObject = new JSONObject(processResult);
            String response_code = jsonObject.getString("response_code");

            Log.d("debug", processResult);

            Log.d("debug", jsonObject.getString("response_msg"));

            if (response_code.equals("0x01") || response_code.equals("0x02") || response_code.equals("0x03")) {

                if (!response_code.equals("0x03")) {


                    JSONArray accountProfile = jsonObject.getJSONArray("response_data");

                    loadUserData(accountProfile, response_code);

                    Log.d("debug", jsonObject.getString("response_data"));

                }

                response = jsonObject.getString("response_msg");

            } else if (response_code.equals("0x10") || response_code.equals("0x11") || response_code.equals("0x12")) {

                List<Place> place = new ArrayList<>();

                JSONArray place_list = jsonObject.getJSONArray("response_data");

                for (int index = 0; index < place_list.length(); index++) {

                    Place setPlace = new Place();
                    JSONObject place_item = place_list.getJSONObject(index);
                    Log.d("LOG", place_item.getString("placeID") + " " + response_code);
                    setPlace.setPlaceID(place_item.getString("placeID"));
                    setPlace.setplaceName(place_item.getString("Name"));
                    setPlace.setplaceType(place_item.getString("Type"));
                    setPlace.setplaceAddress(place_item.getString("Address"));
                    setPlace.setPlaceLat(place_item.getString("Latitude"));
                    setPlace.setPlaceLng(place_item.getString("Longitude"));

                    if (response_code.equals("0x10") || response_code.equals("0x12")) {

                        setPlace.setplaceLocality(place_item.getString("Locality"));
                        setPlace.setplaceDescription(place_item.getString("Description"));
                        setPlace.setplaceImageLink(place_item.getString("Image"));
                        setPlace.setplaceClass(place_item.getString("Class"));
                        setPlace.setplacePriceRange(place_item.getString("PriceRange"));
                        setPlace.setRecommended(place_item.getString("Recommended"));
                        setPlace.setRating(place_item.getString("Rating"));
                        setPlace.setUserReviews(place_item.getString("userReviews"));
                        setPlace.setplaceImageLink(place_item.getString("Image"));

                        Log.d("IMAGE-JSON", place_item.getString("Image"));

                        if (response_code.equals("0x10")) {

                            JSONObject responseData = new JSONObject(jsonObject.getString("response_offsetCount"));
                            offSet = responseData.getString("endOffset");
                            tableCount = responseData.getString("tableCount");
                        }

                    }

                    place.add(setPlace);

                }

                if (response_code.equals("0x10") || response_code.equals("0x12"))
                    placeList = new ArrayList<>(place);
                else
                    placeNames = new ArrayList<>(place);

                response = jsonObject.getString("response_msg");

            } else if (response_code.equals("1x01")) {
                response = jsonObject.getString("response_msg");
            }

        }catch(JSONException e){
                e.printStackTrace();
                Utilities.logError(activity, e.getMessage());
                response = e.getMessage();
            }
        }

    public List<Place> getPlaceNames() { return placeNames; }
    public List<Place> getPlacesList () {
        return placeList;
    }
    public String getTableCount () { return tableCount; }
    public String getResult(){ return response; }
    public String getOffSet(){ return offSet; }

}
