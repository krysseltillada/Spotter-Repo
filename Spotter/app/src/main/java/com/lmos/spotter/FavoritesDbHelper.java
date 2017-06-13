package com.lmos.spotter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by emman on 6/13/2017.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Favorites";
    private static final String TABLE_NAME = "favorites";
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STREET = "street";
    private static final String KEY_DESC = "description";
    private static final String KEY_STATE = "state";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longtitude";
    private static final String KEY_RATING = "rating";
    Context context;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("DB", "Constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Creating");
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," + KEY_STREET + " TEXT," + KEY_DESC + " TEXT," + KEY_STATE + " TEXT," +
                        KEY_LAT + " INTEGER," + KEY_LNG + " INTEGER," + KEY_RATING + " INTEGER)"
        );
        Log.d("DB", "PATH=" + context.getDatabasePath(DATABASE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addToFavorites(){
        Log.d("DB", "Adding...");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, "Solaire");
        cv.put(KEY_LAT, 120.8787);
        cv.put(KEY_LNG, 12.1348);

        db.insert(TABLE_NAME, null, cv);
        Log.d("DB", "Added!");
    }

}
