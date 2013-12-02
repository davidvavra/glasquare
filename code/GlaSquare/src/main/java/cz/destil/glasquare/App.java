package cz.destil.glasquare;

import android.app.Application;

/**
 * Created by Destil on 2.12.13.
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
