package cz.destil.glasquare.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

/**
 * Set of intent-related utils.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class IntentUtils {
    public static void launchNavigation(Activity activity, double latitude, double longitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.google.glass.maps", "com.google.glass.maps.NavigationActivity"));
        intent.setData(Uri.parse("google.navigation:q=" + latitude + "," + longitude));
        activity.startActivity(intent);
    }
}
