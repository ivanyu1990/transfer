package com.example.transfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.example.transfer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SearchResult extends FragmentActivity {

	private GoogleMap mMap;
	private String s = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		// String[] temp;// =
		// Log.i("i", getIntent().getExtras().getString("longlat0"));
		// i.putExtra("longlat" + x, x++ + "," +
		// c.getString(c.getColumnIndex("CHINAME")) + "," +
		// c.getString(c.getColumnIndex("LAT")) + "," +
		// c.getString(c.getColumnIndex("LONG")) + "," +
		// c.getString(c.getColumnIndex("DISTRICT")) );
		// i.putExtra("longlat", c.getColumnIndex("LAT") + "," +
		// c.getColumnIndex("LONG"));
		// Log.i("i", temp[0] + temp[1] + temp[2] + temp[3] + temp[4]);
		// Log.i("i", temp[1]);
		// LatLng hk = new LatLng(Double.parseDouble(temp[2]),
		// Double.parseDouble(temp[3]));
		// mMap.addMarker(new MarkerOptions().position(hk).title(temp[1])
		// .snippet(temp[4]));
		// String s = getIntent().getExtras().get("district").toString();
		// Log.i("i", s);

		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(
				"Marker"));

		DBHelper dbh = new DBHelper(this);
		Cursor c = dbh.getKen(getIntent().getExtras().get("type").toString());
		while (c.moveToNext()) {
			if (c.getString(c.getColumnIndex("DISTRICT")).equals(
					getIntent().getExtras().get("district").toString())) {
				LatLng hk = new LatLng(Double.parseDouble(c.getString(c
						.getColumnIndex("LAT"))), Double.parseDouble(c
						.getString(c.getColumnIndex("LONG"))));
				// Double.parseDouble(temp[3]));
				mMap.addMarker(new MarkerOptions().position(hk)
						.title(c.getString(c.getColumnIndex("CHINAME")))
						.snippet(c.getString(c.getColumnIndex("DISTRICT"))));// .title(temp[1])
				// .snippet(temp[4]));

				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hk, 13));
			}
		}
		dbh.close();

		// s = getIntent().getExtras().get("district").toString();

		/*
		 * String temp = c.getString(c.getColumnIndex("DISTRICT"));
		 * 
		 * LatLng hk = new LatLng(Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LAT"))), Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LONG"))));
		 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hk, 13));
		 * mMap.addMarker(new MarkerOptions().position(hk)
		 * .title(c.getString(c.getColumnIndex("CHINAME")))
		 * .snippet(c.getString(c.getColumnIndex("DISTRICT"))));
		 */
		// updateMaker(0);
	}

	class Mark {
		String chiname;
		float lat;
		float log;
		String district;

		public Mark(String chiname, float lat, float log, String district) {
			this.chiname = chiname;
			this.lat = lat;
			this.log = log;
			this.district = district;
		}
	}
}
