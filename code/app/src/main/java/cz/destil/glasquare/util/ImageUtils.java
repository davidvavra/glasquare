package cz.destil.glasquare.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.FileObserver;

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

    public static void processPictureWhenReady(final Activity activity, final File picture, final OnPictureReadyListener listener) {

        if (picture.exists()) {
            // The picture is ready; process it.
            listener.onPictureReady();
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = picture.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath()) {
                // Protect against additional pending events after CLOSE_WRITE is
                // handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = (event == FileObserver.CLOSE_WRITE
                                && affectedFile.equals(picture));

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(activity, picture, listener);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    public interface OnPictureReadyListener {
        public void onPictureReady();
    }
}
