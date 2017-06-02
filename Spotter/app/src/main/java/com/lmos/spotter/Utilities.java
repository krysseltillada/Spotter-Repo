package com.lmos.spotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by Kryssel on 6/2/2017.
 */

public class Utilities {

    public static class BlurImg {

            private static final float BITMAP_SCALE = 0.4f;

            public static Bitmap blurImg(Context context, Bitmap blurme, float blurValue){

                int width  = Math.round(blurme.getWidth() * BITMAP_SCALE);
                int height  = Math.round(blurme.getHeight() * BITMAP_SCALE);

                Bitmap input_bitmap = Bitmap.createScaledBitmap(blurme, width, height, true);
                Bitmap output_bitmap = Bitmap.createBitmap(input_bitmap);

                RenderScript rs = RenderScript.create(context);
                ScriptIntrinsicBlur sblur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

                Allocation tmpIn = Allocation.createFromBitmap(rs, input_bitmap);
                Allocation tmpOut = Allocation.createFromBitmap(rs, output_bitmap);

                sblur.setRadius((blurValue > 25) ? 25 : (blurValue < 0) ? 0 : blurValue);
                sblur.setInput(tmpIn);
                sblur.forEach(tmpOut);

                tmpOut.copyTo(output_bitmap);

                return output_bitmap;
            }

        }


    public static void QuerySearchResults(String searchValue, SimpleCursorAdapter suggestion, String[] keywords) {

        MatrixCursor suggestions = new MatrixCursor(new String[]{BaseColumns._ID, "judy"});

        for (int i = 0; i != keywords.length; ++i) {
            if (keywords[i].toLowerCase().startsWith(searchValue.toLowerCase())) {
                Log.d("sample", searchValue);
                suggestions.addRow(new Object[]{i, keywords[i]});
            }
        }

        suggestion.changeCursor(suggestions);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
