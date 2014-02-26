package com.example.transfer;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
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
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gov_act);
		Log.i("i", getIntent().getExtras().get("temp").toString());
		Log.i("i", getIntent().getExtras().get("district").toString());
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(getIntent().getExtras().get("temp").toString() + "¡C");
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setText(getIntent().getExtras().get("district").toString() + "\n"
				+ getIntent().getExtras().get("address").toString());

		ArrayList<LocationKenKen> closestLoc = new ArrayList<LocationKenKen>();
		DBHelper dbh = new DBHelper(this);
		Cursor c = dbh.getKen(getIntent().getExtras().get("district")
				.toString());
		
		float lat = Float.parseFloat(getIntent().getExtras().get("lat").toString());
		float log = Float.parseFloat(getIntent().getExtras().get("log").toString());
		
		//gps2m(lat, log, float lat_b, float lng_b) 
		//double min = 99999999;
		
		while (c.moveToNext()) {
			if (c.getString(c.getColumnIndex("DISTRICT")).equals(
					getIntent().getExtras().get("district").toString())) {
				
				String locationLat = c.getString(c.getColumnIndex("LAT"));
				String locationLong = c.getString(c.getColumnIndex("LONG"));
				String chi = c.getString(c.getColumnIndex("CHINAME"));
				String e = c.getString(c.getColumnIndex("ENGNAME"));
				String d = c.getString(c.getColumnIndex("DISTRICT"));
				
				//LocationKenKen
				double dis = gps2m(lat, log, Float.parseFloat(locationLat), Float.parseFloat(locationLong));
				
				closestLoc.add(new LocationKenKen(Float.parseFloat(locationLat),Float.parseFloat(locationLong),dis,chi,d));
				
					
				
			}
		}
		
		Collections.sort(closestLoc);
		for(int i=0;i<closestLoc.size();i++)
			Log.i("i", closestLoc.toString());
		
		// tv1.setText(getIntent().getExtras().get("district").toString() );
		n1 = (Button) findViewById(R.id.button_n1);
		a = (Button) findViewById(R.id.button_a);
		n1.setOnClickListener(buttonAddOnClickListener);
		a.setOnClickListener(buttonAddOnClickListener);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
	    float pk = (float) (180/3.14169);

	    float a1 = lat_a / pk;
	    float a2 = lng_a / pk;
	    float b1 = lat_b / pk;
	    float b2 = lng_b / pk;

	    float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
	    float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
	    float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000*tt;
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
	
	class LocationKenKen implements Comparable<LocationKenKen> {
		private float lat;
		private float log;
		private double distance;
		private String chiname;
		private String district;
		
		public LocationKenKen( float lat, float log,double distance, String chiname, String district) {
			this.lat = lat;
			this.log = log;
			this.distance = distance;
			this.chiname = chiname;
			this.distance = distance;
		}
		
		public String toString(){
			return distance + "";
		}

		@Override
		public int compareTo(LocationKenKen arg0) {
			// TODO Auto-generated method stub
	        //int lastCmp = lastName.compareTo(arg0.distance);
	        //return (lastCmp != 0 ? lastCmp : firstName.compareTo(arg0.distance));
			//double distanceCmp = this.distance.compareTo(arg0.distance);
			if(this.distance > arg0.distance)
				return 1;
			
			return 0;
		}
	}
}
