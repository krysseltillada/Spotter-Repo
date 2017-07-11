package com.lmos.spotter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emman on 6/13/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    // Database version. Needed for onUpgrade
    private static final int DATABASE_VERSION = 1;
    // Database
    private static final String DATABASE_NAME = "Spotter";
    // Tables
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_PLACE_NAME = "place_name";
    // Column Names
    private static final String KEY_ID = "_id";
    private static final String KEY_PLACEID = "placeID";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DESC = "description";
    private static final String KEY_TYPE = "type";
    private static final String KEY_LOCALITY = "locality";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";
    private static final String KEY_DEALS = "deals";
    private static final String KEY_CLASS = "class";
    private static final String KEY_PRICE_RANGE = "price_range";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_RECOMMENDED = "recommended";
    private static final String KEY_RATING = "rating";

    Context context;
    Utilities.OnDbResponseListener onDbResponseListener;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DbHelper(Context context, Utilities.OnDbResponseListener onDbResponseListener) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.onDbResponseListener = onDbResponseListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + "(" +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_PLACEID + " INTEGER," +
                        KEY_NAME + " TEXT," +
                        KEY_ADDRESS + " TEXT," +
                        KEY_DESC + " TEXT," +
                        KEY_LOCALITY + " TEXT," +
                        KEY_LAT + " DOUBLE," +
                        KEY_LNG + " DOUBLE," +
                        KEY_TYPE + " TEXT," +
                        KEY_DEALS + " TEXT," +
                        KEY_CLASS + " INTEGER," +
                        KEY_PRICE_RANGE + " TEXT," +
                        KEY_IMAGE + " TEXT," +
                        KEY_RECOMMENDED + " TEXT," +
                        KEY_RATING + " TEXT)"

        );

        db.execSQL(
                "Create virtual table " + TABLE_PLACE_NAME + " using fts4 (" +
                        KEY_NAME + " TEXT," +
                        KEY_ADDRESS + " TEXT," +
                        KEY_TYPE + " TEXT," +
                        KEY_LAT + " DOUBLE," +
                        KEY_LNG + " DOUBLE," +
                        " notindexed=" + KEY_ADDRESS + "," +
                        " notindexed=" + KEY_LAT + "," +
                        " notindexed=" + KEY_LNG + "," +
                        " notindexed=" + KEY_TYPE +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addToFavorites(Place place){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_PLACEID, place.getPlaceID());
        cv.put(KEY_NAME, place.getPlaceName());
        cv.put(KEY_ADDRESS, place.getPlaceAddress());
        cv.put(KEY_LOCALITY, place.getPlaceLocality());
        cv.put(KEY_DESC, place.getPlaceDescription());
        cv.put(KEY_TYPE, place.getPlaceType());
        cv.put(KEY_LAT, place.getPlaceLat());
        cv.put(KEY_LNG, place.getPlaceLng());
        cv.put(KEY_DEALS, place.getPlaceDeals());
        cv.put(KEY_CLASS, place.getPlaceClass());
        cv.put(KEY_PRICE_RANGE, place.getPlacePriceRange());
        cv.put(KEY_IMAGE, place.getPlaceImageLink());
        cv.put(KEY_RECOMMENDED, place.getRecommended());
        cv.put(KEY_RATING, place.getRating());

        db.insert(TABLE_FAVORITES, null, cv);
        onDbResponseListener.onDbResponse("Place has been bookmarked.", KEY_PLACEID);

        db.close();

    }

    public void deleteBookmark(String[] placeID){

        SQLiteDatabase db = this.getWritableDatabase();

        if(placeID != null) {
            for (String id : placeID)
                db.delete(TABLE_FAVORITES, KEY_PLACEID, new String[]{id});
        }
        db.close();

    }

    public Cursor querySearch(String[] keyword){

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("Select rowid as _id," +
                        KEY_NAME + "," + KEY_ADDRESS + "," +
                        KEY_LAT + "," + KEY_LNG + "," +
                        KEY_TYPE + " from " +
                        TABLE_PLACE_NAME + " where " +
                        TABLE_PLACE_NAME + "  match ? ", keyword);

    }

    public void savePlaceName(List<Place> places){

        SQLiteDatabase db = this.getWritableDatabase();

        for(Place place : places ){

            ContentValues cv = new ContentValues();
            cv.put(KEY_NAME, place.getPlaceName());
            cv.put(KEY_ADDRESS, place.getPlaceAddress());
            cv.put(KEY_TYPE, place.getPlaceType());
            cv.put(KEY_LAT, place.getPlaceLat());
            cv.put(KEY_LNG, place.getPlaceLng());

            db.insert(TABLE_PLACE_NAME, null, cv);

        }

        db.close();

    }

    public List<Place> getBookmarks(String placeType){

        Log.d("debug", "from db: " + placeType);

        SQLiteDatabase db = this.getReadableDatabase();
        List<Place> bookmarks = new ArrayList<>();

        Cursor cursor = db.rawQuery("Select * from favorites WHERE type = '" + placeType + "'", null);


        while(cursor.moveToNext()){

            Place place = new Place();


            place.setPlaceID(cursor.getString(cursor.getColumnIndex(KEY_PLACEID)));
            place.setplaceName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            place.setplaceAddress(cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
            place.setplaceLocality(cursor.getString(cursor.getColumnIndex(KEY_LOCALITY)));
            place.setPlaceLat(cursor.getString(cursor.getColumnIndex(KEY_LAT)));
            place.setPlaceLng(cursor.getString(cursor.getColumnIndex(KEY_LNG)));
            place.setplaceDescription(cursor.getString(cursor.getColumnIndex(KEY_DESC)));
            place.setplaceClass(cursor.getString(cursor.getColumnIndex(KEY_CLASS)));
            place.setplaceType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            place.setPlaceDeals(cursor.getString(cursor.getColumnIndex(KEY_DEALS)));
            place.setplacePriceRange(cursor.getString(cursor.getColumnIndex(KEY_PRICE_RANGE)));
            place.setplaceImageLink(cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
            //place.setRecommended(cursor.getString(cursor.getColumnIndex(KEY_RECOMMENDED)));
           // place.setRating(cursor.getString(cursor.getColumnIndex(KEY_RATING)));

           // Log.d("BK-IMAGE_SET", place.getPlaceImageLink());

            bookmarks.add(place);

        }

        cursor.close();

        db.close();

        return bookmarks;
    }

}
