ACloudStore
===========
a showcase for a key value store using Cloud Save


# ACloudStore
===========
a showcase for a key value store using Cloud Save

Steps to fullfill to get the gameservice working for a mobile applikation

## extend the dependecies as follows

android \{

 signingConfigs \{

        releaseConfig \{
            storeFile file("/path to your keystore")
            storePassword "passwd"
            keyAlias "alias_name"
            keyPassword "key passwd"

        \}

    \}

  dependencies

  \{

    compile com.android.support:support-v4:19.0.+
    compile com.android.support:appcompat-v7:19.0.+
    compile com.google.android.gms:play-services:4.0.30

  \}

\}


## extend the android manifest

<application>
<meta-data 
             android:name="com.google.android.gms.version" 
             android:value="@integer/google_play_services_version" /> 
         <meta-data 
             android:name="com.google.android.gms.games.APP_ID" 
             android:value="@string/app_id" /> 
         <meta-data 
             android:name="com.google.android.gms.appstate.APP_ID" 
             android:value="@string/app_id" /> 
         <meta-data 
             android:name="com.google.android.gms.version" 
             android:value="@integer/google_play_services_version" /> 
</application> 

## upload your apk to the playstore with a valid certificate.

1. init(Context)
-  mCloudMap retrieved from the G Cloud.
2. get
- simple get from the mCloudMap
3. put
- simples put into the mCloudMap
4. flush
- upload of the mCloudMap into the Cloud