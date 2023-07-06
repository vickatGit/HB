package com.example.habit.domain.Repository

import java.time.LocalDateTime

interface AlarmSchedulerRepo {

    suspend fun scheduleAlarm(reqCode:Int,msg:String,time: LocalDateTime)
    suspend fun removeAlarm(reqCode: Int)

}