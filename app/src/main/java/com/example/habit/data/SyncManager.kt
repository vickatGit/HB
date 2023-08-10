package com.example.habit.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.Repository.HabitRepo
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltWorker
class SyncManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workParams: WorkerParameters,
    private val habitRepo: HabitRepo
) : CoroutineWorker(context, workParams) {
    //    @Inject
//    lateinit var habitRepo: HabitRepo
    override suspend fun doWork(): Result {
        Log.e("TAG", "doWork: Sync Manager invoked")
        withContext(Dispatchers.IO) {
            habitRepo.getUnSyncedHabits().collect { habits ->
                habits?.map { habit ->
                    Log.e("TAG", "--doWork: ${Gson().toJson(habit)}")
                    when (habit.habitSyncType) {
                        HabitRecordSyncType.AddHabit -> {
                            habitRepo.addOrUpdateHabitToRemote(habit)
                        }

                        HabitRecordSyncType.SyncedHabit -> {}
                        HabitRecordSyncType.DeleteHabit -> {
                            habitRepo.deleteFromRemote(habit.id)
                        }

                        HabitRecordSyncType.DeletedHabit -> {
                            habitRepo.deleteFromLocal(habit.id)
                        }

                        HabitRecordSyncType.UpdateHabit -> {
                            habitRepo.updateHabitToRemote(habit)
                        }

                        HabitRecordSyncType.UpdateHabitEntries -> {
                            habitRepo.updateHabitEntriesToRemote(habit.id, habit.entryList)
                        }
                    }
                }
            }
        }
        return Result.success()

    }
}