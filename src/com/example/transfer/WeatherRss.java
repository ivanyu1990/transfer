package com.example.transfer;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class WeatherRss extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_rss);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		WeatherRSSpaser wrss = new WeatherRSSpaser("http://rss.weather.gov.hk/rss/CurrentWeather_uc.xml");
		wrss.readRSS();
		Log.i("log", wrss.getResult().get(0));
		String str = wrss.getResult().get(0);
		System.out.println(str);
		String[] tdstr = str.split("<tr>");
		for(int count = 0 ; count < tdstr.length ; count++ ){
			//System.out.println(count + tdstr[count]);
			int s = tdstr[count].indexOf("<td>") + 4;
			int e = tdstr[count].indexOf("</td>");
			//System.out.println(tdstr[count].indexOf("<td>"));
			//System.out.println(tdstr[count].indexOf("</td>"));
			//System.out.println(s+" | "+e);	
			if(s > 0 && e > 0)
				Log.i("log", tdstr[count].substring(s,e));
				//System.out.println(tdstr[count].substring(s,e));
		
			//System.out.println(tdstr[count].indexOf("<td width=\"100\" align=\"right\">"));
			//System.out.println(tdstr[count].indexOf("</SPAN>"));
			s = tdstr[count].indexOf("<td width=\"100\" align=\"right\">") + 30;
			e = tdstr[count].indexOf("</SPAN>");
			//System.out.println(s+" | "+e);	
			if(s > 0 && e > 0)
				Log.i("log", tdstr[count].substring(s,e));
				//System.out.println(tdstr[count].substring(s,e));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.weather_rss, menu);
		return true;
	}

}
