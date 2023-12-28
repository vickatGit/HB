package com.example.habit.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.core.app.NotificationManagerCompat
import com.example.habit.ui.services.UpdateHabitEntriesService

class UpdateHabitEntryBroadRecieve : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", "onReceive: update ${intent!!.getBooleanExtra("isUpgrade",false)}", )
        UpdateHabitEntriesService.enqueueWork(context!!,intent!!)
        val notificationManager = NotificationManagerCompat.from(context)
        Log.e("TAG", "onReceive: notificationId ${intent.getStringExtra("habitId")}", )
        notificationManager.cancel(intent.getStringExtra("habitId")?.onlyIntegers()?:0)
    }

    private fun String.onlyIntegers(): Int {
        val regex = Regex("\\d")
        val digits = regex.findAll(this).map { it.value.toIntOrNull() ?: 0 }
        return digits.sum()
    }

}