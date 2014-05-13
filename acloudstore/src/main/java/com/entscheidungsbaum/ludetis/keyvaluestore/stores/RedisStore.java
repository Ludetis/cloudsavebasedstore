package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

/**
 * A simple Redis/Jedis based store.
 * It uses to a local write/read cache to minimize internet connections.
 * The cache is currently cleared after a flush() which is not optimal.
 * Created by uwe on 12.05.14.
 */
public class RedisStore extends BaseKeyValueStore {

    private  Jedis jedis;
    private final Map<String,Object> cache = new HashMap<String, Object>();
    private Handler handler = new Handler();
    private String keyPrefix="";
    private String host;
    private String password;

    /**
     * create a store instance for a remote Redis server. May be called on main thread.
     * @param listener the status listener. Will be called immediately because we connect later "on demand".
     * @param host hostname or IP. Default port will be used.
     * @param keyPrefix String to prepend to each key. To create a user specific storage region,
     *                  use something like a UUID+"_" here which cannot be guessed and save that UUID locally.
     *                  It becomes the user's key to his storage region
     * @param password The password to auth against the server if it is protected. If null, no auth will be done.
     */
    public RedisStore(StatusListener listener, String host, String keyPrefix, String password) {
        super(listener);
        if(keyPrefix!=null) this.keyPrefix=keyPrefix;
        this.host=host;
        this.password=password;

        handler.post(new Runnable() {
            @Override
            public void run() {
                statusListener.onConnected();
            }
        });
    }

    private synchronized void connect() {
        if(jedis==null) {
            jedis = new Jedis(host);
            Log.i(getClass().getSimpleName(),"created new Jedis client");
        }
        if(!jedis.isConnected()) {
            jedis.connect();
            Log.i(getClass().getSimpleName(),"connected to Redis server " + host);
            if (!TextUtils.isEmpty(password)) jedis.auth(password);
        }
    }

    @Override
    public void flush() throws IOException {
        connect();
        synchronized (cache) {
            for(Map.Entry<String,Object> e : cache.entrySet()) {
                jedis.set(keyPrefix+e.getKey(), serialize(e.getValue()));
            }
            cache.clear();
        }
    }

    @Override
    public Object get(String key) {
        // we might first want to look into the write cache
//        synchronized (cache) {
//            if(cache.containsKey(key)) {
//                return cache.get(key);
//            }
//        }
        connect();
        String value = jedis.get(keyPrefix+key);
        if(value==null) return null;
        return deserialize(value);
    }

    @Override
    public void put(String key, Object value) {
        synchronized (cache) {
            cache.put(key, value);
        }
    }

    @Override
    public void delete(String key) {
        // currently not supported
        Log.w(getClass().getSimpleName(), "deleting keys is currently not supported");
    }
}
