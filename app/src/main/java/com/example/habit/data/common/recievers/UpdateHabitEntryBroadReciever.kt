package com.example.habit.data.common.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.habit.ui.services.UpdateHabitEntriesService

class UpdateHabitEntryBroadRecieve : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", "onReceive: update ${intent!!.getBooleanExtra("isUpgrade",false)}", )
        UpdateHabitEntriesService.enqueueWork(context!!,intent!!)
    }
}