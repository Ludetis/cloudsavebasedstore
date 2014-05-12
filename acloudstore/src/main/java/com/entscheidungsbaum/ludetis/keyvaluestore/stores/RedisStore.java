package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.os.Handler;
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

    private final Jedis jedis;
    private final Map<String,Object> cache = new HashMap<String, Object>();
    private Handler handler = new Handler();
    private String keyPrefix="";

    public RedisStore(StatusListener listener, String host, String keyPrefix) {
        super(listener);
        if(keyPrefix!=null) this.keyPrefix=keyPrefix;
        jedis = new Jedis(host);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(jedis!=null) {
                    statusListener.onConnected();
                } else {
                    statusListener.onError("could not connect");
                }
            }
        });
        Log.i(getClass().getSimpleName(),"connected to Jedis");
    }

    @Override
    public void flush() throws IOException {
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
