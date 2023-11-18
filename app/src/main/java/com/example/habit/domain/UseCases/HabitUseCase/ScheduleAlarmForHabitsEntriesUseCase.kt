package com.example.habit.domain.UseCases.HabitUseCase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.habit.recievers.AlarmReceiver
import com.example.habit.recievers.HabitEntriesConsistencyBroadReceiver
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ScheduleAlarmForHabitsEntriesUseCase @Inject constructor() {
    operator fun invoke(time: LocalDateTime, context: Context){
//        val alarmManager = context.getSystemService(AlarmManager::class.java)
//        val intent = Intent(context, HabitEntriesConsistencyBroadReceiver::class.java)
//        val todayTime =time.withYear(LocalDate.now().year).withMonth(LocalDate.now().monthValue).withDayOfMonth(LocalDate.now().dayOfMonth)
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            todayTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//            PendingIntent.getBroadcast(
//                context,
//                101,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
//        val formattedTime = timeFormat.format(Date(todayTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
//
//        Log.e("alarm ScheduleAlarmForHabitsEntriesUseCase", "invoke: alarm scheduled at $formattedTime $todayTime")
    }

}