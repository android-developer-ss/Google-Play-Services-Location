package com.androidapps.snehal.googlelocation;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class GoogleContext extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    //----------------------------------------------------------------------------------------------
    // Member variable Declarations
    private static TextView mStatus;
    private static Button mRequestUpdatesButton;
    private static Button mRemoveUpdatesButton;
    protected GoogleApiClient mGoogleApiClient;
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    protected static final String TAG = "activity";

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing member variables
        mStatus = (TextView) findViewById(R.id.detectedActivities);
        mRemoveUpdatesButton = (Button) findViewById(R.id.remove_activity_updates_button);
        mRequestUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        buildGoogleApiClient();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        //Unregister the broadcast receiver that was registeres during onResume
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Successfully added activity detection.");

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /***********************************************************************************************
     * Build Google API Client
     **********************************************************************************************/
    protected synchronized void buildGoogleApiClient() {
        // GoogleApiClient.Builder addApi - Specify which Apis are requested by your app.
        // GoogleApiClient.Builder addConnectionCallbacks- Registers a listener to receive connection
        // events from this GoogleApiClient.
        // GoogleApiClient.Builder addOnConnectionFailedListener - Adds a listener to register to
        // receive connection failed events from this GoogleApiClient
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    /***********************************************************************************************
     * Methods to be implemented for GoogleApiClient.ConnectionCallbacks Interface.
     * Provides callbacks that are called when the client is connected or disconnected from the
     * service. Most applications implement onConnected(Bundle) to start making requests.
     **********************************************************************************************/
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /***********************************************************************************************
     * Methods to be implemented for GoogleApiClient.OnConnectionFailedListener Interface.
     * Provides callbacks for scenarios that result in a failed attempt to connect the client to the
     * service.
     * A ConnectionResult that can be used for resolving the error, and deciding what sort of error
     * occurred. To resolve the error, the resolution must be started from an activity with a
     * non-negative requestCode passed to startResolutionForResult(Activity, int). Applications
     * should implement onActivityResult in their Activity to call connect() again if the user has
     * resolved the issue (resultCode is RESULT_OK).
     **********************************************************************************************/
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /***********************************************************************************************
     * @param detectedActivityType
     * @return
     **********************************************************************************************/
    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    /***********************************************************************************************
     * PRIVATE INNER CLASS
     **********************************************************************************************/
    private class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        public static final String TAG = "receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String strStatus = "";
            for (DetectedActivity thisActivity : updatedActivities) {
                strStatus += getActivityString(thisActivity.getType()) + thisActivity.getConfidence() + "%\n";
            }
            mStatus.setText(strStatus);
        }
    }

    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
//        mRequestUpdatesButton.setEnabled(false);
//        mRemoveUpdatesButton.setEnabled(true);

    }

    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
