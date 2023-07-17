package com.example.habit.data.Mapper

import com.example.habit.data.models.EntryEntity
import com.example.habit.data.models.HabitEntity
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import java.time.LocalDate
import javax.inject.Inject

class HabitMapper @Inject constructor(private val entryMapper: EntryMapper) :
    HabitMapperI<HabitEntity?, HabitThumb, Habit> {
    override fun mapFromHabitEntity(type: HabitEntity?): HabitThumb {
        return HabitThumb(
            type?.id!!,
            type?.title,
            type?.startDate,
            type?.endDate,
//            null
            type.entryList?.let {
                it.mapValues {
                    entryMapper.mapToEntry(it.value)
                }.toMutableMap() as java.util.HashMap<LocalDate, Entry>?
            }

        )
    }


    override fun mapToHabitEntity(type: Habit): HabitEntity {
        return HabitEntity(
            null,
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
                } as Map<LocalDate, EntryEntity>
            }


        )
    }

    override fun mapToHabit(type: HabitEntity?): Habit {
        return Habit(
            type?.id,
            type?.title,
            type?.description,
            type?.reminderQuestion,
            type?.startDate,
            type?.endDate,
            type?.isReminderOn,
            type?.reminderTime,
            type?.entryList?.let {
                it.mapValues {
                    entryMapper.mapToEntry(it.value)
                } as HashMap<LocalDate, Entry>
            }

        )
    }

}
