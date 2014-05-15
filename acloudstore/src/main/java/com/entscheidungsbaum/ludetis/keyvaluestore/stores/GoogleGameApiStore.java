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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

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
    private boolean mIntentInProgress;

    public GoogleGameApiStore(Activity activity, StatusListener listener) {
        super(listener);
        mActivity = activity;

        Log.d(LOG_TAG, "setting up GoogleApiClient" + activity.getApplicationContext() + " ");
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(AppStateManager.API)
                .addScope(AppStateManager.SCOPE_APP_STATE)
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

        // load cache from cloud
        final byte[][] results = new byte[4][];
        for (int i=0; i<4; i++) {
            final PendingResult<AppStateManager.StateResult> result = AppStateManager.load(mGoogleApiClient, i);
            result.setResultCallback(new ResultCallback<AppStateManager.StateResult>() {
                @Override
                public void onResult(AppStateManager.StateResult stateResult) {
                    final AppStateManager.StateLoadedResult loadedResult = stateResult.getLoadedResult();
                    Log.d(LOG_TAG, "got stateResult for slot " + loadedResult.getStateKey());
                    results[loadedResult.getStateKey()] = loadedResult.getLocalData();
                    for(int j=0; j<4; j++) {
                        if(results[j]==null) return;
                    }
                    loadResults(results);
                }
            });

        }
        // unzip
        // split into cache

        if(statusListener!=null) statusListener.onConnected();
    }

    private void loadResults(byte[][] results) {
        // TODO concat results[0]..[3]
        // TODO uncompress results
        // TODO deserialize to our cache map
    }

    @Override
    public void onConnectionSuspended(int i) {
        mState = STATE_DISCONNECTED;
        Log.d(LOG_TAG, "onConnectionSuspended invoked state =[" + mState + "], trying to connect again");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mState = STATE_DISCONNECTED;
        Log.w(LOG_TAG, "connection failed: " + connectionResult.toString());
        if(!mIntentInProgress && connectionResult.hasResolution()) {
            mIntentInProgress = true;
            Log.d(LOG_TAG, "trying resolution");
            try {
                connectionResult.startResolutionForResult(mActivity, RESOLUTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.w(LOG_TAG, "could not start resolution", e);
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }

    }


    @Override
    public boolean notifyActivityResult(int requestCode, int resultCode) {
        if(requestCode==RESOLUTION_REQUEST_CODE) {
            Log.d(LOG_TAG, "notified by activity result, resultCode=" + resultCode);
            mIntentInProgress = false;
            if(resultCode==Activity.RESULT_OK) {
                if (!mGoogleApiClient.isConnecting()) {
                    Log.d(LOG_TAG, "trying to connect again");
                    mGoogleApiClient.connect();
                }
            }
            return true;
        }
        return false;
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
                AppStateManager.update(mGoogleApiClient, i, chunk);

                if (chunkLength < (256 * 1024L))
                    break;
                // fill unused slots with 0 length byte arrays
            }

            // write chunks to cloud chunk #0


        } else {
            Log.d(LOG_TAG, "chunks below limit so persisting  ");
            // FIXME???
            AppStateManager.update(mGoogleApiClient, 0, bytes);
            // fill unused slots with 0 length byte arrays
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
