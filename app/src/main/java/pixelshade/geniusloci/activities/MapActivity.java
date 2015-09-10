package pixelshade.geniusloci.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pixelshade.geniusloci.R;
import pixelshade.geniusloci.helpers.IntentHelper;
import pixelshade.geniusloci.model.GhostEntry;
import pixelshade.geniusloci.model.ServerListGhostsResponse;
import pixelshade.geniusloci.services.ServerApiService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    @Bind(R.id.lblLocation)
    TextView lblLocation;
    @Bind(R.id.btnShowLocation)
    Button btnShowLocation;
    @Bind(R.id.btnLocationUpdates)
    Button btnLocationUpdates;
    @Bind(R.id.btnAddGhost)
    Button btnAddGhost;
    @Bind(R.id.getAllBtn)
    Button btnGetAll;
    @Bind(R.id.responseTV)
    TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }

    }


    @OnClick(R.id.btnAddGhost)
    public void startUploadActivity() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("lat", latitude);
            intent.putExtra("lon",longitude);
            startActivity(intent);

        } else {

            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    // Show location button click listener
    @OnClick(R.id.btnShowLocation)
    public void onShowLocation() {
        displayLocation();
    }

    // Toggling the periodic location updates
    @OnClick(R.id.btnLocationUpdates)
    public void onStartLocationUpdates() {
        togglePeriodicLocationUpdates();
    }


    @OnClick(R.id.getAllBtn)
    public void getAllEntries() {
        new ServerApiService(this).GetAllEntries(new Callback<List<GhostEntry>>() {
            @Override
            public void success(List<GhostEntry> serverListGhostsResponse, Response response) {
                String resp = response.getBody().toString();
                if (serverListGhostsResponse != null) {
                    resp += "\n";
                    for (GhostEntry entry : serverListGhostsResponse) {
                        resp += entry.name + '\n';
                        resp += entry.content + "\n";
                        resp += "-------------------" + '\n';
                    }
                }
                tvResponse.setText(resp);
            }

            @Override
            public void failure(RetrofitError error) {
                tvResponse.setText(error.getCause().getLocalizedMessage());
            }
        });

    }

    private void getNearEntries(double longtitude, double latitude) {
        new ServerApiService(this).GetNear(
                latitude,
                longtitude,
                new Callback<ServerListGhostsResponse>() {
                    @Override
                    public void success(ServerListGhostsResponse serverListGhostsResponse, Response response) {
                        String resp = response.getBody().toString();
                        if (serverListGhostsResponse != null) {
                            resp += "\n";
                            for (GhostEntry entry : serverListGhostsResponse.ghostEntries) {
                                resp += entry.name + '\n';
                                resp += entry.content + "\n";
                                resp += "-------------------" + '\n';
                            }
                        }
                        tvResponse.setText(resp);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        tvResponse.setText(error.getMessage());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            lblLocation.setText(latitude + ", " + longitude);
            getNearEntries(latitude,longitude);
        } else {

            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnLocationUpdates
                    .setText(getString(R.string.btn_stop_location_updates));

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btnLocationUpdates
                    .setText(getString(R.string.btn_start_location_updates));

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
}

