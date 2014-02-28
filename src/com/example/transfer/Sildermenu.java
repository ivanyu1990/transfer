package com.example.transfer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.myapp.venuemap.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;

public class Sildermenu extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;

	private String[] mymPlanetTitles = null;
	private String[] mymPlanetTitlesmapping = null;
	private GoogleMap mMap;
	private String cat;
	private String district;

	// private String cat = mymPlanetTitlesmapping[0];
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sildermenu);
		district = getIntent().getExtras().get("district").toString();

		Log.i("i", district + "");
		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		// added====
		String[] mymPlanetTitles2 = { "網球場", "籃球場", "羽毛球場", "攀石場", "高爾夫球場",
				"乒乓球場", "健身室", "游泳池" };
		String[] mymPlanetTitles2mapping = { "TENNIS_COURT", "BASKET_COURT",
				"BADMINTON", "CLIMB", "GOLF", "TABLE_TENNIS", "FITNESS",
				"SWIMMING_POOL" };
		// cat = mymPlanetTitlesmapping[5];
		mymPlanetTitles = mymPlanetTitles2;
		mymPlanetTitlesmapping = mymPlanetTitles2mapping;
		cat = mymPlanetTitlesmapping[0];
		if (mymPlanetTitles != null && mymPlanetTitles.length > 0) {
			mPlanetTitles = mymPlanetTitles;
		}
		// ======
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// selectItem(0);
		}
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sildermenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			// Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			// intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			/*
			 * if (intent.resolveActivity(getPackageManager()) != null) {
			 * startActivity(intent); } else { Toast.makeText(this,
			 * R.string.app_not_available, Toast.LENGTH_LONG).show(); }
			 */
			startActivity(new Intent(this, Search.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.i("o", "clicked" + position);
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update selected item and title, then close the drawer
		Log.i("i", "" + position);
		updateMaker(position);
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame", shows a planet
	 */
	/*
	 * public static class PlanetFragment extends Fragment { public static final
	 * String ARG_PLANET_NUMBER = "planet_number";
	 * 
	 * public PlanetFragment() { // Empty constructor required for fragment
	 * subclasses }
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { View rootView =
	 * inflater.inflate(R.layout.fragment_planet, container, false); int i =
	 * getArguments().getInt(ARG_PLANET_NUMBER); String planet =
	 * getResources().getStringArray(R.array.planets_array)[i];
	 * 
	 * int imageId =
	 * getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
	 * "drawable", getActivity().getPackageName()); ((ImageView)
	 * rootView.findViewById(R.id.image)).setImageResource(imageId);
	 * getActivity().setTitle(planet); return rootView; } }
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

	private void setUpMap() {

		mDrawerList.setItemChecked(0, true);
		setTitle(mPlanetTitles[0]);
		mDrawerLayout.closeDrawer(mDrawerList);

		double latitude = 0;
		double longitude = 0;

		latitude = 22.400872;
		longitude = 114.124374;

		/*
		 * gpstracker = new GPSTracker(this);
		 * 
		 * if(gpstracker.canGetLocation()){ latitude = gpstracker.getLatitude();
		 * longitude = gpstracker.getLongitude();
		 * 
		 * Toast.makeText(getApplicationContext(),"Your Location is - \nLat: " +
		 * latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show(); }else{
		 * gpstracker.showSettingsAlert(); }
		 */
		mMap.setMyLocationEnabled(true);
		/*
		 * Log.i("i", "i" + cat); DBHelper dbh = new DBHelper(this); // Cursor c
		 * = dbh.getKen(cat); Cursor c = dbh.getKen(cat); while (c.moveToNext())
		 * { Log.i("i", "HelloKen" + c.getString(c.getColumnIndex("DISTRICT")) +
		 * c.getString(c.getColumnIndex("LAT")) +
		 * c.getString(c.getColumnIndex("LONG"))); String temp =
		 * c.getString(c.getColumnIndex("DISTRICT"));
		 * 
		 * LatLng hk = new LatLng(Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LAT"))), Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LONG"))));
		 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hk, 13));
		 * mMap.addMarker(new MarkerOptions().position(hk)
		 * .title(c.getString(c.getColumnIndex("CHINAME")))
		 * .snippet(c.getString(c.getColumnIndex("DISTRICT"))));
		 * //updateMaker(0); } dbh.close();
		 */

		/*
		 * LatLng hk = new LatLng(Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LAT"))), Double.parseDouble("" +
		 * c.getString(c.getColumnIndex("LONG"))));
		 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hk, 13));
		 * mMap.addMarker(new MarkerOptions().position(hk)
		 * .title(c.getString(c.getColumnIndex("CHINAME")))
		 * .snippet(c.getString(c.getColumnIndex("DISTRICT")))); //
		 * updateMaker(0); } dbh.close();
		 */
		updateMaker(0);
	}

	private void updateMaker(int in_position) {
		mMap.clear();
		Log.d("ooo", "hihi");// "mymPlanetTitlesmapping size" +
								// mymPlanetTitlesmapping.length);
		if (mymPlanetTitlesmapping.length > 0) {
			Log.d("My log", "hihi-->selected:" + in_position + "|"
					+ mymPlanetTitlesmapping[in_position]);
			cat = mymPlanetTitlesmapping[in_position];
		} else {
			Log.i("oo", "mymPlanetTitlesmapping null");
		}

		Log.i("i", "i" + cat);
		DBHelper dbh = new DBHelper(this);
		// Cursor c = dbh.getKen(cat);
		Cursor c = dbh.getKen(cat);

		c.moveToFirst();

		Log.i("i",
				"HelloKen" + c.getString(c.getColumnIndex("DISTRICT"))
						+ c.getString(c.getColumnIndex("LAT"))
						+ c.getString(c.getColumnIndex("LONG")));
		String temp = c.getString(c.getColumnIndex("DISTRICT"));
		LatLng hk = new LatLng(Double.parseDouble(""
				+ c.getString(c.getColumnIndex("LAT"))), Double.parseDouble(""
				+ c.getString(c.getColumnIndex("LONG"))));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hk, 13));
		mMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
				.position(hk).title(c.getString(c.getColumnIndex("CHINAME")))
				.snippet(c.getString(c.getColumnIndex("DISTRICT"))));
		// marker.icon(BitmapDescriptorFactory
		// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.3,
				114.18), 12));
		while (c.moveToNext()) {
			// if (c.getString(c.getColumnIndex("DISTRICT")).equals(district)) {
			// please do district comparision in english
			Log.i("i",
					"HelloKen" + c.getString(c.getColumnIndex("DISTRICT"))
							+ c.getString(c.getColumnIndex("LAT"))
							+ c.getString(c.getColumnIndex("LONG")));
			// String temp = c.getString(c.getColumnIndex("DISTRICT"));

			LatLng hk2 = new LatLng(Double.parseDouble(""
					+ c.getString(c.getColumnIndex("LAT"))),
					Double.parseDouble(""
							+ c.getString(c.getColumnIndex("LONG"))));
			if (Search.switchDistrict(district).equals(
					c.getString(c.getColumnIndex("DISTRICT")))) {
				mMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker)).position(hk2)
						.title(c.getString(c.getColumnIndex("CHINAME")))
						.snippet(c.getString(c.getColumnIndex("DISTRICT"))));
			} else {
				mMap.addMarker(new MarkerOptions().position(hk2)
						.title(c.getString(c.getColumnIndex("CHINAME")))
						.snippet(c.getString(c.getColumnIndex("DISTRICT"))));
			}
			// updateMaker(0);
			// }
		}
		dbh.close();
	}
}
