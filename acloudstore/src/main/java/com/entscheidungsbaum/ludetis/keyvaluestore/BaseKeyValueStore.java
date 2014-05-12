package com.entscheidungsbaum.ludetis.keyvaluestore;

import java.io.IOException;

/**
 * Created by marcus on 1/20/14.
 */
public abstract class BaseKeyValueStore {

    protected StatusListener statusListener;

    public interface StatusListener {
        public void onConnected();
        public void onError(String cause);
    }

    protected BaseKeyValueStore(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    /**
     * flushes the map to cloud
     */
    public abstract void flush() throws IOException;

    public abstract Object get(String key);

    public abstract void put(String key, Object value);


}
