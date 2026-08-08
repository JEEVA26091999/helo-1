package com.example.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID_FOLLOWERS = "helo_followers_channel"
    private const val CHANNEL_NAME_FOLLOWERS = "Followers & Activity"

    private const val CHANNEL_ID_MESSAGES = "helo_messages_channel"
    private const val CHANNEL_NAME_MESSAGES = "Direct Messages"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val followersChannel = NotificationChannel(
                CHANNEL_ID_FOLLOWERS,
                CHANNEL_NAME_FOLLOWERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when someone follows your Helo profile or interacts with your posts."
                enableVibration(true)
            }

            val messagesChannel = NotificationChannel(
                CHANNEL_ID_MESSAGES,
                CHANNEL_NAME_MESSAGES,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Direct message alerts from users and friends on Helo."
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(followersChannel)
            manager.createNotificationChannel(messagesChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showNewFollowerNotification(context: Context, followerName: String) {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_FOLLOWERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Follower on Helo!")
            .setContentText("$followerName started following you.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: SecurityException) {
            // Permission missing or denied by user
        }
    }

    fun showDirectMessageNotification(context: Context, senderName: String, messageText: String) {
        createNotificationChannels(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MESSAGES)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("New Message from $senderName")
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: SecurityException) {
            // Permission missing or denied by user
        }
    }
}
