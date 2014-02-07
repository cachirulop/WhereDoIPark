package com.cachirulop.whereiparked.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.cachirulop.whereiparked.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initMap();
	}

	private void initMap() {
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		if (map != null) {
			Location currentLocation;
			LatLng currentLatLng;

			map.setMyLocationEnabled(true);
			map.getUiSettings().setCompassEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);

			currentLocation = map.getMyLocation();
			if (currentLocation != null) {
				currentLatLng = new LatLng(currentLocation.getLatitude(),
						currentLocation.getLongitude());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
						13));
			}
		} else {
			ImageButton img;

			img = (ImageButton) findViewById(R.id.ibParked);
			img.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showPreferences();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showPreferences() {
		startActivity(new Intent(this, SettingsActivity.class));
	}
}
