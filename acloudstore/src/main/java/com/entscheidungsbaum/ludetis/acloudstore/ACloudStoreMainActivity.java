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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Start
 */
public class ACloudStoreMainActivity extends Activity implements View.OnClickListener {


    private static final String LOG_TAG = ACloudStoreMainActivity.class.getName();

    private CloudMap<String,String> mCloudMap;
    private EditText gamelevel;
    private EditText points;
    private EditText nickname;
    private EditText email;


    public ACloudStoreMainActivity() {
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_acloud_store_main);

        mCloudMap = new CloudMapImpl(this);

        /*
        get the data from all the fields
         */
        gamelevel = (EditText) findViewById(R.id.gamelevel);
        points = (EditText) findViewById(R.id.points);
        nickname = (EditText) findViewById(R.id.nickname);
        email = (EditText) findViewById(R.id.email);


        findViewById(R.id.submitToCloud).setOnClickListener(this);
        findViewById(R.id.loadFromCloud).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submitToCloud) {
            submitToCloud();
        } else if(v.getId()==R.id.loadFromCloud) {
            loadFromCloud();
        }
    }

    private void loadFromCloud() {
        Log.d(LOG_TAG, "loading from google cloud service [" + mCloudMap.toString() + "]");

        gamelevel.setText( mCloudMap.getValue("gameLevel") );
        points.setText( mCloudMap.getValue("points") );
        nickname.setText( mCloudMap.getValue("nickname") );
        email.setText( mCloudMap.getValue("email") );
    }

    private void submitToCloud() {
        Log.d(LOG_TAG, "submitting to google cloud service [" + mCloudMap.toString() + "]");

        mCloudMap.put("gameLevel", gamelevel.getText().toString());
        mCloudMap.put("points", points.getText().toString());
        mCloudMap.put("nickname", nickname.getText().toString());
        mCloudMap.put("email", email.getText().toString());

        mCloudMap.flush();
    }
}
