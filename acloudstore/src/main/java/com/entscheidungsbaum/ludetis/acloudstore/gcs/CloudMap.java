package com.entscheidungsbaum.ludetis.acloudstore.gcs;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by marcus on 1/20/14.
 */
public interface CloudMap  {


    public boolean onConnect2Cloud() throws Exception;
    /**
     * flushes the map to cloud
     */
    public void flush() throws IOException;

    public Collection<String> getAllKeys();

    public Object get(String key);

    public void put(String key, String value);

    public  Set<Map.Entry<String, Object>> entrySet() ;
}
