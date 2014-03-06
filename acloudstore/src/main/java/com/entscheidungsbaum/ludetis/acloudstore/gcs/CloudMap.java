package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Created by marcus on 1/20/14.
 */
public interface CloudMap  {

    /**
     * flushes the map to cloud
     */
    public void flush();

    public Collection<String> getAllKeys();

    public Serializable get(String key);

    public void put(String key, Serializable value);


}
