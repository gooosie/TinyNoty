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

        NotyUtil.showNoty(context, title, content);
    }
}
