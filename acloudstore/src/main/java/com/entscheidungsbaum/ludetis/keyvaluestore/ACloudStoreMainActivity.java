package com.entscheidungsbaum.ludetis.keyvaluestore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.entscheidungsbaum.ludetis.acloudstore.R;
import com.entscheidungsbaum.ludetis.keyvaluestore.stores.GoogleGameApiStore;

import java.io.IOException;

import static com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore.StatusListener;


/**
 * Start
 *
 * @author marcus
 */
public class ACloudStoreMainActivity extends Activity implements View.OnClickListener, StatusListener {

    private static final String LOG_TAG = ACloudStoreMainActivity.class.getName();

    private BaseKeyValueStore store;
    private EditText gamelevel;
    private EditText points;
    private EditText nickname;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_acloud_store_main);
        Log.d(LOG_TAG, "Oncreated started");
        // uncomment one of these lines to use a certain store

        // shared preferences based local store (works)
        //store = new SharedPreferencesStore(getApplicationContext(), this, null);

        // Google Games based user specific cloud store (TODO)
        store = new GoogleGameApiStore(this, this);
        Log.d(LOG_TAG, "Status Listener =>" + store.statusListener);

        // Redis nosql based non user specific cloud store (works)
        //store = new RedisStore(this, "ludetis.de", "com.entscheidungsbaum.acloudstoretest.", null);

        // wire editTexts
        gamelevel = (EditText) findViewById(R.id.gamelevel);
        points = (EditText) findViewById(R.id.points);
        nickname = (EditText) findViewById(R.id.nickname);
        email = (EditText) findViewById(R.id.email);

        findViewById(R.id.save).setEnabled(false);
        findViewById(R.id.load).setEnabled(false);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            submitToStore();
        } else if (v.getId() == R.id.load) {
            loadFromStore();
        }
    }

    private void loadFromStore() {
        Log.d(LOG_TAG, "loading from store in background...");
        (new Thread() {
            @Override
            public void run() {
                final String gameLevel = (String) store.get("gameLevel");
                final String points1 = (String) store.get("points");
                final String nickname1 = (String) store.get("nickname");
                final String email1 = (String) store.get("email");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        gamelevel.setText(gameLevel);
                        points.setText(points1);
                        nickname.setText(nickname1);
                        email.setText(email1);

                        Toast.makeText(getApplicationContext(), "loaded!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }

    private void submitToStore() {
        Log.d(LOG_TAG, "writing values to store...");

        store.put("gameLevel", gamelevel.getText().toString());
        store.put("points", points.getText().toString());
        store.put("nickname", nickname.getText().toString());
        store.put("email", email.getText().toString());

        Log.d(LOG_TAG, "flushing store in background...");
        (new Thread() {
            @Override
            public void run() {
                try {
                    store.flush();

                    // show toast on UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "saved!", Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (IOException e) {
                    Log.d(LOG_TAG, "cannot flush: " + e);
                }
            }
        }).start();


    }

    @Override
    public void onConnected() {

        Log.d(LOG_TAG, "On Connected ");
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();

        findViewById(R.id.save).setEnabled(true);
        findViewById(R.id.load).setEnabled(true);

        loadFromStore();

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.load).setOnClickListener(this);

    }

    @Override
    public void onError(String cause) {
        Toast.makeText(this, "error: " + cause, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        store.notifyActivityResult(requestCode, resultCode);
    }
}
