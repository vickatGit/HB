package com.habitude.habit.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.habitude.habit.domain.UseCases.HabitUseCase.HabitEntryConsistencyUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.ScheduleAlarmForHabitsEntriesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class HabitEntriesConsistencyBroadReceiver: BroadcastReceiver() {
//    @Inject
//    lateinit var habitEntryConsistencyUseCase: HabitEntryConsistencyUseCase
//
//    @Inject
//    lateinit var scheduleAlarmForHabitsEntriesUseCase: ScheduleAlarmForHabitsEntriesUseCase
    override fun onReceive(context: Context?, intent: Intent?) {
//        if(isAfterOrEqualMidnight()){
//            Log.e("TAG", "alarm onReceive: HabitEntriesConsistencyBroadReceiver " )
//            CoroutineScope(Dispatchers.IO).launch {
//                habitEntryConsistencyUseCase(this)
//                context?.let {
//                    scheduleHabitEntryAlarm(it)
//                }
//
//            }
//        }
    }
//    fun isAfterOrEqualMidnight(): Boolean {
//        val now = LocalTime.now()
//        val midnight = LocalTime.MIDNIGHT
//
//        return !now.isBefore(midnight)
//    }
//    private fun scheduleHabitEntryAlarm(context: Context) {
//        val tomorrow = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay()
//        val time = tomorrow.atZone(ZoneId.systemDefault()).toLocalDateTime()
//        scheduleAlarmForHabitsEntriesUseCase(time,context)
//    }
}