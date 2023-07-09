package com.example.habit.domain.UseCases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.habit.data.common.recievers.AlarmReceiver
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.util.Date
import java.util.Locale

class ScheduleAlarmUseCase {

    suspend operator fun invoke(reqCode:Int,msg:String,time: LocalDateTime,context:Context){
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", msg)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000))

        Log.e("alarm scheduler", "invoke: alarm scheduled at $formattedTime ${time.toString()}", )
    }
}