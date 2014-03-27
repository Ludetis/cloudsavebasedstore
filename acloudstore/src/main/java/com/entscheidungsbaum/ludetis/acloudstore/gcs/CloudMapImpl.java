package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;

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

    AppStateClient mAppStateClient;
    GoogleApiClient.Builder mGoogleApiClient = null;

    public CloudMapImpl(Context context) {
        GoogleApiClient.Builder mGoogleApiClient = new GoogleApiClient(context.getApplicationContext(), this, this);
//        mAppStateClient = new AppStateClient.Builder(context, this, this)
//                .setScopes(mScopes)
//                .create();
        mState = STATE_DISCONNECTED;


        // TODO load cache from cloud
    }

    /**
     * 1. Step to run the google cloud save
     * first you need to ensure the installation of the google play service
     * located in extras within the Android SDK Manager
     * <p/>
     * using the android studio ide you need to upgrade the
     * build.gradle
     * dependencies {
     * compile 'com.google.android.gms:play-services:4.0.30'
     * }
     */


    @Override
    public void onConnected(Bundle bundle) {
        mState = STATE_CONNECTED;
        Log.i(LOG_TAG, "onConnected invoked state =[" + mState + "]");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //  @Override
    public void onDisconnected() {
        mState = STATE_DISCONNECTED;
        Log.i(LOG_TAG, "onDisconnected invoked state =[" + mState + "]");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mState = STATE_DISCONNECTED;
        Log.i(LOG_TAG, "onConnectionFailed invoked state =[" + mState + "]");

    }

    @Override
    public synchronized void flush() throws IOException {

        int chunkLength = 0;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new GZIPOutputStream(baos));
        Log.d(LOG_TAG, "Cache = " + cache);
        pw.println(cloudMap2Json(cache));

        pw.close();

        byte[] bytes = baos.toByteArray();
        mAppStateClient.connect();
        mState = STATE_CONNECTED;

        Log.d(LOG_TAG, " BYTES  " + bytes + " connected = {" + mAppStateClient.isConnected() + "}");



        /* if cloudMap is larger then 256 split to the 4 available slots */
        if (bytes.length > 256 * 1024L) {
            for (int i = 0; i < 4; i++) {
                int off = i * 256 * 1024;
                chunkLength = (bytes.length - off) > (256 * 1024) ? (256 * 1024) : bytes.length - off;

                byte[] chunk = new byte[chunkLength];

                System.arraycopy(bytes, off, chunkLength, 0, chunkLength);
                Log.d(LOG_TAG, " Chunk = {" + chunkLength + "}" + " at part {" + i + "}");
                /* write chunk to cloud slot #i */

                if (mAppStateClient.isConnected()) {
                    Log.d(LOG_TAG, "Cloud connected and ready to connect");
                    mAppStateClient.updateState(mState, chunk);
                } else {
                    break;
                }
                if (chunkLength < (256 * 1024L))
                    break;
            }

            // write chunks to cloud chunk #0


        } else {
            Log.d(LOG_TAG, "chunks below limit so persisting  " + mAppStateClient.isConnected());
            mAppStateClient.updateState(mState, bytes);

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
