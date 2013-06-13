package co.joyatwork.maps;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.LocationSource;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class LocationFragmentActivity extends FragmentActivity  
	implements LocationSource, 
			LocationListener, 
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_fragment_activity);
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
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * LocationListener
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * LocationListener
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * LocationListener
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}


	/**
	 * ConnectionCallbacks
	 */
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
    
}
