package com.gooosie.tinynoty;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * NotyUtil
 */

public class NotyUtil {
    public static void showNoty(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // API < 16.
            notification = new Notification(R.drawable.ic_noty, title, System.currentTimeMillis());
            notification.flags = Notification.FLAG_NO_CLEAR;
            // Remove by target API > 22
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
