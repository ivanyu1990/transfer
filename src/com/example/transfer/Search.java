package com.example.transfer;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity {
	private Spinner spinner1, spinner2;
	private Button btnSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		addItemsOnSpinner2();
		addListenerOnButton();
		addListenerOnSpinnerItemSelection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void addItemsOnSpinner2() {

		spinner2 = (Spinner) findViewById(R.id.spinner2);
		ArrayList<String> list = new ArrayList<String>();
		list.add("羽毛球場");
		list.add("籃球場");
		list.add("游泳池");
		list.add("乒乓球場");
		list.add("網球場");
		list.add("健身室");
		list.add("高爾夫球場");
		list.add("攀石場");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	}

	public void addListenerOnSpinnerItemSelection() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	// get the selected dropdown list value
	public void addListenerOnButton() {

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * String sql = "select * from "+
				 * switchTable(String.valueOf(spinner2.getSelectedItem())) +
				 * " where DISTRICT = \"" +
				 * switchDistrict(String.valueOf(spinner1.getSelectedItem()))+
				 * "\"";
				 * 
				 * Toast.makeText(Search.this, "OnClickListener : " +
				 * "\nSpinner 1 : "+
				 * switchDistrict(String.valueOf(spinner1.getSelectedItem())) +
				 * "\nSpinner 2 : "+
				 * switchTable(String.valueOf(spinner2.getSelectedItem()))+ "\n"
				 * + sql, Toast.LENGTH_SHORT).show();
				 */
				DBHelper dbh = new DBHelper(v.getContext());
				Cursor c = dbh.getKen(switchTable(String.valueOf(spinner2
						.getSelectedItem())));
				TextView tv1 = (TextView) findViewById(R.id.tv1);
				tv1.setText("");
				tv1.setMovementMethod(new ScrollingMovementMethod());
				c.moveToFirst();
				while (c.moveToNext()) {
					// Toast.makeText(Search.this,
					// c.getString(c.getColumnIndex("DISTRICT")), 0);
					Log.i("i", c.getString(c.getColumnIndex("CHINAME")));

					if (switchDistrict(
							String.valueOf(spinner1.getSelectedItem())).equals(
							c.getString(c.getColumnIndex("DISTRICT")))) {
						tv1.setText(tv1.getText() + "\n"
								+ (c.getString(c.getColumnIndex("CHINAME"))));
					}
				}
				dbh.close();
			}

		});
	}

	public static String switchDistrict(String temp) {
		String d = "999";
		if (temp.equals("大埔區")) {
			d = "TAI PO";
		} else if (temp.equals("沙田區")) {
			d = "SHA TIN";
		} else if (temp.equals("南區")) {
			d = "SOUTHERN";
		} else if (temp.equals("北區")) {
			d = "NORTH";
		} else if (temp.equals("油尖旺區")) {
			d = "YAU TSIM MONG";
		} else if (temp.equals("離島區")) {
			d = "ISLANDS";
		} else if (temp.equals("西貢區")) {
			d = "SAI KUNG";
		} else if (temp.equals("屯門區")) {
			d = "TUEN MUN";
		} else if (temp.equals("九龍城區")) {
			d = "KOWLOON CITY";
		} else if (temp.equals("灣仔區")) {
			d = "WAN CHAI";
		} else if (temp.equals("中西區")) {
			d = "CENTRAL & WESTERN";
		} else if (temp.equals("荃灣區")) {
			d = "TSUEN WAN";
		} else if (temp.equals("元朗區")) {
			d = "YUEN LONG";
		} else if (temp.equals("東區")) {
			d = "EASTERN";
		} else if (temp.equals("葵青區")) {
			d = "KWAI TSING";
		} else if (temp.equals("深水埗區")) {
			d = "SHAM SHUI PO";
		} else if (temp.equals("觀塘區")) {
			d = "KWUN TONG";
		} else if (temp.equals("黃大仙區")) {
			d = "WONG TAI SIN";
		}
		return d;

	}

	public static String switchTable(String tmp) {
		String d = "111";
		String[] mymPlanetTitles2mapping = { "TENNIS_COURT", "BASKET_COURT",
				"BADMINTON", "CLIMB", "GOLF", "TABLE_TENNIS", "FITNESS",
				"SWIMMING_POOL" };
		if (tmp.equals("羽毛球場"))
			d = "BADMINTON";
		else if (tmp.equals("籃球場")) {
			d = "BASKET_COURT";
		} else if (tmp.equals("游泳池")) {
			d = "SWIMMING_POOL";
		} else if (tmp.equals("網球場")) {
			d = "TENNIS_COURT";
		} else if (tmp.equals("健身室")) {
			d = "FITNESS";
		} else if (tmp.equals("高爾夫球場")) {
			d = "GOLF";
		} else if (tmp.equals("攀石場")) {
			d = "CLIMB";
		} else if (tmp.equals("乒乓球場")) {
			d = "TABLE_TENNIS";
		}
		return d;
	}

	public void mapResults(View v) {
		DBHelper dbh = new DBHelper(v.getContext());
		Cursor c = dbh.getKen(switchTable(String.valueOf(spinner2
				.getSelectedItem())));
		// TextView tv1 = (TextView) findViewById(R.id.tv1);
		// tv1.setText("");
		// tv1.setMovementMethod(new ScrollingMovementMethod());
		Intent i = new Intent(v.getContext(), SearchResult.class);
		// i.putStringArrayListExtra("chiname", chiname);
		i.putExtra("type",
				switchTable(String.valueOf(spinner2.getSelectedItem())));
		i.putExtra("district",
				switchDistrict(String.valueOf(spinner1.getSelectedItem())));
		startActivity(i);
	}
}
