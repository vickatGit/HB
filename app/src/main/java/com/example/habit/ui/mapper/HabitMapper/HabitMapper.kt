package com.example.habit.ui.mapper.HabitMapper

import android.util.Log
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.example.habit.ui.model.EntryView
import com.example.habit.ui.model.HabitThumbView
import com.example.habit.ui.model.HabitView
import java.time.LocalDate
import javax.inject.Inject

class HabitMapper @Inject constructor(val entryMapper: EntryMapper) :
    HabitMapperI<HabitView, Habit, HabitThumb, HabitThumbView> {
    override fun mapFromHabit(type: HabitView): Habit {
        return Habit(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime,
            null,
            habitGroupId = type.habitGroupId,
            habitGroupLocalId = type.habitGroupLocalId


        )
    }

    override fun mapToHabit(type: Habit): HabitView {
        Log.e("TAG", "mapToHabit: $type", )
        return HabitView(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime,
            type.entries?.let {
                it.mapValues {
                    entryMapper.mapFromEntry(it.value)
                }.toMutableMap() as HashMap<LocalDate, EntryView>
            },
            type.habitGroupId?:null,
            type.userId?:null,
            habitGroupLocalId = type.habitGroupLocalId


        )
    }

    override fun mapToHabitThumbView(type: HabitThumb): HabitThumbView {
        return HabitThumbView(
            type.id,
            type.title,
            type.startDate,
            type.endDate,
            type.entries?.let {
                it.mapValues {
                    entryMapper.mapFromEntry(it.value)
                }.toMutableMap() as HashMap<LocalDate, EntryView>?
            }
        )
    }
}