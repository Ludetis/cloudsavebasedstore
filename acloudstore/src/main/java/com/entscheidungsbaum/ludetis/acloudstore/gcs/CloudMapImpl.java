package com.entscheidungsbaum.ludetis.acloudstore.gcs;

/**
 * Created by marcus on 1/20/14.
 */
public class CloudMapImpl implements CloudMap {

    final String LOG_TAG = CloadMapImpl.class.getName();


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
    public Object getValue() {
        return null;
    }

    @Override
    public Object getAll(Object key) {
        return null;
    }

    @Override
    public void update(CloudMap cloudMap) {

    }
}
