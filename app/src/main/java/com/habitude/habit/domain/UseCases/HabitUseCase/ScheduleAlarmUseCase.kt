package com.habitude.habit.domain.UseCases.HabitUseCase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.habitude.habit.recievers.AlarmReceiver
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class ScheduleAlarmUseCase {

    operator fun invoke(id:String, time: LocalDateTime, context:Context){
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply { putExtra("habitId",id) }
        val todayTime =time.withYear(LocalDate.now().year).withMonth(LocalDate.now().monthValue).withDayOfMonth(LocalDate.now().dayOfMonth)
        Log.e("TAG", "invoke: alarm ${ZoneId.systemDefault()}", )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            todayTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            PendingIntent.getBroadcast(
                context,
                retrieveNumbersFromString(id),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(todayTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))

        Log.e("alarm scheduler", "invoke: alarm scheduled at $formattedTime $todayTime")
    }
    private fun retrieveNumbersFromString(input: String): Int {
        val numbersOnly = input.replace("\\D".toRegex(), "")
        return numbersOnly.substring(0,4).toInt()
    }
}