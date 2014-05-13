package com.entscheidungsbaum.ludetis.keyvaluestore.stores;

import android.content.Context;

import com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore;

import java.io.IOException;

/**
 * Stub for a store based on a folder with one file per key.
 * Created by uwe on 12.05.14.
 */
public class FileBasedStore extends BaseKeyValueStore {

    protected FileBasedStore(Context context,StatusListener statusListener,String folderPath) {
        super(statusListener);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void delete(String key) {

    }
}
