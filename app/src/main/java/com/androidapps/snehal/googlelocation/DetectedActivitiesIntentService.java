package com.androidapps.snehal.googlelocation;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by sneha on 11/16/2015.
 */
public class DetectedActivitiesIntentService  extends IntentService{
    protected static final  String TAG = "detection_is";
    public  DetectedActivitiesIntentService(){
        //Use the TAG to name the worker thread
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //This is a utility function which extracts the ActivityRecognitionResult from the extras of
        // an Intent that was sent from the activity detection service.
        ActivityRecognitionResult activityRecognitionResult= ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        //Returns the list of activities that where detected with the confidence value associated
        // with each activity. The activities are sorted by most probable activity first.
        ArrayList<DetectedActivity> detectedActivities= (ArrayList)activityRecognitionResult.getProbableActivities();

        //Log each Activity
        Log.i(TAG,"activities detected");

        //Broadcast the list of detected activities
        localIntent.putExtra(Constants.ACTIVITY_EXTRA,detectedActivities);
        // LocalBroadcastManager - Helper to register for and send broadcasts of Intents to local
        // objects within your process. This is has a number of advantages over sending global
        // broadcasts with sendBroadcast(Intent).
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
