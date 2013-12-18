package cz.destil.glasquare.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;

import java.util.List;

/**
 * Set of intent-related utils.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class IntentUtils {

    private static final int SPEECH_REQUEST = 0;

    public static void launchNavigation(Activity activity, double latitude, double longitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.google.glass.maps", "com.google.glass.maps.NavigationActivity"));
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
}
