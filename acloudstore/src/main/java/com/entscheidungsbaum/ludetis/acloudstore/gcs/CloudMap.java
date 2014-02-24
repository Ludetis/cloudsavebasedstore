package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import java.util.Map;

/**
 * Created by marcus on 1/20/14.
 */
public interface CloudMap<K, V> extends Map<K,V>{

    public int size();

    public boolean isEmpty();

    //public boolean containsKey(K key);

   // public boolean containsValue(V value);

//    public void put(K key, V value);

    /**
     * flushes the map if task is done to upstream
     */
    public void flush(CloudMap aCloudMap);

    public K getKey();

    public K getAllKey();

    public V getValue(K key);

    public V getAll(K key);

    public void update(CloudMap<K, V> cloudMap);

    public byte[] toBytes();

    public String toString();

}
