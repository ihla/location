package co.joyatwork.maps;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.LocationSource;

public class LocationFragmentActivity extends FragmentActivity  
	implements LocationSource, 
			LocationListener, 
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationClient locationClient;
	private Location currentLocation;
	private static String TAG = "Location";


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_fragment_activity);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        locationClient = new LocationClient(this, this, this);
    }

	@Override
	protected void onResume() {
		
		super.onResume();
		locationClient.connect();

	}

	@Override
	protected void onPause() {
	
		if (locationClient.isConnected()) {

			locationClient.removeLocationUpdates(this);
		
		}
		locationClient.disconnect();
		super.onPause();

	}


    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);
        return true;
    }


    /**
     * LocationSource
     */
	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}


    /**
     * LocationSource
     */
	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		currentLocation = location;
		Log.d(TAG, "onLocationChanged - " + currentLocation.toString());
		
	}


	/**
	 * OnConnectionFailedListener
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ConnectionCallbacks
	 */
	@Override
	public void onConnected(Bundle arg0) {

		/* 
		 * If a location is not available, which should happen very rarely, null will be returned.
		 */
		currentLocation = locationClient.getLastLocation();
		
		Toast.makeText(this, "LocationClient connected", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onConnected - " + (currentLocation != null ? currentLocation.toString() : "?"));
		
		LocationRequest locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(5000)
				;
		
		locationClient.requestLocationUpdates(locationRequest, this);
	}


	/**
	 * ConnectionCallbacks
	 */
	@Override
	public void onDisconnected() {

		Toast.makeText(this, "LocationClient disconnected", Toast.LENGTH_SHORT).show();

	}
    
}
