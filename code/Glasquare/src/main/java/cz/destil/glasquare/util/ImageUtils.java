package cz.destil.glasquare.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Utils for manipulating with images.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class ImageUtils {

    private static final int TARGET_WIDTH = 960; // in pixels

    public static File resize(File original) {
        // get info about dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(original.getAbsolutePath(), options);
        int targetHeight = (int) (options.outHeight / ((double) options.outWidth / (double) TARGET_WIDTH));
        options.inSampleSize = calculateInSampleSize(options);
        // resize image
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(original.getAbsolutePath(), options);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, TARGET_WIDTH, targetHeight, true);
        bitmap.recycle();
        // save scaled image to a file
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(original);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        } catch (FileNotFoundException e) {
            DebugLog.e(e);
        } finally {
            IO.close(outputStream);
            scaledBitmap.recycle();
        }
        return original;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > TARGET_WIDTH) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > TARGET_WIDTH) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
