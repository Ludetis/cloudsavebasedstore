package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by marcus on 1/20/14.
 */
public class CloudMapImpl<K, V> implements CloudMap<K, V>, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    final String LOG_TAG = CloudMapImpl.class.getName();

    public static final String[] mScopes = {Scopes.APP_STATE};

    public static final int STATE_UNCONFIGURED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    //@TODO change to something smarter and use AppStateClient().getMaxNumKeys();
    static final int SAVEPOINT_KEY = 0;

    int mState = STATE_UNCONFIGURED;

    // private CloudMap<K, V>[]values ;

    final int FLUSHBUFFERCAPACITY = 1024;
    int size;

    AppStateClient mAppStateClient;
    Activity mActivity;

//    Map<String, String> mCloudMap = new HashMap<String, String>();

    /**
     *
     */
    private Map<K, V> mCloudMap;

    public CloudMapImpl(Activity activity) {

        mAppStateClient = new AppStateClient.Builder(activity, this, this)
                .setScopes(mScopes)
                .create();
        mActivity = activity;
        Log.d(LOG_TAG, "connecting to cloud for cloudMap " + mActivity.getApplication());

        mAppStateClient.connect();
        mState = STATE_DISCONNECTED;
        this.mCloudMap = new HashMap<K, V>();

        ///Log.d(this.mCloudMap.toString();
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
    public boolean containsValue(Object value) {
        return false;
    }


    // map

    @Override
    public V put(K key, V value) {
        Log.d(LOG_TAG, "Object => " + key + " value " + value);
        //mCloudMap.put(key, value);
//        CloudMap<K, V> cMap = (CloudMap) new HashMap<K, V>();
//
//
//        boolean insert = true;
//        int i = cMap.size();
//        for (i = 0; i < size; i++) {
//
//            Log.d(LOG_TAG, " key=" + key + " value=" + value);
//            if (cMap.getKey().equals(key)) {
//                cMap.put(key, value);
//                insert = false;
//            }
//        }
//        if (insert) {
//            ensureCapacity();
//
//            //cMap[size++] = (CloudMap)new  HashMap<K, V>(key, value);
//        }
        return mCloudMap.put(key, value);
    }


    /**
     * Removes all elements from this {@code Map}, leaving it empty.
     *
     * @throws UnsupportedOperationException if removing elements from this {@code Map} is not supported.
     * @see #isEmpty()
     * @see #size()
     */
    @Override
    public void clear() {

    }

    /**
     * Returns a {@code Set} containing all of the mappings in this {@code Map}. Each mapping is
     * an instance of {@link java.util.Map.Entry}. As the {@code Set} is backed by this {@code Map},
     * changes in one will be reflected in the other.
     *
     * @return a set of the mappings
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return mCloudMap.entrySet();
    }

    /**
     * Returns the value of the mapping with the specified key.
     *
     * @param key the key.
     * @return the value of the mapping with the specified key, or {@code null}
     * if no mapping for the specified key is found.
     */
    @Override
    public V get(Object key) {
        return mCloudMap.get(key);
    }

    /**
     * Returns a set of the keys contained in this {@code Map}. The {@code Set} is backed by
     * this {@code Map} so changes to one are reflected by the other. The {@code Set} does not
     * support adding.
     *
     * @return a set of the keys.
     */
    @Override
    public Set<K> keySet() {
        return mCloudMap.keySet();
    }

    /**
     * Copies every mapping in the specified {@code Map} to this {@code Map}.
     *
     * @param map the {@code Map} to copy mappings from.
     * @throws UnsupportedOperationException if adding to this {@code Map} is not supported.
     * @throws ClassCastException            if the class of a key or a value of the specified {@code Map} is
     *                                       inappropriate for this {@code Map}.
     * @throws IllegalArgumentException      if a key or value cannot be added to this {@code Map}.
     * @throws NullPointerException          if a key or value is {@code null} and this {@code Map} does not
     *                                       support {@code null} keys or values.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    /**
     * Removes a mapping with the specified key from this {@code Map}.
     *
     * @param key the key of the mapping to remove.
     * @return the value of the removed mapping or {@code null} if no mapping
     * for the specified key was found.
     * @throws UnsupportedOperationException if removing from this {@code Map} is not supported.
     */
    @Override
    public V remove(Object key) {
        return mCloudMap.remove(key);
    }

    /**
     * Returns a {@code Collection} of the values contained in this {@code Map}. The {@code Collection}
     * is backed by this {@code Map} so changes to one are reflected by the other. The
     * {@code Collection} supports {@link java.util.Collection#remove}, {@link java.util.Collection#removeAll},
     * {@link java.util.Collection#retainAll}, and {@link java.util.Collection#clear} operations,
     * and it does not support {@link java.util.Collection#add} or {@link java.util.Collection#addAll} operations.
     * <p/>
     * This method returns a {@code Collection} which is the subclass of
     * {@ link AbstractCollection }. The {@ link AbstractCollection#iterator} method of this subclass returns a
     * "wrapper object" over the iterator of this {@code Map}'s {@ link #entrySet()}. The {@ link AbstractCollection#size} method
     * wraps this {@code Map}'s {@link #size} method and the {@ link AbstractCollection#contains} method wraps this {@code Map}'s
     * {@link #containsValue} method.
     * <p/>
     * The collection is created when this method is called at first time and
     * returned in response to all subsequent calls. This method may return
     * different Collection when multiple calls to this method, since it has no
     * synchronization performed.
     *
     * @return a collection of the values contained in this map.
     */
    @Override
    public Collection<V> values() {
        return mCloudMap.values();
    }

    private void ensureCapacity() {
        if (size == mCloudMap.size()) {
            int newSize = mCloudMap.size() * 2;
            //  mCloudMap = Arrays.copyOf(null,0);
        }
    }

    @Override
    public K getKey() {
        return null;
    }

    @Override
    public K getAllKey() {
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
        return mCloudMap.isEmpty();
    }

    @Override
    public byte[] toBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        //        try {
        //
        //        }
        Iterator iter = mCloudMap.keySet().iterator();
        while (iter.hasNext()) {
            sb.append("{ ");
            sb.append(iter.next());
            sb.append(" - ");
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public void flush(CloudMap aCloudMap) {


        //  aCloudMap = mCloudMap;

        Log.d(LOG_TAG, "flush CloudMap { " + aCloudMap + " }");
        if (mState == STATE_CONNECTED) {
            Log.d(LOG_TAG, "ready to flush !!");
            byte[] appState = new byte[FLUSHBUFFERCAPACITY];

//        appState[1024] = (byte) bestLevel;
//        key value paare hier holen

            mAppStateClient.updateState(SAVEPOINT_KEY, appState);
        }
    }

    @Override
    public int size() {
        return mCloudMap.size();
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
        Log.i(LOG_TAG, "onConnectionFailed invoked state =[" + mState + "]" +
                " result " + errorCodeToString(connectionResult.getErrorCode()) + " Details {" + connectionResult.toString() + "}");

        if (connectionResult.hasResolution()) {
            // This problem can be fixed. So let's try to fix it.
            Log.d(LOG_TAG, "Result has resolution. Starting it.");
            try {
                // launch appropriate UI flow (which might, for example, be the
                // sign-in flow)
                // mExpectingResolution = true;
                connectionResult.startResolutionForResult(mActivity, 9001);
            } catch (IntentSender.SendIntentException e) {
                // Try connecting again
                Log.d(LOG_TAG, "SendIntentException, so connecting again.");
                // connectCurrentClient();
            }
        } else {
            // It's not a problem what we can solve, so give up and show an
            // error.
            Log.d(LOG_TAG, "resolveConnectionResult: result has no resolution. Giving up.");
            //giveUp(new SignInFailureReason(mConnectionResult.getErrorCode()));
        }
    }


    static String errorCodeToString(int errorCode) {
        switch (errorCode) {
            case ConnectionResult.DEVELOPER_ERROR:
                return "DEVELOPER_ERROR(" + errorCode + ")";
            case ConnectionResult.INTERNAL_ERROR:
                return "INTERNAL_ERROR(" + errorCode + ")";
            case ConnectionResult.INVALID_ACCOUNT:
                return "INVALID_ACCOUNT(" + errorCode + ")";
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return "LICENSE_CHECK_FAILED(" + errorCode + ")";
            case ConnectionResult.NETWORK_ERROR:
                return "NETWORK_ERROR(" + errorCode + ")";
            case ConnectionResult.RESOLUTION_REQUIRED:
                return "RESOLUTION_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SERVICE_DISABLED:
                return "SERVICE_DISABLED(" + errorCode + ")";
            case ConnectionResult.SERVICE_INVALID:
                return "SERVICE_INVALID(" + errorCode + ")";
            case ConnectionResult.SERVICE_MISSING:
                return "SERVICE_MISSING(" + errorCode + ")";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "SERVICE_VERSION_UPDATE_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SIGN_IN_REQUIRED:
                return "SIGN_IN_REQUIRED(" + errorCode + ")";
            case ConnectionResult.SUCCESS:
                return "SUCCESS(" + errorCode + ")";
            default:
                return "Unknown error code " + errorCode;
        }
    }


}
