package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by marcus on 1/20/14.
 */
public class GoogleGameApiStore extends BaseKeyValueStore implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /* migrated to the GoogleApiClient */
    //GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String LOG_TAG = GoogleGameApiStore.class.getSimpleName();

    public static final String[] mScopes = {Scopes.APP_STATE};
    public static final int RESOLUTION_REQUEST_CODE = 984168;

    private Map<String, Object> cache = new HashMap<String, Object>();

    public static final int STATE_UNCONFIGURED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    int mState = STATE_UNCONFIGURED;
    boolean isConnected;

    //AppStateClient mAppStateClient;
    GoogleApiClient mGoogleApiClient = null;
    Activity mActivity = null;

    public GoogleGameApiStore(Activity activity, StatusListener listener) {
        super(listener);
        mActivity = activity;

        Log.d(LOG_TAG, "setting up GoogleApiClient" + activity.getApplicationContext() + " ");
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(LOG_TAG, "mGoogleApiClient => " + mGoogleApiClient);
//        mAppStateClient = new AppStateClient.Builder(context, this, this)
//                .setScopes(mScopes)
//                .create();
        mState = STATE_DISCONNECTED;

        Log.d(LOG_TAG, "do In Background connecting to cloud save !! ");

        mGoogleApiClient.connect();
    }
//
//    void setupGoogleApiClient() {
//        // could be that there is pending instance ?
//        if (mGoogleApiClient != null) {
//            Log.e(LOG_TAG, " error api state occured ");
//            throw new IllegalStateException("Illegal State Exception");
//        }
//    }

    @Override
    public void onConnected(Bundle bundle) {
        mState = STATE_CONNECTED;
        Log.i(LOG_TAG, "onConnected invoked state =[" + mState + "]");

        // TODO load cache from cloud
        // unzip
        // split into cache

        if(statusListener!=null) statusListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mState = STATE_DISCONNECTED;
        Log.d(LOG_TAG, "onConnectionSuspended invoked state =[" + mState + "]");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mState = STATE_DISCONNECTED;
        Log.w(LOG_TAG, "connection failed: " + connectionResult.toString());
        if(connectionResult.hasResolution()) {
            Log.d(LOG_TAG, "trying resolution");
            try {
                connectionResult.startResolutionForResult(mActivity, RESOLUTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.w(LOG_TAG, "could not start resolution", e);
            }
        }

    }


    @Override
    public synchronized void flush() throws IOException {

        // create zipped content from cache
        Log.d(LOG_TAG,"compressing cache...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new GZIPOutputStream(baos));
        for(Map.Entry<String,Object> e :  cache.entrySet()) {
            pw.println(e.getKey()+"|"+serialize(e.getValue()));
        }
        pw.flush();

        int chunkLength = 0;

        byte[] bytes = baos.toByteArray();
        Log.d(LOG_TAG,"compressed length="+bytes.length);

        /* if cloudMap is larger than 256 split to the 4 available slots */
        if (bytes.length > 256 * 1024L) {
            for (int i = 0; i < 4; i++) {
                int off = i * 256 * 1024;
                chunkLength = (bytes.length - off) > (256 * 1024) ? (256 * 1024) : bytes.length - off;

                byte[] chunk = new byte[chunkLength];

                System.arraycopy(bytes, off, chunkLength, 0, chunkLength);
                Log.d(LOG_TAG, " Chunk = {" + chunkLength + "}" + " at part {" + i + "}");
                /* write chunk to cloud slot #i */

                //new googleApiClient approach
                // FIXME???
                AppStateManager.update(mGoogleApiClient, mState, chunk);

                if (chunkLength < (256 * 1024L))
                    break;
            }

            // write chunks to cloud chunk #0


        } else {
            Log.d(LOG_TAG, "chunks below limit so persisting  ");
            // FIXME???
            AppStateManager.update(mGoogleApiClient, mState, bytes);
        }

    }


    @Override
    public synchronized Object get(String key) {
        return cache.get(key);
    }


    @Override
    public synchronized void put(String key, Object value) {
        if(!isKeyValid(key)) throw new IllegalArgumentException("Keys may not contain pipe characters");
        cache.put(key, value);
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }


}
