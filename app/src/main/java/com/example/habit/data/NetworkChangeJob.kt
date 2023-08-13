package com.example.habit.data

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.Repository.HabitRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("SpecifyJobSchedulerIdRange")
@AndroidEntryPoint
class NetworkChangeJob : JobService() {

    @Inject
    lateinit var habitRepo: HabitRepo

    @Inject
    lateinit var syncRequest: OneTimeWorkRequest


    override fun onStartJob(params: JobParameters?): Boolean {
        Log.e("TAG", "onStartJob: invoked again NetworkChangeJob")
        CoroutineScope(Dispatchers.IO).launch {
            habitRepo.getUnSyncedHabits().collect{ habits ->
                habits.forEach { habit->
                    when(habit.habitSyncType){
                        HabitRecordSyncType.AddHabit -> {
                            habitRepo.addOrUpdateHabitToRemote(habit)
                        }
                        HabitRecordSyncType.UpdateHabit -> {
                            habitRepo.updateHabitToRemote(habit)
                        }
                        HabitRecordSyncType.UpdateHabitEntries -> {
                            habitRepo.updateHabitEntriesToRemote(habit.serverId,habit.entryList)
                        }
                        HabitRecordSyncType.DeleteHabit -> {
                            habitRepo.deleteFromRemote(habit.id, habit.serverId)
                        }
                        HabitRecordSyncType.DeletedHabit -> {
                            habitRepo.deleteFromLocal(habit.id)
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        showNotification(this,"smaple","sample")
        return true
    }

    fun showNotification(context: Context, title: String?, content: String?) {
        val channelId = createNotificationChannel(context)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationId = 123 // You can set a unique ID for each notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel(context: Context): String {
        val channelId = "my_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Channel"
            val description = "Notification Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }
    override fun onStopJob(params: JobParameters?): Boolean {
        val schedular=getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val component= ComponentName(this,NetworkChangeJob::class.java)
        val jobinfo = JobInfo.Builder(1,component)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()
        schedular.schedule(jobinfo)
        return false
    }
}