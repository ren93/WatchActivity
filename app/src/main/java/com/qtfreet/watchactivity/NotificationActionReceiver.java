package com.qtfreet.watchactivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationActionReceiver extends BroadcastReceiver {
	public static PendingIntent pendingIntent(Context context, int i) {
		Intent intent = new Intent("com.qtfreet.watchactivity.ACTION_NOTIFICATION_RECEIVER");
		intent.putExtra("command", i);
		return PendingIntent.getBroadcast(context, i, intent, 0);
	}

	public static void initNotification(Context context) {
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
	}

	public static void showNotification(Context context, boolean autocancel) {

		PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
		NotificationCompat.Builder mNotificationCompat = new NotificationCompat.Builder(context)
				.setContentTitle(
						context.getString(R.string.is_running, new Object[] { context.getString(R.string.app_name) }))
				.setSmallIcon(R.drawable.ic_notification).setContentText(context.getString(R.string.touch_to_open)).setAutoCancel(!autocancel);
		if (autocancel) {
			mNotificationCompat.addAction(R.drawable.ic_noti_action_resume, context.getString(R.string.noti_action_resume),
					pendingIntent(context, 1));
		} else {
			mNotificationCompat.addAction(R.drawable.ic_noti_action_pause, context.getString(R.string.noti_action_pause), pendingIntent(context, 0));
		}
		mNotificationCompat.addAction(R.drawable.ic_noti_action_stop, context.getString(R.string.noti_action_stop), pendingIntent(context, 2))
				.setContentIntent(activity);
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, mNotificationCompat.build());
	}

	public void onReceive(Context context, Intent intent) {
		switch (intent.getIntExtra("command", -1)) {
		case 0:
			showNotification(context, true);
			ViewWindow.removeView();
			DefaultSharedPreferences.save(context, false);
			return;
		case 1:
			showNotification(context, false);
			DefaultSharedPreferences.save(context, true);
			return;
		case 2:
			ViewWindow.removeView();
			DefaultSharedPreferences.save(context, false);
			initNotification(context);
			return;
		default:
			return;
		}
	}
}