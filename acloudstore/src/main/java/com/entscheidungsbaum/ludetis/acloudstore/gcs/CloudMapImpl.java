package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by marcus on 1/20/14.
 */
public class CloudMapImpl implements CloudMap, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    final String LOG_TAG = CloudMapImpl.class.getName();

    public static final String[] mScopes = {Scopes.APP_STATE};

    private Map<String, Serializable> cache = new HashMap<String, Serializable>();

    public static final int STATE_UNCONFIGURED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    int mState = STATE_UNCONFIGURED;

    AppStateClient mAppStateClient;

    public CloudMapImpl(Context context) {

        mAppStateClient = new AppStateClient.Builder(context, this, this)
                .setScopes(mScopes)
                .create();
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new GZIPOutputStream(baos));

        pw.println("enter JSON map here");

        pw.close();

        byte[] bytes = baos.toByteArray();

        if (bytes.length > 256 * 1024L) {
            for (int i = 0; i < 4; i++) {
                int off = i * 256 * 1024;
                int length = (bytes.length - off) > 256 * 1024 ? 256 * 1024 : bytes.length - off;

                byte[] chunk = new byte[length];

                System.arraycopy(bytes, off, chunk, 0, length);

                // write chunk to cloud slot #i
                mAppStateClient.connect();
                mState=STATE_CONNECTED;

                if (length < 256 * 1024)
                    break;
            }
            // chunk

            // write chunks to cloud chunk #0
        } else {
            // write data to cloud
        }

        // TODO send cache to cloud
    }

    @Override
    public synchronized Collection getAllKeys() {
        return cache.keySet();
    }

    @Override
    public synchronized Serializable get(String key) {
        return cache.get(key);
    }


    @Override
    public synchronized void put(String key, Serializable value) {
        cache.put(key, value);
    }

    private JSONObject cloudMap2Json(CloudMap aCloudMap) {
        JSONObject jObject = new JSONObject();

        return jObject;
    }


}
