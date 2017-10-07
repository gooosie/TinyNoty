package com.gooosie.tinynoty;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * BootReceiver
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefeneces = context.getSharedPreferences(MainActivity.KEY_SETTING, Context.MODE_PRIVATE);

        if (!sharedPrefeneces.getBoolean(MainActivity.KEY_SELF_STARTING, false)) {
            return;
        }

        String title = sharedPrefeneces.getString(MainActivity.KEY_TITLE, "");
        String content = sharedPrefeneces.getString(MainActivity.KEY_CONTENT, "");
        if (title.length() <= 0 && content.length() <= 0) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // API < 16.
            notification = new Notification(R.drawable.ic_noty, title, System.currentTimeMillis());
            notification.flags = Notification.FLAG_NO_CLEAR;
            notification.setLatestEventInfo(context, title, content, pendingIntent);
        } else {
            // API >= 16
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_noty)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setStyle(new Notification.BigTextStyle().bigText(content))
                    .build();
            notification.flags = Notification.FLAG_NO_CLEAR;
        }

        notificationManager.notify(1, notification);
    }
}
