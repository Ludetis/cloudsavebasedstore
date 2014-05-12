package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
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
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(res, Base64.DEFAULT));
        ObjectInputStream oInputStream = null;
        try {
            oInputStream = new ObjectInputStream(bis);
            //Log.d(LOG_TAG, "reading key: "+key);
            return oInputStream.readObject();
        } catch (IOException e) {
            Log.e(LOG_TAG,"IOException: " + e);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG,"class not found: " + e);
        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(value); // will throw if object is not serializable
            os.close();
            if(editor==null) {
                editor = sharedPreferences.edit();
            }
            editor.putString(keyPrefix+"_"+key, Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT) );
            //Log.d(LOG_TAG, "wrote key: "+key);
        } catch (IOException e) {
            Log.e(LOG_TAG, "ioexception: " + e);
        }
    }

    @Override
    public String toString() {
        return STORE_NAME;
    }
}
