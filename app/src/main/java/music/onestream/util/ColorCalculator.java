package music.onestream.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * Created by ruspe_000 on 2017-03-19.
 */

public class ColorCalculator {

    public static int getAverageColor(Bitmap bmp) {
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int intArray[] = new int[bmp.getWidth()*bmp.getHeight()];
        bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        return intArray[0];
    }

    public static Bitmap getBitmapFromBytes(byte[] art) {
        Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);
        return Bitmap.createScaledBitmap(image, 300, 300, false);
    }

    public static int getColorInversion(int backgroundColor) {
        int textColor = Color.rgb(Constants.maxColor-Color.red(backgroundColor),
                Constants.maxColor-Color.green(backgroundColor),
                Constants.maxColor-Color.blue(backgroundColor));
        if (Math.abs(textColor - backgroundColor)<= Constants.minColorDifference)
        {
            textColor = Color.WHITE;
        }
        return textColor;
    }
}
