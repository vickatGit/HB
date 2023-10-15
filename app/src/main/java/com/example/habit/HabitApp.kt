package com.example.habit

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.example.habit.data.NetworkChangeJob
import com.example.habit.domain.Repository.SocialRepo
import com.google.android.material.color.DynamicColors
import com.judemanutd.autostarter.AutoStartPermissionHelper
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class HabitApp : Application(),Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var socket:Socket



    @Inject
    lateinit var socialRepo: SocialRepo
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        MultiDex.install(this);
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
        CoroutineScope(Dispatchers.IO).launch {
            socialRepo.getHomeData()
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
