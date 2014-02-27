package com.example.transfer;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	private static Context myContext;
	private static SplashScreen instance;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		instance = this;

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				View v = findViewById(R.layout.activity_splash);
				if (isNetworkConnected()) {
					Intent i = new Intent(SplashScreen.this, MainActivity.class);
					startActivity(i);
					finish();
				} else {
					// Toast.makeText(context,
					// "Internet Connection is not avaliable", 5000);
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							SplashScreen.getContext());

					// Setting Dialog Title
					alertDialog.setTitle("Confirm...");

					// Setting Dialog Message
					alertDialog
							.setMessage("Do you want to go to wifi settings?");

					// Setting Icon to Dialog
					// alertDialog.setIcon(R.drawable.ic_launcher);

					// Setting Negative "NO" Button
					alertDialog.setNegativeButton("no",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Write your code here to invoke NO event

									dialog.cancel();
								}
							});
					// Setting Positive "Yes" Button
					alertDialog.setPositiveButton("yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									// Activity transfer to wifi settings
									startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS));
								}
							});

					// Showing Alert Message
					alertDialog.show();

				}
				// close this activity
				// finish();
			}
		}, SPLASH_TIME_OUT);
		// if(isNetworkConnected();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	public static Context getContext() {
		return instance;
		// or return instance.getApplicationContext();
	}

}
