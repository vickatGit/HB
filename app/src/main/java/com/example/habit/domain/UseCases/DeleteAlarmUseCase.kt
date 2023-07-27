package com.example.habit.domain.UseCases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import com.example.habit.recievers.AlarmReceiver
import java.time.LocalDateTime
import java.time.ZoneId


class DeleteAlarmUseCase {
    operator fun invoke(habitId:Int,context:Context,time: LocalDateTime){
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("habitId",habitId)
        }
        val alarmIntent=PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            alarmIntent
        )
        alarmManager?.cancel(alarmIntent)

    }
}