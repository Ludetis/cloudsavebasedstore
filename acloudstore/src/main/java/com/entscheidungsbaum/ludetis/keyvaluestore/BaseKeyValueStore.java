package com.entscheidungsbaum.ludetis.keyvaluestore;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by marcus on 1/20/14.
 */
public abstract class BaseKeyValueStore {

    protected StatusListener statusListener;

    public interface StatusListener {
        public void onConnected();
        public void onError(String cause);
    }

    /**
     * Internal constructor. Implementations will propably need more parameters.
     * @param statusListener
     */
    protected BaseKeyValueStore(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    /**
     * flushes values to store (in some implementations, this might do nothing)
     * Note that if the store needs to access the network to do this, it MUST NOT be called by the foreground thread.
     */
    public abstract void flush() throws IOException;

    /**
     * gets a value from the store
     * Note that if the store needs to access the network to do this, it MUST NOT be called by the foreground thread.
     * @param key
     * @return null if the key does not exist
     */
    public abstract Object get(String key);

    /**
     * deserialize a serialized string to an object. Will only work if String has been serialized using serialize().
     * @param serialized
     * @return
     */
    protected Object deserialize(String serialized) {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(serialized, Base64.DEFAULT));
        ObjectInputStream oInputStream = null;
        try {
            oInputStream = new ObjectInputStream(bis);
            //Log.d(LOG_TAG, "reading key: "+key);
            return oInputStream.readObject();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "IOException: " + e);
        } catch (ClassNotFoundException e) {
            Log.e(getClass().getSimpleName(),"class not found: " + e);
        }
        return null;
    }

    /**
     * puts a value into the store, overriding the privious value. IMPORTANT: You MUST call flush() to actually store your changes.
     * @param key
     * @param value
     */
    public abstract void put(String key, Object value);

    /**
     * serialize an object to a serialized form which can be written to string bases stores.
     * This will fail if the Object cannot be serialized (does not implement Serializable).
     * @param value
     * @return serialized value
     * @throws IOException
     */
    protected String serialize(Object value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(value); // will throw if object is not serializable
        os.close();
        return Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
    }

    /**
     * remove a key and its value from the store. Will do nothing if the key does not exist.
     * You MUST call flush() to ensure this is really done.
     * @param key
     */
    public abstract void delete(String key);

    /**
     * check if this key may be used in this store. Some stores might reject keys if they contain certain characters.
     * @param key
     * @return true if key is valid.
     */
    public boolean isKeyValid(String key) {
        return true;
    }


}
