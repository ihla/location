/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.joyatwork.maps;

import com.android.debug.hv.ViewServer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
public class MapViewActivity extends Activity {
//public class MapViewActivity extends android.support.v4.app.FragmentActivity {
    private MapView mapView;
    private GoogleMap map;

    private GLSurfaceView glView;

    class MyGLSurfaceView extends GLSurfaceView {

        public MyGLSurfaceView(Context context) {
            super(context);

            // Create an OpenGL ES 2.0 context.
            setEGLContextClientVersion(2);

            //????
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            
            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(new MyGLRenderer());
            
            //????
            getHolder().setFormat(PixelFormat.TRANSLUCENT);

            // Have to do this or else 
            // GlSurfaceView wont be transparent.
            setZOrderOnTop(true);
            
            // Render the view only when there is a change in the drawing data
            //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mapview);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        glView = new MyGLSurfaceView(this);
        
        setContentView(glView);
        
        //addContentView(mapView, 
        	//	new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT ));
        
        //setUpMapIfNeeded();
        /**/
        mapView = new MapView(this);
        setContentView(mapView);
        mapView.onCreate(savedInstanceState);
        mapView.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,
        		ViewGroup.LayoutParams.MATCH_PARENT ));
        setUpMapIfNeeded();
        

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        glView.onResume();

        //setUpMapIfNeeded();
        
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onPause() {
        mapView.onPause();
    	
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        glView.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
    	
        super.onDestroy();
    	
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
