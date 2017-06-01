package com.lmos.spotter;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by linker on 31/05/2017.
 *
 *  This class will blur the image that is mostly
 *  used as a background component.
 *
 *  BITMAP_SCALE and BITMAP_RADIUS will set the depth of blur effect on an image.
 *
 */

public class BlurImg {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BITMAP_RADIUS = 2.5f;

    public static Bitmap blurImg(Context context, Bitmap blurme){

        int width  = Math.round(blurme.getWidth() * BITMAP_SCALE);
        int height  = Math.round(blurme.getHeight() * BITMAP_SCALE);

        Bitmap input_bitmap = Bitmap.createScaledBitmap(blurme, width, height, true);
        Bitmap output_bitmap = Bitmap.createBitmap(input_bitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur sblur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, input_bitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, output_bitmap);

        sblur.setRadius(BITMAP_RADIUS);
        sblur.setInput(tmpIn);
        sblur.forEach(tmpOut);

        tmpOut.copyTo(output_bitmap);

        return output_bitmap;
    }

}
