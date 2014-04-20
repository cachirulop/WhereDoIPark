
package com.cachirulop.whereiparked.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cachirulop.whereiparked.R;
import com.cachirulop.whereiparked.broadcast.BluetoothBroadcastReceiver;
import com.cachirulop.whereiparked.common.ErrorDialogFragment;
import com.cachirulop.whereiparked.manager.ContextManager;
import com.cachirulop.whereiparked.manager.MapFilesManager;
import com.cachirulop.whereiparked.manager.ProgressDialogListener;
import com.cachirulop.whereiparked.providers.MapsForgeTileProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;

public class MainActivity
        extends Activity
{
    BluetoothBroadcastReceiver _bluetoothReceiver;

    private final static int   CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        ContextManager.initContext (this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView (R.layout.activity_main);

        initMap ();
    }

    @Override
    protected void onStart ()
    {
        updateMapDatabase ();

        super.onStart ();
    }

    @Override
    protected void onPause ()
    {
        unregisterReceiver (_bluetoothReceiver);

        super.onPause ();
    }

    @Override
    protected void onResume ()
    {
        _bluetoothReceiver = new BluetoothBroadcastReceiver ();

        registerReceiver (_bluetoothReceiver,
                          new IntentFilter (BluetoothDevice.ACTION_ACL_DISCONNECTED));

        super.onResume ();
    }

    /**
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data)
    {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                // If the result code is Activity.RESULT_OK, try to connect
                // again
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        initMap ();
                        break;
                }
        }
    }

    private void initMap ()
    {
        if (servicesConnected ()) {
            GoogleMap map;

            map = getMap ();
            if (map != null) {
                map.setMyLocationEnabled (true);
                map.getUiSettings ().setCompassEnabled (false);
                map.getUiSettings ().setMyLocationButtonEnabled (false);

                // moveToCurrentLocation ();

                // TODO: Only when not connection available
                map.setMapType (GoogleMap.MAP_TYPE_NONE);
                map.addTileOverlay (new TileOverlayOptions ().tileProvider (new MapsForgeTileProvider ()));
                //map.addTileOverlay (new TileOverlayOptions ().tileProvider (new TestMapForgeTileProvider ()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.main,
                                    menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId ()) {
            case R.id.action_settings:
                showPreferences ();
                return true;

            default:
                return super.onOptionsItemSelected (item);
        }
    }

    private void moveToCurrentLocation ()
    {
        Location currentLocation;
        LatLng currentLatLng;
        GoogleMap map;

        map = getMap ();
        if (map != null) {
            currentLocation = map.getMyLocation ();
            if (currentLocation != null) {
                currentLatLng = new LatLng (currentLocation.getLatitude (),
                                            currentLocation.getLongitude ());

                map.animateCamera (CameraUpdateFactory.newLatLngZoom (currentLatLng,
                                                                      13));
                // map.animateCamera(CameraUpdateFactory.zoomIn());

            }
        }
    }

    private GoogleMap getMap ()
    {
        return ((MapFragment) getFragmentManager ().findFragmentById (R.id.map)).getMap ();
    }

    private void showPreferences ()
    {
        startActivity (new Intent (this,
                                   SettingsActivity.class));
    }

    public void setMyLocation (View v)
    {
        moveToCurrentLocation ();
    }

    private boolean servicesConnected ()
    {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable (this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d ("Location Updates",
                   "Google Play services is available.");

            // Continue
            return true;
        }
        else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog (resultCode,
                                                                        this,
                                                                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment ();

                // Set the dialog in the DialogFragment
                errorFragment.setDialog (errorDialog);

                // Show the error dialog in the DialogFragment
                errorFragment.show (getFragmentManager (),
                                    "Location Updates");

            }

            return false;
        }
    }

    public void updateMapDatabase ()
    {
        final ProgressDialog barProgressDialog;
        final Handler updateBarHandler;
        ProgressDialogListener listener;

        updateBarHandler = new Handler ();
        barProgressDialog = new ProgressDialog (MainActivity.this);

        barProgressDialog.setTitle ("Downloading Image ...");
        barProgressDialog.setMessage ("Download in progress ...");
        barProgressDialog.setProgressStyle (barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress (0);
        barProgressDialog.setMax (20);
        barProgressDialog.show ();
        barProgressDialog.setCancelable (false);
        
        listener = new ProgressDialogListener (barProgressDialog);
        MapFilesManager.updateMapDatabase (listener);
        
/*
        new Thread (new Runnable ()
        {
            @Override
            public void run ()
            {
                try {
                    // Here you should write your time consuming task...
                    while (barProgressDialog.getProgress () <= barProgressDialog.getMax ()) {
                        Thread.sleep (2000);

                        updateBarHandler.post (new Runnable ()
                        {
                            public void run ()
                            {
                                barProgressDialog.incrementProgressBy (2);
                            }
                        });

                        if (barProgressDialog.getProgress () == barProgressDialog.getMax ()) {
                            barProgressDialog.dismiss ();
                        }
                    }
                }
                catch (Exception e) {
                }
            }
        }).start ();
*/
    }
}
