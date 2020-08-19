package com.jacobarau.helium.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.jacobarau.helium.R;

public class Notifications {
    private static final String CHANNEL_ID = "update";
    public static final int ONGOING_NOTIFICATION_ID = 1;

    public Notification buildUpdateServiceNotification(Context context) {
        Intent notificationIntent = new Intent(context, SubscriptionsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(context.getText(R.string.update_notification_title))
                .setContentText(context.getText(R.string.update_notification_message))
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pendingIntent)
                .setTicker(context.getText(R.string.update_ticker_text));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }

        return notificationBuilder.getNotification();
    }
}
