package com.example.habit.data.common.recievers

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
        val id=intent?.getIntExtra("habitId",-1)
        Log.e("TAG", "onReceive: alarm triggered" )
        id?.let {
            notificationBuilder.sendNotification(context!!,id)
        }
    }
}