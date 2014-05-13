package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore;

import java.io.IOException;

/**
 * sample implemtation for a store based on Android's local SharedPreferences.
 * Created by uwe on 12.05.14.
 */
public class SharedPreferencesStore extends BaseKeyValueStore {

    private static final String STORE_NAME = "SharedPreferencesStore";
    private static final String LOG_TAG = STORE_NAME;
    private Context context;
    private String keyPrefix;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Handler handler;

    /**
     * Creates a keyvalue store based on Android's SharedPreferences.
     * @param context Use the application context to make sure this survives activity switching
     * @param listener the statuslistener. OnConnected will be called implicitely.
     * @param keyPrefix a prefix used for all keys, may be null
     */
    public SharedPreferencesStore(Context context, StatusListener listener, String keyPrefix) {
        super(listener);
        this.context = context;
        this.keyPrefix = keyPrefix==null?"":keyPrefix; // use "" as prefix if null has been passed
        sharedPreferences = context.getSharedPreferences(STORE_NAME, 0);
        // queue the statusListener onConnected
        handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                statusListener.onConnected();
            }
        });
    }

    @Override
    public void flush() throws IOException {
        if(editor!=null) {
            editor.commit();
            editor=null;
        }
    }

    @Override
    public Object get(String key) {
        String res = sharedPreferences.getString(keyPrefix+"_"+key,null);
        if(res==null) {
            Log.d(LOG_TAG, "key not found: "+key);
            return null;
        }
        //Log.d(LOG_TAG, "key found: "+key + "=" + res);
        return deserialize(res);
    }

    @Override
    public void put(String key, Object value) {
        if(!isKeyValid(key)) throw new IllegalArgumentException("Keys may not contain underscores");
        try {
            if(editor==null) {
                editor = sharedPreferences.edit();
            }
            editor.putString(keyPrefix+"_"+key, serialize(value));
            //Log.d(LOG_TAG, "wrote key: "+key);
        } catch (IOException e) {
            Log.e(LOG_TAG, "ioexception: " + e);
        }
    }

    @Override
    public void delete(String key) {
        // currently not implemented
        Log.w(LOG_TAG, "deleting keys is not implemented");
    }

    @Override
    public String toString() {
        return STORE_NAME;
    }

    @Override
    public boolean isKeyValid(String key) {
        return !key.contains("_");
    }
}
