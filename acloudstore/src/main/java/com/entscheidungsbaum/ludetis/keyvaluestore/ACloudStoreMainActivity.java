package com.entscheidungsbaum.ludetis.keyvaluestore;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.entscheidungsbaum.ludetis.acloudstore.R;
import com.entscheidungsbaum.ludetis.keyvaluestore.stores.SharedPreferencesStore;

import java.io.IOException;

import static com.entscheidungsbaum.ludetis.keyvaluestore.BaseKeyValueStore.*;


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

        store = new SharedPreferencesStore(getApplicationContext(), this, null);
        //store = new GoogleGameApiStore(this,this);

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
        Log.d(LOG_TAG, "loading from store...");

        gamelevel.setText((String) store.get("gameLevel"));
        points.setText((String) store.get("points"));
        nickname.setText((String) store.get("nickname"));
        email.setText((String) store.get("email"));

        Toast.makeText(this,"loaded!",Toast.LENGTH_SHORT).show();
    }

    private void submitToStore() {
        Log.d(LOG_TAG, "writing values to store...");

        store.put("gameLevel", gamelevel.getText().toString());
        store.put("points", points.getText().toString());
        store.put("nickname", nickname.getText().toString());
        store.put("email", email.getText().toString());

        Log.d(LOG_TAG, "flushing store...");

        try {
            store.flush();

            Toast.makeText(this,"saved!",Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.d(LOG_TAG, "cannot flush: " + e);
        }

    }

    @Override
    public void onConnected() {

        Toast.makeText(this,"connected",Toast.LENGTH_SHORT).show();

        findViewById(R.id.save).setEnabled(true);
        findViewById(R.id.load).setEnabled(true);

        loadFromStore();

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.load).setOnClickListener(this);

    }

    @Override
    public void onError(String cause) {
        Toast.makeText(this,"error: " + cause,Toast.LENGTH_SHORT).show();
    }
}
