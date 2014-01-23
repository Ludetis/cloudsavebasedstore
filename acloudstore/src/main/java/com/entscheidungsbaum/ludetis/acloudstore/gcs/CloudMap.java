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

    public K getKey();

    public K getAllKey();

    public V getValue();

    public V getAll(Object key);

    public void update(CloudMap<K, V> cloudMap);

}
