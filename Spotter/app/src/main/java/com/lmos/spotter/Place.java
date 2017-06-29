package com.lmos.spotter;

import android.util.Log;

/**
 * Created by emman on 6/21/2017.
 * This class will hold the contents of information about Places.
 */

public class Place  {

    private String placeID;
    private String placeName;
    private String placeAddress;
    private String placeLocality;
    private String placeDescription;
    private String placeClass;
    private String placeType;
    private String placePriceRange;
    private String placeImage;

    public Place(){}

    public void setplaceName(String placeName){this.placeName = placeName;}

    public void setplaceAddress(String placeAddress){this.placeAddress = placeAddress;}

    public void setplaceLocality(String placeLocality){this.placeLocality = placeLocality;}

    public void setplaceDescription(String placeDescription){this.placeDescription = placeDescription;}

    public void setplaceClass(String placeClass){this.placeClass = placeClass;}

    public void setplaceType(String placeType){this.placeType = placeType;}

    public void setplacePriceRange(String placePriceRange){this.placePriceRange = placePriceRange;}

    public void setplaceImage(String placeImage){this.placeImage = placeImage;}

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID){this.placeID = placeID;}

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public String getPlaceLocality() {
        return placeLocality;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public String getPlaceClass() {
        return placeClass;
    }

    public String getPlaceType() {
        return placeType;
    }

    public String getPlacePriceRange() {
        return placePriceRange;
    }

    public String getPlaceImage() {
        return placeImage;
    }

    public void print () {

        Log.d("debug", getPlaceName() + "\n");

    }
}
