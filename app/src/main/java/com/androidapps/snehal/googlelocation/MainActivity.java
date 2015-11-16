package com.androidapps.snehal.googlelocation;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final String LOG_TAG = "SnehalGoogleLocation";
    private TextView txtOutput;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // GoogleApiClient.Builder addApi - Specify which Apis are requested by your app.
        // GoogleApiClient.Builder addConnectionCallbacks- Registers a listener to receive connection
        // events from this GoogleApiClient.
        // GoogleApiClient.Builder addOnConnectionFailedListener - Adds a listener to register to
        // receive connection failed events from this GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Handle for txtOutput from UI.
        txtOutput = (TextView) findViewById(R.id.txtOutput);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Methods to be implemented for GoogleApiClient.ConnectionCallbacks Interface.
     * Provides callbacks that are called when the client is connected or disconnected from the
     * service. Most applications implement onConnected(Bundle) to start making requests.
     **********************************************************************************************/
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //PRIORITY_HIGH_ACCURACY This will return the finest location available.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Update location every second.
        mLocationRequest.setInterval(1000);

        //Entry point to the fused location APIs.
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {

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

    }

    /***********************************************************************************************
     * Methods to be implemented for LocationListener Interface.
     **********************************************************************************************/
    @Override
    public void onLocationChanged(Location location) {
        String str;
        Log.i(LOG_TAG, location.toString());
        str = "LATITUDE: " + Double.toString(location.getLatitude()) +
                " LONGITUDE: " +Double.toString(location.getLongitude()) +
        " ALTITUDE: " +Double.toString(location.getAltitude());
        txtOutput.setText(str);
    }

    /***********************************************************************************************
     * On start and onStop should always be called when we use Google Location Services.
     **********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        //connect the client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        //Disconnect the client
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
