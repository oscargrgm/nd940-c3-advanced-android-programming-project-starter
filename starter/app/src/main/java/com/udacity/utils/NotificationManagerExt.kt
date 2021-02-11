package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R

private const val NOTIFICATION_ID = 1
private const val REQUEST_CODE = 100
private const val FLAGS = 0

fun <T> NotificationManager.sendNotification(
    context: Context,
    destination: Class<T>,
    title: String,
    isSuccessful: Boolean
) {
    val contentIntent = Intent(context, destination).apply {
        putExtra(EXTRA_TITLE, title)
        putExtra(EXTRA_SUCCESS, isSuccessful)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val buttonPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        contentIntent,
        FLAGS
    )

    val builder = NotificationCompat
        .Builder(
            context,
            context.getString(R.string.download_notification_channel_id)
        )
        .setContentTitle(
            context.getString(R.string.notification_title)
        )
        .setContentText(
            context.getString(R.string.notification_description)
        )
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            buttonPendingIntent
        )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}