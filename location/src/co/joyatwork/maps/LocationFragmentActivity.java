package co.joyatwork.maps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.google.maps.android.SphericalUtil;

public class LocationFragmentActivity extends FragmentActivity  
	implements SensorEventListener, 
			LocationSource, 
			LocationListener, 
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationClient locationClient;
	private GoogleMap map = null;
	private OnLocationChangedListener onLocationChangedListener = null;
	private SensorManager sensorManager;
	private Sensor vectorSensor;
	private static String TAG = "Location";
	private float[] rotationMatrix = new float[16];
	private float[] orientionValues = new float[3];
	private double azimuth;
	private double tilt;
	private Location lastLocation;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_fragment_activity);

        /*
         * get vector sensor for sensing device orientation
         */
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        
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
		sensorManager.registerListener(this, vectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
		locationClient.connect();
        setUpMapIfNeeded();

	}

	@Override
	protected void onPause() {

		sensorManager.unregisterListener(this);
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
     * SensorEventListener
     */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//calculate azimuth and tilt for camera position based on the device orientation
		SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
		SensorManager.getOrientation(rotationMatrix, orientionValues);
		
		azimuth = Math.toDegrees(orientionValues[0]);
		tilt = Math.toDegrees(orientionValues[1]);
		// TODO this is executed on UI thread, be aware of performance impacts!
		moveCamera();
		
		//Log.d(TAG, "azimuth " + azimuth + " tilt " + tilt);
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
		
		lastLocation = location;
		if( onLocationChangedListener != null ) {
			 
			onLocationChangedListener.onLocationChanged(location);
			
		}
		moveCamera();
		 
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
		lastLocation = locationClient.getLastLocation();
		moveCamera();
		
		Toast.makeText(this, "LocationClient connected", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onConnected - " + (lastLocation != null ? lastLocation.toString() : "?"));
		
		/* Request high accuracy location & update interval 5 sec.
		 * This would be appropriate for mapping applications that are showing your location in real-time.
		 */
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

	private void moveCamera() {

		if (map == null || lastLocation == null) {
			return;
		}
		CameraPosition newCameraPosition =  new CameraPosition.Builder()
				// set target a little bit offset from the center of map
				.target(SphericalUtil.computeOffset(
						new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 100, azimuth))
				//.zoom(18f)
				.zoom((float)(16 + 3*(Math.abs(tilt)/90)))
				.bearing((float) azimuth)
				//.tilt(65)
				.tilt((float) clamp(tilt)) //.tilt(clamp(0, tilt, 70))???
				.build()
				;
		
		 map.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        
	}

	private double clamp(double tilt) {
		double t = Math.abs(tilt);
		return t > 65 ? 65 : t;
	}

}
