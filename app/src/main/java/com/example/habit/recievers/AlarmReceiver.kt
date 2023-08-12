package com.example.habit.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.habit.ui.notification.NotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationBuilder:NotificationBuilder
    override fun onReceive(context: Context?, intent: Intent?) {
        val habitId=intent?.getStringExtra("habitId")
        val habitServerId=intent?.getStringExtra("habitServerId")
        Log.e("TAG", "onReceive: alarm triggered" )
        habitId?.let {
            notificationBuilder.sendNotification(context!!,habitId,habitServerId)
        }
    }
}