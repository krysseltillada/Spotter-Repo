package com.lmos.spotter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emman on 6/21/2017.
 * This class will hold the contents of information about Places.
 */

public class Place implements Parcelable {

    public static final Parcelable.Creator<Place> CREATOR
            = new Parcelable.Creator<Place>(){

        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private String placeID;
    private String placeName;
    private String placeAddress;
    private String placeLocality;
    private String placeDescription;
    private String placeLat;
    private String placeLng;
    private String placeClass;
    private String placeType;
    private String placePriceRange;
    private String placeDeals;
    private String placeImageLink;
    private String recommended;
    private String rating;
    private String userReviews;
    private String bookmarks;

    public Place(){}
    public Place(Parcel in){

        placeID = in.readString();
        placeName = in.readString();
        placeAddress = in.readString();
        placeLocality = in.readString();
        placeDescription = in.readString();
        placeLat = in.readString();
        placeLng = in.readString();
        placeClass = in.readString();
        placeType = in.readString();
        placePriceRange = in.readString();
        placeImageLink = in.readString();
        placeDeals = in.readString();
        recommended = in.readString();
        rating = in.readString();
        userReviews = in.readString();
        bookmarks = in.readString();

    }

    public void setplaceName(String placeName){this.placeName = placeName;}

    public void setplaceAddress(String placeAddress){this.placeAddress = placeAddress;}

    public void setplaceLocality(String placeLocality){this.placeLocality = placeLocality;}

    public void setplaceDescription(String placeDescription){this.placeDescription = placeDescription;}

    public void setplaceClass(String placeClass){this.placeClass = placeClass;}

    public void setplaceType(String placeType){this.placeType = placeType;}

    public void setplacePriceRange(String placePriceRange){this.placePriceRange = placePriceRange;}

    public void setplaceImageLink(String placeImageLink){this.placeImageLink = placeImageLink;}

    public String getUserReviews() {return userReviews;}

    public void setUserReviews(String us) {userReviews = us;}

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

    public String getPlaceImageLink() {
        return placeImageLink;
    }

    public String getPlaceDeals(){ return placeDeals; }

    public void setPlaceDeals(String placeDeals){ this.placeDeals = placeDeals; }

    public String getPlaceLat(){ return placeLat; }

    public void setPlaceLat(String placeLat){ this.placeLat = placeLat; }

    public String getPlaceLng(){ return placeLng; }

    public void setPlaceLng(String placeLng){ this.placeLng = placeLng; }

    public String getRecommended () { return recommended; }

    public void setRecommended(String recom) {this.recommended = recom;}

    public String getRating () { return rating; }

    public void setRating(String rt) {this.rating = rt;}

    public String getPlaceRating(){ return rating; }

    public String getBookmarks(){ return bookmarks; }

    public void setBookmarks(String bookmarks){ this.bookmarks = bookmarks; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(placeID);
        dest.writeString(placeName);
        dest.writeString(placeAddress);
        dest.writeString(placeLocality);
        dest.writeString(placeDescription);
        dest.writeString(placeLat);
        dest.writeString(placeLng);
        dest.writeString(placeClass);
        dest.writeString(placeType);
        dest.writeString(placePriceRange);
        dest.writeString(placeImageLink);
        dest.writeString(placeDeals);
        dest.writeString(recommended);
        dest.writeString(rating);
        dest.writeString(bookmarks);

    }

}
