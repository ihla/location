package co.joyatwork.maps;

import com.android.debug.hv.ViewServer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.app.Activity;
import android.graphics.PixelFormat;

public class MainActivity extends Activity {

	private MapView mapView;
    private GoogleMap map;
	private GLSurfaceView glView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        glView = new GLSurfaceView(this);
        // Create an OpenGL ES 2.0 context.
        glView.setEGLContextClientVersion(2);
        //rgba_8888
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        glView.setRenderer(new MyGLRenderer());
        
        setContentView(glView);
        
        //set translucent view
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // put glView on the top of window
        glView.setZOrderOnTop(true);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        
        addContentView(mapView, 
        		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        glView.getParent().bringChildToFront(glView);

        setUpMapIfNeeded();

        ViewServer.get(this).addWindow(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		mapView.onDestroy();
        ViewServer.get(this).removeWindow(this);
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		mapView.onDestroy();
		super.onLowMemory();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        glView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        glView.onResume();

        ViewServer.get(this).setFocusedWindow(this);
	}

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = mapView.getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        
    }

}
