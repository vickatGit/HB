package com.example.habit.data.common.recievers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.habit.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", "onReceive: alarm triggered" )
        context?.let { showNotification(it) }
    }
    fun showNotification(context: Context) {
        // Create a notification builder
        val builder = NotificationCompat.Builder(context, "test")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification Triggered")
            .setContentText("This is a sample notification.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Create a notification manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("test", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Display the notification
        notificationManager.notify(0, builder.build())
    }
}