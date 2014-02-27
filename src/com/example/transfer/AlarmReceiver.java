package com.example.transfer;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

public class AlarmReceiver extends BroadcastReceiver {
	static MediaPlayer myS = Reminder.mSound;

	@Override
	public void onReceive(Context k1, Intent k2) {
		// mSound=MediaPlayer.create(AlarmReceiver.this, R.raw.haha );
		Log.i("hello", "helloKen" + k2.getExtras().getString("1"));

		if (k2.getExtras().getString("1").equals("la")) {
			//Reminder.mSound = MediaPlayer.create(k1, R.raw.haha);
			//Reminder.mSound.start();
		} else if (k2.getExtras().getString("1").equals("val")) {
			//Reminder.mSound.stop();
			// MainActivity.mSound = null;
		}
		// We get a reference to the NotificationManager
		NotificationManager notificationManager = (NotificationManager) k1
				.getSystemService(k1.NOTIFICATION_SERVICE);

		String MyText = "Reminder";
		Notification mNotification = new Notification(R.drawable.ic_launcher,
				MyText, System.currentTimeMillis());
		// The three parameters are: 1. an icon, 2. a title, 3. time when the
		// notification appears
		
		String MyNotificationTitle = k2.getExtras().getString("arena");//"Ken!";
		String MyNotificationText = k2.getExtras().getString("type") + k2.getExtras().getString("district");
		
		mNotification.vibrate = new long[] { 100, 200, 100, 10000 };
		Intent MyIntent = new Intent(((ContextWrapper) k1).getBaseContext(),
				Reminder.class);
		PendingIntent StartIntent = PendingIntent.getActivity(
				k1.getApplicationContext(), 0, MyIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		// A PendingIntent will be fired when the notification is clicked. The
		// FLAG_CANCEL_CURRENT flag cancels the pendingintent

		mNotification.setLatestEventInfo(k1.getApplicationContext(),
				MyNotificationTitle, MyNotificationText, StartIntent);

		int NOTIFICATION_ID = 1;
		notificationManager.notify(NOTIFICATION_ID, mNotification);
		// We are passing the notification to the NotificationManager with a
		// unique id.
		  ActivityManager am = (ActivityManager) k1.getSystemService(k1.ACTIVITY_SERVICE);
		  List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		  ComponentName componentInfo = taskInfo.get(0).topActivity;

		Toast.makeText(k1, "Alarm received!", Toast.LENGTH_LONG).show();
	}

}
