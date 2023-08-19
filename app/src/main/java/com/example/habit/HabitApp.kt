package com.example.habit

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.habit.data.NetworkChangeJob
import com.google.android.material.color.DynamicColors
import com.judemanutd.autostarter.AutoStartPermissionHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class HabitApp : Application(),Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
//        val schedular=getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
//        val component= ComponentName(this,NetworkChangeJob::class.java)
//        val jobinfo = JobInfo.Builder(1,component)
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//            .setPersisted(true)
//            .build()
//        schedular.schedule(jobinfo)
//        if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this,true)) {
//            Log.e("TAG", "onCreate: its working on this device", )
//            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this, true, true)
//        }else{
//            Log.e("TAG", "onCreate: its not working on this device", )
//        }

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
