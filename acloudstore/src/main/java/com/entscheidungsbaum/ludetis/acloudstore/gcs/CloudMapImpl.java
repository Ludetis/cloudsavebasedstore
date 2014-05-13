package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * Created by marcus on 1/20/14.
 */
public class CloudMapImpl implements CloudMap, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /* migrated to the GoogleApiClient */
    //GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    final String LOG_TAG = CloudMapImpl.class.getName();

    public static final String[] mScopes = {Scopes.APP_STATE};

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

    public CloudMapImpl(Context context) {


        Log.d(LOG_TAG, "setting up GoogleApiClient" + context.getApplicationContext() + " ");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
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
    public boolean onUpdated() throws Exception {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mState = STATE_CONNECTED;
        Log.i(LOG_TAG, "onConnected invoked state =[" + mState + "]");

        // TODO load cache from cloud and notify our Callback (which does not yet exist)
    }

    @Override
    public void onConnectionSuspended(int i) {
        mState = STATE_DISCONNECTED;
        Log.i(LOG_TAG, "onConnectionSuspended invoked state =[" + mState + "]");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mState = STATE_DISCONNECTED;
        Log.i(LOG_TAG, "onConnectionFailed invoked state =[" + mState + "]" + connectionResult.toString());

    }

    @Override
    public synchronized void flush() throws IOException {

        int chunkLength = 0;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new GZIPOutputStream(baos));
        Log.d(LOG_TAG, "Cache = " + cache);
        pw.println(cloudMap2Json(cache));
        Log.d(LOG_TAG, "Printwriter - " + pw);
        pw.close();

        byte[] bytes = baos.toByteArray();
        // mAppStateClient.connect();
        // mState = STATE_CONNECTED;

        Log.d(LOG_TAG, " BYTES  " + bytes);



        /* if cloudMap is larger then 256 split to the 4 available slots */
        if (bytes.length > 256 * 1024L) {
            for (int i = 0; i < 4; i++) {
                int off = i * 256 * 1024;
                chunkLength = (bytes.length - off) > (256 * 1024) ? (256 * 1024) : bytes.length - off;

                byte[] chunk = new byte[chunkLength];

                System.arraycopy(bytes, off, chunkLength, 0, chunkLength);
                Log.d(LOG_TAG, " Chunk = {" + chunkLength + "}" + " at part {" + i + "}");
                /* write chunk to cloud slot #i */

                Log.d(LOG_TAG, "Cloud connected and ready to connect");
                //new googleApiClient approach
                AppStateManager.update(mGoogleApiClient, mState, chunk);

                if (chunkLength < (256 * 1024L))
                    break;
            }

            // write chunks to cloud chunk #0


        } else {
            Log.d(LOG_TAG, "chunks below limit so persisting  ");
            AppStateManager.update(mGoogleApiClient, mState, bytes);
        }

    }

    @Override
    public synchronized Collection getAllKeys() {
        return cache.keySet();
    }

    @Override
    public synchronized Object get(String key) {
        return cache.get(key);
    }


    @Override
    public synchronized void put(String key, String value) {
        cache.put(key, value);

    }


    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return cache.entrySet();
    }

    private JSONObject cloudMap2Json(Map<String, Object> aCloudMap) {
        JSONObject jObject = new JSONObject();
        Iterator iter = aCloudMap.values().iterator();
        Log.d(LOG_TAG, "aCloudMap EntrySet = " + aCloudMap.entrySet());

        try {
            for (Map.Entry<String, Object> cloudEntry : aCloudMap.entrySet()) {
                jObject.put(cloudEntry.getKey(), cloudEntry.getValue());

            }
            Log.d(LOG_TAG, " JsonObject [ " + jObject.toString() + " ]");

        } catch (JSONException jsonE)

        {
            Log.e(LOG_TAG, "cannot create json intermediate object" + jsonE);
        }

        return jObject;
    }


    /**
     * refactored byte generator ! not in use so far !
     */
    private void generateBytefixedArray() {
    }


    private boolean cloudAction() {
        Log.d(LOG_TAG, "Method cloudAction invoked");


        return true;
    }
}
