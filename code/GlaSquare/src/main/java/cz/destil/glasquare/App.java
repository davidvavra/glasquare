package cz.destil.glasquare;

import android.app.Application;

/**
 * Main application class.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class App extends Application {

    static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App get() {
        return sInstance;
    }
}
