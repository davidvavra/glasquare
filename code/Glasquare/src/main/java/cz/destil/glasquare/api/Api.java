package cz.destil.glasquare.api;

import cz.destil.glasquare.util.DebugLog;
import retrofit.RestAdapter;

/**
 * Processor for 4sq API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class Api {

    public static final String BUILD_DATE = "20131129";
    /**
     * Create a class Hidden with fields CLIENT_ID and CLIENT_SECRET and ignore it in git
     */
    public static final String AUTH = "&client_id=" + Hidden.CLIENT_ID + "&client_secret=" + Hidden.CLIENT_SECRET + "&v=" + BUILD_DATE;

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
