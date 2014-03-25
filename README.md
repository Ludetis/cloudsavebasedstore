ACloudStore
===========
a showcase for a key value store using Cloud Save


# ACloudStore
===========
a showcase for a key value store using Cloud Save

Steps to fullfill to get the gameservice working for a mobile applikation

## extend the dependecies as follows
```JAVA
android {

 signingConfigs {

        releaseConfig {
            storeFile file("/path to your keystore")
            storePassword "passwd"
            keyAlias "alias_name"
            keyPassword "key passwd"

        }

    }

  dependencies

  {

    compile com.android.support:support-v4:19.0.+
    compile com.android.support:appcompat-v7:19.0.+
    compile com.google.android.gms:play-services:4.0.30

  }

}
```


## extend the android manifest
```JAVA
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
```
## upload your apk to the playstore with a valid certificate.

1. init(Context)
-  mCloudMap retrieved from the G Cloud.
2. get
- simple get from the mCloudMap
3. put
- simples put into the mCloudMap
4. flush
- upload of the mCloudMap into the Cloud
- 


## the bucket approach and the appropriate slicing 

we needed to split the data to be persisted into the well known 4 availabel 1024Kbytes slices.
To do so we found out to head for flush approach. 
To learn more about the bit buckets refer to the google API description Cloud Save and Google Drive
https://developers.google.com/games/services/common/concepts/cloudsave#cloud_save_title_and_google_drive


