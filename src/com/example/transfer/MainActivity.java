/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.transfer;

//import com.example.android.location.LocationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This the app's main Activity. It provides buttons for requesting the various
 * features of the app, displays the current location, the current address, and
 * the status of the location client and updating services.
 * 
 * {@link #getLocation} gets the current location using the Location Services
 * getLastLocation() function. {@link #getAddress} calls geocoding to get a
 * street address for the current location. {@link #startUpdates} sends a
 * request to Location Services to send periodic location updates to the
 * Activity. {@link #stopUpdates} cancels previous periodic update requests.
 * 
 * The update interval is hard-coded to be 5 seconds.
 */
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;

	// Handles to UI widgets
	private TextView mLatLng;
	private TextView mAddress;
	private ProgressBar mActivityIndicator;
	private TextView mConnectionState;
	private TextView mConnectionStatus;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	/*
	 * Initialize the Activity
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Get handles to the UI view objects
		mLatLng = (TextView) findViewById(R.id.lat_lng);
		mAddress = (TextView) findViewById(R.id.address);
		mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);
		mConnectionState = (TextView) findViewById(R.id.text_connection_state);
		mConnectionStatus = (TextView) findViewById(R.id.text_connection_status);

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Note that location updates are off until the user turns them on
		mUpdatesRequested = false;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);
		ImageButton myB = (ImageButton) findViewById(R.id.bgImage);
		myB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String district = null;
				String address = null;
				String lat = null;
				String log = null;
				// If Google Play Services is available
				if (servicesConnected()) {

					// Get the current location
					Location currentLocation = mLocationClient
							.getLastLocation();

					try {
						// Display the current location in the UI
						Log.i("i", LocationUtils.getLatLng(v.getContext(),
								currentLocation));
						String[] curloc = LocationUtils.getLatLng(
								v.getContext(), currentLocation).split(",");
						String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
								+ curloc[0].trim()
								+ ","
								+ curloc[1].trim()
								+ "&sensor=true&language=zh-TW";
						lat = curloc[0].trim();
						log = curloc[1].trim();
						JSONParser jsp = new JSONParser();
						Log.i("hihihi",
								"Helloken http://maps.googleapis.com/maps/api/geocode/json?latlng="
										+ curloc[0].trim() + ","
										+ curloc[1].trim() + "&sensor=true");
						JSONObject obj = jsp.getJSONFromUrl(url);

						// TextView tw = (TextView) findViewById(R.id.address);
						mAddress.setText(obj.getJSONArray("results")
								.getJSONObject(0)
								.getString("formatted_address"));
						address = obj.getJSONArray("results").getJSONObject(0)
								.getString("formatted_address");
						JSONArray result = obj.getJSONArray("results");
						for (int i = 0; result.getJSONObject(i) != null; i++) {
							JSONObject jo = result.getJSONObject(i);
							JSONArray a = jo.getJSONArray("address_components");
							for (int x = 0; x < a.length(); x++) {
								JSONObject b = a.getJSONObject(x);
								if (b.getString("long_name").contains("區")) {
									mAddress.setText(mAddress.getText()
											+ b.getString("long_name"));
									Log.i("i",
											"HelloKen "
													+ b.getString("long_name"));
									district = b.getString("long_name");
								}
							}
							if (district != null)
								break;
						}

					} catch (Exception e) {
						Log.i("i", "i error" + e.getMessage());
					} finally {
						String districtTemp = null;
						String myDistrict = null;
						String districtAirPol = null;
						try {
							DBHelper dbh = new DBHelper(v.getContext());
							Cursor c = dbh.getDistricts();
							// get weather
							WeatherRSSpaser wrss = new WeatherRSSpaser(
									"http://rss.weather.gov.hk/rss/CurrentWeather_uc.xml");
							wrss.readRSS();

							String str = wrss.getResult().get(0);
							String disfromrss = "nothing";
							String tempfromrss = "";
							// System.out.println(str);
							String[] tdstr = str.split("<tr>");
							for (int count = 0; count < tdstr.length; count++) {
								int s = tdstr[count].indexOf("<td>") + 4;
								int e = tdstr[count].indexOf("</td>");
								if (s > 0 && e > 0) {
									Log.i("log",
											"->["
													+ tdstr[count].substring(s,
															e) + "]");
									disfromrss = obserlocationToDistrict(tdstr[count]
											.substring(s, e));
								}
								s = tdstr[count]
										.indexOf("<td width=\"100\" align=\"right\">") + 30;
								e = tdstr[count].indexOf("</SPAN>");
								if (s > 0 && e > 0)
									tempfromrss = tdstr[count].substring(s, e);
								if (disfromrss.equals(district)) {
									mAddress.setText(mAddress.getText()
											+ disfromrss + tempfromrss + "\n");
									districtTemp = tempfromrss;
								}
							}
							// weather index
							ArrayList<String> al;
							RSSpaser rssp = new RSSpaser(
									"http://www.aqhi.gov.hk/epd/ddata/html/out/aqhi_ind_rss_ChT.xml");
							rssp.readRSS();
							al = rssp.getResult();
							for (int cc = 0; cc < al.size(); cc++) {
								// if(al.get(cc).contains(district))
								mAddress.setText(mAddress.getText()
										+ al.get(cc) + "\n");
							}

							while (c.moveToNext()) {
								Log.i("i",
										"HelloKen"
												+ c.getString(c
														.getColumnIndex("DISTRICT")));
								String temp = c.getString(c
										.getColumnIndex("DISTRICT"));
								// mAddress.setText(mAddress.getText() +
								// "your text here"+disfromrss + tempfromrss);
								if (temp.equals("TAI PO")
										&& district.equals("大埔區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("SHA TIN")
										&& district.equals("沙田區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("SOUTHERN")
										&& district.equals("南區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("NORTH")
										&& district.equals("北區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("YAU TSIM MONG")
										&& district.equals("油尖旺區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("TUEN MUN")
										&& district.equals("屯門區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("SAI KUNG")
										&& district.equals("西貢區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("ISLANDS")
										&& district.equals("離島區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("KWAI TSING")
										&& district.equals("葵青區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("YUEN LONG")
										&& district.equals("元朗區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("TSUEN WAN")
										&& district.equals("荃灣區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("CENTRAL & WESTERN")
										&& district.equals("中西區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("WAN CHAI")
										&& district.equals("灣仔區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("KOWLOON CITY")
										&& district.equals("九龍城區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("WONG TAI SIN")
										&& district.equals("黃大仙區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("KWUN TONG")
										&& district.equals("觀塘區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("SHAM SHUI PO")
										&& district.equals("深水區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
								if (temp.equals("EASTERN")
										&& district.equals("東區")) {
									mAddress.setText(mAddress.getText()
											+ c.getString(c
													.getColumnIndex("CHINAME"))
											+ "\n");
								}
							}
							dbh.close();
							Intent i = new Intent(v.getContext(),
									GovActivity.class);
							i.putExtra("lag", lat);
							i.putExtra("log", log);
							i.putExtra("temp", districtTemp);
							i.putExtra("district", district);
							i.putExtra("address", address);
							startActivity(i);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.i("i", "HelloKen " + e.getMessage());
						}
					}
				}

			}
		});
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	public void onStop() {

		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();

		super.onStop();
	}

	/*
	 * Called when the Activity is going into the background. Parts of the UI
	 * may be visible, but the Activity is inactive.
	 */
	@Override
	public void onPause() {

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED,
				mUpdatesRequested);
		mEditor.commit();

		super.onPause();
	}

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {

		super.onStart();

		/*
		 * Connect the client. Don't re-start any requests here; instead, wait
		 * for onResume()
		 */
		mLocationClient.connect();

	}

	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(
					LocationUtils.KEY_UPDATES_REQUESTED, false);

			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}
		try {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);

			Location currentLocation = mLocationClient.getLastLocation();
			Log.i("as1234",
					"uhi" + LocationUtils.getLatLng(this, currentLocation));

			// mLocationClient.removeLocationUpdates(this);
		} catch (Exception e) {
			Log.i("errorfound",
					"uhi" + e.getLocalizedMessage() + e.getMessage());
		}
	}

	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed()
	 * in LocationUpdateRemover and LocationUpdateRequester may call
	 * startResolutionForResult() to start an Activity that handles Google Play
	 * services problems. The result of this call returns here, to
	 * onActivityResult.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

				// Display the result
				mConnectionState.setText(R.string.connected);
				mConnectionStatus.setText(R.string.resolved);
				break;

			// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

				// Display the result
				mConnectionState.setText(R.string.disconnected);
				mConnectionStatus.setText(R.string.no_resolution);

				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(LocationUtils.APPTAG,
					getString(R.string.unknown_activity_request_code,
							requestCode));

			break;
		}
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getSupportFragmentManager(),
						LocationUtils.APPTAG);
			}
			return false;
		}
	}

	/**
	 * Invoked by the "Get Location" button.
	 * 
	 * Calls getLastLocation() to get the current location
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void getLocation(View v) {
		String district = null;
		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			Location currentLocation = mLocationClient.getLastLocation();

			try {
				// Display the current location in the UI
				mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
				String[] curloc = LocationUtils
						.getLatLng(this, currentLocation).split(",");
				String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
						+ curloc[0].trim()
						+ ","
						+ curloc[1].trim()
						+ "&sensor=true&language=zh-TW";
				JSONParser jsp = new JSONParser();
				Log.i("hihihi",
						"Helloken http://maps.googleapis.com/maps/api/geocode/json?latlng="
								+ curloc[0].trim() + "," + curloc[1].trim()
								+ "&sensor=true");
				JSONObject obj = jsp.getJSONFromUrl(url);

				// TextView tw = (TextView) findViewById(R.id.address);
				mAddress.setText(obj.getJSONArray("results").getJSONObject(0)
						.getString("formatted_address"));

				JSONArray result = obj.getJSONArray("results");
				for (int i = 0; result.getJSONObject(i) != null; i++) {
					JSONObject jo = result.getJSONObject(i);
					JSONArray a = jo.getJSONArray("address_components");
					for (int x = 0; x < a.length(); x++) {
						JSONObject b = a.getJSONObject(x);
						if (b.getString("long_name").contains("區")) {
							mAddress.setText(mAddress.getText()
									+ b.getString("long_name"));
							Log.i("i", "HelloKen " + b.getString("long_name"));
							district = b.getString("long_name");
						}
					}
					if (district != null)
						break;
				}

			} catch (Exception e) {
				Log.i("i", "i error" + e.getMessage());
			} finally {

				try {
					DBHelper dbh = new DBHelper(this);
					Cursor c = dbh.getDistricts();
					// get weather
					WeatherRSSpaser wrss = new WeatherRSSpaser(
							"http://rss.weather.gov.hk/rss/CurrentWeather_uc.xml");
					wrss.readRSS();

					String str = wrss.getResult().get(0);
					String disfromrss = "nothing";
					String tempfromrss = "";
					// System.out.println(str);
					String[] tdstr = str.split("<tr>");
					for (int count = 0; count < tdstr.length; count++) {
						int s = tdstr[count].indexOf("<td>") + 4;
						int e = tdstr[count].indexOf("</td>");
						if (s > 0 && e > 0) {
							Log.i("log", "->[" + tdstr[count].substring(s, e)
									+ "]");
							disfromrss = obserlocationToDistrict(tdstr[count]
									.substring(s, e));
						}
						s = tdstr[count]
								.indexOf("<td width=\"100\" align=\"right\">") + 30;
						e = tdstr[count].indexOf("</SPAN>");
						if (s > 0 && e > 0)
							tempfromrss = tdstr[count].substring(s, e);
						if (disfromrss.equals(district))
							mAddress.setText(mAddress.getText() + disfromrss
									+ tempfromrss + "\n");
					}
					// weather index
					ArrayList<String> al;
					RSSpaser rssp = new RSSpaser(
							"http://www.aqhi.gov.hk/epd/ddata/html/out/aqhi_ind_rss_ChT.xml");
					rssp.readRSS();
					al = rssp.getResult();
					for (int cc = 0; cc < al.size(); cc++) {
						// if(al.get(cc).contains(district))
						mAddress.setText(mAddress.getText() + al.get(cc) + "\n");
					}

					while (c.moveToNext()) {
						Log.i("i",
								"HelloKen"
										+ c.getString(c
												.getColumnIndex("DISTRICT")));
						String temp = c.getString(c.getColumnIndex("DISTRICT"));
						// mAddress.setText(mAddress.getText() +
						// "your text here"+disfromrss + tempfromrss);
						if (temp.equals("TAI PO") && district.equals("大埔區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("SHA TIN") && district.equals("沙田區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("SOUTHERN") && district.equals("南區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("NORTH") && district.equals("北區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("YAU TSIM MONG")
								&& district.equals("油尖旺區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("TUEN MUN") && district.equals("屯門區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("SAI KUNG") && district.equals("西貢區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("ISLANDS") && district.equals("離島區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("KWAI TSING") && district.equals("葵青區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("YUEN LONG") && district.equals("元朗區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("TSUEN WAN") && district.equals("荃灣區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("CENTRAL & WESTERN")
								&& district.equals("中西區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("WAN CHAI") && district.equals("灣仔區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("KOWLOON CITY")
								&& district.equals("九龍城區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("WONG TAI SIN")
								&& district.equals("黃大仙區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("KWUN TONG") && district.equals("觀塘區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("SHAM SHUI PO")
								&& district.equals("深水區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
						if (temp.equals("EASTERN") && district.equals("東區")) {
							mAddress.setText(mAddress.getText()
									+ c.getString(c.getColumnIndex("CHINAME"))
									+ "\n");
						}
					}
					dbh.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("i", "HelloKen " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Invoked by the "Get Address" button. Get the address of the current
	 * location, using reverse geocoding. This only works if a geocoding service
	 * is available.
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	// For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
	@SuppressLint("NewApi")
	public void getAddress(View v) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a
		// geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(this, R.string.no_geocoder_available,
					Toast.LENGTH_LONG).show();
			return;
		}

		if (servicesConnected()) {

			// Get the current location
			Location currentLocation = mLocationClient.getLastLocation();

			// Turn the indefinite activity indicator on
			mActivityIndicator.setVisibility(View.VISIBLE);

			// Start the background task
			(new MainActivity.GetAddressTask(this)).execute(currentLocation);
		}
	}

	/**
	 * Invoked by the "Start Updates" button Sends a request to start location
	 * updates
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void startUpdates(View v) {
		mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/**
	 * Invoked by the "Stop Updates" button Sends a request to remove location
	 * updates request them.
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void stopUpdates(View v) {
		mUpdatesRequested = false;

		if (servicesConnected()) {
			stopPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		mConnectionStatus.setText(R.string.connected);

		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		mConnectionStatus.setText(R.string.disconnected);
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {

		// Report to the UI that the location was updated
		mConnectionStatus.setText(R.string.location_updated);

		// In the UI, set the latitude and longitude to the value received
		mLatLng.setText(LocationUtils.getLatLng(this, location));
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		mConnectionState.setText(R.string.location_requested);
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
		mConnectionState.setText(R.string.location_updates_stopped);
	}

	/**
	 * An AsyncTask that calls getFromLocation() in the background. The class
	 * uses the following generic types: Location - A
	 * {@link android.location.Location} object containing the current location,
	 * passed as the input parameter to doInBackground() Void - indicates that
	 * progress units are not used by this subclass String - An address passed
	 * to onPostExecute()
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it,
		 * format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			/*
			 * Get a new geocoding service instance, set for localized
			 * addresses. This example uses android.location.Geocoder, but other
			 * geocoders that conform to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or
			// network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the
				 * latitude and longitude of the current location. Return at
				 * most 1 address.
				 */
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG,
						getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(
						R.string.illegal_argument_exception,
						location.getLatitude(), location.getLongitude());
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);

				// Format the first line of address
				String addressText = getString(
						R.string.address_output_string,

						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",

						// Locality is usually a city
						address.getLocality(),

						// The country of the address
						address.getCountryName());

				// Return the text
				return addressText;

				// If there aren't any addresses, post a message
			} else {
				return getString(R.string.no_address_found);
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text
		 * of the UI element that displays the address. This method runs on the
		 * UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {

			// Turn off the progress bar
			mActivityIndicator.setVisibility(View.GONE);

			// Set the address in the UI
			mAddress.setText(address);
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(),
					LocationUtils.APPTAG);
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 * 
		 * @param dialog
		 *            An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	private byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}

	private String obserlocationToDistrict(String s) {
		String tmp = "Nothing";
		s = s.trim();
		Log.i("log", "-->[" + s + "]");
		if (s.compareTo("天 文 台") == 0) {
			tmp = "油尖旺區";
		} else if (s.compareTo("京 士 柏") == 0) {
			tmp = "油尖旺區";
		} else if (s.compareTo("黃 竹 坑") == 0) {
			tmp = "南區";
		} else if (s.compareTo("打 鼓 嶺") == 0) {
			tmp = "北區";
		} else if (s.compareTo("大 埔") == 0) {
			tmp = "大埔區";
		} else if (s.compareTo("沙 田") == 0) {
			tmp = "沙田區";
		} else if (s.compareTo("屯 門") == 0) {
			tmp = "屯門區";
		} else if (s.compareTo("將 軍 澳") == 0) {
			tmp = "西貢區";
		} else if (s.compareTo("沙 田") == 0) {
			tmp = "沙田區";
		} else if (s.compareTo("長 洲") == 0) {
			tmp = "離島區";
		} else if (s.compareTo("青 衣") == 0) {
			tmp = "葵青區";
		} else if (s.compareTo("石 崗") == 0) {
			tmp = "元朗區";
		} else if (s.compareTo("荃 灣 可 觀") == 0) {
			tmp = "荃灣區";
		} else if (s.compareTo("荃 灣 城 門 谷") == 0) {
			tmp = "荃灣區";
		} else if (s.compareTo("香 港 公 園") == 0) {
			tmp = "中西區";
		} else if (s.compareTo("筲 箕 灣") == 0) {
			tmp = "東區";
		} else if (s.compareTo("九 龍 城") == 0) {
			tmp = "九龍城區";
		} else if (s.compareTo("跑 馬 地") == 0) {
			tmp = "灣仔區";
		} else if (s.compareTo("黃 大 仙") == 0) {
			tmp = "黃大仙區";
		} else if (s.compareTo("赤 柱") == 0) {
			tmp = "南區";
		} else if (s.compareTo("觀 塘") == 0) {
			tmp = "觀塘區";
		} else if (s.compareTo("深 水 埗") == 0) {
			tmp = "深水埗區";
		}
		return tmp;
	}
}
