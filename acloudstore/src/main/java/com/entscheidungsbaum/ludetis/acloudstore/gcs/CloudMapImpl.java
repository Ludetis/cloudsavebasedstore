package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;

/**
 * Created by marcus on 1/20/14.
 */
public class CloudMapImpl implements CloudMap, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    final String LOG_TAG = CloudMapImpl.class.getName();

    public static final String[] mScopes = {Scopes.APP_STATE};

    public static final int STATE_UNCONFIGURED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    int mState = STATE_UNCONFIGURED;

    AppStateClient mAppStateClient ;

    public CloudMapImpl(Context context) {

         mAppStateClient = new AppStateClient.Builder(context, this, this)
                .setScopes(mScopes)
                .create();
        mState = STATE_DISCONNECTED;

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
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containValue(Object value) {
        return false;
    }

    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public Object getKey() {
        return null;
    }

    @Override
    public Object getAllKey() {
        return null;
    }

    @Override
    public Object getValue(Object key) {
        return null;
    }

    @Override
    public Object getAll(Object key) {
        return null;
    }

    @Override
    public void update(CloudMap cloudMap) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public int size() {
        return 0;
    }

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
}
