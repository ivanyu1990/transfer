package com.example.transfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("NewApi")
public class Reminder extends SherlockListActivity {
	private String district;
	private String type;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mhour;
	private int mminute;
	private ArrayList<String> mStrings = new ArrayList<String>();
	private ArrayAdapter<String> myArrayAdapter;
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/";
	public static MediaPlayer mSound;
	private static Context context;

	Date myDate;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock;
		menu.add("Save").setTitle("Add event")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		Reminder.context = getApplicationContext();

		File file = new File(dir, "myfile.kenken");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.app.ActionBar ab = getActionBar();
			ab.setTitle("Reminder");
			ab.setSubtitle("Simply add an event here");
			ab.setIcon(R.color.abs__background_holo_dark);
		}

		readDataFile();

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mhour = c.get(Calendar.HOUR_OF_DAY);
		mminute = c.get(Calendar.MINUTE);

		myArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings);
		setListAdapter(myArrayAdapter);

		getListView().setTextFilterEnabled(true);

	}

	protected void setContent(TextView view) {
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		// startActivity(new Intent(this, Datepicker.class));

		final CharSequence[] items = { "香港", "九龍", "新界" };
		final CharSequence[] nt = { "離島", "葵青區", "北區", "西貢區", "沙田區", "大埔區",
				"荃灣區", "屯門區", "元朗區" };
		final CharSequence[] kl = { "深水埗區", "九龍城區", "觀塘區", "黃大仙區", "油尖旺區" };
		final CharSequence[] hki = { "中西區", "東區", "南區", "灣仔區" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Make your selection");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection
				switch (item) {
				case 0:
					showMyDialogList(hki);
					break;
				case 1:
					showMyDialogList(nt);
					break;
				case 2:
					showMyDialogList(kl);
					break;
				default:
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	private void updateDate() {
		showDialog(0);
	}

	// -------------------------------------------update
	// time----------------------------------------//
	public void updatetime() {
		myDate = new Date(mYear, mMonth, mDay, mhour, mminute);
		if (!mStrings.contains(myDate.toString())) {
			mStrings.add(myDate.toString());
			myArrayAdapter.notifyDataSetChanged();

			File file = new File(dir, "myfile.kenken");

			FileWriter fWriter;
			try {
				// fWriter = new FileWriter(file);
				PrintWriter pWriter = new PrintWriter((new FileOutputStream(
						file, true /* append = true */)));
				pWriter.println(myDate.toString());
				pWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Datepicker dialog generation

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDate();
		}
	};

	// Timepicker dialog generation
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mhour = hourOfDay;
			mminute = minute;
			updatetime();

			Calendar calNow = Calendar.getInstance();
			Calendar calSet = (Calendar) calNow.clone();

			calSet.set(Calendar.YEAR, mYear);
			calSet.set(Calendar.MONTH, mMonth);
			calSet.set(Calendar.DATE, mDay);
			calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calSet.set(Calendar.MINUTE, minute);
			calSet.set(Calendar.SECOND, 0);
			calSet.set(Calendar.MILLISECOND, 0);

			if (calSet.compareTo(calNow) <= 0) {
				// Today Set time passed, count to tomorrow
				calSet.add(Calendar.DATE, 1);
			}

			setAlarm(calSet);
		}

	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:

			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);

		case 0:
			return new TimePickerDialog(this, mTimeSetListener, mhour, mminute,
					false);

		}
		return null;

	}

	private void readDataFile() {
		File data = new File(dir + "myfile.kenken");
		if (data != null) {
			try {
				Scanner scanner = new Scanner(data);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					mStrings.add(line);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// myArrayAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final String item = (String) getListAdapter().getItem(position);
		final int pos = position;
		// Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					mStrings.remove(pos);
					myArrayAdapter.notifyDataSetChanged();
					Scanner scanner;
					try {
						File data = new File(dir + "myfile.kenken");
						File newFile = new File(dir + "temp.kenken");
						FileWriter fWriter;
						scanner = new Scanner(data);
						// fWriter = new FileWriter(file);
						PrintWriter pWriter = new PrintWriter(newFile);
						while (scanner.hasNextLine()) {
							String line = scanner.nextLine();
							if (line.equals(item)) {

							} else {
								pWriter.println(line);
								Log.i("i", line);
							}
							Log.i("heha", line);
						}
						data.delete();
						newFile.renameTo(new File(dir + "myfile.kenken"));
						pWriter.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Cancel this event?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}

	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			Calendar calNow = Calendar.getInstance();
			Calendar calSet = (Calendar) calNow.clone();

			calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calSet.set(Calendar.MINUTE, minute);
			calSet.set(Calendar.SECOND, 0);
			calSet.set(Calendar.MILLISECOND, 0);

			if (calSet.compareTo(calNow) <= 0) {
				// Today Set time passed, count to tomorrow
				calSet.add(Calendar.DATE, 1);
			}

			setAlarm(calSet);
		}
	};

	private void setAlarm(Calendar targetCal) {
		Log.i("i", targetCal.toString());
		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		intent.putExtra("1", "la");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
				pendingIntent);

	}

	private void showMyDialogList(final CharSequence[] items) {

		final CharSequence[] arena = { "羽毛球場", "籃球場", "游泳池", "乒乓球場", "網球場",
				"健身室", "高爾夫球場", "攀石場" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Make your selection");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection
				district = items[item].toString();
				showMyTypeList(arena);
				// showDialog(1);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showMyTypeList(final CharSequence[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Make your selection");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection
				type = items[item].toString();
				Log.i("i",
						Search.switchDistrict(district) + " "
								+ Search.switchTable(type));

				DBHelper dbh = new DBHelper(Reminder.context);
				Cursor c = dbh.getKen(Search.switchTable(type));
				c.moveToFirst();
				ArrayList<String> list = new ArrayList<String>();
				while (c.moveToNext()) {
					// list.add
					if (c.getString(c.getColumnIndex("DISTRICT")).equals(
							Search.switchDistrict(district))) {// &&
																// c.getString(c.getColumnIndex("type")).equals(Search.switchTable(type))){
						list.add(c.getString(c.getColumnIndex("CHINAME")));
					}
				}
				CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
				showChoice(cs);
				// showDialog(1);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showChoice(final CharSequence[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Make your selection");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection
				showDialog(1);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static Context getAppContext() {
		return Reminder.context;
	}

}
