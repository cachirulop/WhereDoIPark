
package com.cachirulop.whereiparked.activity;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cachirulop.whereiparked.R;
import com.cachirulop.whereiparked.broadcast.BluetoothBroadcastReceiver;

public class MainActivityOsmDroid
        extends Activity
{
    BluetoothBroadcastReceiver _bluetoothReceiver;
    MyLocationNewOverlay       _locationOverlay;

    private final static int   CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main_osmdroid);

        initMap ();
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
        MapView view;
        ScaleBarOverlay scaleOverlay;

        view = getMap ();
        _locationOverlay = new MyLocationNewOverlay (this,
                                                     new GpsMyLocationProvider (this),
                                                     view);
        scaleOverlay = new ScaleBarOverlay (this);
        scaleOverlay.setCentred (true);
        scaleOverlay.setScaleBarOffset (getResources ().getDisplayMetrics ().widthPixels / 2,
                                        10);

        // _locationOverlay.disableMyLocation (); // not on by default
        // _locationOverlay.disableFollowLocation ();
        _locationOverlay.setDrawAccuracyEnabled (true);
        _locationOverlay.runOnFirstFix (new Runnable ()
        {
            public void run ()
            {
                getMap ().getController ().animateTo (_locationOverlay.getMyLocation ());
            }
        });

        view.setBuiltInZoomControls (true);
        view.setMultiTouchControls (true);

        view.getOverlays ().add (_locationOverlay);
        view.getOverlays ().add (scaleOverlay);

        _locationOverlay.enableMyLocation ();
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
        IMapController controller;
        GeoPoint currentLocation;

        controller = getMap ().getController ();
        currentLocation = _locationOverlay.getMyLocation ();

        controller.animateTo (currentLocation);
        controller.setCenter (currentLocation);
        controller.setZoom (21);
    }

    private MapView getMap ()
    {
        return (MapView) findViewById (R.id.mapview);
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
        /*
         * // Check that Google Play services is available int resultCode =
         * GooglePlayServicesUtil.isGooglePlayServicesAvailable (this);
         * 
         * // If Google Play services is available if (ConnectionResult.SUCCESS
         * == resultCode) { // In debug mode, log the status Log.d
         * ("Location Updates", "Google Play services is available.");
         * 
         * // Continue return true; } else { // Get the error dialog from Google
         * Play services Dialog errorDialog =
         * GooglePlayServicesUtil.getErrorDialog (resultCode, this,
         * CONNECTION_FAILURE_RESOLUTION_REQUEST);
         * 
         * // If Google Play services can provide an error dialog if
         * (errorDialog != null) { // Create a new DialogFragment for the error
         * dialog ErrorDialogFragment errorFragment = new ErrorDialogFragment
         * ();
         * 
         * // Set the dialog in the DialogFragment errorFragment.setDialog
         * (errorDialog);
         * 
         * // Show the error dialog in the DialogFragment errorFragment.show
         * (getFragmentManager (), "Location Updates");
         * 
         * }
         * 
         * return false; }
         */
        return false;
    }

}
