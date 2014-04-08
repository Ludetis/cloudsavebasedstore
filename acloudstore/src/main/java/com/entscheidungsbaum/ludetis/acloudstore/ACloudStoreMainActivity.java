package com.entscheidungsbaum.ludetis.acloudstore;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.entscheidungsbaum.ludetis.acloudstore.gcs.CloudMap;
import com.entscheidungsbaum.ludetis.acloudstore.gcs.CloudMapImpl;
import com.google.example.games.basegameutils.GameHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Start
 *
 * @author marcus
 */
public class ACloudStoreMainActivity extends Activity implements View.OnClickListener {


    private static final String LOG_TAG = ACloudStoreMainActivity.class.getName();

    private CloudMap mCloudMap;
    private EditText gamelevel;
    private EditText points;
    private EditText nickname;
    private EditText email;

    private boolean onConnected;


    public ACloudStoreMainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_acloud_store_main);

        mCloudMap = new CloudMapImpl(this); // will also start to connect
//        try {
//            mCloudMap.onConnect2Cloud();
//            //Log.d(LOG_TAG, " Connected ? = " + onConnected);
//        } catch (Exception e) {
//            Log.e(LOG_TAG, " cannot connect from activity to google cloud +" + e);
//        }


        /*
        get the data from all the fields
         */
        gamelevel = (EditText) findViewById(R.id.gamelevel);
        points = (EditText) findViewById(R.id.points);
        nickname = (EditText) findViewById(R.id.nickname);
        email = (EditText) findViewById(R.id.email);

        //loadFromCloud(); // TODO move this into some onConnected listener or just postpone

        findViewById(R.id.submitToCloud).setOnClickListener(this);
        findViewById(R.id.loadFromCloud).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitToCloud) {
            Log.d(LOG_TAG, "submitting fields to google cloud GAMELEVEL GET TEXT[" + gamelevel.getText().toString() + "] + gamelevel [" + gamelevel.getText().toString() + "]");

            submitToCloud();
        } else if (v.getId() == R.id.loadFromCloud) {
            loadFromCloud();
        }
    }

    private void loadFromCloud() {
        Log.d(LOG_TAG, "loading from google cloud service [" + mCloudMap.toString() + "] + gamelevel [" + gamelevel.getText().toString() + "]");

        gamelevel.setText((String) mCloudMap.get("gameLevel"));
        points.setText((String) mCloudMap.get("points"));
        nickname.setText((String) mCloudMap.get("nickname"));
        email.setText((String) mCloudMap.get("email"));
    }

    private void submitToCloud() {
        Log.d(LOG_TAG, "about to submit google cloud service for nickname [" + nickname.getText().toString() + "] + gamelevel [" + gamelevel.getText().toString() + "]");


        mCloudMap.put("gameLevel", gamelevel.getText().toString());
        mCloudMap.put("points", points.getText().toString());
        mCloudMap.put("nickname", nickname.getText().toString());
        mCloudMap.put("email", email.getText().toString());
        Log.d(LOG_TAG, "submitting to google cloud service gameLevel [" + mCloudMap.toString() + "]");

        try {
            if (onConnected) {
                Log.d(LOG_TAG, " about to flush onConnected = " + onConnected);
                mCloudMap.flush();

            } else {
                Log.d(LOG_TAG, "do it locally here first !!");

            }
        } catch (IOException e) {

            Log.d(LOG_TAG, "cannot flush !!");
        }

    }

}
