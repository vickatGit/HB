package com.habitude.habit

import android.app.Activity
import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.habitude.habit.data.NetworkChangeJob
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.UseCases.HabitUseCase.ScheduleAlarmForHabitsEntriesUseCase
import com.habitude.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import com.google.android.material.color.DynamicColors
import com.judemanutd.autostarter.AutoStartPermissionHelper
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject


@HiltAndroidApp
class HabitApp : Application(),Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var socket:Socket

    @Inject
    lateinit var auth:AuthPref






    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        MultiDex.install(this)
        socket.connect()
        try {
            socket.connect()
            Log.e("TAG", "provideSocket: socket"+socket.id() )
            socket.emit("msg","from client")
            if(socket.connected())
                Log.e("TAG", "provideSocket: socket connected", )
            else
                Log.e("TAG", "provideSocket: socket failed to connect", )

        }catch (e:Exception){
            Log.e("TAG", "provideSocket: socket failed to connnect " )
            e.printStackTrace()
        }
        //

         val scheduler=getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val component= ComponentName(this,NetworkChangeJob::class.java)
        val jobinfo = JobInfo.Builder(1,component)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()
        scheduler.schedule(jobinfo)
        if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this,true)) {
            Log.e("TAG", "onCreate: its working on this device", )
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this, true, true)
        }else{
            Log.e("TAG", "onCreate: its not working on this device", )
        }


    }



    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.ERROR)
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }


//    override fun getWorkManagerConfiguration(): Configuration {
//        return Configuration.Builder().setMinimumLoggingLevel(Log.INFO).setWorkerFactory(workerFactory).build()
//    }



}
