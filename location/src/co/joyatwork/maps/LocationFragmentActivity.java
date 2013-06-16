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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LocationFragmentActivity extends FragmentActivity  
	implements LocationSource, 
			LocationListener, 
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationClient locationClient;
	private GoogleMap map = null;
	private OnLocationChangedListener onLocationChangedListener = null;
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
        setUpMapIfNeeded();
    }

	@Override
	protected void onResume() {
		
		super.onResume();
		locationClient.connect();
        setUpMapIfNeeded();


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
	public void activate(OnLocationChangedListener listener) {
		
		 onLocationChangedListener = listener;
		
	}


    /**
     * LocationSource
     */
	@Override
	public void deactivate() {

		onLocationChangedListener = null;
	}


	/**
	 * LocationListener
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		updateCurrentLocationOnMap(location);
		 
		Log.d(TAG, "onLocationChanged - " + location.toString());
		
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
		Location lastLocation = locationClient.getLastLocation();
		if (lastLocation != null) {
			updateCurrentLocationOnMap(lastLocation);
		}
		
		Toast.makeText(this, "LocationClient connected", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onConnected - " + (lastLocation != null ? lastLocation.toString() : "?"));
		
		LocationRequest locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(5000) //TODO 5 sec interval????
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
    
	 /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView
     * MapView}) will show a prompt for the user to install/update the Google Play services APK on
     * their device.
     * <p>
     * A user can return to this Activity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the Activity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {

    	// Do a null check to confirm that we have not already instantiated the map.
    	if (map != null) {
			return;
		}
        
    	// Try to obtain the map from the SupportMapFragment.
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        // Check if we were successful in obtaining the map.
        if (map != null) {
            setUpMap();
        }
 
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
    	
        map.setMyLocationEnabled(true);

        //This is how you register the LocationSource
        map.setLocationSource(this);
        
    }

	private void updateCurrentLocationOnMap(Location location) {
		if( onLocationChangedListener != null ) {
			 
			 onLocationChangedListener.onLocationChanged(location);
			 
			 LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
			 //  prevent the user’s location from going “off-screen”
		     if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
			 
		    	CameraPosition newCameraPosition =  new CameraPosition.Builder()
 					.target(new LatLng(location.getLatitude(), location.getLongitude()))
 					.zoom(18f)
 					.bearing(0)
 					.tilt(65)
 					.build()
 					;

		    	 //map.animateCamera(
	        		// CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		    	 map.moveCamera(
		        		 CameraUpdateFactory.newCameraPosition(newCameraPosition));
		    	 
		     }
	         
        }
	}


}
