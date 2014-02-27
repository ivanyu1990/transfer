package com.example.transfer;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;

public class GovActivity extends Activity {
	private Button n1;
	private Button a;
	private Button no1;
	private Button no2;
	private Button no3;
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gov_act);
		try {
			Log.i("i", getIntent().getExtras().get("temp").toString());
			Log.i("i", getIntent().getExtras().get("district").toString());

			TextView tv = (TextView) findViewById(R.id.textView2);
			tv.setText(getIntent().getExtras().get("temp").toString() + "°C");
			TextView tv1 = (TextView) findViewById(R.id.textView1);
			tv1.setText(getIntent().getExtras().get("district").toString()
					+ " " + getIntent().getExtras().get("weather").toString()
					+ "\n估計位置:"
					+ getIntent().getExtras().get("address").toString());

			ArrayList<LocationKenKen> closestLoc = new ArrayList<LocationKenKen>();
			DBHelper dbh = new DBHelper(this);
			Cursor c = dbh.getKen(("BASKET_COURT").toString());

			float lat = Float.parseFloat(getIntent().getExtras().get("lat")
					.toString());
			float log = Float.parseFloat(getIntent().getExtras().get("log")
					.toString());

			// gps2m(lat, log, float lat_b, float lng_b)
			// double min = 99999999;

			while (c.moveToNext()) {
				// if (c.getString(c.getColumnIndex("DISTRICT")).equals(
				// getIntent().getExtras().get("district").toString())) {

				String locationLat = c.getString(c.getColumnIndex("LAT"));
				String locationLong = c.getString(c.getColumnIndex("LONG"));
				String chi = c.getString(c.getColumnIndex("CHINAME"));
				String e = c.getString(c.getColumnIndex("ENGNAME"));
				String d = c.getString(c.getColumnIndex("DISTRICT"));

				// LocationKenKen
				double dis = gps2m(lat, log, Float.parseFloat(locationLat),
						Float.parseFloat(locationLong));

				closestLoc.add(new LocationKenKen(
						Float.parseFloat(locationLat), Float
								.parseFloat(locationLong), dis, chi, d));

				// }
			}

			Collections.sort(closestLoc);
			for (int i = 0; i < 3; i++)
				Log.i("i", closestLoc.get(i).toString());

			// tv1.setText(getIntent().getExtras().get("district").toString() );
			n1 = (Button) findViewById(R.id.button_n1);
			a = (Button) findViewById(R.id.button_a);
			n1.setOnClickListener(buttonAddOnClickListener);
			a.setOnClickListener(buttonAddOnClickListener);

			no1 = (Button) findViewById(R.id.button_n1);
			no2 = (Button) findViewById(R.id.button_n2);
			no3 = (Button) findViewById(R.id.button_n3);
			Log.i("i", closestLoc.get(0).getChiname());
			no1.setText(niceString(closestLoc.get(0).getChiname()));
			no2.setText(niceString(closestLoc.get(1).getChiname()));
			no3.setText(niceString(closestLoc.get(2).getChiname()));
		} catch (Exception e) {

		}
	}

	Button.OnClickListener buttonAddOnClickListener = new Button.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// Switch statement so you don't have to use a lot of click
			// listeners

			switch (arg0.getId()) {
			case R.id.button_n1:
				// Intent intent = new Intent(arg0.getContext(),
				// DisplayMessageActivity.class);
				// String msg = "yoooo";
				// intent.putExtra(EXTRA_MESSAGE, msg);
				// arg0.getContext().startActivity(intent);
				// n1.setText("n1 pressed");
				break;
			// doSomething();
			case R.id.button_a:
				Intent intent = new Intent(arg0.getContext(), Reminder.class);
				startActivity(intent);
				// arg0.getContext().startActivity(intent);
				// a.setText("A pressed");
				break;
			// doSomethingElse();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	/*
	 * public void sendMessage(View view) { Intent intent = new Intent(this,
	 * DisplayMessageActivity.class);
	 * 
	 * // if(view.getId()==R.id.button_a){ Button A = (Button)
	 * findViewById(R.id.button_b); String msg = "pressed";
	 * intent.putExtra(EXTRA_MESSAGE, msg); startActivity(intent);
	 * 
	 * // }
	 * 
	 * 
	 * // Do something in response to button // Intent intent = new Intent(this,
	 * DisplayMessageActivity.class); // EditText editText = (EditText)
	 * findViewById(R.id.edit_message); // String message =
	 * editText.getText().toString(); // intent.putExtra(EXTRA_MESSAGE,
	 * message); // startActivity(intent); }
	 */
	/*
	 * private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b)
	 * { float pk = (float) (180 / 3.14169);
	 * 
	 * float a1 = lat_a / pk; float a2 = lng_a / pk; float b1 = lat_b / pk;
	 * float b2 = lng_b / pk;
	 * 
	 * float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1)
	 * FloatMath.cos(b2); float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) *
	 * FloatMath.cos(b1) FloatMath.sin(b2); float t3 = FloatMath.sin(a1) *
	 * FloatMath.sin(b1); double tt = Math.acos(t1 + t2 + t3);
	 * 
	 * return 6366000 * tt; }
	 */

	public double gps2m(float lat1, float lon1, float lat2, float lon2) {
		double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
		double L = Math.toRadians(lon2 - lon1);
		double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
		double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
		double cosSqAlpha;
		double sinSigma;
		double cos2SigmaM;
		double cosSigma;
		double sigma;

		double lambda = L, lambdaP, iterLimit = 100;
		do {
			double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
					* (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
			if (sinSigma == 0) {
				return 0;
			}

			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L
					+ (1 - C)
					* f
					* sinAlpha
					* (sigma + C
							* sinSigma
							* (cos2SigmaM + C * cosSigma
									* (-1 + 2 * cos2SigmaM * cos2SigmaM)));

		} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0) {
			return 0;
		}

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384
				* (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B
								/ 6 * cos2SigmaM
								* (-3 + 4 * sinSigma * sinSigma)
								* (-3 + 4 * cos2SigmaM * cos2SigmaM)));

		double s = b * A * (sigma - deltaSigma);

		return s;
	}

	public void clickButtonB(View v) {
		startActivity(new Intent(this, Search.class));
	}

	public void clickButtonC(View v) {
		Intent i = new Intent(this, Sildermenu.class);
		i.putExtra("district", getIntent().getExtras().get("district")
				.toString());
		startActivity(i);
	}

	public String niceString(String s) {
		// String temp = "";
		char[] temp = new char[30];
		int x = 0;
		for (int i = 0; i < s.length(); i++) {
			Log.i("i", i + " " + s.charAt(i));
			temp[x++] = s.charAt(i);
			if (i % 3 == 2) {
				temp[x++] = '\n';
				// temp.concat("\n");
				// s = s.index
			}
		}
		// temp += '\0';
		Log.i("i", "ken: " + s + " " + String.valueOf(temp).trim());
		return String.valueOf(temp).trim();// new String(temp);
	}

	class LocationKenKen implements Comparable<LocationKenKen> {
		private float lat;
		private float log;
		private double distance;
		private String chiname;
		private String district;

		public LocationKenKen(float lat, float log, double distance,
				String chiname, String district) {
			this.lat = lat;
			this.log = log;
			this.distance = distance;
			this.chiname = chiname;
			this.distance = distance;
		}

		public String getChiname() {
			return this.chiname;
		}

		public String toString() {
			return lat + "," + log + "," + chiname + "," + distance + "";
		}

		@Override
		public int compareTo(LocationKenKen arg0) {
			// TODO Auto-generated method stub
			// int lastCmp = lastName.compareTo(arg0.distance);
			// return (lastCmp != 0 ? lastCmp :
			// firstName.compareTo(arg0.distance));
			// double distanceCmp = this.distance.compareTo(arg0.distance);
			if (this.distance == arg0.distance)
				return 0;
			else if (this.distance > arg0.distance)
				return 1;
			else
				return -1;

		}
	}

	public void onBackPressed() {
	}

	public void settingPage(View v) {
		Intent i = new Intent(this, UserSettingActivity.class);
		startActivityForResult(i, 1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, 1);
			break;

		}

		return true;
	}
}
