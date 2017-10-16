package com.qtfreet.watchactivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationActionReceiver extends BroadcastReceiver {

    public static void removeNotification(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

    public static void showNotification(Context context, boolean autoCancel) {

        PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        NotificationCompat.Builder mNotificationCompat = new NotificationCompat.Builder(context)
                .setContentTitle(
                        context.getString(R.string.is_running, new Object[]{context.getString(R.string.app_name)}))
                .setSmallIcon(R.drawable.ic_notification).setContentText(context.getString(R.string.touch_to_open)).setAutoCancel(!autoCancel);
        if (autoCancel) {
            mNotificationCompat.addAction(R.drawable.ic_noti_action_resume, context.getString(R.string.noti_action_resume),
                    pendingIntent(context, 1));
        } else {
            mNotificationCompat.addAction(R.drawable.ic_noti_action_pause, context.getString(R.string.noti_action_pause), pendingIntent(context, 0));
        }
        mNotificationCompat.setOngoing(true);
        mNotificationCompat.addAction(R.drawable.ic_noti_action_stop, context.getString(R.string.noti_action_stop), pendingIntent(context, 2))
                .setContentIntent(activity);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(1, mNotificationCompat.build());
    }

    public static PendingIntent pendingIntent(Context context, int i) {
        Intent intent = new Intent("ACTION_NOTIFICATION_RECEIVER");
        intent.putExtra("command", i);
        return PendingIntent.getBroadcast(context, i, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {
        final int command = intent.getIntExtra("command", -1);
        switch (command) {
            case 0:
                DefaultSharedPreferences.save(context, false);
                ViewWindow.removeView();
                showNotification(context, true);
                return;
            case 1:
                DefaultSharedPreferences.save(context, true);
                showNotification(context, false);
                return;
            case 2:
                DefaultSharedPreferences.save(context, false);
                ViewWindow.removeView();
                removeNotification(context);
                return;
            default:
                return;
        }
    }
}