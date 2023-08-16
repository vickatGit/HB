package com.example.habit.ui.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbUseCase
import com.example.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import com.example.habit.domain.UseCases.HabitUseCase.UpdateHabitEntriesUseCase
import com.example.habit.domain.models.Entry
import com.example.habit.ui.mapper.HabitMapper.EntryMapper
import com.example.habit.ui.model.EntryView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class UpdateHabitEntriesService : JobIntentService() {

    @Inject
    lateinit var entryMapper: EntryMapper

    @Inject
    lateinit var getHabitThumbUseCase: GetHabitThumbUseCase

    @Inject
    lateinit var updateHabitEntriesUseCase: UpdateHabitEntriesUseCase

    @Inject
    lateinit var scheduleAlarmUseCase: ScheduleAlarmUseCase

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, UpdateHabitEntriesService::class.java, 123, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val isUpgrade = intent.getBooleanExtra("isUpgrade", false)
        Log.e("TAG", "onHandleWork: $isUpgrade")
        val habitId = intent.getStringExtra("habitId")
        val habitServerId = intent.getStringExtra("habitServerId")
        val todayDate = intent.getStringExtra("todayDate")
        Log.e("TAG", "onHandleWork: ")
        CoroutineScope(Dispatchers.IO).launch {
            var entries = hashMapOf<LocalDate, Entry>()
            val habit = getHabitThumbUseCase(habitId = habitId!!)
            Log.e("TAG", "onHandleWork: ${Gson().toJson(habit)}")
            habit.entries?.let {
                entries = it
            }
            selectDate(
                habitId,habitServerId, LocalDate.parse(todayDate),
                isUpgrade,
                entries.mapValues {
                    entryMapper.mapFromEntry(it.value)
                }.toMutableMap() as java.util.HashMap<LocalDate, EntryView>,
                habit.isReminderOn!!,
                habit.reminderTime
            )
        }
    }

    private fun selectDate(
        habitId: String,
        habitServerId: String?,
        date: LocalDate?,
        isUpgrade: Boolean,
        habitEntries: java.util.HashMap<LocalDate, EntryView>,
        reminderOn: Boolean,
        reminderTime: LocalDateTime?
    ) {
        date?.let {
            if (habitEntries.containsKey(it)) {
                habitEntries[it]!!.completed = !habitEntries[it]!!.completed
                Log.e("TAG", "updateEntry selectDate: present $date ")
                updateEntries(habitId,habitServerId, it, isUpgrade, habitEntries, reminderOn, reminderTime)
            } else {
                habitEntries[it] = EntryView(it!!, 0, true)
                Log.e("TAG", "updateEntry selectDate: not present $date ")
                updateEntries(habitId,habitServerId, it, isUpgrade, habitEntries, reminderOn, reminderTime)
            }
        }

    }


    private fun updateEntries(
        habitId: String,
        habitServerId: String?,
        date: LocalDate,
        isUpgrade: Boolean,
        habitEntries: HashMap<LocalDate, EntryView>,
        reminderOn: Boolean,
        reminderTime: LocalDateTime?
    ) {
        val prevEntry = habitEntries[date.minusDays(1)]

        if (prevEntry == null) {
            habitEntries[date] = EntryView(date, if (isUpgrade) 1 else 0, isUpgrade)
        }
        val habitList = mutableListOf<EntryView>()
        habitList.addAll(habitEntries.values)
        habitList.sortBy { it.timestamp }

        habitList.forEachIndexed { index, it ->
            if (index > 0 && (it.timestamp!!.isAfter(date) || it.timestamp.isEqual(date))) {
                var score = habitList.get(if (index != 0) index - 1 else index).score
                habitList[index].score = if (isUpgrade) score!! + 1 else it.score!! - 1
            }
        }
        habitEntries.putAll(habitList.associateBy { it.timestamp!! })

        CoroutineScope(Dispatchers.IO).launch {
            updateHabitEntriesUseCase(habitServerId,habitId, habitEntries.mapValues {
                entryMapper.mapToEntry(it.value)
            }.toMutableMap() as HashMap<LocalDate, Entry>)
        }

    }
}
