package cz.destil.glasquare.api;

import cz.destil.glasquare.util.DebugLog;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by Destil on 23.10.13.
 */
public class Api {

    public static final String CLIENT_ID = "3XRAA220QQWY4XHJH11TGRGEFYSW03YOBUL3225Y3KBMJ3XY";
    public static final String CLIENT_SECRET = "4WYHXNQQVUYSTJGFMQZNRFBUKU4GFKPEBKFM0HFBVD42HN5U";
    public static final String BUILD_DATE = "20131129";

    public static final String URL = "https://api.foursquare.com/v2";

    public static RestAdapter get() {
        return new RestAdapter.Builder().setServer(URL).setLogLevel(RestAdapter.LogLevel.BASIC).setLog(new RestAdapter.Log() {
            @Override
            public void log(String s) {
                DebugLog.d(s);
            }
        }).build();
    }
}
