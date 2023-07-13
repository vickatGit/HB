package com.example.habit.data.Mapper

import com.example.habit.data.models.HabitEntity
import com.example.habit.domain.models.Habit

import com.example.habit.domain.models.HabitThumb

class HabitMapper : Mapper<HabitEntity?,HabitThumb,Habit>{
    override fun mapFromHabitEntity(type: HabitEntity?): HabitThumb {
        return HabitThumb(type?.id!!,type?.title,type?.startDate,type?.endDate)
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
            type.reminderTime
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
            type?.reminderTime
        )
    }

}