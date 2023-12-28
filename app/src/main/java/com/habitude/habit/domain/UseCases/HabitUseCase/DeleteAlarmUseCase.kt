package com.habitude.habit.domain.UseCases.HabitUseCase

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import com.habitude.habit.recievers.AlarmReceiver
import java.time.LocalDateTime


class DeleteAlarmUseCase {
    operator fun invoke(habitId:String,context:Context,time: LocalDateTime){
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("habitId",habitId)
        }
//        val alarmIntent=PendingIntent.getBroadcast(
//            context,
//            habitId,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
//            alarmIntent
//        )
//        alarmManager?.cancel(alarmIntent)

    }
}