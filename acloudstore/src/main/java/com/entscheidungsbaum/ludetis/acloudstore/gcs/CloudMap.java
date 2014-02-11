package com.entscheidungsbaum.ludetis.acloudstore.gcs;

/**
 * Created by marcus on 1/20/14.
 */
public interface CloudMap<K, V> {

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object key);

    public boolean containValue(Object value);

    public void put(K key, V value);

    /**
     * flushes the map if task is done to upstream
     */
    public void flush();

    public K getKey();

    public K getAllKey();

    public V getValue(K key);

    public V getAll(Object key);

    public void update(CloudMap<K, V> cloudMap);

}
