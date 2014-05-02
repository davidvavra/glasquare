package cz.destil.glasquare.util;

import android.os.AsyncTask;

/**
 * Base class for all AsyncTasks
 * @author David Vávra (me@destil.cz)
 */
public abstract class BaseAsyncTask extends AsyncTask<Void, Void, Void> {

    public void start() {
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Void doInBackground(Void... params) {
        inBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        postExecute();
    }

    public abstract void inBackground();

    public abstract void postExecute();
}
