package cz.destil.glasquare.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;

import com.google.android.glass.media.CameraManager;

import java.io.File;
import java.util.List;

/**
 * Set of intent-related utils.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class IntentUtils {

    private static final int SPEECH_REQUEST = 0;
    private static final int TAKE_PICTURE_REQUEST = 1;

    public static void launchNavigation(Activity activity, double latitude, double longitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("google.navigation:q=" + latitude + "," + longitude));
        activity.startActivity(intent);
    }

    public static void startSpeechRecognition(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        activity.startActivityForResult(intent, SPEECH_REQUEST);
    }

    public static String processSpeechRecognitionResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                return results.get(0);
            }
        }
        return null;
    }

    public static void takePicture(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    public static void processTakePictureResult(Activity activity, int requestCode, int resultCode, Intent data, OnPictureReadyListener listener) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            String picturePath = data.getStringExtra(
                    CameraManager.EXTRA_PICTURE_FILE_PATH);
            listener.onPathKnown();
            processPictureWhenReady(activity, picturePath, listener);
        }
    }

    private static void processPictureWhenReady(final Activity activity, final String picturePath, final OnPictureReadyListener listener) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
            listener.onPictureReady(pictureFile);
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
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
                                && affectedFile.equals(pictureFile));

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(activity, picturePath, listener);
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
        public void onPathKnown();
        public void onPictureReady(File image);
    }
}
